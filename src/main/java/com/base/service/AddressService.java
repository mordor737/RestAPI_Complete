package com.base.service;

import com.base.shared.dto.AddressDto;
import java.util.List;

public interface AddressService {

  List<AddressDto> getAddresses(String user);

  AddressDto getAddress(String addressId);
}
