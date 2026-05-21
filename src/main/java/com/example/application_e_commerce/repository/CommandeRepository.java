package com.example.application_e_commerce.repository;
import com.example.application_e_commerce.models.Commande;
import com.example.application_e_commerce.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    
    List<Commande> findByUtilisateurOrderByDateCommandeDesc(Utilisateur utilisateur);
    
    Commande findByNumeroCommande(String numeroCommande);
    
    List<Commande> findByStatutAndDateCommandeBefore(String statut, LocalDateTime date);


    // ========== MÉTHODES ADMIN ==========
    
    List<Commande> findAllByOrderByDateCommandeDesc();
    
    List<Commande> findByStatut(String statut);
    
    List<Commande> findByDateCommandeBetween(LocalDateTime debut, LocalDateTime fin);
    
    long countByStatut(String statut);
    
    @Query("SELECT SUM(c.montantTotal) FROM Commande c WHERE c.statut = 'PAYEE'")
    BigDecimal sumMontantTotalByStatut(String statut);
}