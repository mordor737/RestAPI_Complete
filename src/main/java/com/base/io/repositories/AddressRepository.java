package com.base.io.repositories;

import com.base.io.entity.AddressEntity;
import com.base.io.entity.UserEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

  List<AddressEntity> findAllByUserDetails(UserEntity userEntity);

  AddressEntity findByAddressesId(String addressId);

}
