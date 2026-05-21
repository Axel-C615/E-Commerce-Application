package com.example.application_e_commerce.repository;

import com.example.application_e_commerce.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    List<Article> findByCategorie(String categorie);
    
    List<Article> findByNomContainingIgnoreCase(String nom);
    
    @Query("SELECT DISTINCT a.categorie FROM Article a")
    List<String> findAllCategories();
    
    List<Article> findByDisponibleTrue();
    
    // Méthode corrigée - avec un paramètre seuil
    List<Article> findByStockLessThan(Integer stock);
}