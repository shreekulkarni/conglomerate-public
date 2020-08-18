package com.conglomerate.dev.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class DocsController {
    @GetMapping("/docs")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/swagger-ui.html");
    }
}
