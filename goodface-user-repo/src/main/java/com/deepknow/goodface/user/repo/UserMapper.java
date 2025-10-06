package com.deepknow.goodface.user.repo;

import com.deepknow.goodface.user.repo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByPhone(@Param("phone") String phone);
    void save(User user);
    User findById(@Param("id") Long id);
}