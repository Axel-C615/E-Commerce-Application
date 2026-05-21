package com.example.application_e_commerce.service;
import com.example.application_e_commerce.models.Article;
import com.example.application_e_commerce.repository.ArticleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private ImageService imageStorageService;
    
    // ========== Méthodes de lecture ==========
    
    @Transactional(readOnly = true)
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Article non trouvé avec id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Article> getArticlesByCategorie(String categorie) {
        return articleRepository.findByCategorie(categorie);
    }
    
    @Transactional(readOnly = true)
    public List<Article> searchArticles(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllArticles();
        }
        return articleRepository.findByNomContainingIgnoreCase(keyword);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return articleRepository.findAllCategories();
    }
    
    // ========== Méthodes d'écriture (sans image) ==========
    
    @Transactional
    public Article createArticle(Article article) {
        if (article.getStock() == null) {
            article.setStock(0);
        }
        if (article.getDisponible() == null) {
            article.setDisponible(article.getStock() > 0);
        }
        return articleRepository.save(article);
    }
    
    @Transactional
    public Article updateArticle(Long id, Article articleDetails) {
        Article article = getArticleById(id);
        article.setNom(articleDetails.getNom());
        article.setDescription(articleDetails.getDescription());
        article.setPrix(articleDetails.getPrix());
        article.setCategorie(articleDetails.getCategorie());
        article.setStock(articleDetails.getStock());
        article.setDisponible(articleDetails.getDisponible() != null ? articleDetails.getDisponible() : articleDetails.getStock() > 0);
        return articleRepository.save(article);
    }
    
    @Transactional
    public void deleteArticle(Long id) {
        Article article = getArticleById(id);
        // Supprimer l'image associée si elle existe
        if (article.getImageUrl() != null) {
            try {
                imageStorageService.deleteImage(article.getImageUrl());
            } catch (IOException e) {
                // Log l'erreur mais continue la suppression
                System.err.println("Erreur lors de la suppression de l'image: " + e.getMessage());
            }
        }
        articleRepository.deleteById(id);
    }
    
    // ========== Méthodes avec gestion d'image ==========
    
    /**
     * Crée un article avec image
     */
    @Transactional
    public Article createArticleWithImage(Article article, MultipartFile imageFile) throws IOException {
        // Validation de base
        if (article.getNom() == null || article.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom de l'article est requis");
        }
        
        if (article.getPrix() == null || article.getPrix().doubleValue() <= 0) {
            throw new RuntimeException("Le prix doit être supérieur à 0");
        }
        
        // Sauvegarde de l'image
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imageStorageService.saveImage(imageFile);
            article.setImageUrl(imageUrl);
            article.setImageName(imageFile.getOriginalFilename());
            article.setImageType(imageFile.getContentType());
        }
        
        // Définir la disponibilité
        article.setDisponible(article.getStock() > 0);
        
        return articleRepository.save(article);
    }
    
    /**
     * Met à jour un article avec nouvelle image
     */
    @Transactional
    public Article updateArticleWithImage(Long id, Article articleDetails, MultipartFile imageFile) throws IOException {
        Article article = getArticleById(id);
        
        // Mise à jour des champs
        article.setNom(articleDetails.getNom());
        article.setDescription(articleDetails.getDescription());
        article.setPrix(articleDetails.getPrix());
        article.setCategorie(articleDetails.getCategorie());
        article.setStock(articleDetails.getStock());
        article.setDisponible(articleDetails.getDisponible() != null ? articleDetails.getDisponible() : articleDetails.getStock() > 0);
        
        // Gestion de l'image
        if (imageFile != null && !imageFile.isEmpty()) {
            // Supprimer l'ancienne image
            if (article.getImageUrl() != null) {
                imageStorageService.deleteImage(article.getImageUrl());
            }
            // Sauvegarder la nouvelle
            String newImageUrl = imageStorageService.saveImage(imageFile);
            article.setImageUrl(newImageUrl);
            article.setImageName(imageFile.getOriginalFilename());
            article.setImageType(imageFile.getContentType());
        }
        
        return articleRepository.save(article);
    }
    
    /**
     * Supprime uniquement l'image d'un article
     */
    @Transactional
    public void deleteArticleImage(Long articleId) throws IOException {
        Article article = getArticleById(articleId);
        if (article.getImageUrl() != null) {
            imageStorageService.deleteImage(article.getImageUrl());
            article.setImageUrl(null);
            article.setImageName(null);
            article.setImageType(null);
            articleRepository.save(article);
        }
    }
    
    /**
     * Met à jour le stock d'un article
     */
    @Transactional
    public void updateStock(Long articleId, int quantite) {
        Article article = getArticleById(articleId);
        int newStock = article.getStock() - quantite;
        if (newStock < 0) {
            throw new RuntimeException("Stock insuffisant pour l'article: " + article.getNom());
        }
        article.setStock(newStock);
        article.setDisponible(newStock > 0);
        articleRepository.save(article);
    }

    // ========== MÉTHODES ADMINISTRATION ==========
    
    /**
     * Met à jour le stock d'un article (admin)
     */
    @Transactional
    public void updateStockAdmin(Long articleId, int nouveauStock) {
        Article article = getArticleById(articleId);
        article.setStock(nouveauStock);
        article.setDisponible(nouveauStock > 0);
        articleRepository.save(article);
    }
    
    /**
     * Met à jour le prix d'un article (admin)
     */
    @Transactional
    public void updatePriceAdmin(Long articleId, BigDecimal nouveauPrix) {
        Article article = getArticleById(articleId);
        article.setPrix(nouveauPrix);
        articleRepository.save(article);
    }
    
    /**
     * Met à jour la catégorie d'un article (admin)
     */
    @Transactional
    public void updateCategorieAdmin(Long articleId, String nouvelleCategorie) {
        Article article = getArticleById(articleId);
        article.setCategorie(nouvelleCategorie);
        articleRepository.save(article);
    }




    public Object findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }
}