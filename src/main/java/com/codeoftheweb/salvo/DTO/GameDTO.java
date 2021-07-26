package com.codeoftheweb.salvo.DTO;

import com.codeoftheweb.salvo.Classes.Game;
import com.codeoftheweb.salvo.Classes.GamePlayer;
import com.codeoftheweb.salvo.Classes.Score;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class GameDTO {
    private long id;
    private Date created;
    Set<GamePlayerDTO> gamePlayers;
    Set<ScoreDTO> scores;

    public GameDTO() {
    }

    public GameDTO(Game game) {
        this.id = game.getId();
        this.created = game.getCreated();
        this.gamePlayers = game.getGamePlayers().stream().map(gp -> new GamePlayerDTO(gp)).collect(Collectors.toSet());
        this.scores = game.getGamePlayers().stream().map(s -> new ScoreDTO(s)).collect(Collectors.toSet());
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

    public Set<GamePlayerDTO> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayerDTO> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<ScoreDTO> getScores() {
        return scores;
    }

    public void setScores(Set<ScoreDTO> scores) {
        this.scores = scores;
    }
}
