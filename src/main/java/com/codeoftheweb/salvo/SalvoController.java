package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.Classes.*;
import com.codeoftheweb.salvo.Interfaces.GamePlayerRepository;
import com.codeoftheweb.salvo.Interfaces.GameRepository;
import com.codeoftheweb.salvo.Interfaces.SalvoRepository;
import com.codeoftheweb.salvo.Interfaces.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

                /*
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
                .map(g -> g.makeGameDTO(g))
                .collect(Collectors.toList());
    }

    // game_view toma Id de gameplayer y hace un objeto GameView con todos los datos requeridos
    // la url sería /api/game_view/nn donde nn es la variable

    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> findGame(@PathVariable Long nn) {
        GamePlayer gamePlayer = gamePlayerRepository.getById(nn);
        return gamePlayer.makeGameViewDTO(gamePlayer);
    }


    // Prueba de salvoes
    @RequestMapping("/salvoes")
    public List<Object> listaSalvos() {
        return salvoRepository
                .findAll()
                .stream()
                .map(s -> s.makeSalvoDTO(s)).collect(Collectors.toList());
    }




}
