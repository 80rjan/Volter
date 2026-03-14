package com.volter.backend.staff;


import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    boolean existsById(Long id);
    boolean existsByUsername(String username);
}
