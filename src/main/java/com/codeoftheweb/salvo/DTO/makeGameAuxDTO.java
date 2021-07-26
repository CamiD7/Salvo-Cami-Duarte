package com.codeoftheweb.salvo.DTO;

import com.codeoftheweb.salvo.Classes.Game;
import com.codeoftheweb.salvo.Classes.GamePlayer;
import com.codeoftheweb.salvo.Classes.Salvo;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;

import javax.xml.crypto.Data;
import java.util.*;
import java.util.stream.Collectors;

public class makeGameAuxDTO {
    private long id;
    private Date created;
    private String gameState;
    private Set<GamePlayerDTO> gamePlayers;
    private Set<ShipDTO> ships;
    private Set<SalvoDTO> salvoes;
    private HitsDTO hits;

    public makeGameAuxDTO() {
    }

    public makeGameAuxDTO( GamePlayer gamePlayer) {
        this.id = gamePlayer.getGame().getId();
        this.created = gamePlayer.getGame().getCreated();
        this.gameState =  getStates(gamePlayer);
        this.gamePlayers = gamePlayer.getGame().getGamePlayers().stream().map(GamePlayerDTO::new).collect(Collectors.toSet());//game.getGamePlayers().stream().map(GamePlayerDTO::new).collect(Collectors.toSet());
        this.ships = gamePlayer.getShip().stream().map(ShipDTO::new).collect(Collectors.toSet());
        if (gamePlayer.getOpponentPlayer().isPresent()){
            this.salvoes = gamePlayer.getGame().getGamePlayers()
                    .stream()
                    .map(GamePlayer::getSalvo)
                    .flatMap(Collection::stream)
                    .map(SalvoDTO::new)
                    .collect(Collectors.toSet());
        }
        else{
            this.salvoes = new HashSet<>();
        }

        this.hits = new HitsDTO(gamePlayer);
    }

    public String getStates(GamePlayer gamePlayer) {
        if (gamePlayer.getShip().size() < 5) {
            return  "PLACESHIPS";
        }
        if (gamePlayer.getOpponentPlayer().get().getShip().size() < 5 || gamePlayer.getOpponentPlayer().isEmpty()){
            return  "WAITINGFOROPP";
        }

        if (gamePlayer.getSalvo().size() >  gamePlayer.getOpponentPlayer().get().getSalvo().size() ){
            return  "WAIT";
        }
        if (allHits(gamePlayer) == 17 && allHits(gamePlayer.getOpponentPlayer().get()) == 17 && gamePlayer.getSalvo().size() == gamePlayer.getOpponentPlayer().get().getSalvo().size()){
            return "TIE";
        }
        if (allHits(gamePlayer) == 17 && allHits(gamePlayer.getOpponentPlayer().get()) < 17 && gamePlayer.getSalvo().size() == gamePlayer.getOpponentPlayer().get().getSalvo().size()){ //){
            return "WON";
        }
        if (allHits(gamePlayer) < 17 && allHits(gamePlayer.getOpponentPlayer().get()) == 17 && gamePlayer.getSalvo().size() == gamePlayer.getOpponentPlayer().get().getSalvo().size()){
            return "LOST";
        }
        if (gamePlayer.getSalvo().size() < gamePlayer.getOpponentPlayer().get().getSalvo().size() ||gamePlayer.getSalvo().size() == gamePlayer.getOpponentPlayer().get().getSalvo().size()  ){
            return "PLAY";
        }
        return "UNDEFINED";
    }

    public int allHits(GamePlayer gamePlayer){
        return gamePlayer.getSalvo().stream().flatMap(sl -> getHitsLocations(sl).stream()).collect(Collectors.toList()).size();
    }

    public List<String> getHitsLocations(Salvo salvo){
        GamePlayer opponent = salvo.getGamePlayer().getOpponentPlayer().get();
        List<String> loc = opponent.getShip().stream().flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList());
        List<String> hits = salvo.getSalvoLocations();
        return hits.stream().filter(loc::contains).collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public Set<GamePlayerDTO> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayerDTO> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<ShipDTO> getShips() {
        return ships;
    }

    public void setShips(Set<ShipDTO> ships) {
        this.ships = ships;
    }

    public Set<SalvoDTO> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<SalvoDTO> salvoes) {
        this.salvoes = salvoes;
    }

    public HitsDTO getHits() {
        return hits;
    }

    public void setHits(HitsDTO hits) {
        this.hits = hits;
    }
}
