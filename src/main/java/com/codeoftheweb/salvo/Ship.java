package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerID")
    private GamePlayer gameplayerID;

    @OneToMany(mappedBy="shipID", fetch=FetchType.EAGER)
    Set<ShipLocations> shipLocations = new HashSet<>();

    // CONSTRUCTORES


    public Ship() {
    }

    public Ship(long id, GamePlayer gameplayerID) {
        this.id = id;
        this.gameplayerID = gameplayerID;
    }



    // SETTERS Y GETTERS


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGameplayerID() {
        return gameplayerID;
    }

    public void setGameplayerID(GamePlayer gameplayerID) {
        this.gameplayerID = gameplayerID;
    }

    public Set<ShipLocations> getShipLocations() {
        return shipLocations;
    }

    public void setShipLocations(Set<ShipLocations> shipLocations) {
        this.shipLocations = shipLocations;
    }


}
