package com.base.controller;

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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users") // http://localhost:8080/mobile-app-ws/users
public class UserController {

    UserService userService;
    AddressService addressService;

    UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

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
        UserRest returnValue;

        /*UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);*/

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
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
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
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDto> addressesDto = addressService.getAddresses(id);

        if (addressesDto != null && !addressesDto.isEmpty()) {
            java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            returnValue = new ModelMapper().map(addressesDto, listType);

            for (AddressesRest addressRest : returnValue) {
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(id, addressRest.getAddressesId()))
                        .withSelfRel();
                addressRest.add(selfLink);
            }
        }

        //https://localhost:8080/users/<id>
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
        //https://localhost:8080/users/<id>/addresses/<addressId>
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                .getUserAddresses(id))
                .withSelfRel();
        return CollectionModel.of(returnValue, userLink, selfLink);
    }

    //http://localhost:8080/mobile-app-ws/users/jjdslkoalk/addresses/lskdmolkenfg
    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        AddressDto addressesDto = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressesRest returnValue = modelMapper.map(addressesDto, AddressesRest.class);

        //https://localhost:8080/users/<id>
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        //https://localhost:8080/users/<id>/addresses
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
                //.slash(id)
                //.slash("addresses")
                .withRel("addresses");
        //https://localhost:8080/users/<id>/addresses/<addressId>
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
                //.slash(id)
                //.slash("addresses")
                //.slash(addressId)
                .withSelfRel();

        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
    }

    //http://localhost:8080/mobile-app-ws/users/email-verification?token=wvrsmg
    @GetMapping(path = "/email-verification", produces = {MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }
}
