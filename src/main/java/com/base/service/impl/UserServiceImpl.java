package com.base.service.impl;

import com.base.exceptions.UserServiceException;
import com.base.io.entity.UserEntity;
import com.base.io.repositories.UserRepository;
import com.base.service.UserService;
import com.base.shared.Utils;
import com.base.shared.dto.AddressDto;
import com.base.shared.dto.UserDto;
import com.base.ui.model.response.ErrorMessages;
import java.util.List;
import org.apache.tomcat.jni.Address;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  Utils utils;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public UserDto createUser(UserDto user) {
    List<AddressDto> addressList = new ArrayList<>();
    if (userRepository.findByEmail(user.getEmail()) != null) {
      throw new RuntimeException("Record already exists");
    }

    for (AddressDto addressDto : user.getAddresses()) {
      addressDto.setAddressesId(utils.generateAddressId(30));
      addressDto.setUserDetails(user);
      addressList.add(addressDto);
    }
    user.setAddresses(addressList);
    //BeanUtils.copyProperties(user, userEntity);

    ModelMapper modelMapper = new ModelMapper();

    UserEntity userEntity = modelMapper.map(user, UserEntity.class);

    String publicUserId = utils.generateUserId(30);
    userEntity.setUserId(publicUserId);
    userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

    UserEntity storedUserDetails = userRepository.save(userEntity);

    UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

    return returnValue;
  }

  @Override
  public UserDto getUser(String email) {
    UserEntity userEntity = userRepository.findByEmail(email);
    UserDto returnValue = new UserDto();
    BeanUtils.copyProperties(userEntity, returnValue);
    return returnValue;
  }


  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity userEntity = userRepository.findByEmail(email);
    if (userEntity == null) {
      throw new UsernameNotFoundException(email);
    }
    return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
  }

  @Override
  public UserDto getUserByUserId(String id) {
    UserDto returnValue = new UserDto();
    UserEntity userEntity = userRepository.findByUserId(id);

    if (userEntity == null) {
      throw new UsernameNotFoundException("User with ID: " + id + " not found");
    }

    BeanUtils.copyProperties(userEntity, returnValue);

    return returnValue;
  }

  @Override
  public UserDto updateUser(String id, UserDto userDto) {
    UserDto returnValue = new UserDto();
    UserEntity userEntity = userRepository.findByUserId(id);

    if (userEntity == null) {
      throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
    }

    userEntity.setFirstName(userDto.getFirstName());
    userEntity.setLastName(userDto.getLastName());

    UserEntity updatedUserDetails = userRepository.save(userEntity);

    BeanUtils.copyProperties(updatedUserDetails, returnValue);

    return returnValue;
  }

  @Override
  public void deleteUser(String userId) {
    UserEntity userEntity = userRepository.findByUserId(userId);

    if (userEntity == null) {
      throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
    }

    userRepository.delete(userEntity);
  }

  @Override
  public List<UserDto> getUsers(int page, int limit) {
    List<UserDto> returnValue = new ArrayList<>();

    Pageable pageableRequest = PageRequest.of(page, limit);

    Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
    List<UserEntity> users = usersPage.getContent();

    for (UserEntity userEntity : users) {
      UserDto userDto = new UserDto();
      BeanUtils.copyProperties(userEntity, userDto);
      returnValue.add(userDto);
    }

    return returnValue;
  }
}
