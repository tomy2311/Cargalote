package com.cargalote.cargalote.repository;

import com.cargalote.cargalote.model.Personal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalRepository extends JpaRepository<Personal, Long> {
}