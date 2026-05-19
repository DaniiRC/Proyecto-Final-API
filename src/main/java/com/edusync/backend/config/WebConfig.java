package com.edusync.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración Web MVC de la aplicación.
 * Gestiona y mapea la resolución de recursos estáticos del servidor (como imágenes subidas).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Mapea la ruta URL estática '/uploads/**' para resolverla directamente
     * a la carpeta física 'uploads/' en el directorio raíz del servidor.
     * Permite servir las fotos de perfil e imágenes del grupo de manera directa por HTTP.
     *
     * @param registry Registrador de manejadores de recursos de Spring.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Esto mapea la URL /uploads/** a la carpeta física del servidor
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); 
    }
}
