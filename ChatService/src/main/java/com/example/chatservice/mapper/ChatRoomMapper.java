package com.example.chatservice.mapper;

import com.example.chatservice.dto.ChatRoomDTO;
import com.example.chatservice.model.ChatRoom;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {
    ChatRoomDTO toDTO(ChatRoom chatRoom);

    ChatRoom toModel(ChatRoomDTO chatRoomDTO);
}
