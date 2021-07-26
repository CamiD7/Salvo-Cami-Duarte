package com.codeoftheweb.salvo.Controller;

import com.codeoftheweb.salvo.Classes.*;

import com.codeoftheweb.salvo.DTO.*;
import com.codeoftheweb.salvo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository repo;

    @Autowired
    private GamePlayerRepository gamePlayRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;
    @Autowired
    private ScoreRepository scoreRepository;



    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByEmail(email) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/games")
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();

        if (isGuest(authentication)) {
            dto.put("player", "Guest");

        } else {
            Player player = playerRepository.findByEmail(authentication.getName());
            dto.put("player", new PlayerDTO(player));

        }
        dto.put("games", repo.findAll().stream().map(GameDTO::new).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "not logged in"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByEmail(authentication.getName());
        if (player == null) {
            return new ResponseEntity<>(makeMap("error", "player is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        Game game = new Game(new Date());
        repo.save(game);
        GamePlayer gamePlayer = new GamePlayer(game, player, new Date());
        gamePlayRepo.save(gamePlayer);
        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }


    @RequestMapping(path = "/game/{game_id}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long game_id, Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        Date joinDate = new Date();
        Game game = repo.findById(game_id).get();
        GamePlayer gamePlayer = game.getGamePlayers().stream().findFirst().get();
        Player player = playerRepository.findByEmail(authentication.getName());
        if (isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error","You must log in"),HttpStatus.UNAUTHORIZED);
        }
        if (game == null){
            return new ResponseEntity<>(makeMap("error","The game does not exists"),HttpStatus.NOT_FOUND);
        }
        if (game.getGamePlayers().size() != 1) {
            return new ResponseEntity<>(makeMap("error", "The game is full"), HttpStatus.FORBIDDEN);
        }
        if (gamePlayer.getPlayer() == player){
            return new ResponseEntity<>(makeMap("error","You cant re-join a game"),HttpStatus.FORBIDDEN);}

        GamePlayer gamePlayer1= new GamePlayer(game,player,new Date());
        gamePlayRepo.save(gamePlayer1);
        return new ResponseEntity<>(makeMap("gpid",gamePlayer1.getId()),HttpStatus.CREATED);


    }


    @RequestMapping("/game_view/{gamePl_id}")
    public ResponseEntity<?> getGame(@PathVariable long gamePl_id, Authentication authentication) {
        GamePlayer gp1 = gamePlayRepo.findById(gamePl_id).get();
        Game game = gp1.getGame();
        Player player = playerRepository.findByEmail(authentication.getName());
        if (gp1.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "dont cheat"), HttpStatus.UNAUTHORIZED);
        } else {
            makeGameAuxDTO gameAux = new makeGameAuxDTO(gp1);
            if (gameAux.getStates(gp1).equals("WON")){
                scoreRepository.save(new Score(1,new Date(),gp1.getPlayer(),gp1.getGame()));
            }
            if (gameAux.getStates(gp1).equals("TIE")){
                scoreRepository.save(new Score(0.5,new Date(),gp1.getPlayer(),gp1.getGame()));
            }
            if (gameAux.getStates(gp1).equals("LOST")){
                scoreRepository.save(new Score(0,new Date(),gp1.getPlayer(),gp1.getGame()));
            }
            return new ResponseEntity<>(gameAux,HttpStatus.CREATED);

        }
    }

    @RequestMapping(value = "/games/players/{gpid}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable long gpid, Authentication authentication, @RequestBody List<Ship> ship) {
        GamePlayer gamePlayer = gamePlayRepo.findById(gpid).get();
        Player player = playerRepository.findByEmail(authentication.getName());
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "you must log in"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != player.getId()){
            return new ResponseEntity<>(makeMap("error", "you dont belong here"), HttpStatus.UNAUTHORIZED);}

        if (gamePlayer.getShip().size() != 0) {
            return new ResponseEntity<>(makeMap("error", "You already have ships"), HttpStatus.FORBIDDEN);
        }
        if (ship.size() == 5) {
            ship.forEach(ships -> shipRepository.save(new Ship(ships.getType(), ships.getShipLocations(), gamePlayer)));
            return new ResponseEntity<>(makeMap("OK", "Ships placed"), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(makeMap("error", "You cant place more than 5 ships"), HttpStatus.FORBIDDEN);
    }

    @RequestMapping (value = "/games/players/{gamePlayerId}/salvoes",method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> storeSalvoes(@PathVariable long gamePlayerId, Authentication authentication, @RequestBody Salvo salvo){
        GamePlayer gamePlayer = gamePlayRepo.findById(gamePlayerId).get();
        Player player = playerRepository.findByEmail(authentication.getName());
        if (isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error","You must log in"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != player.getId()){
            return new ResponseEntity<>(makeMap("error","You don´t belong here"),HttpStatus.UNAUTHORIZED);
        }
        Optional<GamePlayer> opponent = gamePlayer.getOpponentPlayer();
        if (opponent.isEmpty()){
            return new ResponseEntity<>(makeMap("error","no opponent"),HttpStatus.FORBIDDEN);
        }
        if (gamePlayer.getSalvo().size() > opponent.get().getSalvo().size()){
            return new ResponseEntity<>(makeMap("error","salvoe already submitted for turn"),HttpStatus.FORBIDDEN);
        }
        if (salvo.getSalvoLocations().size()== 0){
            return new ResponseEntity<>(makeMap("error","You have to fire at least one salvo"), HttpStatus.FORBIDDEN);
        }
        if (salvo.getSalvoLocations().size()  > 5 ){
            return new ResponseEntity<>(makeMap("error","You have to submit a max of 5"), HttpStatus.FORBIDDEN);
        }

        salvo.setTurn(gamePlayer.getSalvo().size() + 1);
        salvo.setGamePlayer(gamePlayer);
        salvoRepository.save(new Salvo(salvo.getGamePlayer(), salvo.getTurn(),salvo.getSalvoLocations()));
        System.out.println("turn" + gamePlayer.getSalvo().size() + 1);
        return new ResponseEntity<>(makeMap("OK","Your shots were created"),HttpStatus.CREATED);

    }






    private boolean isGuest (Authentication authentication){
            return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}