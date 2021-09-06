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
                .map(s -> s.makeSalvoDTO(s)).collect(Collectors.toList());
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

}
