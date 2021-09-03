package com.codeoftheweb.salvo.Classes;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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


    // Método getScore utilizado en makeGamePlayerDTO para no utilizar el DTO de scores en Game #001
    /*public Score getScore (){
        return player.getScore(this.game);
    }*/

    // DTO GAMEPLAYER
    // Se crea un DTO para Gameplayer, donde se indica el ID de la fila en esa tabla
    // También se agrega una key "player", con el método para hacer un nuevo array Player mediante makePlayerDTO

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO());


        // Prueba GetScore sin DTO en GameDTO
        /*if(this.getScore() != null){
            //dto.put("score", this.getScore().getScore());
            dto.put("score", this.getScore().getScore());
        }*/



        return dto;
    }

    // Game_view DTO game
    // Utiliza Ships DTO
    public Map<String, Object> makeGameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getGame().getId());
        dto.put("created", this.getGame().getCreationDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers",
                this.getGame().getGameplayers()
                        .stream()
                        .map(gp -> gp.makeGamePlayerDTO()).collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips().stream()
                .map(sh -> sh.makeShipDTO()).collect(Collectors.toList()));

        // Se utiliza Flat Map, que a diferencia del map, resuelve (aplana) los arrays en un solo nivel
        dto.put("salvoes", gamePlayer.getGame().getGameplayers().stream()
                .flatMap(gamePlayerSalvos -> gamePlayerSalvos.getSalvos().stream()
                        .map(s -> s.makeSalvoDTO(s))).collect(Collectors.toList()));

        // Usando For
            /*
            List<Map<String, Object>> listaux = new  ArrayList<>();

                    // "Gameplayer gp" es el nombre que va a tener cada recorrido >
                    // dentro de lo que va a la derecha de los ":"
                    // ,al igual que "Salvo s"

            for (GamePlayer gp: gamePlayer.getGame().gamePlayers) {
                for (Salvo s:gp.getSalvos()){
                    listaux.add(s.makeSalvoDTO(s));
                }
            }
            dto.put("salvoes2", listaux);*/


                // Usando forEach - PROBAR intento 1
                            /*List<List<Integer>> listabidimensional = new ArrayList<List<Integer>>(Arrays.asList(
                                    new ArrayList<Integer>(Arrays.asList(1,2)),
                                    new ArrayList<Integer>(Arrays.asList(3,4))
                            ));
                            System.out.println(listabidimensional);
                            List<Integer> listaAux = new ArrayList<>();
                            for (List<Integer> l1:listabidimensional){
                                for(Integer l2:l1){
                                    listaAux.add(l2);
                                }
                            }
                            dto.put("salvoes2", listaAux);*/
        Map<String, Object> hits = new LinkedHashMap<String, Object>();
        hits.put("self", new ArrayList<>());
        hits.put("opponent", new ArrayList<>());
        dto.put("hits", hits);

    return dto;
    }
}