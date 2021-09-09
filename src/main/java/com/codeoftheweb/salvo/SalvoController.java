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
        GamePlayer gamePlayer = gamePlayerRepository.getById(nn);
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
                    (makeMap("error", "Gameplayer no pertenece al usuario actual, no hagas trampa pilluelo"), HttpStatus.FORBIDDEN);
        }

        else  {
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
            return new ResponseEntity<>(makeMap("error", "Falta completar Datos"), HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(userName) !=  null) {
            return new ResponseEntity<>(makeMap("error", "Usuario Existente"), HttpStatus.FORBIDDEN);
        }

        Player newPlayer= playerRepository.save(new Player(userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>(makeMap("name", newPlayer.getUserName()),HttpStatus.CREATED);
    }


    // CREACION DE GAMES
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {


        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "No estás logueado"), HttpStatus.FORBIDDEN);
        }

        Player currentPlayer = playerRepository.findByUserName(authentication.getName());

        if(currentPlayer==null){
            return new ResponseEntity<>(makeMap("error", "No estás logueado"), HttpStatus.FORBIDDEN);
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
            return new ResponseEntity<>(makeMap("error", "No estás logueado"), HttpStatus.FORBIDDEN);
        }

        // Comprobación si existe el game
        if (!gameRepository.existsById(gameID)){
            return new ResponseEntity<>(makeMap("error", "No existe Game"), HttpStatus.FORBIDDEN);
        }
        else {

            Game currentGame = gameRepository.getById(gameID);

            Player currentPlayer=playerRepository.findByUserName(authentication.getName());

            // Comprobación si el usuario está ok
            if(currentPlayer==null){
                return new ResponseEntity<>(makeMap("error", "No estás logueado"), HttpStatus.FORBIDDEN);
            }

            // Comprobación si el juego está lleno
            if (currentGame.getGamePlayers().size()>1){
                return new ResponseEntity<>(makeMap("error", "el juego está lleno"), HttpStatus.FORBIDDEN);
            }

            // Comprobación si un jugador quiere unirse a un juego del que ya forma parte
            if (currentGame.getGameplayers().stream()
                    .anyMatch(gamePlayer -> gamePlayer.getPlayer().getId() == currentPlayer.getId()))
                     {
                return new ResponseEntity<>(makeMap("error", "el jugador ya participa del juego"), HttpStatus.FORBIDDEN);}


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
            return new ResponseEntity<>(makeMap("error", "No estás logueado"), HttpStatus.FORBIDDEN);
        }

        Player currentPlayer = playerRepository.findByUserName(authentication.getName());


        // Comprobación si el usuario está ok
        if (currentPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "No estás logueado"), HttpStatus.FORBIDDEN);
        }

        // Comprobación si existe el GamePlayer
        if (!gamePlayerRepository.existsById(gamePlayerId)) {
            return new ResponseEntity<>(makeMap("error", "No existe el GamePlayer"), HttpStatus.FORBIDDEN);
        }

        GamePlayer currentGamePlayer = gamePlayerRepository.getById(gamePlayerId);

        if (currentGamePlayer.getPlayer() != currentPlayer) {
            return new ResponseEntity<>(makeMap("error", "No tienes permiso, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
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
            return new ResponseEntity<>(makeMap("error", "No estás logueado"), HttpStatus.UNAUTHORIZED);
        }

        Player currentPlayer = playerRepository.findByUserName(authentication.getName());


        // Comprobación si el usuario está ok
        if (currentPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "No estás logueado correctamente"), HttpStatus.UNAUTHORIZED);
        }

        // Comprobación si existe el GamePlayer
        if (!gamePlayerRepository.existsById(gamePlayerId)) {
            return new ResponseEntity<>(makeMap("error", "No existe el GamePlayer"), HttpStatus.FORBIDDEN);
        }

        GamePlayer currentGamePlayer = gamePlayerRepository.getById(gamePlayerId);

        // Validación si el player no es el mismo que el gameplayer
        if (currentGamePlayer.getPlayer() != currentPlayer) {
            return new ResponseEntity<>(makeMap("error", "No tienes permiso, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
        }

        // Validación si ya tenía barcos asigandos
        if(currentGamePlayer.getShips().size()!=0){
            return new ResponseEntity<>(makeMap("error", "Ya tienes barcos en este juego, no seas buitre"), HttpStatus.FORBIDDEN);
        }

        //  opción 1 validación conjunta de cantidad de barcos (tipo) por gameplayer
        if(ships.stream().filter(ship -> ship.getType().equals("carrier")).count()>1 ||
                ships.stream().filter(ship -> ship.getType().equals("battleship")).count()>1 ||
                ships.stream().filter(ship -> ship.getType().equals("submarine")).count()>1 ||
                ships.stream().filter(ship -> ship.getType().equals("destroyer")).count()>1 ||
                ships.stream().filter(ship -> ship.getType().equals("patrolboat")).count()>1) {
            return new ResponseEntity<>(makeMap("error", "No puedes modificar la cantidad de los barcos por partida, no te hagas el loco"), HttpStatus.FORBIDDEN);
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
            return new ResponseEntity<>(makeMap("error", "Faltan barcos"), HttpStatus.FORBIDDEN);

        }

        if (ships.size()==5) {

            // Opcion 1 con stream
            ships.stream().forEach(s ->{
                    s.setGamePlayer(currentGamePlayer);
                    shipRepository.save(s);
            });
            return new ResponseEntity<>(makeMap("OK", "Creado"), HttpStatus.CREATED);}

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
            return new ResponseEntity<>(makeMap("error", "No se crearon 5 Ships"), HttpStatus.FORBIDDEN);
        }

    }

    // get salvoes
    @RequestMapping(path="/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> viewSalvoes(@PathVariable long gamePlayerId, Authentication authentication) {

        // Comprobación de autenticación
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "No estás logueado"), HttpStatus.FORBIDDEN);
        }

        Player currentPlayer = playerRepository.findByUserName(authentication.getName());


        // Comprobación si el usuario está ok
        if (currentPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "No estás logueado"), HttpStatus.FORBIDDEN);
        }

        // Comprobación si existe el GamePlayer
        if (!gamePlayerRepository.existsById(gamePlayerId)) {
            return new ResponseEntity<>(makeMap("error", "No existe el GamePlayer"), HttpStatus.FORBIDDEN);
        }

        GamePlayer currentGamePlayer = gamePlayerRepository.getById(gamePlayerId);

        if (currentGamePlayer.getPlayer() != currentPlayer) {
            return new ResponseEntity<>(makeMap("error", "No tienes permiso, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
        }

        List<Map<String, Object>> listsalvoes = currentGamePlayer.getSalvos().stream()
                .map(salvo -> salvo.makeSalvoDTO()).collect(Collectors.toList());

        return new ResponseEntity<>(makeMap("salvoes", listsalvoes), HttpStatus.CREATED);

    }



    @RequestMapping(path="/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createSalvoes(@PathVariable long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {

        // Comprobación de autenticación
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "No estás logueado"), HttpStatus.UNAUTHORIZED);
        }

        Player currentPlayer = playerRepository.findByUserName(authentication.getName());


        // Comprobación si el usuario está ok
        if (currentPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "No estás logueado correctamente"), HttpStatus.UNAUTHORIZED);
        }

        // Comprobación si existe el GamePlayer
        if (!gamePlayerRepository.existsById(gamePlayerId)) {
            return new ResponseEntity<>(makeMap("error", "No existe el GamePlayer"), HttpStatus.FORBIDDEN);
        }

        GamePlayer currentGamePlayer = gamePlayerRepository.getById(gamePlayerId);

        // Validación si el player no es el mismo que el gameplayer
        if (currentGamePlayer.getPlayer() != currentPlayer) {
            return new ResponseEntity<>(makeMap("error", "No tienes permiso, y no está permitido la manganeta ;)"), HttpStatus.FORBIDDEN);
        }

        // tiene que haber dos jugadores
        if (currentGamePlayer.getGame().getGameplayers().size()!=2){
            return new ResponseEntity<>(makeMap("error","Para disparar debes tener un oponente"), HttpStatus.FORBIDDEN);
        }

        // Obtener Game Actual
        Game currentGame = currentGamePlayer.getGame();

        // Obtener Oponente actual (si existiera, por eso el Optional)
        Optional<GamePlayer> currentOpponent = currentGame.getGamePlayers()
                .stream().findFirst().filter(gp -> gp.getId()!=currentPlayer.getId());
        //return new ResponseEntity<>(makeMap("current opponent gamePlayer id",currentOpponent.get().getId()), HttpStatus.ACCEPTED);


        // que el jugador actual tenga los 5 barcos ubicados
        if(currentGamePlayer.getShips().size()!=5){
            return new ResponseEntity<>(makeMap("error","Primero debes ubicar los 5 barcos"), HttpStatus.ACCEPTED);
        }

        // que el oponente tenga los 5 barcos ubicados
        if(currentOpponent.get().getShips().size()!=5){
            return new ResponseEntity<>(makeMap("error","El oponente no ubicó aún los 5 barcos"), HttpStatus.ACCEPTED);
        }

        // Más de un salvo y menos o igual 5
        if(!(salvo.getSalvoLocations().size()>1 && salvo.getSalvoLocations().size()<6)) {
            return new ResponseEntity<>(makeMap("error","Tienes que tener al menos un disparo, y no más de 5"), HttpStatus.FORBIDDEN);
        }
            //return new ResponseEntity<>(makeMap("cantidad de turnos", currentGamePlayer.getSalvos().size()),HttpStatus.ACCEPTED);


        // get turn y sumarle 1 para un nuevo turno de salvo
        int turnoActual= currentGamePlayer.getSalvos().size();

        // Salvo Constructor (GamePlayer gamePlayer, int turn, List<String> locations)
        salvoRepository.save(new Salvo(currentGamePlayer, turnoActual+1,salvo.getSalvoLocations()));
        return new ResponseEntity<>(makeMap("salvoLocations", currentGamePlayer.getSalvos().size()),HttpStatus.CREATED);

    }


}





