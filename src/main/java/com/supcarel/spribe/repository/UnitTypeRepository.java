package com.supcarel.spribe.repository;

import com.supcarel.spribe.model.UnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitTypeRepository extends JpaRepository<UnitType, Integer> {
    List<UnitType> findAllByOrderByNameAsc();
}