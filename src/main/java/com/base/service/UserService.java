package com.base.service;

import com.base.shared.dto.UserDto;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  UserDto createUser(UserDto user);

  UserDto getUser(String email);

  UserDto getUserByUserId(String id);

  UserDto updateUser(String id, UserDto userDto);

  void deleteUser(String userId);

  List<UserDto> getUsers(int page, int limit);
}
