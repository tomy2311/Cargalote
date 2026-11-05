package com.cargalote.cargalote.repository;

import com.cargalote.cargalote.model.Carga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargaRepository extends JpaRepository<Carga, Long> {
}