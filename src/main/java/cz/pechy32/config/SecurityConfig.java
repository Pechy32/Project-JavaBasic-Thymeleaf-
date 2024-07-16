package cz.pechy32.config;

import cz.pechy32.DB.Database;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;


import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Nastavení Spring Security
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig{
    /**
     * Nastavení spring security chain filtru
     * @param http veškeré http požadavky
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain settingLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers("/insurance-app/about").permitAll()
                        .requestMatchers("insurance-app/statistics").permitAll()

                        //požadavky požadující autentizaci (přihlášení)
                        .requestMatchers("insurance-app/insureds/**").authenticated()
                        .requestMatchers("insurance-app/insurances/**").authenticated()

                        //požadavky požadující roli administrátora
                        .requestMatchers("insurance-app/insureds/add-insured").hasAuthority("admin")
                        .requestMatchers("insurance-app/insureds/edit-insured").hasAuthority("admin")
                        .requestMatchers("insurance-app/insureds/delete-insured").hasAuthority("admin")
                        .requestMatchers("insurance-app/insureds/search-insured").hasAuthority("admin")
                        .requestMatchers("insurance-app/insureds/add-insurance").hasAuthority("admin")
                        .requestMatchers("insurance-app/insureds/edit-insurance").hasAuthority("admin")
                        .requestMatchers("insurance-app/insureds/delete-insurance").hasAuthority("admin")
                )

                .formLogin().loginPage("/insurance-app/login")//odkaz na přihlašovací stránku
                .permitAll()
                .defaultSuccessUrl("/insurance-app/insureds")//defaultní stránka po úspěšném přihlášení
                .and().
                logout().permitAll()
                .logoutSuccessUrl("/insurance-app/logout")
                .permitAll()
                .and()
                .csrf().disable();//deaktivace tzv cross-site - bez toho nefungujou POST požadavky (??)

        return http.build();
    }

    /**
     * Načítání uživatelů z databáze
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            try {
                Database database = null;
                try {
                    database = new Database("insurance-app_db", "root", "");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                String tables = "users JOIN user_roles ON users.user_id = user_roles.user_id JOIN roles ON user_roles.role_id = roles.role_id";
                String[] columns = {"users.password", "roles.name"};
                Object[] params = {username};

                ResultSet rs = database.select(tables, columns, "users.username = ?", params);

                if (rs.next()) {
                    String password = rs.getString("password");
                    String role = rs.getString("name");

                    //tento typ kódování hesla je nebezpečný - pro účely aplikace však stačí
                    UserDetails user = User.withDefaultPasswordEncoder()
                            .username(username)
                            .password(password)
                            .roles(role)
                            .build();

                    return user;
                } else {
                    throw new UsernameNotFoundException("Uživatel nenalezen: " + username);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Chyba připojení k databázi", e);
            }
        };
    }
    /**
     * Umožňuje přístup k statickým souborům pro správné zobrazení přihlašovací stránky
     * a dalších podstránek nevyžadujících přihlášení
     * @return
     */
    @Bean
    WebSecurityCustomizer customizeWebSecurity(){
        return web -> (web).ignoring().requestMatchers("/style.css", "/pechy32_logo.png", "/man_siluete.png", "/hearth_btn.png", "/house_btn.png", "/car_btn.png");
    }
}