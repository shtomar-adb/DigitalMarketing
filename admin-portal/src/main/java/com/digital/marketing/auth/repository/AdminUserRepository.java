package com.digital.marketing.auth.repository;

import com.digital.marketing.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("adminUserRepository")
public interface AdminUserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
