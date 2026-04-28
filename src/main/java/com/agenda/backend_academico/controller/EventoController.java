package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.model.Evento;
import com.agenda.backend_academico.model.Usuario;
import com.agenda.backend_academico.repository.EventoRepository;
import com.agenda.backend_academico.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

	@Autowired
	private EventoRepository eventoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<List<Evento>> obtenerEventosUnificados(@PathVariable Long usuarioId) {

		Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
		if (usuario == null) {
			return ResponseEntity.badRequest().build();
		}

		// 1. Añadimos los eventos personales creados por el propio usuario
		List<Evento> listaUnificada = new ArrayList<>(eventoRepository.findByCreadorId(usuarioId));

		// 2. Recorremos TODAS las clases (grupos) a las que pertenece y añadimos sus eventos
				if (usuario.getGruposParticipados() != null) {
					for (com.agenda.backend_academico.model.Grupo grupo : usuario.getGruposParticipados()) {
		                // AHORA SÍ: Usamos el método que bucea de Evento -> Asignatura -> Grupo
						List<Evento> eventosDelGrupo = eventoRepository.findByAsignaturaGrupoId(grupo.getId());
						listaUnificada.addAll(eventosDelGrupo);
					}
				}

		return ResponseEntity.ok(listaUnificada);
	}

	@PostMapping("/crear")
	public ResponseEntity<Evento> crearEvento(@RequestBody Evento evento) {
		// Asegurar que si viene un grupo, JPA lo gestione correctamente
		if (evento.getGrupo() != null && evento.getGrupo().getId() != null) {
			// Opcional: Podrías recargar el objeto Grupo desde la BD para ser más seguro,
			// pero normalmente con el objeto que contiene el ID es suficiente para Spring Data JPA
		}

		// Vincular subtareas bidireccionalmente antes de guardar
		if (evento.getSubtareas() != null) {
			for (com.agenda.backend_academico.model.Subtarea sub : evento.getSubtareas()) {
				sub.setEvento(evento);
			}
		}

		Evento guardado = eventoRepository.save(evento);
		return ResponseEntity.ok(guardado);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {

		if (eventoRepository.existsById(id)) {
			eventoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}

		return ResponseEntity.notFound().build();
	}

	// ==========================================
	// 4. Actualizar un evento existente
	// ==========================================
	@PutMapping("/{id}")
	public ResponseEntity<Evento> actualizarEvento(@PathVariable Long id, @RequestBody Evento eventoActualizado) {
		return eventoRepository.findById(id).map(eventoExistente -> {

			eventoExistente.setTitulo(eventoActualizado.getTitulo());
			eventoExistente.setDescripcion(eventoActualizado.getDescripcion());
			eventoExistente.setHora(eventoActualizado.getHora());
			eventoExistente.setFecha(eventoActualizado.getFecha());
			eventoExistente.setTipo(eventoActualizado.getTipo());
			eventoExistente.setFocusMode(eventoActualizado.isFocusMode());

			// --- NUEVO: ACTUALIZAR SUBTAREAS ---
			if (eventoActualizado.getSubtareas() != null) {
				// Primero, vinculamos cada nueva subtarea con este evento
				for (com.agenda.backend_academico.model.Subtarea sub : eventoActualizado.getSubtareas()) {
					sub.setEvento(eventoExistente);
				}
				// Limpiamos la lista vieja y metemos las nuevas
				eventoExistente.getSubtareas().clear();
				eventoExistente.getSubtareas().addAll(eventoActualizado.getSubtareas());
			}
			// -----------------------------------

			Evento eventoGuardado = eventoRepository.save(eventoExistente);
			return ResponseEntity.ok(eventoGuardado);

		}).orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Evento> obtenerEventoPorId(@PathVariable Long id) {
		return eventoRepository.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	// [NUEVO] GET: Pedir TODOS los eventos de un Grupo (Para la pantalla
	// PerfilClaseActivity)
	@GetMapping("/grupo/{grupoId}")
	public ResponseEntity<List<Evento>> obtenerEventosDeGrupo(@PathVariable Long grupoId) {
		// Buscamos directamente por grupo_id en la tabla evento
		List<Evento> eventosDelGrupo = eventoRepository.findByGrupoId(grupoId);
		
        return ResponseEntity.ok(eventosDelGrupo);
	}
	
	@PostMapping("/{id}/completar")
	public ResponseEntity<?> completarEvento(@PathVariable Long id, @RequestParam Long usuarioId) {
	    return eventoRepository.findById(id).map(evento -> {
	        evento.setCompletado(true); // Asegúrate de que tu modelo Evento tenga este campo
	        eventoRepository.save(evento);
	        // Aquí podrías añadir lógica para sumar puntos al usuario si lo deseas
	        return ResponseEntity.ok().build();
	    }).orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping("/{id}/tiempo-invertido")
	public ResponseEntity<?> guardarTiempoInvertido(@PathVariable Long id, @RequestParam Long tiempoInvertido) {
	    return eventoRepository.findById(id).map(evento -> {
	        // IMPORTANTE: Aquí guardamos el tiempo que nos manda la App
	        evento.setTiempoInvertidoFocus(tiempoInvertido);
	        eventoRepository.save(evento);
	        return ResponseEntity.ok().build();
	    }).orElse(ResponseEntity.notFound().build());
	}
}