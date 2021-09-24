package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.Classes.*;
import com.codeoftheweb.salvo.Interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;


@SpringBootApplication
public class SalvoApplication {

	//Iniciador del proyecto
	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	// Se agregan todos los repositorios en init data.
	// Se crean instancias de cada objeto de acuerdo al modelo de constructo (argumentos)
	// Se guardan con save indicando el repositorio y la nueva instancia a guardar
	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository,
									  SalvoRepository salvoRepository,
									  ScoreRepository scoreRepository) {

		return (args) -> {
			// forzado de grabación players de acuerdo al constructor en Player.java
			Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
			Player player3 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("kb"));
			Player player4 = new Player("d.palmer@whitehouse.gov", passwordEncoder().encode("mole"));
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

			// forzado grabación Ships
			Ship ship1 = new Ship ("destroyer", gameplayer1, Arrays.asList("A2","A3","A4"));
			Ship ship2 = new Ship ("submarine", gameplayer1, Arrays.asList("C1","C2","C3"));
			Ship ship3 = new Ship ("patrolboat", gameplayer2, Arrays.asList("C2","D2"));
			Ship ship4 = new Ship ("submarine", gameplayer2, Arrays.asList("A2","A3","A4"));
			Ship ship5 = new Ship ("patrolboat", gameplayer1, Arrays.asList("G8","H8"));
			Ship ship6 = new Ship ("submarine", gameplayer3, Arrays.asList("C2","C3","C4"));
			Ship ship7 = new Ship ("destroyer", gameplayer4, Arrays.asList("A2","B2"));
			Ship ship8 = new Ship ("carrier", gameplayer1, Arrays.asList("E1","E2","E3","E4","E5"));
			Ship ship9 = new Ship ("battleship", gameplayer1, Arrays.asList("G2","H2","I2","J2"));



			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
			shipRepository.save(ship7);
			shipRepository.save(ship8);
			shipRepository.save(ship9);


			// forzado grabación Salvo

			Salvo salvo1 = new Salvo(gameplayer1,1,Arrays.asList("A1", "A2"));
			Salvo salvo2 = new Salvo(gameplayer2,1,Arrays.asList("D1","D3"));
			Salvo salvo3 = new Salvo(gameplayer1,2,Arrays.asList("A5","B5"));
			Salvo salvo4 = new Salvo(gameplayer2,2,Arrays.asList("F5","F4"));
			Salvo salvo5 = new Salvo(gameplayer3,1,Arrays.asList("G5","G4"));
			Salvo salvo6 = new Salvo(gameplayer4,1,Arrays.asList("H5","H4"));

			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);
			salvoRepository.save(salvo5);
			salvoRepository.save(salvo6);

			// forzado grabación Score
			Score score1 = new Score(game1, player1, 1.0);
			Score score2 = new Score(game1, player2, 0.0);
			Score score3 = new Score(game2, player1, 0.5);
			Score score4 = new Score(game2, player2, 0.5);
			Score score5 = new Score(game3, player2, 1.0);
			Score score6 = new Score(game3, player3, 0.0);
			Score score7 = new Score(game4, player1, 0.5);
			Score score8 = new Score(game4, player2, 0.5);

			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);
			scoreRepository.save(score5);
			scoreRepository.save(score6);
			scoreRepository.save(score7);
			scoreRepository.save(score8);

		};
	}


	// Password Encoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}



}


// AUTHENTICATION
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("PLAYER"));
			} else {
				throw new UsernameNotFoundException("Usuario Desconocido: " + inputName);
			}
		});
	}


}


// AUTHORIZATION

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {


	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				//antMatchers("/admin/**").hasAuthority("ADMIN")
				.antMatchers("/index.html").permitAll()
				.antMatchers("/api/login").permitAll()
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/games").permitAll()
				.antMatchers("/api/players").permitAll()
				.antMatchers("/h2-console/**").permitAll()
				.and().headers().frameOptions().disable()
				.and().csrf().ignoringAntMatchers("/h2-console/**")
				.and()
				.cors().disable();
		http.authorizeRequests().
				antMatchers("**").hasAuthority("PLAYER")
				;

			// modificar la ruta del formLogin predeterminado
			http.formLogin()
					.usernameParameter("name")
					.passwordParameter("pwd")
					.loginPage("/api/login")
					;
			http.logout().logoutUrl("/api/logout");


		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}

	}


}





