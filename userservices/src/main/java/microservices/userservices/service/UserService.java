package microservices.userservices.service;

import microservices.userservices.dto.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<UserDTO> createUser(UserDTO userDTO);

    ResponseEntity<UserDTO> getUser(Long id);

    ResponseEntity<UserDTO> updateUser(Long id, UserDTO userDTO);

    ResponseEntity<List<UserDTO>> getAllUsers();
}
