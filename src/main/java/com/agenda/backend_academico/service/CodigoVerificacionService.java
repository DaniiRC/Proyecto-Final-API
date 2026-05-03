package com.agenda.backend_academico.service;

import com.agenda.backend_academico.model.CodigoVerificacion;
import com.agenda.backend_academico.repository.CodigoVerificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CodigoVerificacionService {

    @Autowired
    private CodigoVerificacionRepository codigoVerificacionRepository;

    @Transactional
    public String guardarCodigo(String email) {
        // Invalidar códigos anteriores del mismo email (marcarlos como usados)
        List<CodigoVerificacion> codigosAnteriores = codigoVerificacionRepository.findByEmailAndUsadoFalse(email);
        for (CodigoVerificacion cod : codigosAnteriores) {
            cod.setUsado(true);
        }
        codigoVerificacionRepository.saveAll(codigosAnteriores);

        // Generar código de 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(999999));
        
        // Guardar el nuevo
        CodigoVerificacion nuevoCodigo = new CodigoVerificacion(email, codigo);
        codigoVerificacionRepository.save(nuevoCodigo);

        return codigo;
    }

    @Transactional
    public boolean verificarCodigo(String email, String codigo) {
        Optional<CodigoVerificacion> optCodigo = codigoVerificacionRepository.findByEmailAndCodigoAndUsadoFalse(email, codigo);
        
        if (optCodigo.isPresent()) {
            CodigoVerificacion codigoEntity = optCodigo.get();
            
            // Comprobar que han pasado menos de 15 minutos
            Duration duracion = Duration.between(codigoEntity.getFechaCreacion(), LocalDateTime.now());
            if (duracion.toMinutes() <= 15) {
                // Es válido, marcar como usado
                codigoEntity.setUsado(true);
                codigoVerificacionRepository.save(codigoEntity);
                return true;
            } else {
                // Caducó, lo marcamos también como usado para que no moleste
                codigoEntity.setUsado(true);
                codigoVerificacionRepository.save(codigoEntity);
            }
        }
        
        return false;
    }
}
