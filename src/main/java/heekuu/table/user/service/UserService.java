package heekuu.table.user.service;

import heekuu.table.user.dto.CustomUserDetails;
import heekuu.table.user.dto.LoginDTO;
import heekuu.table.user.dto.UserResponseDTO;
import heekuu.table.user.dto.UserUpdateDTO;
import heekuu.table.user.entity.User;

public interface UserService {

  String loginUser(LoginDTO loginDTO);

  User getUserById(Long userId);

  UserResponseDTO getLoggedInUser();

  UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO);

  CustomUserDetails getLoggedInUserDetails();

  UserResponseDTO updateUserRole(Long userId, String newRole, String refresh);

  void addRefreshTokenPublic(User user, String refreshToken, Long expiredMs);

  void deleteUser();
}