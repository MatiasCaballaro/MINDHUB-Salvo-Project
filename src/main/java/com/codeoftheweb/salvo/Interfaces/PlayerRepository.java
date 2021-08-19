package com.codeoftheweb.salvo.Interfaces;

import com.codeoftheweb.salvo.Classes.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
