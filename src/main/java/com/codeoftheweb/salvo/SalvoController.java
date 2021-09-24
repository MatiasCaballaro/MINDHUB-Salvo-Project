package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.Classes.*;
import com.codeoftheweb.salvo.Interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    PasswordEncoder passwordEncoder;




    private Map <String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new HashMap<>();
        map.put(key,value);
        return map;
    }

    // Creo un List Object, y no paso una variable específica, sino un Map (Array)
    // Lo que hace el stream () es para cada uno de los find all, todo lo que está adentro del map
    // el map, va cambiando la variable "game" a "game+1", generando bucle por cada objeto, y va devolviendo
    // los atributos creados en el DTO
    @RequestMapping("/games")
    public Map <String, Object> game (Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
       if (!isGuest(authentication)) {
           dto.put("player", playerRepository.findByUserName(authentication.getName()).makePlayerDTO());
       }
       else {dto.put("player", "Guest");}

        dto.put("games", gameRepository.findAll()
                .stream()
                .map(game1 -> game1.makeGameDTO())
                .collect(Collectors.toList()));
        return dto;
    }



    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    @RequestMapping("/sessions")
    public Player getAll(Authentication authentication) {
        return playerRepository.findByUserName(authentication.getName());
    }

    /*public List<Object> listaGameId() {
        return gameRepository
                .findAll()
                .stream()
                .map(g -> g.makeGameDTO(g))
                .collect(Collectors.toList());
    }*/

    // game_view toma Id de gameplayer y hace un objeto GameView con todos los datos requeridos
    // la url sería /api/game_view/nn donde nn es la variable

    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String,Object>> findGame(@PathVariable Long nn, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(nn).get();
        Player currentPlayer = playerRepository.findByUserName(authentication.getName());

        /*

        // opcion 1
        if (currentPlayer.getGamePlayers()
                .stream().anyMatch(gamePlayer1 -> gamePlayer1.getId()==nn)) {
            return new ResponseEntity<>(gamePlayer.makeGameViewDTO(gamePlayer), HttpStatus.ACCEPTED);
        }
        else {
            return new ResponseEntity<>
                    (makeMap("error", "Gameplayer no pertenece al usuario actual, no hagas trampa pilluelo"), HttpStatus.FORBIDDEN);
        }

        */

        // opcion 2
        if(gamePlayer.getPlayer() != currentPlayer) {
            return new ResponseEntity<>
                    (makeMap("error", "Do not cheat"), HttpStatus.FORBIDDEN);
        }

        else  {

            if(gamePlayer.gamestate().equals("WON")){
                Score puntaje= new Score(gamePlayer.getGame(),gamePlayer.getPlayer(),1.0);
                scoreRepository.save(puntaje);
            }
            else if (gamePlayer.gamestate().equals("TIE")){
                Score puntaje= new Score(gamePlayer.getGame(),gamePlayer.getPlayer(),0.5);
                scoreRepository.save(puntaje);
            }
            else if (gamePlayer.gamestate().equals("LOST")){
                Score puntaje= new Score(gamePlayer.getGame(),gamePlayer.getPlayer(),0.0);
                scoreRepository.save(puntaje);
            }
            return new ResponseEntity<>(gamePlayer.makeGameViewDTO(gamePlayer), HttpStatus.ACCEPTED);
        }



    }

    // Prueba de salvoes
    @RequestMapping("/salvoes")
    public List<Object> listaSalvos() {
        return salvoRepository
                .findAll()
                .stream()
                .map(s -> s.makeSalvoDTO()).collect(Collectors.toList());
    }






    /*@Autowired
    private GameRepository gameRepository;

    @RequestMapping("/games")
    public List<Game> getAll(Authentication authentication) {
        return gameRepository.findByUserName(authentication.getName());
    }*/


    // AUTHENTICATION

    /*@RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam (value="email") String userName, @RequestParam (value="password")String password) {

        if (userName.isEmpty() || password.isEmpty() ) {
            return new ResponseEntity<>("Te falta completar datos Mossstro", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(userName) !=  null) {
            return new ResponseEntity<>("El nombre de usuario ya existe, usá otro crack ;)", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>("usuario creado",HttpStatus.CREATED);
    }*/

   

    // CREACION DE PLAYERS (CON VERIFICACION Y RESPONSE ENTITIES)
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam (value="email") String userName, @RequestParam (value="password")String password) {

        if (userName.isEmpty() || password.isEmpty() ) {
            return new ResponseEntity<>(makeMap("error", "Incomplete Data"), HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(userName) !=  null) {
            return new ResponseEntity<>(makeMap("error", "User already exist"), HttpStatus.FORBIDDEN);
        }

        Player newPlayer= playerRepository.save(new Player(userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("name", newPlayer.getUserName()),HttpStatus.CREATED);
    }


    // CREACION DE GAMES
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {


        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "You are not logged"), HttpStatus.FORBIDDEN);
        }

        Player currentPlayer = playerRepository.findByUserName(authentication.getName());

        if(currentPlayer==null){
            return new ResponseEntity<>(makeMap("error", "You are not logged"), HttpStatus.FORBIDDEN);
        }

        // CREATE GAME
        Game newGame= gameRepository.save(new Game(LocalDateTime.now()));

        // JOIN PLAYER INTO NEW GAME
        GamePlayer newGamePlayer= gamePlayerRepository
                .save(new GamePlayer(LocalDateTime.now(), currentPlayer, newGame));

        return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
    }


    // JOIN GAME
    @RequestMapping(path = "/game/{gameID}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication,
                                                        @PathVariable Long gameID){

        // Comprobación de autenticación
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "You are not logged"), HttpStatus.FORBIDDEN);
        }

        // Comprobación si existe el game
        if (!gameRepository.existsById(gameID)){
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }
        else {

            Game currentGame = gameRepository.findById(gameID).get();

            Player currentPlayer=playerRepository.findByUserName(authentication.getName());

            // Comprobación si el usuario está ok
            if(currentPlayer==null){
                return new ResponseEntity<>(makeMap("error", "You are not logged"), HttpStatus.FORBIDDEN);
            }

            // Comprobación si el juego está lleno
            if (currentGame.getGamePlayers().size()>1){
                return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
            }

            // Comprobación si un jugador quiere unirse a un juego del que ya forma parte
            if (currentGame.getGameplayers().stream()
                    .anyMatch(gamePlayer -> gamePlayer.getPlayer().getId() == currentPlayer.getId()))
                     {
                return new ResponseEntity<>(makeMap("error", "You are already playing this game"), HttpStatus.FORBIDDEN);}


            GamePlayer newGamePlayer = gamePlayerRepository
                    .save(new GamePlayer(LocalDateTime.now(), currentPlayer, currentGame));

            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.ACCEPTED);

        }
    }
    // Get Ships from Gameplayer
    @RequestMapping(path="/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> viewShips(@PathVariable long gamePlayerId, Authentication authentication) {

        // Comprobación de autenticación
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "you are not logged"), HttpStatus.FORBIDDEN);
        }

        Player currentPlayer = playerRepository.findByUserName(authentication.getName());


        // Comprobación si el usuario está ok
        if (currentPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "You are not logged"), HttpStatus.FORBIDDEN);
        }

        // Comprobación si existe el GamePlayer
        if (!gamePlayerRepository.existsById(gamePlayerId)) {
            return new ResponseEntity<>(makeMap("error", "No such Gameplayer"), HttpStatus.FORBIDDEN);
        }

        GamePlayer currentGamePlayer = gamePlayerRepository.findById(gamePlayerId).get();

        if (currentGamePlayer.getPlayer() != currentPlayer) {
            return new ResponseEntity<>(makeMap("error", "Do not cheat"), HttpStatus.FORBIDDEN);
        }

        // Opcion 1
        List<Map<String, Object>> listShips = currentGamePlayer.getShips().stream()
                .map(ship -> ship.makeShipDTO()).collect(Collectors.toList());

        return new ResponseEntity<>(makeMap("ships", listShips), HttpStatus.CREATED);

        // Opcion 2 (se puede hacer dentro del Response Entity

        /*return new ResponseEntity<>(makeMap(
                "ships", currentGamePlayer.getShips()
                        .stream().map(ship -> ship.makeShipDTO()).collect(Collectors.toList())),HttpStatus.ACCEPTED);
        */
    }


    // Method create Ships (positions) in a Gameplayer
    @RequestMapping(path="/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {

        // Comprobación de autenticación
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "You are not logged"), HttpStatus.UNAUTHORIZED);
        }

        Player currentPlayer = playerRepository.findByUserName(authentication.getName());


        // Comprobación si el usuario está ok
        if (currentPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "You are not correctly logged"), HttpStatus.UNAUTHORIZED);
        }

        // Comprobación si existe el GamePlayer
        if (!gamePlayerRepository.existsById(gamePlayerId)) {
            return new ResponseEntity<>(makeMap("error", "No such GamePlayer"), HttpStatus.FORBIDDEN);
        }

        GamePlayer currentGamePlayer = gamePlayerRepository.findById(gamePlayerId).get();

        // Validación si el player no es el mismo que el gameplayer
        if (currentGamePlayer.getPlayer() != currentPlayer) {
            return new ResponseEntity<>(makeMap("error", "Do not cheat"), HttpStatus.FORBIDDEN);
        }

        // Validación si ya tenía barcos asigandos
        if(currentGamePlayer.getShips().size()!=0){
            return new ResponseEntity<>(makeMap("error", "Do not cheat"), HttpStatus.FORBIDDEN);
        }

        //  opción 1 validación conjunta de cantidad de barcos (tipo) por gameplayer
        if(ships.stream().filter(ship -> ship.getType().equals("carrier")).count()>1 ||
                ships.stream().filter(ship -> ship.getType().equals("battleship")).count()>1 ||
                ships.stream().filter(ship -> ship.getType().equals("submarine")).count()>1 ||
                ships.stream().filter(ship -> ship.getType().equals("destroyer")).count()>1 ||
                ships.stream().filter(ship -> ship.getType().equals("patrolboat")).count()>1) {
            return new ResponseEntity<>(makeMap("error", "You can´t change the size or quantity of ships"), HttpStatus.FORBIDDEN);
        }

        /* opción 2 validación individual de cantidad de barcos (tipo) por gameplayer
        if(ships.stream().filter(ship -> ship.getType().equals("carrier")).count()>1){
            return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de los barcos, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
        }

        if(ships.stream().filter(ship -> ship.getType().equals("battleship")).count()>1){
            return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de los barcos, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
        }

        if(ships.stream().filter(ship -> ship.getType().equals("submarine")).count()>1){
            return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de los barcos, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
        }

        if(ships.stream().filter(ship -> ship.getType().equals("destroyer")).count()>1){
            return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de los barcos, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
        }

        if(ships.stream().filter(ship -> ship.getType().equals("patrolboat")).count()>1){
            return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de los barcos, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
        }*/

        //ResponseEntity shipRelocations = new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de los barcos, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);

        if(ships.size()!=5){
            return new ResponseEntity<>(makeMap("error", "Ships missing"), HttpStatus.FORBIDDEN);

        }

        if (ships.size()==5) {

            // Opcion 1 con stream
            ships.stream().forEach(s ->{
                    s.setGamePlayer(currentGamePlayer);
                    shipRepository.save(s);
            });
            return new ResponseEntity<>(makeMap("OK", "Ships placed!"), HttpStatus.CREATED);}

            // Opcion 2 con For y algunas validaciones más
            /*

            for (Ship newship : ships) {

                if ((newship.getType().equals("carrier") && newship.getShipLocations().size()!=5) ||
                        (newship.getType().equals("battleship") && newship.getShipLocations().size()!=4) ||
                        (newship.getType().equals("submarine")  && newship.getShipLocations().size()!=3) ||
                        (newship.getType().equals("destroyer") && newship.getShipLocations().size()!=3) ||
                        (newship.getType().equals("patrolboat") && newship.getShipLocations().size()!=2)) {

                    return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de los barcos, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);

                }
                */

                /*
                if(newship.getType().equals("carrier") && newship.getShipLocations().size()!=5){
                    return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de carrier, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
                }

                if(newship.getType().equals("battleship") && newship.getShipLocations().size()!=4){
                    return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de battleship o destroyer, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
                }

                if(newship.getType().equals("submarine")  && newship.getShipLocations().size()!=3){
                    return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de submarine, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
                }

                if(newship.getType().equals("destroyer") && newship.getShipLocations().size()!=3){
                    return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de destroyer, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
                }

                if(newship.getType().equals("patrolboat") && newship.getShipLocations().size()!=2){
                    return new ResponseEntity<>(makeMap("error", "No puedes modificar el tamaño de patrolboat, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
                }
                */


                /*shipRepository.save(new Ship (newship.getType(), currentGamePlayer, newship.getShipLocations()));
            }
            return new ResponseEntity<>(makeMap("Ships Creados para Gameplayer:", currentGamePlayer.getId()), HttpStatus.CREATED);
        }*/
        else {
            return new ResponseEntity<>(makeMap("error", "You did not place all the ships"), HttpStatus.FORBIDDEN);
        }

    }

    // get salvoes
    @RequestMapping(path="/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> viewSalvoes(@PathVariable long gamePlayerId, Authentication authentication) {

        // Comprobación de autenticación
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "You are not logged"), HttpStatus.FORBIDDEN);
        }

        Player currentPlayer = playerRepository.findByUserName(authentication.getName());


        // Comprobación si el usuario está ok
        if (currentPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "You are not logged"), HttpStatus.FORBIDDEN);
        }

        // Comprobación si existe el GamePlayer
        if (!gamePlayerRepository.existsById(gamePlayerId)) {
            return new ResponseEntity<>(makeMap("error", "No such Gameplayer"), HttpStatus.FORBIDDEN);
        }

        GamePlayer currentGamePlayer = gamePlayerRepository.findById(gamePlayerId).get();

        if (currentGamePlayer.getPlayer() != currentPlayer) {
            return new ResponseEntity<>(makeMap("error", "Do not cheat"), HttpStatus.FORBIDDEN);
        }

        List<Map<String, Object>> listsalvoes = currentGamePlayer.getSalvos().stream()
                .map(salvo -> salvo.makeSalvoDTO()).collect(Collectors.toList());

        return new ResponseEntity<>(makeMap("salvoes", listsalvoes), HttpStatus.CREATED);

    }



    @RequestMapping(path="/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createSalvoes(@PathVariable long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {

        // Comprobación de autenticación
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "You are not logged"), HttpStatus.UNAUTHORIZED);
        }

        Player currentPlayer = playerRepository.findByUserName(authentication.getName());


        // Comprobación si el usuario está ok
        if (currentPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "You are not logged"), HttpStatus.UNAUTHORIZED);
        }

        // Comprobación si existe el GamePlayer
        if (!gamePlayerRepository.existsById(gamePlayerId)) {
            return new ResponseEntity<>(makeMap("error", "No such Gameplayer"), HttpStatus.FORBIDDEN);
        }

        GamePlayer currentGamePlayer = gamePlayerRepository.findById(gamePlayerId).get();

        // Validación si el player no es el mismo que el gameplayer
        if (currentGamePlayer.getPlayer() != currentPlayer) {
            return new ResponseEntity<>(makeMap("error", "Do not cheat"), HttpStatus.FORBIDDEN);
        }

        // tiene que haber dos jugadores
        if (currentGamePlayer.getGame().getGameplayers().size()!=2){
            return new ResponseEntity<>(makeMap("error","You must wait until the opponent ships are placed to place salvoes"), HttpStatus.FORBIDDEN);
        }

        // Obtener Game Actual
        Game currentGame = currentGamePlayer.getGame();




        // Obtener player 1
        GamePlayer player1 = currentGame.getGamePlayers().stream().min(Comparator.comparing(gamePlayer -> gamePlayer.getId())).get();
            // System.out.println("player1:" + player1.getId());

        // Obtener player 2
        GamePlayer player2 = currentGame.getGamePlayers().stream().max(Comparator.comparing(gamePlayer -> gamePlayer.getId())).get();
            // System.out.println("player2:" +  player2.getId());

        // Que el jugador actual tenga los 5 barcos ubicados
        if(player1.getShips().size()!=5 || player1.getShips().size()==0){
            return new ResponseEntity<>(makeMap("error","Player 1 must place ships"), HttpStatus.FORBIDDEN);
        }

        // Que el oponente tenga los 5 barcos ubicados
        if(player2.getShips().size()!=5 || player2.getShips().size()==0){
            return new ResponseEntity<>(makeMap("error","Player 2 must place ships"), HttpStatus.FORBIDDEN);
        }

        // Más de un salvo y menos o igual 5
        if(!(salvo.getSalvoLocations().size()>0 && salvo.getSalvoLocations().size()<6)) {
            return new ResponseEntity<>(makeMap("error","There must be between 1 and 5 shots per turn"), HttpStatus.FORBIDDEN);
        }



        // Esperar al otro jugador
        int player1Turns = player1.getSalvos().size();
        int player2Turns = player2.getSalvos().size();
            // System.out.println("Turno de player 1 es: " + player1Turns + " y el del player 2 es: " + player2Turns );
            // System.out.println(player1Turns - player2Turns);


        // Validaciones para que cada jugador pueda disparar salvo cuando le corresponda
        // Evita que se disparen salvos consecutivos sin que el otro jugador juegue

        if(player1Turns>player2Turns && currentGamePlayer.getId()==player1.getId()){
            return new ResponseEntity<>(makeMap("error","Wait for Player 2 salvoes please"), HttpStatus.FORBIDDEN);
        }

        if(player1Turns==player2Turns && currentGamePlayer.getId()==player2.getId()){
            return new ResponseEntity<>(makeMap("error","Wait for player 1 salvoes please"), HttpStatus.FORBIDDEN);
        }



        // get turn y sumarle 1 para un nuevo turno de salvo
        int turnoActual= currentGamePlayer.getSalvos().size();

        // Salvo Constructor (GamePlayer gamePlayer, int turn, List<String> locations)
        salvoRepository.save(new Salvo(currentGamePlayer, turnoActual+1, salvo.getSalvoLocations()));
            // System.out.println("Turno de player 1 es: " + player1Turns + " y el del player 2 es: " + player2Turns );
        return new ResponseEntity<>(makeMap("OK", "Fired"), HttpStatus.OK);

    }


}









