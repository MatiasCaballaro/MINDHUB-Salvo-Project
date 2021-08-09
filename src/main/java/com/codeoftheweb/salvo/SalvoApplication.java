package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Date;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	// Se agregan todos los repositorios en init data.
	// Se crean instancias de cada objeto de acuerdo al modelo de constructo (argumentos)
	// Se guardan con save indicando el repositorio y la nueva instancia a guardar
	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
		return (args) -> {
			// forzado de grabación players de acuerdo al constructor en Player.java
			Player player1 = new Player("j.bauer@ctu.gov");
			Player player2 = new Player("c.obrian@ctu.gov");
			Player player3 = new Player("t.almeida@ctu.gov");
			Player player4 = new Player("d.palmer@whitehouse.gov");
			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);


			// forzado de grabación games de acuerdo al constructor en Game.java
			Game game1 = new Game(LocalDateTime.now());
			Game game2 = new Game(LocalDateTime.now().plusHours(1));
			Game game3 = new Game(LocalDateTime.now().plusHours(2));
			Game game4 = new Game(LocalDateTime.now().plusHours(2));
			Game game5 = new Game(LocalDateTime.now().plusHours(2));
			Game game6 = new Game(LocalDateTime.now().plusHours(2));
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);

			//forzado grabación gameplayers de acuerdo al constructor en GamePlayer.java
			GamePlayer gameplayer1 = new GamePlayer (LocalDateTime.now(), player1, game1 );
			GamePlayer gameplayer2 = new GamePlayer (LocalDateTime.now(), player2, game1 );
			GamePlayer gameplayer3 = new GamePlayer (LocalDateTime.now().plusHours(1), player1, game2 );
			GamePlayer gameplayer4 = new GamePlayer (LocalDateTime.now().plusHours(1), player2, game2 );
			GamePlayer gameplayer5 = new GamePlayer (LocalDateTime.now().plusHours(2), player2, game3 );
			GamePlayer gameplayer6 = new GamePlayer (LocalDateTime.now().plusHours(2), player3, game3 );
			GamePlayer gameplayer7 = new GamePlayer (LocalDateTime.now().plusHours(3), player1, game4 );
			GamePlayer gameplayer8 = new GamePlayer (LocalDateTime.now().plusHours(3), player2, game4 );
			GamePlayer gameplayer9 = new GamePlayer (LocalDateTime.now().plusHours(4), player1, game5 );
			GamePlayer gameplayer10 = new GamePlayer (LocalDateTime.now().plusHours(4), player3, game5 );
			GamePlayer gameplayer11 = new GamePlayer (LocalDateTime.now().plusHours(5), player4, game6 );
			gamePlayerRepository.save(gameplayer1);
			gamePlayerRepository.save(gameplayer2);
			gamePlayerRepository.save(gameplayer3);
			gamePlayerRepository.save(gameplayer4);
			gamePlayerRepository.save(gameplayer5);
			gamePlayerRepository.save(gameplayer6);
			gamePlayerRepository.save(gameplayer7);
			gamePlayerRepository.save(gameplayer8);
			gamePlayerRepository.save(gameplayer9);
			gamePlayerRepository.save(gameplayer10);
			gamePlayerRepository.save(gameplayer11);

		};
	}

}

