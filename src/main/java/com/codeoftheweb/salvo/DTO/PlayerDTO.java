
package com.codeoftheweb.salvo.DTO;


import com.codeoftheweb.salvo.Classes.GamePlayer;
import com.codeoftheweb.salvo.Classes.Player;
import com.codeoftheweb.salvo.Classes.Score;

import java.util.HashSet;
import java.util.Set;

public class PlayerDTO {
    private long id;
    private String email;


    public PlayerDTO() {
    }

    public PlayerDTO(Player player ) {
        this.id = player.getId();
        this.email = player.getEmail();

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



}


