package com.example.trabalho2_in.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import com.example.trabalho2_in.dtos.GameCalcularDto;
import com.example.trabalho2_in.dtos.GameRecomendacao;
import com.example.trabalho2_in.services.GameService;

@Controller
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("dto", new GameCalcularDto());
        return "games-form";
    }

    @PostMapping("/calcular")
    public String calcular(@ModelAttribute GameCalcularDto dto, Model model) {
        GameRecomendacao resultado = gameService.calcular(dto);
        model.addAttribute("resultado", resultado);
        return "games-resultado";
    }
}