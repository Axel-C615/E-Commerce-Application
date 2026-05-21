package com.example.application_e_commerce.controller;
import com.example.application_e_commerce.models.Utilisateur;
import com.example.application_e_commerce.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    @GetMapping("/inscription")
    public String showRegistrationForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "auth/inscription";
    }
    
    @PostMapping("/inscription")
    public String register(@ModelAttribute Utilisateur utilisateur, Model model) {
        try {
            utilisateurService.inscrire(utilisateur);
            return "redirect:/login?success";
        } catch (RuntimeException e) {
            model.addAttribute("erreur", e.getMessage());
            return "auth/inscription";
        }
    }
}
