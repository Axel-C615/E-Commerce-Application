package com.example.application_e_commerce.service;
import com.example.application_e_commerce.models.Article;
import com.example.application_e_commerce.models.LigneCommande;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SessionScope
public class PanierService {
    
    @Autowired
    private ArticleService articleService;
    
    private Map<Long, PanierItem> items = new HashMap<>();
    
    public void ajouterArticle(Long articleId, int quantite) {
        Article article = articleService.getArticleById(articleId);
        
        if (article.getStock() < quantite) {
            throw new RuntimeException("Stock insuffisant");
        }
        
        if (items.containsKey(articleId)) {
            PanierItem item = items.get(articleId);
            item.setQuantite(item.getQuantite() + quantite);
        } else {
            items.put(articleId, new PanierItem(article, quantite));
        }
    }
    
    public void modifierQuantite(Long articleId, int quantite) {
        if (quantite <= 0) {
            supprimerArticle(articleId);
        } else {
            Article article = articleService.getArticleById(articleId);
            if (article.getStock() < quantite) {
                throw new RuntimeException("Stock insuffisant");
            }
            items.get(articleId).setQuantite(quantite);
        }
    }
    
    public void supprimerArticle(Long articleId) {
        items.remove(articleId);
    }
    
    public void viderPanier() {
        items.clear();
    }
    
    public List<PanierItem> getItems() {
        return new ArrayList<>(items.values());
    }
    
    public BigDecimal getTotal() {
        return items.values().stream()
            .map(item -> item.getSousTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int getNombreArticles() {
        return items.values().stream()
            .mapToInt(PanierItem::getQuantite)
            .sum();
    }
    
    public List<LigneCommande> convertirEnLignesCommande() {
        List<LigneCommande> lignes = new ArrayList<>();
        for (PanierItem item : items.values()) {
            LigneCommande ligne = LigneCommande.builder()
                .article(item.getArticle())
                .quantite(item.getQuantite())
                .prixUnitaire(item.getArticle().getPrix())
                .sousTotal(item.getSousTotal())
                .build();
            lignes.add(ligne);
        }
        return lignes;
    }
    
    // Classe interne pour les items du panier
    public static class PanierItem {
        private Article article;
        private int quantite;
        
        public PanierItem(Article article, int quantite) {
            this.article = article;
            this.quantite = quantite;
        }
        
        public Article getArticle() { return article; }
        public int getQuantite() { return quantite; }
        public void setQuantite(int quantite) { this.quantite = quantite; }
        public BigDecimal getSousTotal() { 
            return article.getPrix().multiply(BigDecimal.valueOf(quantite)); 
        }
    }
}
