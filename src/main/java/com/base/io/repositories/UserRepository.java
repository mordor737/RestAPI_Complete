package com.base.io.repositories;

import com.base.io.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

  UserEntity findByEmail(String email);

  UserEntity findByUserId(String id);

  UserEntity findUserByEmailVerificationToken(String token);
}
