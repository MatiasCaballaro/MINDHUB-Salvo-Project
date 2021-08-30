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
       else {dto.put("player", null);}

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






    /*@Autowired
    private GameRepository gameRepository;

    @RequestMapping("/games")
    public List<Game> getAll(Authentication authentication) {
        return gameRepository.findByUserName(authentication.getName());
    }*/


    // AUTHENTICATION

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String userName, @RequestParam String password) {

        if (userName.isEmpty() || password.isEmpty() ) {
            return new ResponseEntity<>("Te falta completar datos Mossstro", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(userName) !=  null) {
            return new ResponseEntity<>("El nombre de usuario ya existe, usá otro crack ;)", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
