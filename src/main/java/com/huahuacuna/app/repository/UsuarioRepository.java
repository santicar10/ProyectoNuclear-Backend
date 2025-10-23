package com.huahuacuna.app.repository;

import com.huahuacuna.app.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 *
 * <p>
 * Extiende {@link JpaRepository} para proporcionar operaciones CRUD básicas y
 * permite la definición de consultas derivadas por nombre de método.
 * </p>
 *
 * Uso:
 * - Inyectar con @Autowired o por constructor en servicios para acceder a la
 *   persistencia de {@code Usuario}.
 *
 * Seguridad / Consideraciones:
 * - Las consultas derivadas aquí son simples y no exponen datos sensibles por sí mismas.
 * - Asegurarse de que los campos indexados/únicos en la BD coincidan con las anotaciones
 *   (por ejemplo, {@code correo} marcado como unique en la entidad).
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param correo correo del usuario a buscar
     * @return Optional con el {@link Usuario} si existe, empty si no existe
     */
    Optional<Usuario> findByCorreo(String correo);

    /**
     * Comprueba si existe un usuario con el correo proporcionado.
     *
     * @param correo correo a comprobar
     * @return true si existe al menos un usuario con ese correo, false en caso contrario
     */
    boolean existsByCorreo(String correo);

}

