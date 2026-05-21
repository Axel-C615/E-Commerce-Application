package com.example.application_e_commerce.service;

import com.example.application_e_commerce.models.Commande;
import com.example.application_e_commerce.models.LigneCommande;
import com.example.application_e_commerce.models.Utilisateur;
import com.example.application_e_commerce.repository.CommandeRepository;
import com.example.application_e_commerce.payment.PaymentContext;
import com.example.application_e_commerce.payment.PaymentDetails;
import com.example.application_e_commerce.payment.PaymentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CommandeService {
    
    @Autowired
    private CommandeRepository commandeRepository;
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private PaymentContext paymentContext;
    
    @Transactional
    public Commande creerCommande(Utilisateur utilisateur, List<LigneCommande> lignesCommande, 
                                  BigDecimal montantTotal, String modePaiement) {
        
        String numeroCommande = generateNumeroCommande();
        
        Commande commande = Commande.builder()
            .numeroCommande(numeroCommande)
            .utilisateur(utilisateur)
            .montantTotal(montantTotal)
            .modePaiement(modePaiement)
            .statut("EN_ATTENTE")
            .dateCommande(LocalDateTime.now())
            .build();
        
        commande = commandeRepository.save(commande);
        
        // Associer les lignes de commande
        for (LigneCommande ligne : lignesCommande) {
            ligne.setCommande(commande);
        }
        
        // CORRECTION : Utiliser le bon nom de méthode (camelCase)
        commande.setLignesCommande(lignesCommande);
        
        return commandeRepository.save(commande);
    }
    
    @Transactional
    public PaymentResult traiterPaiement(Commande commande, PaymentDetails details) {
        // Traitement du paiement
        PaymentResult paymentResult = paymentContext.executePayment(
            commande.getModePaiement(),
            commande.getMontantTotal(),
            "EUR",
            details
        );
        
        if (paymentResult.isSuccess()) {
            // Mise à jour de la commande
            commande.setStatut("PAYEE");
            commande.setTransactionId(paymentResult.getTransactionId());
            commandeRepository.save(commande);
            
            // CORRECTION : Utiliser le bon nom de méthode (camelCase)
            for (LigneCommande ligne : commande.getLignesCommande()) {
                articleService.updateStock(ligne.getArticle().getId(), ligne.getQuantite());
            }
        }
        
        return paymentResult;
    }
    
    @Transactional(readOnly = true)
    public Commande getCommandeByNumero(String numeroCommande) {
        return commandeRepository.findByNumeroCommande(numeroCommande);
    }
    
    @Transactional(readOnly = true)
    public List<Commande> getCommandesByUtilisateur(Utilisateur utilisateur) {
        return commandeRepository.findByUtilisateurOrderByDateCommandeDesc(utilisateur);
    }
    
    @Transactional
    public void annulerCommande(String numeroCommande) {
        Commande commande = getCommandeByNumero(numeroCommande);
        if ("PAYEE".equals(commande.getStatut())) {
            throw new RuntimeException("Impossible d'annuler une commande déjà payée");
        }
        commande.setStatut("ANNULEE");
        commandeRepository.save(commande);
    }
    
    private String generateNumeroCommande() {
        return "CMD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }


     // ========== MÉTHODES ADMINISTRATION ==========
    
    /**
     * Récupère TOUTES les commandes (admin)
     */
    @Transactional(readOnly = true)
    public List<Commande> getAllCommandes() {
        return commandeRepository.findAllByOrderByDateCommandeDesc();
    }

    @Transactional(readOnly = true)
public Commande getCommandeById(Long id) {
    return commandeRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Commande non trouvée avec id: " + id));
}
    
    /**
     * Récupère les commandes par statut (admin)
     */
    @Transactional(readOnly = true)
    public List<Commande> getCommandesByStatut(String statut) {
        return commandeRepository.findByStatut(statut);
    }
    
    /**
     * Récupère les commandes d'une période (admin)
     */
    @Transactional(readOnly = true)
    public List<Commande> getCommandesBetweenDates(LocalDateTime debut, LocalDateTime fin) {
        return commandeRepository.findByDateCommandeBetween(debut, fin);
    }
    
    /**
     * Met à jour le statut d'une commande (admin)
     */
    @Transactional
    public void updateStatutCommande(Long commandeId, String nouveauStatut) {
        Commande commande = commandeRepository.findById(commandeId)
            .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        commande.setStatut(nouveauStatut);
        commandeRepository.save(commande);
    }
    
    /**
     * Récupère les statistiques des commandes (admin)
     */
    @Transactional(readOnly = true)
    public AdminStats getAdminStats() {
        AdminStats stats = new AdminStats();
        stats.setTotalCommandes(commandeRepository.count());
        stats.setCommandesEnAttente(commandeRepository.countByStatut("EN_ATTENTE"));
        stats.setCommandesPayees(commandeRepository.countByStatut("PAYEE"));
        stats.setCommandesAnnulees(commandeRepository.countByStatut("ANNULEE"));
        
        BigDecimal chiffreAffaires = commandeRepository.sumMontantTotalByStatut("PAYEE");
        stats.setChiffreAffaires(chiffreAffaires != null ? chiffreAffaires : BigDecimal.ZERO);
        
        return stats;
    }
}