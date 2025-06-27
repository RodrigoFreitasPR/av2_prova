package lucas.atv2.app;

import lucas.atv2.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTests {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        
        // Create test users
        var admin = new lucas.atv2.app.model.User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRole("ADMIN");
        userRepository.save(admin);
        
        var user = new lucas.atv2.app.model.User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole("USER");
        userRepository.save(user);
    }

    @Test
    void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/auth/login")
                .param("username", "admin")
                .param("password", "123456")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isOk())
               .andExpect(content().string(notNullValue()));
    }

    @Test
    void testLoginFailureInvalidPassword() throws Exception {
        mockMvc.perform(post("/auth/login")
                .param("username", "admin")
                .param("password", "wrongpassword")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isUnauthorized())
               .andExpect(content().string(containsString("Senha incorreta")));
    }

    @Test
    void testProtectedEndpointAccessDeniedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/hello"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedEndpointAccessWithValidToken() throws Exception {
        String token = mockMvc.perform(post("/auth/login")
                .param("username", "user")
                .param("password", "password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isOk())
               .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/hello")
                .header("Authorization", "Bearer " + token))
               .andExpect(status().isOk())
               .andExpect(content().string("Olá! Você acessou um endpoint protegido com sucesso!"));
    }

    @Test
    void testAdminEndpointAccessDeniedForUser() throws Exception {
        String userToken = mockMvc.perform(post("/auth/login")
                .param("username", "user")
                .param("password", "password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isOk())
               .andReturn().getResponse().getContentAsString();

        System.out.println(userToken);
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isForbidden());
    }
}