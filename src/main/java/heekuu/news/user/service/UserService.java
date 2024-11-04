package heekuu.news.user.service;

import heekuu.news.user.dto.CountDTO;
import heekuu.news.user.dto.CustomUserDetails;
import heekuu.news.user.dto.LoginDTO;
import heekuu.news.user.dto.UserResponseDTO;
import heekuu.news.user.dto.UserUpdateDTO;
import heekuu.news.user.entity.User;

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