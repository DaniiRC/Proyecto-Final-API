package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.model.Grupo;
import com.agenda.backend_academico.model.Usuario;
import com.agenda.backend_academico.repository.GrupoRepository;
import com.agenda.backend_academico.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*") // Importante para evitar problemas de CORS con Android/Web
public class GrupoController {

	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	// ==========================================
	// 1. CREAR GRUPO
	// ==========================================
	@PostMapping("/crear")
	public ResponseEntity<Grupo> crearGrupo(@RequestParam String nombre,
			@RequestParam(required = false) String descripcion, @RequestParam(required = false) String color,
			@RequestParam Long profesorId) {
		Usuario profesor = usuarioRepository.findById(profesorId).orElse(null);
		if (profesor == null)
			return ResponseEntity.badRequest().build();

		Grupo grupo = new Grupo(nombre);
		grupo.setProfesor(profesor);
		grupo.setDescripcion(descripcion);

		// Guardamos el grupo en la base de datos
		Grupo grupoGuardado = grupoRepository.save(grupo);
		return ResponseEntity.ok(grupoGuardado);
	}

	// ==========================================
	// 2. UNIRSE A UN GRUPO (ALUMNOS)
	// ==========================================
	@PostMapping("/{codigo}/unirse")
	public ResponseEntity<?> unirseAGrupo(@PathVariable String codigo, @RequestParam Long usuarioId) {
		Grupo grupo = grupoRepository.findByCodigo(codigo).orElse(null);
		Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

		if (grupo == null || usuario == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Grupo o usuario no encontrado");
		}

		// Evitamos duplicados: solo lo añadimos si no está ya en la lista
		if (!grupo.getAlumnos().contains(usuario)) {
			grupo.getAlumnos().add(usuario);
			grupoRepository.save(grupo);
		}

		return ResponseEntity.ok(grupo);
	}

	// ==========================================
	// 3. SALIR DE UN GRUPO
	// ==========================================
	@PostMapping("/{grupoId}/salir")
	public ResponseEntity<?> salirDeGrupo(@PathVariable Long grupoId, @RequestParam Long usuarioId) {
		Grupo grupo = grupoRepository.findById(grupoId).orElse(null);
		Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

		if (grupo != null && usuario != null) {
			// Si el usuario está en la lista de alumnos, lo eliminamos
			if (grupo.getAlumnos().contains(usuario)) {
				grupo.getAlumnos().remove(usuario);
				grupoRepository.save(grupo);
			}
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}

	// ==========================================
	// 4. OBTENER GRUPOS (EXTRA RECOMENDADO)
	// ==========================================
	@GetMapping
	public ResponseEntity<List<Grupo>> obtenerTodos() {
		return ResponseEntity.ok(grupoRepository.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Grupo> obtenerPorId(@PathVariable Long id) {
		return grupoRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// ==========================================
	// 5. BUSCAR GRUPO POR CÓDIGO (NUEVO)
	// ==========================================
	@GetMapping("/codigo/{codigo}")
	public ResponseEntity<Grupo> buscarPorCodigo(@PathVariable String codigo) {
		return grupoRepository.findByCodigo(codigo).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	// ==========================================
	// 6. SUBIR FOTO DEL GRUPO (TU CÓDIGO ORIGINAL)
	// ==========================================
	@PostMapping("/{id}/foto")
	public ResponseEntity<Grupo> subirFoto(@PathVariable Long id, @RequestParam("foto") MultipartFile foto) {
		try {
			Grupo grupo = grupoRepository.findById(id).orElseThrow();

			// 1. Crear carpeta si no existe
			Path directorioImagenes = Paths.get("uploads");
			if (!Files.exists(directorioImagenes))
				Files.createDirectories(directorioImagenes);

			// 2. Guardar archivo con nombre único
			String nombreArchivo = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
			Path rutaCompleta = directorioImagenes.resolve(nombreArchivo);
			Files.copy(foto.getInputStream(), rutaCompleta);

			// 3. Guardar la RUTA en la base de datos
			grupo.setFotoUrl("uploads/" + nombreArchivo);
			grupoRepository.save(grupo);

			return ResponseEntity.ok(grupo);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	// ==========================================
    // 7. OBTENER LAS CLASES DE UN ALUMNO
    // ==========================================
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Grupo>> obtenerGruposDeUsuario(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario != null) {
            // Devuelve la lista de clases a las que se ha unido este alumno
            return ResponseEntity.ok(usuario.getGruposParticipados());
        }
        return ResponseEntity.notFound().build();
    }
}