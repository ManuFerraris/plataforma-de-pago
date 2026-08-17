package com.mipasarela;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PasarelaApplication {

        public static void main(String[] args) {
                SpringApplication.run(PasarelaApplication.class, args);
                System.out.println("El servidor de la Pasarela de Pagos está EN LÍNEA en el puerto 8080...");
        }
}