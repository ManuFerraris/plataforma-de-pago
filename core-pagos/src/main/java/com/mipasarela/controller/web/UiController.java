package com.mipasarela.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Para devolver paginas web (HTML) al cliente
public class UiController {
    @GetMapping("/")
    public String showCheckoutPage(Model model) {
        return "checkout";
    }
}
