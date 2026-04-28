package com.agenda.backend_academico.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;

/**
 * Esta clase se encarga de configurar y establecer manualmente la conexión 
 * a la base de datos MySQL. Utiliza las variables de entorno del sistema operativo 
 * o del servidor de despliegue para crear la ruta de enlace de manera segura.
 */
@Configuration
public class ConfiguracionBaseDatos {

    @Bean
    public DataSource origenDatos() {
        String servidor = System.getenv("DB_SERVIDOR");
        String puerto = System.getenv("DB_PUERTO");
        String baseDatos = System.getenv("DB_BASE_DATOS");
        String usuario = System.getenv("DB_USUARIO");
        String contrasena = System.getenv("DB_CONTRASENA");
        
        String rutaConexion = "jdbc:mysql://" + servidor + ":" + puerto + "/" + baseDatos + "?useSSL=false&serverTimezone=UTC";

        return DataSourceBuilder.create()
                .url(rutaConexion)
                .username(usuario)
                .password(contrasena)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
}