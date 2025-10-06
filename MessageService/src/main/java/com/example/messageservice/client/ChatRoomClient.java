package com.example.messageservice.client;

import com.example.messageservice.dto.ChatRoomDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "chatservice", url = "http://localhost:8090")
public interface ChatRoomClient {
    @GetMapping("/room/{id}")
    ChatRoomDTO getRoomById(@PathVariable Long id);

}
