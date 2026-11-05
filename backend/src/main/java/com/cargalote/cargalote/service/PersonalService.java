package com.cargalote.cargalote.service;


import com.cargalote.cargalote.model.Personal;
import com.cargalote.cargalote.repository.PersonalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalService {

    private final PersonalRepository personalRepository;

    public PersonalService(PersonalRepository personalRepository) {
        this.personalRepository = personalRepository;
    }

    public List<Personal> listarPersonal() {
        return personalRepository.findAll();
    }

    public Personal buscarPorId(Long id) {

        Optional<Personal> persona = personalRepository.findById(id);

        return persona.orElse(null);
    }

    public Personal registrarPersonal(Personal persona) {
        return personalRepository.save(persona);
    }

    public Personal actualizarPersonal(Long id, Personal nuevosDatos) {

        Personal persona = buscarPorId(id);

        if (persona == null) {
            return null;
        }

        persona.setNombre(nuevosDatos.getNombre());
        persona.setRol(nuevosDatos.getRol());
        persona.setTurno(nuevosDatos.getTurno());
        persona.setLugar(nuevosDatos.getLugar());

        return personalRepository.save(persona);
    }

    public boolean eliminarPersonal(Long id) {

        Personal persona = buscarPorId(id);

        if (persona == null) {
            return false;
        }

        personalRepository.deleteById(id);
        return true;
    }
}