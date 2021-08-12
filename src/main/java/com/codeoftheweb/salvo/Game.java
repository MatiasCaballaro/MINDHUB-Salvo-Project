package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayway.jsonpath.JsonPath;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {


    // Genera clave ID con secuencia automática
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    // Genera variable String
    private LocalDateTime creationDate;



    @OneToMany(mappedBy="gameID", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();




    //CONSTRUCTORES
    public Game() { }

    public Game(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }


    // GETTER Y SETTER
    public long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Set<GamePlayer> getGameplayers() {
        return gamePlayers;
    }

    public void setGameplayers(Set<GamePlayer> gameplayers) {
        this.gamePlayers = gameplayers;
    }



    // LISTA PLAYER PARA GAME
    @JsonIgnore
    public List<Player> getPlayers() {
        return gamePlayers.stream().map(sub -> sub.getPlayerID()).collect(toList());
    }


}




