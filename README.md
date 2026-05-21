# 🛍️ Application E-Commerce - Ma Boutique

Application web de vente en ligne complète développée avec **Spring Boot** (Backend) et **Thymeleaf** (Frontend). Elle permet aux utilisateurs de consulter des articles, ajouter au panier, passer des commandes et effectuer des paiements (simulés). Un espace d'administration permet de gérer les articles et les commandes.

---

## ✨ Fonctionnalités

### 👤 Utilisateurs
- Consultation du catalogue d'articles avec images
- Recherche par mot-clé et filtrage par catégorie
- Gestion du panier d'achat (ajout, modification, suppression)
- Tunnel de commande
- Paiement multi-canal (Carte bancaire / PayPal) - Mode simulation
- Historique des commandes
- Authentification et inscription

### 👑 Administration
- Dashboard avec statistiques (nombre de commandes, chiffre d'affaires)
- Gestion complète des commandes (consultation, modification du statut)
- Gestion complète des articles (CRUD : Ajout, Modification, Suppression)
- Modification rapide du stock depuis la liste
- Upload d'images pour les articles

---

## 🛠️ Technologies utilisées

| Catégorie | Technologies |
|-----------|--------------|
| Backend | Spring Boot 3.x, Spring MVC, Spring Data JPA, Spring Security |
| Frontend | Thymeleaf, Bootstrap 5, Font Awesome, JavaScript |
| Base de données | MySQL 8.0 |
| ORM | Hibernate |
| Build Tool | Maven |
| Paiement | Pattern Strategy (Carte bancaire simulée, PayPal Sandbox) |

---

## 📋 Prérequis

- **Java 17** ou supérieur
- **MySQL 8.0** (ou XAMPP/WAMP avec MySQL)
- **Maven** (ou utiliser le wrapper Maven inclus)
- Navigateur web moderne

---

##  📁 Structure complète du projet

application_e-commerce/
│
├── pom.xml                                   # Configuration Maven
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/application_e_commerce/
│       │       │
│       │       ├── ApplicationECommerceApplication.java    # Point d'entrée
│       │       │
│       │       ├── config/                                  # Configuration
│       │       │   ├── SecurityConfig.java                  # Sécurité Spring
│       │       │   └── WebConfig.java                       # Configuration web (ressources statiques)
│       │       │
│       │       ├── controller/                              # Contrôleurs MVC
│       │       │   ├── AdminController.java                 # Administration
│       │       │   ├── ArticleController.java               # Gestion des articles
│       │       │   ├── AuthController.java                  # Authentification
│       │       │   ├── CommandeController.java              # Commandes
│       │       │   └── PanierController.java                # Panier d'achat
│       │       │
│       │       ├── models/                                  # Entités JPA
│       │       │   ├── Article.java                         # Produit
│       │       │   ├── Commande.java                        # Commande
│       │       │   ├── LigneCommande.java                   # Ligne de commande
│       │       │   └── Utilisateur.java                     # Utilisateur
│       │       │
│       │       ├── repository/                              # Accès aux données
│       │       │   ├── ArticleRepository.java               # CRUD articles
│       │       │   ├── CommandeRepository.java              # CRUD commandes
│       │       │   └── UtilisateurRepository.java           # CRUD utilisateurs
│       │       │
│       │       ├── service/                                 # Logique métier
│       │       │   ├── ArticleService.java                  # Gestion articles
│       │       │   ├── CommandeService.java                 # Gestion commandes
│       │       │   ├── CustomUserDetailsService.java        # Service utilisateur Spring Security
│       │       │   ├── ImageStorageService.java             # Gestion des images
│       │       │   ├── PanierService.java                   # Gestion panier (session)
│       │       │   ├── UtilisateurService.java              # Gestion utilisateurs
│       │       │   └── AdminStats.java                      # Statistiques admin
│       │       │
│       │       └── payment/                                 # Module paiement (Strategy Pattern)
│       │           ├── PaymentStrategy.java                 # Interface stratégie
│       │           ├── PaymentContext.java                  # Contexte d'exécution
│       │           ├── PaymentDetails.java                  # Détails paiement
│       │           ├── PaymentResult.java                   # Résultat paiement
│       │           ├── CarteBancairePayment.java            # Paiement CB (simulé)
│       │           └── PayPalPayment.java                   # Paiement PayPal
│       │
│       └── resources/
│           ├── application.properties                       # Configuration Spring Boot
│           │
│           ├── static/                                      # Ressources statiques
│           │   ├── css/
│           │   │   └── style.css                            # Styles personnalisés
│           │   ├── js/
│           │   │   └── main.js                              # JavaScript interactif
│           │   └── images/
│           │       └── default-product.jpg                  # Image par défaut
│           │
│           └── templates/                                   # Templates Thymeleaf
│               │
│               ├── layout/
│               │   └── base.html                            # Template de base
│               │
│               ├── admin/                                   # Pages administration
│               │   ├── dashboard.html                       # Tableau de bord
│               │   ├── commandes.html                       # Liste commandes
│               │   ├── commande-detail.html                 # Détail commande
│               │   ├── articles-list.html                   # Gestion articles
│               │   └── article-form.html                    # Formulaire article
│               │
│               ├── articles/                                # Pages articles
│               │   ├── liste.html                           # Liste des articles
│               │   └── details.html                         # Détail article
│               │
│               ├── auth/                                    # Pages authentification
│               │   ├── login.html                           # Connexion
│               │   └── inscription.html                     # Inscription
│               │
│               ├── commande/                                # Pages commande
│               │   ├── checkout.html                        # Validation commande
│               │   ├── paiement.html                        # Formulaire paiement
│               │   ├── succes.html                          # Confirmation
│               │   ├── echec.html                           # Échec paiement
│               │   └── historique.html                      # Historique commandes
│               │
│               └── panier/
│                   └── panier.html                          # Panier d'achat
│
└── uploads/                                                 # Dossier upload images
    └── images/
        └── thumbnails/                                      # Miniatures images


## 📊 Structure de la base de données

┌─────────────────────────────────────────────────────────────────┐
│                          dbVentes                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────┐ │
│  │ utilisateurs│    │  commandes  │    │      articles       │ │
│  ├─────────────┤    ├─────────────┤    ├─────────────────────┤ │
│  │ id (PK)     │◄───│ utilisateur_id│  │ id (PK)            │ │
│  │ email       │    │ id (PK)     │    │ nom                │ │
│  │ mot_de_passe│    │ numeroCommande│   │ description        │ │
│  │ nom         │    │ montantTotal │    │ prix               │ │
│  │ prenom      │    │ modePaiement │    │ categorie          │ │
│  │ role        │    │ statut       │    │ stock              │ │
│  └─────────────┘    │ dateCommande │    │ image_url          │ │
│                     │ transactionId│    │ disponible         │ │
│                     └───────┬─────┘    └─────────────────────┘ │
│                             │                                   │
│                             │                                   │
│                     ┌───────▼─────┐                            │
│                     │lignes_commande│                          │
│                     ├─────────────┤                            │
│                     │ id (PK)     │                            │
│                     │ commande_id │                            │
│                     │ article_id  │────────────────────────────┤
│                     │ quantite    │                            │
│                     │ prixUnitaire│                            │
│                     │ sousTotal   │                            │
│                     └─────────────┘                            │
└─────────────────────────────────────────────────────────────────┘

## 🔄 Flux d'exécution

1. Utilisateur visite l'application
         ↓
2. Consulte le catalogue / Recherche des articles
         ↓
3. Ajoute des articles au panier
         ↓
4. Se connecte / Crée un compte
         ↓
5. Valide la commande
         ↓
6. Choisit le mode de paiement (CB / PayPal)
         ↓
7. Effectue le paiement (simulé)
         ↓
8. Confirmation de la commande
         ↓
9. L'admin peut consulter et gérer les commandes
