
package com.codeoftheweb.salvo.DTO;

import com.codeoftheweb.salvo.Classes.GamePlayer;
import com.codeoftheweb.salvo.Classes.Salvo;
import com.codeoftheweb.salvo.Classes.Ship;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import java.util.List;
import java.util.Set;

public class SalvoDTO {
    private int turn;
    private long player;
    private List<String> locations;

    public SalvoDTO() {
    }

    public SalvoDTO(Salvo salvo) {
        this.turn = salvo.getTurn();
        this.player = salvo.getGamePlayer().getPlayer().getId();
        this.locations = salvo.getSalvoLocations();
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public long getPlayer() {
        return player;
    }

    public void setPlayer(long player) {
        this.player = player;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }


}

