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
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;


    // SHIPS

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();

    // SALVO

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Salvo> salvos = new HashSet<>();

    //CONSTRUCTORES
    public GamePlayer() { }

    public GamePlayer(LocalDateTime joinDate, Player playerID, Game gameID) {
        this.joinDate = joinDate;
        this.player = playerID;
        this.game = gameID;
    }


    public static void add(GamePlayer gamePlayer) {
    }



    /* EJEMPLO - Constructor se agrega automáticamente el Date
    public GamePlayer(Player playerID, Game gameID) {
        this.joinDate = new Date();
        this.playerID = playerID;
        this.gameID = gameID;
    }
    */




    // GETTERS Y SETTERS


    public long getId() {
        return id;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }


    // GETTERS Y SETTERS SHIPS
    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    // GETTERS Y SETTERS SALVO


    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public void setSalvos(Set<Salvo> salvos) {
        this.salvos = salvos;
    }
}