package com.example.application_e_commerce.controller;
import com.example.application_e_commerce.service.PanierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/panier")
public class PanierController {
    
    @Autowired
    private PanierService panierService;
    
    @GetMapping
    public String voirPanier(Model model) {
        model.addAttribute("items", panierService.getItems());
        model.addAttribute("total", panierService.getTotal());
        model.addAttribute("nombreArticles", panierService.getNombreArticles());
        return "panier/panier";
    }
    
    @PostMapping("/ajouter/{articleId}")
    public String ajouterAuPanier(@PathVariable Long articleId, 
                                  @RequestParam(defaultValue = "1") int quantite) {
        panierService.ajouterArticle(articleId, quantite);
        return "redirect:/panier";
    }
    
    @PostMapping("/modifier/{articleId}")
    public String modifierQuantite(@PathVariable Long articleId, @RequestParam int quantite) {
        panierService.modifierQuantite(articleId, quantite);
        return "redirect:/panier";
    }
    
    @GetMapping("/supprimer/{articleId}")
    public String supprimerArticle(@PathVariable Long articleId) {
        panierService.supprimerArticle(articleId);
        return "redirect:/panier";
    }
    
    @GetMapping("/vider")
    public String viderPanier() {
        panierService.viderPanier();
        return "redirect:/panier";
    }
}
