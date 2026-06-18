package com.volter.platform.modules.shop.domain.repository;

import com.volter.platform.modules.shop.domain.model.StaffShop;
import com.volter.platform.modules.shop.domain.model.enums.StaffShopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StaffShopRepository extends JpaRepository<StaffShop, Long> {

    @Query("SELECT ss FROM StaffShop ss JOIN FETCH ss.shop WHERE ss.staffId = :staffId AND ss.status = :status")
    List<StaffShop> findAllByStaffIdAndStatus(Long staffId, StaffShopStatus status);

    // open-in-view is disabled; the assignment response (mapped in the controller)
    // needs the shop name, so fetch the shop here.
    @Query("SELECT ss FROM StaffShop ss JOIN FETCH ss.shop WHERE ss.shop.id = :shopId")
    List<StaffShop> findAllByShop_Id(Long shopId);

    Optional<StaffShop> findByStaffIdAndShop_Id(Long staffId, Long shopId);

    boolean existsByStaffIdAndShop_IdAndStatus(Long staffId, Long shopId, StaffShopStatus status);
}
