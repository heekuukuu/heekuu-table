package helloworld.studytogether.user.controller;

import helloworld.studytogether.user.dto.UserResponseDTO;
import helloworld.studytogether.user.dto.UserUpdateDTO;
import helloworld.studytogether.user.entity.Role;
import helloworld.studytogether.user.entity.User;
import helloworld.studytogether.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

  @InjectMocks
  private UserController userController;

  @Mock
  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getLoggedInUser_ShouldReturnUserResponseDTO_WhenAuthenticated() {
    // Arrange
    UserResponseDTO expectedUserResponse = new UserResponseDTO();
    expectedUserResponse.setUserId(1L);
    expectedUserResponse.setUsername("testUser");
    expectedUserResponse.setEmail("test@example.com");
    expectedUserResponse.setNickname("testNickname");
    expectedUserResponse.setRole("USER");

    when(userService.getLoggedInUser()).thenReturn(expectedUserResponse);

    // Act
    ResponseEntity<UserResponseDTO> response = userController.getMe();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedUserResponse, response.getBody());
    verify(userService, times(1)).getLoggedInUser();
  }

  @Test
  void updateUser_ShouldReturnUpdatedUserResponseDTO() {
    // Arrange
    UserUpdateDTO updateDTO = new UserUpdateDTO();
    updateDTO.setEmail("newemail@example.com");
    updateDTO.setNickname("newNickname");

    UserResponseDTO updatedUserResponse = new UserResponseDTO();
    updatedUserResponse.setUserId(1L);
    updatedUserResponse.setUsername("testUser");
    updatedUserResponse.setEmail("newemail@example.com");
    updatedUserResponse.setNickname("newNickname");
    updatedUserResponse.setRole("USER");

    when(userService.updateUser(updateDTO)).thenReturn(updatedUserResponse);

    // Act
    ResponseEntity<UserResponseDTO> response = userController.updateUser(updateDTO);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("newemail@example.com", response.getBody().getEmail());
    assertEquals("newNickname", response.getBody().getNickname());
    verify(userService, times(1)).updateUser(updateDTO);
  }
}