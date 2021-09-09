package com.codeoftheweb.salvo.Classes;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerID")
    private GamePlayer gamePlayer;


    @ElementCollection
    @Column(name="location")
    private List<String> shipLocations = new ArrayList<>();



    // CONSTRUCTORES


    public Ship() {
    }

    public Ship(String type, GamePlayer gamePlayerID, List<String> shipLocations) {
        this.type = type;
        this.gamePlayer = gamePlayerID;
        this.shipLocations = shipLocations;
    }


    // SETTERS Y GETTERS


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }


    // DTO SHIPS

    // ShipDTO
    public Map<String, Object> makeShipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", this.getType());
        dto.put("locations", this.getShipLocations());
        return dto;
    }

}
