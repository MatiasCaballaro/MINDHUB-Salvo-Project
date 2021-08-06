package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    /* Paso 1 devuelve toda la lista de búsqueda
    @RequestMapping("/games")
    public List<Game> getAll() {
        return gameRepository.findAll();
    }*/
/*
    @RequestMapping("/games")
    public List<Game> getAll(Set<Game> gameId) {
        return gameID.stream().map(Person::getLastName).collect(Collectors::toList);
    }
*/

    /*List<String> getLastNames(Set<Person> people) {
        return people.stream().map(Person::getLastName).collect(Collectors::toList);
    }*/
}
