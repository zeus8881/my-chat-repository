package com.example.chatservice.repository;

import com.example.chatservice.model.MemberShip;
import com.example.chatservice.service.MemberShipService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberShipRepository extends JpaRepository<MemberShip, Long> {
    List<MemberShip> getMemberShipByUserId(Long userId);

    List<MemberShip> findAllByChatRoomId(Long chatRoomId);

    void deleteMemberShipByUserId(Long userId);

    boolean existsMemberShipByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
