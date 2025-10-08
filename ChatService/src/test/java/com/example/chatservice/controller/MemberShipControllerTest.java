package com.example.chatservice.controller;

import com.example.chatservice.client.UserClient;
import com.example.chatservice.dto.ChatRoomDTO;
import com.example.chatservice.dto.MemberShipDTO;
import com.example.chatservice.dto.UserDTO;
import com.example.chatservice.model.ChatRoom;
import com.example.chatservice.model.MemberShip;
import com.example.chatservice.repository.ChatRoomRepository;
import com.example.chatservice.repository.MemberShipRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
class MemberShipControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserClient userClient;

    @MockitoBean
    MemberShipRepository memberShipRepository;

    @MockitoBean
    ChatRoomRepository chatRoomRepository;

    private UserDTO userDTO;
    private ChatRoom chatRoom;
    private MemberShip savedMember;

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @BeforeEach
    void start() {
        userDTO = new UserDTO(1L, "Anton");
        Mockito.when(userClient.getUserById(Mockito.anyLong())).thenReturn(userDTO);

        chatRoom = new ChatRoom();
        chatRoom.setId(2L);
        chatRoom.setName("room 1");
        Mockito.when(chatRoomRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(chatRoom));

        savedMember = new MemberShip();
        savedMember.setId(1L);
        savedMember.setUserId(1L);
        savedMember.setChat(chatRoom);
        savedMember.setRole("ORGANIZER");
        Mockito.when(memberShipRepository.save(Mockito.any(MemberShip.class))).thenReturn(savedMember);
        Mockito.when(memberShipRepository.findById(2L)).thenReturn(Optional.of(savedMember));
        Mockito.when(memberShipRepository.findAllByChatRoomId(Mockito.anyLong())).thenReturn(List.of(savedMember));
    }


    @Test
    void createMember() throws Exception {
        MemberShipDTO memberShipDTO = new MemberShipDTO(null, 2L, 1L, "ORGANIZER", null);
        String json = objectMapper.writeValueAsString(memberShipDTO);

        mockMvc.perform(post("/member/create/" + 1L + "/" + 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatRoomId").value(2))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.role").value("ORGANIZER"))
                .andReturn();
    }

    @Test
    void getAllUsersInRoom() throws Exception {
//        mockMvc.perform(get("/member/2")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].chatRoomId").value(2L))
//                .andReturn();

    }

    @Test
    void deleteUserInRoom() throws Exception {
        MemberShipDTO memberShipDTO = new MemberShipDTO(null, 2L, 1L, "ORGANIZER", userDTO);

        String json = objectMapper.writeValueAsString(memberShipDTO);

        mockMvc.perform(post("/member/create/" + 1L + "/" + 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        mockMvc.perform(delete("/member/delete/" + 1L + "/" + 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    void isUserInRoom() throws Exception {
        MemberShipDTO memberShipDTO = new MemberShipDTO(null, 2L, 1L, "ORGANIZER", userDTO);
        Mockito.when(memberShipRepository.existsMemberShipByUserIdAndChatRoomId(1L, 2L)).thenReturn(true);
        String json = objectMapper.writeValueAsString(memberShipDTO);

        mockMvc.perform(get("/member/isUserInRoom/" + 1L + "/" + 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
    }
}