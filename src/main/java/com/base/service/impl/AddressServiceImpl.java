package com.base.service.impl;

import com.base.io.entity.AddressEntity;
import com.base.io.entity.UserEntity;
import com.base.io.repositories.AddressRepository;
import com.base.io.repositories.UserRepository;
import com.base.service.AddressService;
import com.base.shared.dto.AddressDto;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  AddressRepository addressRepository;

  @Override
  public List<AddressDto> getAddresses(String userId) {
    List<AddressDto> returnValue = new ArrayList();
    ModelMapper modelMapper = new ModelMapper();

    UserEntity userEntity = userRepository.findByUserId(userId);
    if (userEntity == null) {
      return returnValue;
    }

    Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
    for (AddressEntity addressEntity : addresses) {
      returnValue.add(modelMapper.map(addressEntity, AddressDto.class));
    }
    return returnValue;
  }

  @Override
  public AddressDto getAddress(String addressId) {
    AddressDto returnValue = null;
    AddressEntity addressEntity = addressRepository.findByAddressesId(addressId);
    if (addressEntity != null) {
      returnValue = new ModelMapper().map(addressEntity, AddressDto.class);
    }
    return returnValue;
  }
}
