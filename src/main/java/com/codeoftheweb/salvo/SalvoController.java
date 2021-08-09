package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

   // Paso 1 devuelve toda la lista de búsqueda

    @RequestMapping("/games")
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


    /* // DEVUELVE UN SOLO VALOR FORZADO

    @RequestMapping("/games")
    public Map<String, Object> listaGameId(GameRepository gameRepository) {
        Map<String, Object> listGameId = new LinkedHashMap<String, Object>();
        listGameId.put("id", "1");
        listGameId.put("id", "2");
        return listGameId;
    }
    */





    /*
    public List<Billing> getGroceryBills(List<Billing> billings) {
        return
                billings.stream()
                        .filter(b -> b.getType() == Billing.GROCERY)
                        .sorted((b1, b2) -> b2.getValue() - b1.getValue())
                        .collect(toList());
    }
    */

}
