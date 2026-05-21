package com.example.application_e_commerce.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {
    
    @Value("${app.upload.dir}")
    private String uploadDir;
    
    @Value("${app.upload.allowed-extensions}")
    private String allowedExtensions;
    
    private Path uploadPath;
    
    @PostConstruct
    public void init() {
        try {
            this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }
    
    /**
     * Sauvegarde une image et retourne son URL
     */
    public String saveImage(MultipartFile file) throws IOException {
        // Validation du fichier
        validateFile(file);
        
        // Générer un nom unique pour le fichier
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFileName = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + "." + extension;
        
        // Chemin complet du fichier
        Path targetPath = uploadPath.resolve(newFileName);
        
        // Sauvegarder l'image originale
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Créer une version thumbnail (petite image) pour les listes
        createThumbnail(targetPath, newFileName);
        
        // Retourner l'URL relative
        return "/uploads/images/" + newFileName;
    }
    
    /**
     * Crée une version miniature de l'image pour les aperçus
     */
    private void createThumbnail(Path originalPath, String fileName) throws IOException {
        Path thumbnailPath = uploadPath.resolve("thumbnails");
        if (!Files.exists(thumbnailPath)) {
            Files.createDirectories(thumbnailPath);
        }
        
        Path thumbnailFile = thumbnailPath.resolve(fileName);
        
        // Redimensionner l'image à 200x200 pixels
        Thumbnails.of(originalPath.toFile())
            .size(200, 200)
            .keepAspectRatio(true)
            .toFile(thumbnailFile.toFile());
    }
    
    /**
     * Supprime une image
     */
    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path imagePath = uploadPath.resolve(fileName);
            Path thumbnailPath = uploadPath.resolve("thumbnails").resolve(fileName);
            
            Files.deleteIfExists(imagePath);
            Files.deleteIfExists(thumbnailPath);
        }
    }
    
    /**
     * Met à jour l'image d'un article
     */
    public String updateImage(String oldImageUrl, MultipartFile newImage) throws IOException {
        // Supprimer l'ancienne image si elle existe
        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            deleteImage(oldImageUrl);
        }
        
        // Sauvegarder la nouvelle image
        return saveImage(newImage);
    }
    
    /**
     * Valide le fichier uploadé
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        List<String> allowedExtList = Arrays.asList(allowedExtensions.split(","));
        if (!allowedExtList.contains(extension.toLowerCase())) {
            throw new RuntimeException("Extension non autorisée. Extensions autorisées: " + allowedExtensions);
        }
        
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new RuntimeException("Le fichier dépasse la taille maximale autorisée (5MB)");
        }
    }
    
    /**
     * Extrait l'extension d'un nom de fichier
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
