package com.example.application_e_commerce.controller;
import com.example.application_e_commerce.models.Article;
import com.example.application_e_commerce.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    // ========== Méthodes publiques (visiteurs) ==========
    
    /**
     * Affiche la liste de tous les articles
     */
    @GetMapping
    public String listArticles(Model model) {
        List<Article> articles = articleService.getAllArticles();
        List<String> categories = articleService.getAllCategories();
        
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categories);
        return "articles/liste";
    }
    
    /**
     * Affiche les articles par catégorie
     */
    @GetMapping("/categorie/{categorie}")
    public String articlesByCategorie(@PathVariable String categorie, Model model) {
        List<Article> articles = articleService.getArticlesByCategorie(categorie);
        List<String> categories = articleService.getAllCategories();
        
        model.addAttribute("articles", articles);
        model.addAttribute("categorieActuelle", categorie);
        model.addAttribute("categories", categories);
        return "articles/liste";
    }
    
    /**
     * Recherche d'articles par mot-clé
     */
    @GetMapping("/recherche")
    public String rechercherArticles(@RequestParam String keyword, Model model) {
        List<Article> articles = articleService.searchArticles(keyword);
        List<String> categories = articleService.getAllCategories();
        
        model.addAttribute("articles", articles);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categories);
        return "articles/liste";
    }
    
    /**
     * Affiche les détails d'un article
     */
    @GetMapping("/{id}")
    public String voirArticle(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        model.addAttribute("article", article);
        return "articles/details";
    }
    
    // ========== Méthodes Admin (gestion des articles) ==========
    
    /**
     * Affiche le formulaire de création d'un nouvel article
     */
    @GetMapping("/admin/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("isEdit", false);
        return "admin/article-form";
    }
    
    /**
     * Crée un nouvel article avec image
     */
    @PostMapping("/admin/save")
    public String createArticle(@ModelAttribute Article article, 
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                articleService.createArticleWithImage(article, imageFile);
            } else {
                articleService.createArticle(article);
            }
            redirectAttributes.addFlashAttribute("success", "Article créé avec succès !");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du téléchargement de l'image: " + e.getMessage());
            return "redirect:/articles/admin/nouveau";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création: " + e.getMessage());
            return "redirect:/articles/admin/nouveau";
        }
        return "redirect:/articles";
    }
    
    /**
     * Affiche le formulaire d'édition d'un article
     */
    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Article article = articleService.getArticleById(id);
            model.addAttribute("article", article);
            model.addAttribute("isEdit", true);
            return "admin/article-form";
        } catch (Exception e) {
            return "redirect:/articles?error=Article non trouvé";
        }
    }
    
    /**
     * Met à jour un article existant
     */
    @PostMapping("/admin/update/{id}")
    public String updateArticle(@PathVariable Long id, 
                                @ModelAttribute Article article,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                articleService.updateArticleWithImage(id, article, imageFile);
            } else {
                articleService.updateArticle(id, article);
            }
            redirectAttributes.addFlashAttribute("success", "Article mis à jour avec succès !");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de l'image: " + e.getMessage());
            return "redirect:/articles/admin/edit/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
            return "redirect:/articles/admin/edit/" + id;
        }
        return "redirect:/articles";
    }
    
    /**
     * Supprime un article
     */
    @GetMapping("/admin/delete/{id}")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            articleService.deleteArticle(id);
            redirectAttributes.addFlashAttribute("success", "Article supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/articles";
    }
    
    /**
     * Supprime uniquement l'image d'un article
     */
    @GetMapping("/admin/delete-image/{id}")
    public String deleteImage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            articleService.deleteArticleImage(id);
            redirectAttributes.addFlashAttribute("success", "Image supprimée avec succès !");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de l'image: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/articles/admin/edit/" + id;
    }
}