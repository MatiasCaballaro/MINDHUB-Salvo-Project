package com.codeoftheweb.salvo.Classes;


        import org.hibernate.annotations.GenericGenerator;

        import javax.persistence.*;
        import java.util.*;

        import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    // Genera clave ID con secuencia automática
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    // Genera variable String
    private String userName;

    // One to many with GamePlayer
    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers= new HashSet<>();

    // One to many with Score
    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<Score> scores= new HashSet<>();


    //CONSTRUCTORES - DEBE indicar constructor vacío y otro con los argumentos
    public Player() { }

    public Player(String userName) {
        this.userName = userName;
    }




    // GETTER Y SETTER
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void addGameplayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        GamePlayer.add(gamePlayer);
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


    // LISTA GAME PARA PLAYER
    /*public List<Game> getGames() {
        return gamePlayers.stream().map(sub -> sub.getGame()).collect(toList());
    }*/



    // GetScore para evitar DTO Game #001
    /*public Score getScore (Game game) {
    return scores.stream().filter(sc->sc.getGame().getId() == game.getId()).findFirst().orElse(null);
    }*/




    // DTO PLAYER
    // El DTO player toma valores "Id" y "username" (e-mail) para cada gameplayer
    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("email", this.getUserName());
        return dto;
    }


}



