package com.example.chatservice.controller;

import com.example.chatservice.client.UserClient;
import com.example.chatservice.dto.ChatRoomDTO;
import com.example.chatservice.dto.MemberShipDTO;
import com.example.chatservice.dto.UserDTO;
import com.example.chatservice.repository.ChatRoomRepository;
import com.example.chatservice.repository.MemberShipRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
class ChatRoomControllerTest {
    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserClient userClient;

    @Test
    void createRoom() throws Exception {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO(null, "room 1");
        String json = objectMapper.writeValueAsString(chatRoomDTO);

        mockMvc.perform(post("/room/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
    }

    @Test
    void getChatRoomInfo() throws Exception {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO(null, "room 1");
        String json = objectMapper.writeValueAsString(chatRoomDTO);

        var create = mockMvc.perform(post("/room/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        var roomId = objectMapper.readTree(create.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(get("/room/" + roomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(roomId))
                .andReturn();
    }

    @Test
    void getAllRoomsByUser() {
    }

    @Test
    void deleteUserInRoom() throws Exception {
//        ChatRoomDTO chatRoomDTO = new ChatRoomDTO(null, "room 1");
//        String json = objectMapper.writeValueAsString(chatRoomDTO);
//
//        var create = mockMvc.perform(post("/room/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        var roomId = objectMapper.readTree(create.getResponse().getContentAsString()).get("id").asLong();
//        mockMvc.perform(delete("/room/delete/" + roomId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent())
//                .andReturn();
    }
}