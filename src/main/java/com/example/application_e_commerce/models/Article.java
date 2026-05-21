package com.example.application_e_commerce.models;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom de l'article est requis")
    @Column(nullable = false, length = 100)
    private String nom;
    
    @Column(length = 500)
    private String description;
    
    @NotNull(message = "Le prix est requis")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;
    
    @NotNull(message = "La catégorie est requise")
    @Column(nullable = false, length = 50)
    private String categorie;
    
    @NotNull(message = "Le stock est requis")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    @Column(nullable = false)
    private Integer stock;
    
    // Champ pour l'URL de l'image
    @Column(length = 500)
    private String imageUrl;
  
    // Champ pour stocker le nom du fichier image
    @Column(length = 255)
    private String imageName;
    
    // Type MIME de l'image
    @Column(length = 100)
    private String imageType;
    
    // CORRECTION : Ajout de @Builder.Default pour conserver la valeur par défaut
    @Builder.Default
    @Column(nullable = false)
    private Boolean disponible = true;

    
    
    // Méthode utilitaire pour obtenir le chemin complet de l'image
    @Transient
    public String getImagePath() {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return "/images/default-product.jpg";
        }
        return imageUrl;
    }
    
    // Méthode pour vérifier si l'article a une image
    @Transient
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.isEmpty();
    }
}