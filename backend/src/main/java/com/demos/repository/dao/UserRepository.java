package com.demos.repository.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demos.repository.entity.User;

import jakarta.transaction.Transactional;


@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long>{

	Optional<User> findByEmail(String usernameEmail);
    
    Boolean existsByEmail(String email);


    /* @Modifying()
    @Query("update User u set u.firstname=:firstname, u.lastname=:lastname, u.country=:country where u.id = :id")
    void updateUser(@Param(value = "id") Integer id,   @Param(value = "firstname") String firstname, @Param(value = "lastname") String lastname , @Param(value = "country") String country);*/
    
}
