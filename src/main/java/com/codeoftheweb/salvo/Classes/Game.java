package com.codeoftheweb.salvo.Classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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


    //One to many with GamePlayer
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    //One to many with Scores
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<Score> scores = new HashSet<>();


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

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }



    // LISTA PLAYER PARA GAME
    @JsonIgnore
    public List<Player> getPlayers() {
        return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
    }


    //DTO GAME
    // El DTO va escribiendo sobre cada variable, un nombre y un valor (id="" y created ="")
    // en el makeGameDTO, se pasa como variable un objeto de tipo game
    // Además, se crea un Array anidado para gameplayer, usando el método game.getGamePlayers
    // Desde ese método toma los datos y los va trabajando como indica el makeGamePlayerDTO


    public Map<String, Object> makeGameDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gamePlayers",
                this.getGameplayers()
                        .stream()
                        .map(gp -> gp.makeGamePlayerDTO()).collect(Collectors.toList()));
        dto.put("scores", this.getScores().stream()
                   .map(score -> score.makeScoreDTO()).collect(Collectors.toList()));


        /*
            //otra forma sin usar get scores (), la diferencia es que no sale nada si no termina (null) #001
                dto.put("scores", this.getGamePlayers().stream().map(sc->
                            {
                                if (sc.getScore()!=null) {
                                    return sc.getScore().makeScoreDTO();
                                }
                                else
                                {return null;}
                            }
                        )
                );
        */

            return dto;

        }

    public void addScore(Score score) {
        scores.add(score);
    }


}




