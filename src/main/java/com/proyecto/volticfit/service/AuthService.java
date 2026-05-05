package com.proyecto.volticfit.service;
 
import java.util.List;
 
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
 
import com.proyecto.volticfit.dto.MessageResponseDTO;
import com.proyecto.volticfit.dto.Auth.ChangePasswordRequestDTO;
import com.proyecto.volticfit.dto.Auth.ForgotPasswordRequestDTO;
import com.proyecto.volticfit.dto.Auth.LoginRequestDTO;
import com.proyecto.volticfit.dto.Auth.LoginResponseDTO;
import com.proyecto.volticfit.dto.Auth.RefreshTokenResponseDTO;
import com.proyecto.volticfit.dto.Auth.RegisterRequestDTO;
import com.proyecto.volticfit.dto.Auth.RestorePasswordRequestDTO;
import com.proyecto.volticfit.entity.Role;
import com.proyecto.volticfit.entity.Users;
import com.proyecto.volticfit.repository.RoleRepository;
import com.proyecto.volticfit.repository.UsersRepository;
 
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
 
/**
 * Service handling authentication and user account operations.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AuthService {
 
    private static final String DEFAULT_ROLE = "aprendiz";
 
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final RoleRepository rolRepository;
    private final JwtService jwtService;
    private final PasswordResetService passwordResetService;
 
    /**
     * Registers a new user with the default role.
     *
     * @param request the registration data
     * @return success message
     */
    public MessageResponseDTO register(RegisterRequestDTO request) {
        if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Este correo ya está en uso");
        }
 
        Role defaultRole = rolRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
 
        Users user = new Users();
        user.setNames(request.getNames());
        user.setSurnames(request.getSurnames());
        user.setDocType(request.getDocType());
        user.setDocNum(request.getDocNum());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(defaultRole);
        usersRepository.save(user);
 
        log.info("New user registered: {}", request.getEmail());
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Registro exitoso");
        return response;
    }
 
    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request the login credentials
     * @return login response with JWT token
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
 
        if (!user.getState()) {
            throw new RuntimeException("This account is inactive");
        }
 
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
 
        String jwt = jwtService.generateToken(user.getEmail(), user.getRole().getName());
        log.info("User logged in: {}", user.getEmail());
 
        LoginResponseDTO response = new LoginResponseDTO();
        response.setMessage("Inicio de sesión exitoso");
        response.setJwt(jwt);
        return response;
    }
 
    /**
     * Refreshes a JWT token.
     *
     * @param token the existing token
     * @return new token response
     */
    public RefreshTokenResponseDTO refreshToken(String token) {
        String jwt = jwtService.refreshToken(token);
        RefreshTokenResponseDTO response = new RefreshTokenResponseDTO();
        response.setJwt(jwt);
        return response;
    }
 
    /**
     * Changes the password of an authenticated user.
     *
     * @param userId  the user ID
     * @param request the current and new password
     * @return success message
     */
    @Transactional
    public MessageResponseDTO changePassword(Long userId, ChangePasswordRequestDTO request) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
 
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
 
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);
 
        MessageResponseDTO response = new MessageResponseDTO();
        response.setMessage("Contraseña actualizada correctamente");
        return response;
    }
 
    /**
     * Resets a user's password using the recovery code.
     *
     * @param request the email, code and new password
     * @return success message
     */
    @Transactional
    public MessageResponseDTO restorePassword(RestorePasswordRequestDTO request) {
        return passwordResetService.restorePassword(request);
    }
 
    /**
     * Sends a 6-digit recovery code to the user's email.
     *
     * @param request the forgot password request with the user's email
     * @return success message
     */
    public MessageResponseDTO verifyRecoveryCode(ForgotPasswordRequestDTO request) {
        return passwordResetService.forgotPassword(request);
    }

    /**
     * Returns all active users.
     *
     * @return list of active users
     */
    public List<Users> getAllUsers() {
        return usersRepository.findByStateTrue();
    }
}
 