package com.codeoftheweb.salvo.DTO;

import com.codeoftheweb.salvo.Classes.GamePlayer;
import com.codeoftheweb.salvo.Classes.Score;

import java.util.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ScoreDTO {
    private long player;
    private Object score;
    private Date finishDate;

    public ScoreDTO() {
    }
    public ScoreDTO(GamePlayer gamePlayer){
        if (gamePlayer.getScore().isPresent()) {
            this.player = gamePlayer.getPlayer().getId();
            this.score = gamePlayer.getScore().get().getScore();
            this.finishDate = gamePlayer.getScore().get().getFinished();
        } else{
            this.score = "el juego no tiene puntaje";
        }
    }



    public long getPlayer() {
        return player;
    }

    public void setPlayer(long player) {
        this.player = player;
    }

    public Object getScore() {
        return score;
    }

    public void setScore(Object score) {
        this.score = score;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }


}
