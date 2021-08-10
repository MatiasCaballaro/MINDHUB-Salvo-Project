package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class ShipLocations {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ship_ID")
    private Ship ship_ID;




    // CONSTRUCTORES


    public ShipLocations() {
    }

    public ShipLocations(long id, Ship ship_ID) {
        this.id = id;
        this.ship_ID = ship_ID;
    }


    // SETTERS Y GETTERS


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Ship getShip_ID() {
        return ship_ID;
    }

    public void setShip_ID(Ship ship_ID) {
        this.ship_ID = ship_ID;
    }


}
