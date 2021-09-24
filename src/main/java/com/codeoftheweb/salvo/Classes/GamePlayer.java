package com.codeoftheweb.salvo.Classes;

import com.codeoftheweb.salvo.Interfaces.SalvoRepository;
import com.codeoftheweb.salvo.Interfaces.ScoreRepository;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    @OrderBy
    Set<Salvo> salvos;


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


    // OBTENER OPONENTE

    public GamePlayer getOpponent(){

        Game currentGame = this.getGame();

        GamePlayer opponent = currentGame.getGamePlayers().stream()
                .filter(gp -> gp.getId() != this.getId()).findFirst().orElse(null);
        return opponent;
    }

/*

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
        //System.out.println(carrierLocations);
        List<String> battleshipLocations = shipLocations("battleship");
        //System.out.println(battleshipLocations);
        List<String> submarineLocations = shipLocations("submarine");
        //System.out.println(submarineLocations);
        List<String> destroyerLocations = shipLocations("destroyer");
        //System.out.println(destroyerLocations);
        List<String> patrolboatLocations = shipLocations("patrolboat");
        //System.out.println(patrolboatLocations);

        // Forma 2 de obtener barcos
        */
/*Ship carrier1 = this.getShips().stream().filter(sh-> sh.getType().equals("carrier")).findFirst().get();
        List<String> carrierlocation1 = carrier1.getShipLocations();
        System.out.println(carrierlocation1);*//*


        // Forma 3 de obtener barcos
        */
/*List <String> battleship1 = this.getShips().stream().filter(sh-> sh.getType().equals("battleship")).findFirst().get().getShipLocations();
        System.out.println(battleship1);*//*



        // Contadores de daño acumulado
        int carrier=0;
        int battleship=0;
        int submarine=0;
        int destroyer=0;
        int patrolboat=0;

        // 1er For para pasar por los turnos
        // Se obtienen tambien los tiros del gameplayer oponente

        // Opcion por si La relación Salvo da duplicado en List, se deja en Set


        //List <Salvo> listaSalvoOrdenados = new ArrayList<>(this.getOpponent().getSalvos());

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
*/

            public List<Map<String, Object>>makeListHits(){
                List<Map<String, Object>> listHits= new ArrayList<>();
                Ship carrier = this.getShips().stream().filter(s -> s.getType().equals("carrier")).findFirst().get();
                List<String> carrierLocations = carrier.getShipLocations();
                Ship battleship = this.getShips().stream().filter(s -> s.getType().equals("battleship")).findFirst().get();
                List<String> battleshipLocations = battleship.getShipLocations();
                Ship submarine = this.getShips().stream().filter(s -> s.getType().equals("submarine")).findFirst().get();
                List<String> submarineLocations = submarine.getShipLocations();
                Ship destroyer = this.getShips().stream().filter(s -> s.getType().equals("destroyer")).findFirst().get();
                List<String> destroyerLocations = destroyer.getShipLocations();
                Ship patrolboat = this.getShips().stream().filter(s -> s.getType().equals("patrolboat")).findFirst().get();
                List<String> patrolboatLocations = patrolboat.getShipLocations();

                int carrierTotal = 0;
                int battleshipTotal = 0;
                int submarineTotal = 0;
                int destroyerTotal = 0;
                int patrolboatTotal = 0;

                for (Salvo salvoes : this.getOpponent().getSalvos()) {
                    Map<String, Object> hitsTurn= new LinkedHashMap<>();

                    int carrierTurn = 0;
                    int battleshipTurn = 0;
                    int submarineTurn = 0;
                    int destroyerTurn = 0;
                    int patrolboatTurn = 0;
                    int missed = salvoes.getSalvoLocations().size();

                    List<String> hitLocations= new ArrayList<>();
                    for (String salvoLocation : salvoes.getSalvoLocations()) {
                        if (carrierLocations.contains(salvoLocation)) {
                            hitLocations.add(salvoLocation);
                            carrierTotal++;
                            carrierTurn++;
                            missed--;
                        }
                        if (battleshipLocations.contains(salvoLocation)) {
                            hitLocations.add(salvoLocation);
                            battleshipTotal++;
                            battleshipTurn++;
                            missed--;
                        }
                        if (submarineLocations.contains(salvoLocation)) {
                            hitLocations.add(salvoLocation);
                            submarineTotal++;
                            submarineTurn++;
                            missed--;
                        }
                        if (destroyerLocations.contains(salvoLocation)) {
                            hitLocations.add(salvoLocation);
                            destroyerTotal++;
                            destroyerTurn++;
                            missed--;
                        }
                        if (patrolboatLocations.contains(salvoLocation)) {
                            hitLocations.add(salvoLocation);
                            patrolboatTotal++;
                            patrolboatTurn++;
                            missed--;
                        }
                    }

                    Map<String, Object> listDamages= new LinkedHashMap<>();
                    listDamages.put("carrierHits", carrierTurn);
                    listDamages.put("battleshipHits", battleshipTurn);
                    listDamages.put("submarineHits", submarineTurn);
                    listDamages.put("destroyerHits", destroyerTurn);
                    listDamages.put("patrolboatHits", patrolboatTurn);
                    listDamages.put("carrier", carrierTotal);
                    listDamages.put("battleship", battleshipTotal);
                    listDamages.put("submarine", submarineTotal);
                    listDamages.put("destroyer", destroyerTotal);
                    listDamages.put("patrolboat", patrolboatTotal);

                    hitsTurn.put("turn", salvoes.getTurn());
                    hitsTurn.put("hitLocations", hitLocations);
                    hitsTurn.put("damages", listDamages);
                    hitsTurn.put("missed", missed);

                    listHits.add(hitsTurn);
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

    /*

    private String Scoring (GamePlayer gamePlayer, Player player, LocalDateTime localDateTime ){
        localDateTime=LocalDateTime.now();


    }
    */

    public String gamestate () {

        String gamestate = "UNDEFINED";

        GamePlayer currentPlayer=this;
        GamePlayer opponent = this.getOpponent();

        // Obtener player 1
        GamePlayer player1 = this.getGame().getGamePlayers().stream().min(Comparator.comparing(gamePlayer -> gamePlayer.getId())).get();
        //System.out.println("player1:" + player1.getId());

        // Obtener player 2
        GamePlayer player2 = this.getGame().getGamePlayers().stream().max(Comparator.comparing(gamePlayer -> gamePlayer.getId())).get();
        //System.out.println("player2:" + player2.getId());

        int player1Turns = player1.getSalvos().size();
        int player2Turns = player2.getSalvos().size();



        // PLACESHIPS STATE
            if (this.getShips().size() != 5) {
                gamestate = "PLACESHIPS";
                return gamestate;
            }

        // WAITING FOR OPPONENT STATE
            if (this.getOpponent() == null) {
                gamestate = "WAITINGFOROPP";
                return gamestate;
            }

        // WAIT STATE
            if (this.getOpponent().getShips().size() != 5) {
                gamestate =  "WAIT";
                return gamestate;
            }


        // TIE, WIN Y LOST STATE

        if (this.getId()==player1.getId()){
            if (this.barcosHundidos(opponent, currentPlayer)){
                if (player1Turns>player2Turns){
                    gamestate =  "WAIT";
                    return gamestate;
                }
                else if (this.barcosHundidos(currentPlayer,opponent)){
                    gamestate =  "TIE";
                    return gamestate;

                }
                else {
                    gamestate =  "WON";
                    return gamestate;
                }
            }
            if (this.barcosHundidos(currentPlayer,opponent)){
                gamestate =  "LOST";
                return gamestate;
            }
        }
        else {
            if(this.barcosHundidos(opponent,currentPlayer)){
                if (this.barcosHundidos(currentPlayer,opponent)){
                    gamestate = "TIE";
                    return gamestate;
                }
                else {
                    gamestate = "WON";
                    return gamestate;
                }
            }
            if(this.barcosHundidos(currentPlayer,opponent)){
                if (player1Turns==player2Turns){
                    gamestate = "LOST";
                    return gamestate;
                }
            }
        }



        //WAIT STATE

            if (player1Turns > player2Turns && this.getId() == player1.getId()) {
                gamestate =  "WAIT";
                return gamestate;
            }

            if (player1Turns == player2Turns && this.getId() == player2.getId()) {
                gamestate =  "WAIT";
                return gamestate;
            }



        // PLAY STATE
            if (this.getOpponent().getShips().size() == 5) {
                gamestate =  "PLAY";
                return gamestate;
            }


        return gamestate;
    }

    private boolean barcosHundidos(GamePlayer gpBarcos, GamePlayer gpSalvos) {

        GamePlayer opponent = this.getOpponent();

        if (!gpBarcos.getShips().isEmpty() && !gpSalvos.getSalvos().isEmpty()) {
        return  gpSalvos.getSalvos()
                    .stream().flatMap(salvo -> salvo.getSalvoLocations().stream()).collect(Collectors.toList())
                    .containsAll(gpBarcos.getShips()
                            .stream().flatMap(ship -> ship.getShipLocations().stream())
                            .collect(Collectors.toList()));
        }
        return false;
    }



    // Game_view DTO game
    // Utiliza Ships DTO
    public Map<String, Object> makeGameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getGame().getId());
        dto.put("created", this.getGame().getCreationDate());
        dto.put("gameState", gamestate());
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



        GamePlayer opponent = this.getOpponent();

        Map<String, Object> hits = new LinkedHashMap<String, Object>();

        // Evita el error que te tira al entrar a un game sin un oponente
        if(opponent == null) {
            hits.put("self", new ArrayList<>());
            hits.put("opponent",  new ArrayList<>());
        }

        else {
            hits.put("self", this.makeListHits());
            hits.put("opponent",  opponent.makeListHits());
        }

        dto.put("hits", hits);

    return dto;
    }

}