package com.base.controller;

import com.base.exceptions.UserServiceException;
import com.base.service.AddressService;
import com.base.service.UserService;
import com.base.shared.dto.AddressDto;
import com.base.shared.dto.UserDto;
import com.base.ui.model.request.UserDetailsRequestModel;
import com.base.ui.model.response.*;
import java.util.ArrayList;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {

  @Autowired
  UserService userService;

  @Autowired
  AddressService addressService;

  @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE,
      MediaType.APPLICATION_JSON_VALUE})

  public UserRest getUser(@PathVariable String id) {
    UserRest userRest = new UserRest();

    UserDto userDto = userService.getUserByUserId(id);

    BeanUtils.copyProperties(userDto, userRest);

    return userRest;
  }

  @PostMapping(
      consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
  )
  public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
    UserRest returnValue = new UserRest();

    //UserDto userDto = new UserDto();
    //BeanUtils.copyProperties(userDetails, userDto);
    ModelMapper modelMapper = new ModelMapper();
    UserDto userDto = modelMapper.map(userDetails, UserDto.class);

    UserDto createdUser = userService.createUser(userDto);
    returnValue = modelMapper.map(createdUser, UserRest.class);

    return returnValue;
  }

  @PutMapping(path = "/{id}",
      consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
  )
  public UserRest updateUser(@PathVariable String id,
      @RequestBody UserDetailsRequestModel userDetails) {
    UserRest returnValue = new UserRest();
    UserDto userDto = new UserDto();

    BeanUtils.copyProperties(userDetails, userDto);

    UserDto updatedUser = userService.updateUser(id, userDto);
    BeanUtils.copyProperties(updatedUser, returnValue);

    return returnValue;
  }

  @DeleteMapping(path = "/{id}",
      produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
  )
  public OperationStatusModel deleteUser(@PathVariable String id) {

    OperationStatusModel returnValue = new OperationStatusModel();
    returnValue.setOperationName(RequestOperationName.DELETE.name());

    userService.deleteUser(id);
    returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
    return returnValue;
  }

  @GetMapping(
      produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
  )
  public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "25") int page,
      @RequestParam(value = "limit", defaultValue = "0") int limit) {
    List<UserRest> returnValue = new ArrayList<>();

    List<UserDto> users = userService.getUsers(page, limit);

    for (UserDto userDto : users) {
      UserRest userModel = new UserRest();
      BeanUtils.copyProperties(userDto, userModel);
      returnValue.add(userModel);
    }
    return returnValue;
  }

  //http://localhost:8080/mobile-app-ws/users/jjdslkoalk/addresses
  @GetMapping(path = "/{id}/addresses",
      produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public List<AddressesRest> getUserAddresses(@PathVariable String id) {
    List<AddressesRest> returnValue = new ArrayList<>();

    List<AddressDto> addressesDto = addressService.getAddresses(id);

    if (addressesDto != null && !addressesDto.isEmpty()) {
      java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {
      }.getType();
      returnValue = new ModelMapper().map(addressesDto, listType);
    }
    return returnValue;
  }

  //http://localhost:8080/mobile-app-ws/users/jjdslkoalk/addresses/lskdmolkenfg
  @GetMapping(path = "/{id}/addresses/{addressId}",
      produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public AddressesRest getUserAddress(@PathVariable String addressId) {
    List<AddressesRest> returnValue = new ArrayList<>();

    AddressDto addressesDto = addressService.getAddress(addressId);

    ModelMapper modelMapper = new ModelMapper();
    return modelMapper.map(addressesDto, AddressesRest.class);
  }
}
