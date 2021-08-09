package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;



     /* // DEVUELVE UN SOLO VALOR FORZADO

    @RequestMapping("/games")
    public Map<String, Object> listaGameId(GameRepository gameRepository) {
        Map<String, Object> listGameId = new LinkedHashMap<String, Object>();
        listGameId.put("id", "1");
        listGameId.put("id", "2");
        return listGameId;
    }
    */

    // Paso 1 devuelve toda la lista de búsqueda

    @RequestMapping("/games-test1")
    public List<Game> getAll() {
        return gameRepository.findAll();
    }


    // Devuelve todo los gameIds del Game Repository
    // Usando el la función get y una de las declaraciones de la clase
    // en este caso GetId (ya declarada en Game)

    @RequestMapping("/games2")
    public List<Object> listaGameId() {
        return gameRepository
                .findAll()
                .stream()
                .map(game -> game.getId())
                .collect(Collectors.toList());
    }



    /*
    @RequestMapping("/games")
    public List<Object> listaGameId() {
        return gameRepository
                .findAll()
                .stream()
                .map(game -> game.getId())
                .collect(Collectors.toList());
    }
    */


    @RequestMapping("/games")
    public List<Object> listaGames() {
        return gameRepository
                .findAll()
                .stream()
                .map(game -> game.getId())
                .collect(Collectors.toList());
    }






}
