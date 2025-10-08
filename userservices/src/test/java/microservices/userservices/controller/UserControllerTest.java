package microservices.userservices.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import microservices.userservices.dto.UserDTO;
import microservices.userservices.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = Logger.getLogger(UserControllerTest.class.getName());

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasurce.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @Test
    void createUser() throws Exception {
        UserDTO userDTO = new UserDTO(null, "anton", "123", "1234567", Role.USER);
        String json = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
    }

    @Test
    void getUser() throws Exception {
        UserDTO userDTO = new UserDTO(null, "anton", "123", "1234567", Role.USER);
        String json = objectMapper.writeValueAsString(userDTO);

        var create = mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        var userId = objectMapper.readTree(create.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andReturn();
    }

    @Test
    void getAllUsers() throws JsonProcessingException {
//        List<UserDTO> users = new ArrayList<>();
//
//        users.add(new UserDTO(null, "anton", "123", "1234567", Role.USER));
//        users.add(new UserDTO(null, "sergey", "123", "1234567", Role.ADMIN));
//        users.add(new UserDTO(null, "vladimir", "1234", "1234567", Role.ADMIN));
//
//        String json = objectMapper.writeValueAsString(users);
//
//        users.forEach(user -> {
//            try {
//                mockMvc.perform(post("/users/create")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(json))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$[0].id").exists())
//                        .andExpect(jsonPath("$[1].id").exists())
//                        .andExpect(jsonPath("$[2].id").exists())
//                        .andExpect(jsonPath("$.size()").value(3))
//                        .andReturn();
//            } catch (Exception e) {
//                logger.warning(e.getMessage());
//            }
//        });
    }
    
    @Test
    void updateUser() throws Exception {
        UserDTO userDTO = new UserDTO(null, "anton", "123", "1234567", Role.USER);
        String json = objectMapper.writeValueAsString(userDTO);

        var create = mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        var userId = objectMapper.readTree(create.getResponse().getContentAsString()).get("id").asLong();

        UserDTO updateUser = new UserDTO(userId, "sergey", "123", "1234567", Role.ADMIN);
        String updateJson = objectMapper.writeValueAsString(updateUser);

        mockMvc.perform(put("/users/update/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("sergey"))
                .andReturn();
    }
}