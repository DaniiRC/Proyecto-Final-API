package com.edusync.backend.component;

import com.edusync.backend.repository.CodigoVerificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class LimpiezaCodigosComponent {

    @Autowired
    private CodigoVerificacionRepository codigoVerificacionRepository;

    // Ejecutar cada hora: "0 0 * * * *"
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void limpiarCodigosAntiguos() {
        // Borrar códigos que tienen más de 1 hora de antigüedad
        LocalDateTime haceUnaHora = LocalDateTime.now().minusHours(1);
        codigoVerificacionRepository.deleteByFechaCreacionBefore(haceUnaHora);
        System.out.println("Limpieza de códigos antiguos completada en " + LocalDateTime.now());
    }
}
