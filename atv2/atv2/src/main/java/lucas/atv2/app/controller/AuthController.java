package lucas.atv2.app.controller;

import lucas.atv2.app.service.AuthService;
import lucas.atv2.app.service.JwtService;
import lucas.atv2.app.repository.UserRepository;
import lucas.atv2.app.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para login e geração/validação de tokens JWT")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, JwtService jwtService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PreAuthorize("isAuthenticated()") // For any logged-in user
    @Operation(summary = "Realiza o login do usuário e emite um token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido, retorna o token JWT"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        try {
            String token = authService.authenticateUserAndGenerateToken(username, password);
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro interno ao tentar logar.");
        }
    }

    @Operation(summary = "Valida um token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        if (jwtService.validateToken(token)) {
            return ResponseEntity.ok("Token válido! Username: " + jwtService.getUsernameFromToken(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.");
    }

    @PreAuthorize("hasRole('ADMIN')") // For admin-only endpoints
    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username, 
                                        @RequestParam String password,
                                        @RequestParam String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User newUser = new User(null, username, passwordEncoder.encode(password), role);
        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }
}