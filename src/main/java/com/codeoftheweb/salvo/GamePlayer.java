package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class GamePlayer {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player playerID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game gameID;


    // SHIPS

    @OneToMany(mappedBy="gamePlayerID", fetch=FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();


    //CONSTRUCTORES
    public GamePlayer() { }

    public GamePlayer(LocalDateTime joinDate, Player playerID, Game gameID) {
        this.joinDate = joinDate;
        this.playerID = playerID;
        this.gameID = gameID;
    }

    /* se agrega automáticamente el Date
    public GamePlayer(Player playerID, Game gameID) {
        this.joinDate = new Date();
        this.playerID = playerID;
        this.gameID = gameID;
    }
    */



    public static void add(GamePlayer gamePlayer) {
    }


    // Getter y Setter

    public long getId() {
        return id;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Player getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Player playerID) {
        this.playerID = playerID;
    }

    public Game getGameID() {
        return gameID;
    }

    public void setGameID(Game gameID) {
        this.gameID = gameID;
    }



    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }



}