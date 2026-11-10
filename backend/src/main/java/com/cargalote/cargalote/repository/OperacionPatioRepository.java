package com.cargalote.cargalote.repository;

import com.cargalote.cargalote.model.OperacionPatio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperacionPatioRepository extends JpaRepository<OperacionPatio, Long> {
    boolean existsByContenedor_Id(Long contenedorId);
    boolean existsByBloque_Id(Long bloqueId);
}
