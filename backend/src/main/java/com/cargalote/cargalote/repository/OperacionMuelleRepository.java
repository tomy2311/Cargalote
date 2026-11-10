package com.cargalote.cargalote.repository;

import com.cargalote.cargalote.model.OperacionMuelle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperacionMuelleRepository extends JpaRepository<OperacionMuelle, Long> {
    boolean existsByBuque_Id(Long buqueId);
    boolean existsByAtraque_Id(Long atraqueId);
}
