package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Id;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;



     /* // DEVUELVE UN SOLO VALOR FORZADO

    @RequestMapping("/games")
    public Map<String, Object> listaGameId(GameRepository gameRepository) {
        Map<String, Object> listGameId = new LinkedHashMap<String, Object>();
        listGameId.put("id", "1");
        listGameId.put("id", "2");
        return listGameId;
    }


    // Paso 1 devuelve toda la lista de búsqueda

    @RequestMapping("/games-test1")
    public List<Game> getAll() {
        return gameRepository.findAll();
    }


    // Devuelve todo los gameIds del Game Repository
    // Usando el la función get y una de las declaraciones de la clase
    // en este caso GetId (ya declarada en Game)

    @RequestMapping("/games-test-1")
    public List<Object> listaGameId() {
        return gameRepository
                .findAll()
                .stream()
                .map(game -> game.getId())
                .collect(Collectors.toList());
    }
    */



    // Creo un List Object, y no paso una variable específica, sino un Map (Array)
    // Lo que hace el stream () es para cada uno de los find all, todo lo que está adentro del map
    // el map, va cambiando la variable "game" a "game+1", generando bucle por cada objeto, y va devolviendo
    // los atributos creados en el DTO
    @RequestMapping("/games")
    public List<Object> listaGameId() {
        return gameRepository
                .findAll()
                .stream()
                .map(game -> makeGameDTO(game))
                .collect(Collectors.toList());
    }

    // El DTO va escribiendo sobre cada variable, un nombre y un valor (id="" y created ="")
    // en el makeGameDTO, se pasa como variable un objeto de tipo game
    /*
        private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gameplay",
                game.getGameplayers()
                        .stream()
                        .map(gamePlayer -> makeGamePlayerDTO(gamePlayer)).collect(Collectors.toList()));
        return dto;
    }*/

    // Además, se crea un Array anidado para gameplayer, usando el método game.getGamePlayers
    // Desde ese método toma los datos y los va trabajando como indica el makeGamePlayerDTO
    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers",
                game.getGameplayers()
                        .stream()
                        .map(gamePlayer -> makeGamePlayerDTO(gamePlayer)).collect(Collectors.toList()));
        //acá
        /*dto.put("ships", game.getGameplayers().stream()
                .map (gamePlayer -> gamePlayer.getShips().stream()
                        .map(ship -> makeShipDTO(ship)).collect(Collectors.toList())));*/

        return dto;

    }

    // Se crea un DTO para Gameplayer, donde se indica el ID de la fila en esa tabla
    // También se agrega una key "player", donde se pasa la variable para que trabaje el makePlayerDTO

    public Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayerID()));
        return dto;
    }

    // El DTO player toma valores "Id" y "username" (e-mail) para cada gameplayer
    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }


    // game_view toma Id de gameplayer y hace un objeto Game con todos los datos requeridos
    // la url sería /api/game_view/nn donde nn es la variable
    /*
    @RequestMapping("/game_view/{nn}" )
    public Map <String, Object> findGame(@PathVariable Long nn) {
        GamePlayer gamePlayer = gamePlayerRepository.getById(nn);
        return makeGameDTO(gamePlayer.getGameID());
    }*/


    /*
    // PRUEBA LISTA SHIPS
    @RequestMapping("/game_view/{nn}" )
    public Map <String, Object> findGame(@PathVariable Long nn) {
        GamePlayer gamePlayer = gamePlayerRepository.getById(nn);
        return makeShipDTO(shipRepository.getById(gamePlayer.getId()));
    }*/



    @RequestMapping("/game_view/{nn}" )
    public Map <String, Object> findGame(@PathVariable Long nn) {
        GamePlayer gamePlayer = gamePlayerRepository.getById(nn);
        return makeGameViewDTO(gamePlayer);
    }




    // Game_view DTO game
    private Map<String, Object> makeGameViewDTO(GamePlayer gamePlayer) {
             Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getGameID().getId());
        dto.put("created", gamePlayer.getGameID().getCreationDate());
        dto.put("gamePlayers",
                gamePlayer.getGameID().getGameplayers()
                        .stream()
                        .map(gamePlayer1 -> makeGamePlayerDTO(gamePlayer1)).collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips().stream()
                        .map(ship -> makeShipDTO(ship)).collect(Collectors.toList()));
        return dto;
    }



    // ShipDTO
    private Map<String, Object> makeShipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", ship.getShipType());
        dto.put("locations", ship.getLocations());
        return dto;
    }




}
