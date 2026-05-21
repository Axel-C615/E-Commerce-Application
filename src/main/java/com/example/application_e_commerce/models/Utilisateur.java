package com.example.application_e_commerce.models;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "L'email est requis")
    @Email(message = "Format email invalide")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Le nom est requis")
    @Column(nullable = false)
    private String nom;
    
    @NotBlank(message = "Le prénom est requis")
    @Column(nullable = false)
    private String prenom;
    
    @NotBlank(message = "Le mot de passe est requis")
    @Column(nullable = false)
    private String motDePasse;
    
    // CORRECTION : Ajout de @Builder.Default pour conserver la valeur par défaut
    @Builder.Default
    @Column(nullable = false)
    private String role = "USER";
    
    // CORRECTION : Ajout de @Builder.Default pour conserver la valeur par défaut
    @Builder.Default
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Commande> commandes = new ArrayList<>();
}