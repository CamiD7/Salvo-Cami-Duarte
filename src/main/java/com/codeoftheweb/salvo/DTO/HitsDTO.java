package com.codeoftheweb.salvo.DTO;

import com.codeoftheweb.salvo.Classes.GamePlayer;
import com.codeoftheweb.salvo.Classes.Salvo;
import com.codeoftheweb.salvo.Classes.Ship;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HitsDTO {
    private List<Map> self;
    private List<Map> opponent;


    /*private int carrier = 0;
    private int battleship = 0;
    private int submarine =0;
    private int destroyer = 0;
    private int patrolboat = 0;*/



    public HitsDTO() {
    }

    public HitsDTO(GamePlayer gamePlayer){
        if (gamePlayer.getOpponentPlayer().isPresent()){
            this.self = getDamages(gamePlayer.getOpponentPlayer().get());
            this.opponent = getDamages(gamePlayer);
        }
        else{
            this.self = new ArrayList<>();
            this.opponent = new ArrayList<>();
        }

    }


   /* public Map<String,Object> getHitsMap(Salvo salvo){
        Map<String,Object> dto = new LinkedHashMap();
        dto.put("turn",salvo.getTurn());
        dto.put("hitLocations", this.getHitsLocations(salvo));
        dto.put("damages",this.getDamages(salvo));
        dto.put("missed",salvo.getLocation().size() - this.getHitsLocations(salvo).size());
        return dto;
    }*/



    public List<Map> getDamages(GamePlayer gp) {

        List<Map> selfie = new ArrayList<>();
        int carrier = 0;
        int battleship = 0;
        int submarine =0;
        int destroyer = 0;
        int patrolboat = 0;
       /* List<Object> self = gp.getSalvo().stream().map(s -> s.getLocation()).collect(Collectors.toList());
        List<Object> opp = gp.getOpponentPlayer().get().getSalvo().stream().map(o -> o.getLocation()).collect(Collectors.toList());*/
        if (!gp.getOpponentPlayer().isPresent()) {
            return null;
        }

        List<String> carrierLocation = getLocationsByType(gp.getOpponentPlayer().get(), "carrier");
        List<String> battleshipLocation = getLocationsByType(gp.getOpponentPlayer().get(), "battleship");
        List<String> submarineLocation = getLocationsByType(gp.getOpponentPlayer().get(), "submarine");
        List<String> destroyerLocation = getLocationsByType(gp.getOpponentPlayer().get(), "destroyer");
        List<String> patrolboatLocation = getLocationsByType(gp.getOpponentPlayer().get(), "patrolboat");

        for (Salvo salvo : gp.getSalvo()) {


            long carrierHits = 0;
            long battleshipHits = 0;
            long submarineHits = 0;
            long destroyerHits = 0;
            long patrolboatHits = 0;
            long missed = salvo.getSalvoLocations().size();
            List<String> hitList = new ArrayList<>();


            for (String salvoShot : salvo.getSalvoLocations()) {

                if (carrierLocation.contains(salvoShot)) {
                    carrierHits++;
                    missed--;
                    hitList.add(salvoShot);
                    carrier++;
                }
                if (battleshipLocation.contains(salvoShot)) {
                    battleshipHits++;
                    missed--;
                    hitList.add(salvoShot);
                    battleship++;
                }
                if (submarineLocation.contains(salvoShot)) {
                    submarineHits++;
                    missed--;
                    hitList.add(salvoShot);
                    submarine++;
                }
                if (destroyerLocation.contains(salvoShot)) {
                    destroyerHits++;
                    missed--;
                    hitList.add(salvoShot);
                    destroyer++;
                }
                if (patrolboatLocation.contains(salvoShot)) {
                    patrolboatHits++;
                    missed--;
                    hitList.add(salvoShot);
                    patrolboat++;
                }
            }
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("carrierHits", carrierHits);
            dto.put("battleshipHits", battleshipHits);
            dto.put("submarineHits", submarineHits);
            dto.put("destroyerHits", destroyerHits);
            dto.put("patrolboatHits", patrolboatHits);
            dto.put("carrier",carrier);
            dto.put("battleship",battleship);
            dto.put("submarine",submarine);
            dto.put("destroyer",destroyer);
            dto.put("patrolboat", patrolboat);


            Map<String, Object> dtos = new LinkedHashMap<>();
            dtos.put("turn", salvo.getTurn());
            dtos.put("hitLocations", hitList);
            dtos.put("damages", dto);
            dtos.put("missed", missed);
            selfie.add(dtos);
        }

        return selfie;

        }




    private List<String> getLocationsByType(GamePlayer gp, String type ){
        Ship locations = gp.getShip().stream().filter(t -> t.getType().equals(type)).findFirst().orElse(null);
        if (locations != null){
            return locations.getShipLocations();
        }
        return new ArrayList<>();
    }

    public List<Map> getSelf() {
        return self;
    }

    public void setSelf(List<Map> self) {
        this.self = self;
    }

    public List<Map> getOpponent() {
        return opponent;
    }

    public void setOpponent(List<Map> opponent) {
        this.opponent = opponent;
    }
}
