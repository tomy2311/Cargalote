package com.cargalote.cargalote.service;


import com.cargalote.cargalote.model.Bitacora;
import com.cargalote.cargalote.repository.BitacoraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BitacoraService {

    private final BitacoraRepository bitacoraRepository;

    public BitacoraService(BitacoraRepository bitacoraRepository) {
        this.bitacoraRepository = bitacoraRepository;
    }

    public List<Bitacora> listarBitacora() {
        return bitacoraRepository.findAll();
    }

    public Bitacora buscarPorId(Long id) {

        Optional<Bitacora> bitacora = bitacoraRepository.findById(id);

        return bitacora.orElse(null);
    }

    public Bitacora registrarBitacora(Bitacora bitacora) {
        return bitacoraRepository.save(bitacora);
    }

    public Bitacora actualizarBitacora(Long id, Bitacora nuevosDatos) {

        Bitacora bitacora = buscarPorId(id);

        if (bitacora == null) {
            return null;
        }

        bitacora.setFecha(nuevosDatos.getFecha());
        bitacora.setHora(nuevosDatos.getHora());
        bitacora.setDescripcion(nuevosDatos.getDescripcion());
        bitacora.setUsuario(nuevosDatos.getUsuario());

        return bitacoraRepository.save(bitacora);
    }

    public boolean eliminarBitacora(Long id) {

        Bitacora bitacora = buscarPorId(id);

        if (bitacora == null) {
            return false;
        }

        bitacoraRepository.deleteById(id);
        return true;
    }
}