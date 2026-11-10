package com.cargalote.cargalote.repository;

import com.cargalote.cargalote.model.Buque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuqueRepository extends JpaRepository<Buque, Long> {
    boolean existsByImoIgnoreCase(String imo);
}
