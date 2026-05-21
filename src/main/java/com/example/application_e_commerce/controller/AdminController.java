package com.example.application_e_commerce.controller;

import com.example.application_e_commerce.models.Article;
import com.example.application_e_commerce.models.Commande;
import com.example.application_e_commerce.service.ArticleService;
import com.example.application_e_commerce.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private CommandeService commandeService;
    
    @Autowired
    private ArticleService articleService;
    
    /**
     * Dashboard admin
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", commandeService.getAdminStats());
        model.addAttribute("commandesRecentes", 
            commandeService.getAllCommandes().stream().limit(10).toList());
        return "admin/dashboard";
    }
    
    /**
     * Liste de toutes les commandes
     */
    @GetMapping("/commandes")
    public String listCommandes(Model model) {
        model.addAttribute("commandes", commandeService.getAllCommandes());
        return "admin/commandes";
    }
    
    /**
     * Détail d'une commande
     */
    @GetMapping("/commande/{id}")
    public String detailCommande(@PathVariable Long id, Model model) {
        Commande commande = commandeService.getCommandeById(id);
        model.addAttribute("commande", commande);
        return "admin/commande-detail";
    }
    
    /**
     * Modifier le statut d'une commande
     */
    @PostMapping("/commande/{id}/statut")
    public String updateStatutCommande(@PathVariable Long id, 
                                       @RequestParam String statut,
                                       RedirectAttributes redirectAttributes) {
        try {
            commandeService.updateStatutCommande(id, statut);
            redirectAttributes.addFlashAttribute("success", "Statut mis à jour !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/commande/" + id;
    }
    
    /**
     * Liste des articles (admin)
     */
    @GetMapping("/articles")
    public String listArticles(Model model) {
        model.addAttribute("articles", articleService.getAllArticles());
        return "admin/articles-list";
    }
    
    /**
     * Formulaire d'ajout d'article
     */
    @GetMapping("/article/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        return "admin/article-form";
    }
    
    /**
     * Formulaire de modification d'article
     */
    @GetMapping("/article/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("article", articleService.getArticleById(id));
        return "admin/article-form";
    }
    
    /**
     * Sauvegarder l'article (création ou modification)
     */
    @PostMapping("/article/save")
    public String saveArticle(@ModelAttribute Article article, 
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            if (article.getId() == null) {
                articleService.createArticleWithImage(article, imageFile);
                redirectAttributes.addFlashAttribute("success", "Article ajouté !");
            } else {
                articleService.updateArticleWithImage(article.getId(), article, imageFile);
                redirectAttributes.addFlashAttribute("success", "Article modifié !");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/articles";
    }
    
    /**
     * Supprimer un article
     */
    @GetMapping("/article/delete/{id}")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            articleService.deleteArticle(id);
            redirectAttributes.addFlashAttribute("success", "Article supprimé !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/articles";
    }
    
    /**
     * Modification rapide du stock (AJAX)
     */
    @PostMapping("/article/{id}/stock")
    @ResponseBody
    public String updateStock(@PathVariable Long id, @RequestParam int stock) {
        articleService.updateStockAdmin(id, stock);
        return "ok";
    }
}