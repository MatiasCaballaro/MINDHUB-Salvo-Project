package com.codeoftheweb.salvo;


        import org.hibernate.annotations.GenericGenerator;

        import javax.persistence.*;
        import java.util.HashSet;
        import java.util.List;
        import java.util.Set;

        import static java.util.stream.Collectors.toList;

@Entity
public class Player {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;

    @OneToMany(mappedBy="playerID", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers= new HashSet<>();


    //CONSTRUCTORES
    public Player() { }

    public Player(String userName) {
        this.userName = userName;
    }




    // GETTER Y SETTER
    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void addGameplayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayerID(this);
        GamePlayer.add(gamePlayer);
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }




    // LISTA GAME PARA PLAYER
    public List<Game> getGames() {
        return gamePlayers.stream().map(sub -> sub.getGameID()).collect(toList());
    }



}



