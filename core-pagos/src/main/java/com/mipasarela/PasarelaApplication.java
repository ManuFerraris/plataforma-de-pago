package com.mipasarela;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync //(Spring ahora puede ejecutar procesos en segundo plano).
@Slf4j
public class PasarelaApplication {

        public static void main(String[] args) {
                SpringApplication.run(PasarelaApplication.class, args);
                log.info("El servidor de la Pasarela de Pagos esta en linea en el puerto 8080...");
        }
}