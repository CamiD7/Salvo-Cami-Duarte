package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.Classes.*;
import com.codeoftheweb.salvo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired PlayerRepository player;

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(PlayerRepository PlayerRepo, GameRepository GameRepo, GamePlayerRepository gameplayRepo, ShipRepository shipRepo, SalvoRepository salvoRepo, ScoreRepository scoreRepo ) {
		return (args) -> {

			Player player1 = new Player("j.bauer@ctu.gov",passwordEncoder.encode("24"));
			Player player2 = new Player("c.obrian@ctu.gov",passwordEncoder.encode("42"));
			Player player3 = new Player("kim_bauer@gmail.com",passwordEncoder.encode("kb"));

			PlayerRepo.save(player1);
			PlayerRepo.save(player2);
			PlayerRepo.save(player3);


			Game game1 =  new Game();
			Game game2 =  new Game();
			//Game game3 = new Game() ;

			Date date1 = new Date();
			Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
			Date date3 = Date.from(date1.toInstant().plusSeconds(7200));

			game1.setCreated(date1);
			game2.setCreated(date2);
			//game3.setCreated(date3);

			GameRepo.save(game1);
			GameRepo.save(game2);
			//GameRepo.save(game3);*/

			GamePlayer gamePlayer1 = new GamePlayer(game1,player1,date1);
			GamePlayer gamePlayer2 = new GamePlayer(game1,player2,date2);
			GamePlayer gamePlayer3 = new GamePlayer(game2,player3,date3);

			gameplayRepo.save(gamePlayer1);
			gameplayRepo.save(gamePlayer2);
			gameplayRepo.save(gamePlayer3);



			Ship ship1 = new Ship("destroyer", List.of("H2", "H3", "H4"),gamePlayer1);
			Ship ship2 = new Ship( "submarine", List.of("E1", "F1", "G1"),gamePlayer1);
			Ship ship3 = new Ship( "patrolboat", List.of("B5", "B4"),gamePlayer1);
			Ship ship4 = new Ship( "carrier", List.of("J1", "J2", "J3", "J4", "J5"),gamePlayer1);
			Ship ship5 = new Ship( "battleship", List.of("A5", "A6", "A7", "A8"),gamePlayer1);
			Ship ship6 = new Ship( "destroyer", List.of("B5", "C5", "D5"),gamePlayer2);
			Ship ship7 = new Ship( "patrolboat", List.of("F1", "F2"),gamePlayer2);
			Ship ship8 = new Ship( "submarine", List.of("J1", "J2", "J3"),gamePlayer2);
			Ship ship9 = new Ship( "battleship", List.of("A1", "A2", "A3", "A4"),gamePlayer2);
			Ship ship10 = new Ship( "carrier", List.of("G1", "G2", "G3", "G4", "G5"),gamePlayer2);


			shipRepo.save(ship1);
			shipRepo.save(ship2);
			shipRepo.save(ship3);
			shipRepo.save(ship4);
			shipRepo.save(ship5);
			shipRepo.save(ship6);
			shipRepo.save(ship7);
			shipRepo.save(ship8);
			shipRepo.save(ship9);
			shipRepo.save(ship10);

			/*Salvo salvo1= new Salvo(gamePlayer1,1,List.of("B1","H8","E3","F10","J5"));
			Salvo salvo2= new Salvo(gamePlayer2,1,List.of("A6","E3","F1","C4","D4"));
			Salvo salvo3 = new Salvo(gamePlayer1,2,List.of("D3","F6","C8","A1","G5"));
			Salvo salvo4 = new Salvo(gamePlayer2,2,List.of("E4","E3","D1","F5","D6"));
			Salvo salvo5 = new Salvo(gamePlayer1,3,List.of("D3","H6","C6","I7","G10"));
			Salvo salvo6 = new Salvo(gamePlayer2,3,List.of("D10","F6","C6","A1","G5"));
			Salvo salvo7 = new Salvo(gamePlayer1,4,List.of("I3","A6","J6","I5","G7"));
			Salvo salvo8 = new Salvo(gamePlayer2,4,List.of("D3","G9","C6","J10","H9"));

			salvoRepo.save(salvo1);
			salvoRepo.save(salvo2);
			salvoRepo.save(salvo3);
			salvoRepo.save(salvo4);
			salvoRepo.save(salvo5);
			salvoRepo.save(salvo6);
			salvoRepo.save(salvo7);
			salvoRepo.save(salvo8);*/


			Score score1= new Score(1, new Date(),player1,game1);
			Score score2= new Score(2, new Date(),player2,game1);
			//Score score3= new Score(0.5, new Date(),player1,game1);

			scoreRepo.save(score1);
			scoreRepo.save(score2);



		};
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	@Autowired
	PlayerRepository playerRepository;
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputUserName-> {
			Player player = playerRepository.findByEmail(inputUserName);
			if (player != null) {
				return new User(player.getEmail(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputUserName);
			}
		});

	}


@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/api/games","/web/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/players").permitAll()
				.antMatchers("/**").hasAuthority("USER")
				.antMatchers("/h2-console/**").permitAll().anyRequest().authenticated()

				.and().csrf().ignoringAntMatchers("/h2-console/**")
				.and().headers().frameOptions().sameOrigin();


				http
				.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");

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
}}

