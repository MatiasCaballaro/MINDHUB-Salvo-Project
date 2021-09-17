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
    List<Salvo> salvos = new ArrayList<>();

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

    public List<Salvo> getSalvos() {
        return salvos;
    }

    public void setSalvos(List<Salvo> salvos) {
        this.salvos = salvos;
    }





    // OBTENER OPONENTE

    public GamePlayer getOpponent(){

        Game currentGame = this.getGame();

        GamePlayer opponent = currentGame.getGamePlayers().stream()
                .filter(gp -> gp.getId() != this.getId()).findFirst().orElse(null);
        return opponent;
    }

    // OBTENER SHIP LOCATIONS POR NOMBRE
    private List<String> shipLocations(String type) {

        Optional<Ship> shipLocations =  ships.stream()
                .filter(s -> s.getType() == type ).findFirst();

        if(!shipLocations.isPresent()) {
            return new ArrayList<>();
        } else {
            return shipLocations.get().getShipLocations();
        }
    }




    // OBTENER HITS

    private List<Map<String, Object>> listHits(){

        // LISTA PRINCIPAL, ES UNA LISTA DE MAP<STRING,OBJECT>
        List<Map<String, Object>> listHits = new ArrayList<>();

        // Se obtienen las locations de cada barco del Gameplayer
        List<String> carrierLocations = shipLocations("carrier");
        System.out.println(carrierLocations);
        List<String> battleshipLocations = shipLocations("battleship");
        System.out.println(battleshipLocations);
        List<String> submarineLocations = shipLocations("submarine");
        System.out.println(submarineLocations);
        List<String> destroyerLocations = shipLocations("destroyer");
        System.out.println(destroyerLocations);
        List<String> patrolboatLocations = shipLocations("patrolboat");
        System.out.println(patrolboatLocations);

        // Forma 2 de obtener barcos
        /*Ship carrier1 = this.getShips().stream().filter(sh-> sh.getType().equals("carrier")).findFirst().get();
        List<String> carrierlocation1 = carrier1.getShipLocations();
        System.out.println(carrierlocation1);*/

        // Forma 3 de obtener barcos
        /*List <String> battleship1 = this.getShips().stream().filter(sh-> sh.getType().equals("battleship")).findFirst().get().getShipLocations();
        System.out.println(battleship1);*/


        // Contadores de daño acumulado
        int carrier=0;
        int battleship=0;
        int submarine=0;
        int destroyer=0;
        int patrolboat=0;

        // 1er For para pasar por los turnos
        // Se obtienen tambien los tiros del gameplayer oponente
        for(Salvo salvo : this.getOpponent().getSalvos()) {

            // Para agregar luego a la lista "List<Map<String, Object>> listHits"
            Map<String, Object> hitsTurno = new LinkedHashMap<>();

            // Muestra location si pegó a una embarcación enemiga
            List<String> hitLocations = new ArrayList<>();


            //  Contadores daño por turno
            int carrierHits = 0;
            int battleshipHits = 0;
            int submarineHits = 0;
            int destroyerHits = 0;
            int patrolboatHits = 0;
            int missedShots = salvo.getSalvoLocations().size();

            // Agrupa los datos necesarios por turno
            Map<String, Object> damageTurno = new LinkedHashMap<>();

            // Compara cada location de salvo contra las ubicaciones del enemigo
            // Si coincide algún disparo:
            // Muestra locations de donde impacto nuestro tiro
            // Suma 1 al daño general por embarcación
            // Suma 1 al daño realizado en el turno por embarcación
            // Resta -1 a los tiros fallados (que no pegó contra nada)

            for(String location : salvo.getSalvoLocations()) {

                if(carrierLocations.contains(location)) {
                    hitLocations.add(location);
                    carrier++;
                    carrierHits++;
                    missedShots--;
                }

                if(battleshipLocations.contains(location)) {
                    hitLocations.add(location);
                    battleship++;
                    battleshipHits++;
                    missedShots--;
                }

                if(submarineLocations.contains(location)) {
                    hitLocations.add(location);
                    submarine++;
                    submarineHits++;
                    missedShots--;
                }

                if(destroyerLocations.contains(location)) {
                    hitLocations.add(location);
                    destroyer++;
                    destroyerHits++;
                    missedShots--;
                }

                if(patrolboatLocations.contains(location)) {
                    hitLocations.add(location);
                    patrolboat++;
                    patrolboatHits++;
                    missedShots--;
                }

            }

            // Agrupación de daños por turno y acumulados
            damageTurno.put("carrierHits", carrierHits);
            damageTurno.put("battleshipHits", battleshipHits);
            damageTurno.put("submarineHits", submarineHits);
            damageTurno.put("destroyerHits", destroyerHits);
            damageTurno.put("patrolboatHits", patrolboatHits);
            damageTurno.put("carrier", carrier);
            damageTurno.put("battleship", battleship);
            damageTurno.put("submarine", submarine);
            damageTurno.put("destroyer", destroyer);
            damageTurno.put("patrolboat", patrolboat);

            // Agrupacion primer nivel para JSON del front
            hitsTurno.put("turn", salvo.getTurn());
            hitsTurno.put("hitLocations",hitLocations);
            hitsTurno.put("damages",damageTurno);
            hitsTurno.put("missed", missedShots);


            listHits.add(hitsTurno);
        }
        return listHits;
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
                        .map(s -> s.makeSalvoDTO())).collect(Collectors.toList()));

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

        GamePlayer opponent = this.getOpponent();

        // Evita el error que te tira al entrar a un game sin un oponente
        if(opponent == null) {
            hits.put("self", new ArrayList<>());
            hits.put("opponent",  new ArrayList<>());
        }

        else {
            hits.put("self", this.listHits());
            hits.put("opponent",  opponent.listHits());
        }

        dto.put("hits", hits);

    return dto;
    }

}