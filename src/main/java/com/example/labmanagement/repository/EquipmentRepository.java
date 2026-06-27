package com.example.labmanagement.repository;

import com.example.labmanagement.entity.Equipment;
import com.example.labmanagement.entity.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {
    boolean existsByEquipmentCode(String equipmentCode);

    Optional<Equipment> findByEquipmentCode(String equipmentCode);

    List<Equipment> findByStatus(EquipmentStatus status);
}
