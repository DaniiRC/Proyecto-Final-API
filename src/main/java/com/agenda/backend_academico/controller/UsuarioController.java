package com.agenda.backend_academico.controller;

import com.agenda.backend_academico.model.LoginResponseDTO;
import com.agenda.backend_academico.model.Usuario;
import com.agenda.backend_academico.model.Evento;
import com.agenda.backend_academico.model.Grupo;
import com.agenda.backend_academico.repository.UsuarioRepository;
import com.agenda.backend_academico.repository.EventoRepository;
import com.agenda.backend_academico.repository.GrupoRepository;
import com.agenda.backend_academico.security.JwtUtils;
import com.agenda.backend_academico.service.CodigoVerificacionService;
import com.agenda.backend_academico.service.FileStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Controlador REST que gestiona las operaciones relacionadas con los usuarios.
 * Incluye autenticación, registro, recuperación de contraseña y gestión de cuentas.
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CodigoVerificacionService codigoVerificacionService;

    @jakarta.annotation.PostConstruct
    public void initAdmin() {
        String emailAdmin = "druicoc0204@g.educaand.es";
        Usuario admin = usuarioRepository.findByEmail(emailAdmin);
        
        if (admin == null) {
            admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail(emailAdmin);
            System.out.println("DEBUG: Creando nuevo usuario administrador.");
        }
        
        // Siempre aseguramos que tenga esta password y el rol ADMIN
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRol("ADMIN");
        usuarioRepository.save(admin);
        System.out.println("DEBUG: Usuario administrador (" + emailAdmin + ") actualizado/creado correctamente con password 'admin'.");
    }

    /**
     * Autentica a un usuario mediante correo electrónico y contraseña.
     * Genera y devuelve un token JWT si las credenciales son válidas.
     *
     * @param credenciales Objeto Usuario que contiene el email y la contraseña.
     * @return ResponseEntity con LoginResponseDTO y estado 200 si es exitoso, o estado 401 si falla.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody Usuario credenciales) {
        System.out.println("DEBUG LOGIN: Intentando login con email: [" + credenciales.getEmail() + "]");
        Usuario usuario = usuarioRepository.findByEmail(credenciales.getEmail());
        
        if (usuario != null) {
            boolean matches = passwordEncoder.matches(credenciales.getPassword(), usuario.getPassword());
            System.out.println("DEBUG LOGIN: Usuario encontrado. ¿Password coincide?: " + matches);
            
            if (matches) {
                String token = jwtUtils.generateToken(usuario.getEmail());
                LoginResponseDTO dto = new LoginResponseDTO(
                        token,
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getEmail(),
                        usuario.getFotoUrl(),
                        usuario.getRol()
                );
                return ResponseEntity.ok(dto);
            }
        } else {
            System.out.println("DEBUG LOGIN: Usuario NO encontrado en la base de datos.");
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Encripta la contraseña con BCrypt antes de persistirla en la base de datos.
     *
     * @param usuario Datos del nuevo usuario a registrar.
     * @return ResponseEntity con LoginResponseDTO (incluye token) si es exitoso, o 400 si el email ya existe.
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            return ResponseEntity.badRequest().body("El email ya está en uso");
        }
        
        if (!esPasswordSegura(usuario.getPassword())) {
            return ResponseEntity.badRequest().body("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número");
        }

        // Hashear la contraseña antes de persistir
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        String token = jwtUtils.generateToken(nuevoUsuario.getEmail());
        LoginResponseDTO dto = new LoginResponseDTO(
                token,
                nuevoUsuario.getId(),
                nuevoUsuario.getNombre(),
                nuevoUsuario.getEmail(),
                nuevoUsuario.getFotoUrl(),
                nuevoUsuario.getRol()
        );
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Sube y actualiza la foto de perfil de un usuario específico.
     *
     * @param id Identificador único del usuario.
     * @param file Archivo de imagen multiparte.
     * @return ResponseEntity con la entidad Usuario actualizada.
     */
    @PostMapping("/{id}/foto")
    public ResponseEntity<Usuario> subirFoto(@PathVariable Long id, @RequestParam("foto") MultipartFile file) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String fileName = fileStorageService.save(file);
        String urlFoto = "/uploads/" + fileName;
        
        usuario.setFotoUrl(urlFoto);
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok(usuario);
    }

    /**
     * Fase 1 de recuperación de contraseña: Envía un código OTP al correo electrónico asociado.
     *
     * @param email Correo electrónico del usuario que solicita la recuperación.
     * @return ResponseEntity 200 OK si el correo se envía correctamente, o 404 si el usuario no existe.
     */
    @PostMapping("/enviar-codigo")
    public ResponseEntity<Void> enviarCodigo(@RequestParam String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        
        if (usuario != null) {
            try {
                String codigo = codigoVerificacionService.guardarCodigo(email);

                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("ruizcocera.daniel@loscerros.org");
                message.setTo(email);
                message.setSubject("!!! ATENCIÓN: CÓDIGO DE SEGURIDAD !!!");
                message.setText("Hola " + usuario.getNombre() + ",\n\n" +
                        "Tu código de verificación para restablecer la contraseña es: " + codigo + "\n\n" +
                        "Introduce este código en la aplicación para continuar.");
                
                mailSender.send(message);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Fase 2 de recuperación de contraseña: Verifica el código OTP y actualiza la contraseña.
     *
     * @param email Correo electrónico del usuario.
     * @param codigo Código de verificación temporal de 6 dígitos.
     * @param nuevaPassword Nueva contraseña en texto plano (será encriptada internamente).
     * @return ResponseEntity 200 OK si el cambio es exitoso, o 401 si el código es inválido o ha expirado.
     */
    @PostMapping("/verificar-y-cambiar")
    public ResponseEntity<Void> verificarYCambiar(
            @RequestParam String email,
            @RequestParam String codigo,
            @RequestParam String nuevaPassword) {

        if (!esPasswordSegura(nuevaPassword)) {
            return ResponseEntity.badRequest().build();
        }

        boolean esValido = codigoVerificacionService.verificarCodigo(email, codigo);

        if (esValido) {
            Usuario usuario = usuarioRepository.findByEmail(email);
            if (usuario != null) {
                // Hashear la nueva contraseña antes de guardarla
                usuario.setPassword(passwordEncoder.encode(nuevaPassword));
                usuarioRepository.save(usuario);
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.status(401).build(); // Código incorrecto o expirado
    }

    /**
     * Verifica si una contraseña cumple con los requisitos mínimos de seguridad:
     * - Al menos 8 caracteres.
     * - Al menos una letra mayúscula.
     * - Al menos una letra minúscula.
     * - Al menos un número.
     *
     * @param password Contraseña a validar.
     * @return Verdadero si cumple los requisitos, falso en caso contrario.
     */
    private boolean esPasswordSegura(String password) {
        if (password == null) return false;
        // Regex: Al menos 8 caracteres, una mayúscula, una minúscula y un número
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(pattern);
    }

    /**
     * Elimina permanentemente la cuenta de un usuario y gestiona el borrado en cascada.
     * Resuelve dependencias de grupos (profesor/alumno) y elimina eventos personales huérfanos.
     *
     * @param id Identificador único del usuario a eliminar.
     * @return ResponseEntity 204 No Content si la eliminación es exitosa, o 500 en caso de error interno.
     */
    @PostMapping("/{id}/eliminar")
    @Transactional
    public ResponseEntity<?> eliminarUsuario(@PathVariable("id") Long id) {
        System.out.println("DEBUG: Iniciando eliminación total del usuario ID: " + id);
        try {
            Usuario usuario = usuarioRepository.findById(id).orElse(null);
            if (usuario == null) return ResponseEntity.notFound().build();

            // 1. TRATAMIENTO DE GRUPOS Y SUS EVENTOS
            List<Grupo> gruposProfesor = grupoRepository.findByProfesorId(id);
            List<Grupo> gruposAlumno = grupoRepository.findByAlumnosId(id);
            Set<Grupo> todosGrupos = new HashSet<>();
            todosGrupos.addAll(gruposProfesor);
            todosGrupos.addAll(gruposAlumno);

            for (Grupo grupo : todosGrupos) {
                boolean esProfesor = grupo.getProfesor().getId().equals(id);
                
                // Quitamos al usuario de la lista de alumnos
                if (grupo.getAlumnos() != null) {
                    grupo.getAlumnos().removeIf(a -> a.getId().equals(id));
                }

                if (esProfesor) {
                    if (grupo.getAlumnos() == null || grupo.getAlumnos().isEmpty()) {
                        // PROFESOR SOLO: Borrar primero todos los eventos del grupo
                        // Esto soluciona el error TransientPropertyValueException
                        List<Evento> eventosDelGrupo = eventoRepository.findByGrupoId(grupo.getId());
                        eventoRepository.deleteAll(eventosDelGrupo);
                        
                        // Ahora sí podemos borrar el grupo (borrará asignaturas en cascada)
                        grupoRepository.delete(grupo);
                    } else {
                        // HAY MÁS GENTE: Reasignar profesor y sus eventos
                        Usuario nuevoProfesor = grupo.getAlumnos().get(0);
                        grupo.setProfesor(nuevoProfesor);
                        eventoRepository.reasignarEventosDeGrupo(id, nuevoProfesor.getId());
                        grupoRepository.save(grupo);
                    }
                } else {
                    // ERES ALUMNO: Reasignar tus eventos de grupo al profesor actual
                    eventoRepository.reasignarEventosDeGrupo(id, grupo.getProfesor().getId());
                    grupoRepository.save(grupo);
                }
            }
            eventoRepository.flush();

            // 2. EVENTOS PERSONALES (Los que no tienen grupo)
            List<Evento> eventosRestantes = eventoRepository.findByCreadorId(id);
            for (Evento e : eventosRestantes) {
                if (e.getGrupo() == null) {
                    eventoRepository.delete(e);
                }
            }
            eventoRepository.flush();

            // 3. BORRADO FINAL DEL USUARIO
            usuarioRepository.delete(usuario);
            usuarioRepository.flush();
            
            System.out.println("DEBUG: Usuario " + id + " eliminado con éxito.");
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error técnico: " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los usuarios registrados (Solo ADMIN).
     */
    @GetMapping("/todos")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    /**
     * Actualiza un usuario (Edición desde el panel admin).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario datos) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) return ResponseEntity.notFound().build();
        
        usuario.setNombre(datos.getNombre());
        usuario.setEmail(datos.getEmail());
        if (datos.getPassword() != null && !datos.getPassword().isEmpty() && !datos.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
        }
        usuario.setRol(datos.getRol());
        
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }
}