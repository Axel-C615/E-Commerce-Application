package com.example.application_e_commerce.models;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String numeroCommande;
    
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
    
    // CORRECTION : Ajout de @Builder.Default et renommage du champ pour respecter conventions Java
    @Builder.Default
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LigneCommande> lignesCommande = new ArrayList<>();
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;
    
    @Column(nullable = false)
    private String modePaiement; // "CARTE_BANCAIRE" ou "PAYPAL"
    
    @Column(nullable = false)
    private String statut; // "EN_ATTENTE", "PAYEE", "ANNULEE"
    
    @Column(nullable = false)
    private LocalDateTime dateCommande;
    
    private String transactionId;
}