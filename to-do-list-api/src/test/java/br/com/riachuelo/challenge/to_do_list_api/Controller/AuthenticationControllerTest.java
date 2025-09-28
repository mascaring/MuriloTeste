package br.com.riachuelo.challenge.to_do_list_api.controller;

import br.com.riachuelo.challenge.to_do_list_api.dto.AuthenticationRequest;
import br.com.riachuelo.challenge.to_do_list_api.dto.AuthenticationResponse;
import br.com.riachuelo.challenge.to_do_list_api.model.User;
import br.com.riachuelo.challenge.to_do_list_api.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Carrega apenas o contexto da camada web para o AuthenticationController
@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular requisições HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos Java em JSON

    @MockBean // Cria um mock do serviço e o injeta no contexto do Spring
    private AuthenticationService authenticationService;

    @Test
    void deveRegistrarUsuarioERetornarToken() throws Exception {
        // Arrange
        User userRequest = new User("Novo User", "novo@email.com", "password123");
        AuthenticationResponse authResponse = new AuthenticationResponse("novo.jwt.token");

        when(authenticationService.register(any(User.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("novo.jwt.token"));
    }

    @Test
    void deveLogarUsuarioERetornarToken() throws Exception {
        // Arrange
        AuthenticationRequest authRequest = new AuthenticationRequest("teste@email.com", "password123");
        AuthenticationResponse authResponse = new AuthenticationResponse("login.jwt.token");

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("login.jwt.token"));
    }
}