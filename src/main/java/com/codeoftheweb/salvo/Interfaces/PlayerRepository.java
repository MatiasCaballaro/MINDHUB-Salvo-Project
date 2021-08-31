package com.codeoftheweb.salvo.Interfaces;

import com.codeoftheweb.salvo.Classes.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {

    //List<Player> findByUserName (String userName);
    //Player findByUserName(@Param("userName") String userName);
    Player findByUserName(String userName);

}
