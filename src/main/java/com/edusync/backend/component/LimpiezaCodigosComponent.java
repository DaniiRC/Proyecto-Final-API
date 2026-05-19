package com.edusync.backend.component;

import com.edusync.backend.repository.CodigoVerificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Componente programado para la administración automática del sistema.
 * Se encarga de purgar periódicamente códigos de verificación obsoletos de la base de datos.
 */
@Component
public class LimpiezaCodigosComponent {

    @Autowired
    private CodigoVerificacionRepository codigoVerificacionRepository;

    /**
     * Tarea programada (Scheduler) que se ejecuta al inicio de cada hora.
     * Elimina todos los registros de códigos de verificación OTP con más de una hora de antigüedad.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void limpiarCodigosAntiguos() {
        // Borrar códigos que tienen más de 1 hora de antigüedad
        LocalDateTime haceUnaHora = LocalDateTime.now().minusHours(1);
        codigoVerificacionRepository.deleteByFechaCreacionBefore(haceUnaHora);
        System.out.println("Limpieza de códigos antiguos completada en " + LocalDateTime.now());
    }
}
