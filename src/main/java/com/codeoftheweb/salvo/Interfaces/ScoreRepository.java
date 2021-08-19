package com.codeoftheweb.salvo.Interfaces;

import com.codeoftheweb.salvo.Classes.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ScoreRepository extends JpaRepository <Score, Long> {
}
