package lucas.atv2.app;

import lucas.atv2.app.model.User;
import lucas.atv2.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /**
     * Inicializa o banco de dados com usuários de teste (opcional)
     * Pode ser removido se estiver usando o CommandLineRunner do SecurityConfig
     */
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verifica se os usuários já existem para evitar duplicação
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRole("ADMIN");
                userRepository.save(admin);
                System.out.println("Usuário admin criado com senha: 123456");
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("password"));
                user.setRole("USER");
                userRepository.save(user);
                System.out.println("Usuário user criado com senha: password");
            }
        };
    }
}