package br.com.riachuelo.challenge.to_do_list_api.service;

import br.com.riachuelo.challenge.to_do_list_api.dto.AuthenticationRequest;
import br.com.riachuelo.challenge.to_do_list_api.dto.AuthenticationResponse;
import br.com.riachuelo.challenge.to_do_list_api.model.User;
import br.com.riachuelo.challenge.to_do_list_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Teste User", "teste@email.com", "password123");
        user.setId(1L);
    }

    @Test
    void ShouldRegisterUserWithSuccess() {
        String plainPassword = "password123";
        String encodedPassword = "encodedPassword";
        String fakeToken = "fake.jwt.token";
        User userToRegister = new User("Teste User", "teste@email.com", plainPassword);

        when(passwordEncoder.encode(plainPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(fakeToken);

        AuthenticationResponse response = authenticationService.register(userToRegister);

        assertNotNull(response);
        assertEquals(fakeToken, response.getToken());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(encodedPassword, savedUser.getPassword());
        assertEquals("teste@email.com", savedUser.getEmail());

        verify(passwordEncoder, times(1)).encode(plainPassword);
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void ShouldAuthUserWithSuccess() {
        AuthenticationRequest request = new AuthenticationRequest("teste@email.com", "password123");
        String fakeToken = "fake.jwt.token";

        when(userRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(fakeToken);

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals(fakeToken, response.getToken());

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        verify(userRepository, times(1)).findByEmail("teste@email.com");
        verify(jwtService, times(1)).generateToken(user);
    }
}