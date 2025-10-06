package microservices.userservices.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import microservices.userservices.dto.UserDTO;
import microservices.userservices.mapper.UserMapper;
import microservices.userservices.model.User;
import microservices.userservices.repository.UserRepository;
import microservices.userservices.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private final RedisTemplate<String, UserDTO> redisTemplate;

    @Override
    public ResponseEntity<UserDTO> createUser(UserDTO userDTO) {
        User user = userMapper.toModel(userDTO);

        if (user.getPasswordHash().length() < 6) {
            logger.warning("Password is too short");
            return ResponseEntity.badRequest().build();
        }

        User save = userRepository.save(user);
        UserDTO dto = userMapper.toDTO(save);
        redisTemplate.keys("user:*").forEach(redisTemplate::delete
        );

        redisTemplate.opsForValue().set(getCachedKey(user.getId()), dto);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<UserDTO> getUser(Long id) {
        String key = getCachedKey(id);
        UserDTO cachedUser = redisTemplate.opsForValue().get(key);

        if (cachedUser != null) {
            return ResponseEntity.ok(cachedUser);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserDTO dto = userMapper.toDTO(user);
        redisTemplate.opsForValue().set(key, dto);

        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<UserDTO> updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setRole(userDTO.role());
        user.setPasswordHash(userDTO.passwordHash());

        User save = userRepository.save(user);
        UserDTO dto = userMapper.toDTO(user);

        redisTemplate.opsForValue().set(getCachedKey(save.getId()), dto);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> userDTOS = userRepository.findAll();
        List<UserDTO> userDTOList = userDTOS.stream()
                .map(userMapper::toDTO)
                .toList();
        return ResponseEntity.ok(userDTOList);
    }

    public String getCachedKey(Long id) {
        return "user:" + id;
    }
}
