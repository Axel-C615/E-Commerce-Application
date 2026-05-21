package com.example.application_e_commerce.service;

import java.math.BigDecimal;

public class AdminStats {
    private long totalCommandes;
    private long commandesEnAttente;
    private long commandesPayees;
    private long commandesAnnulees;
    private BigDecimal chiffreAffaires;
    
    // Constructeurs
    public AdminStats() {
        this.chiffreAffaires = BigDecimal.ZERO;
    }
    
    // Getters et Setters
    public long getTotalCommandes() {
        return totalCommandes;
    }
    
    public void setTotalCommandes(long totalCommandes) {
        this.totalCommandes = totalCommandes;
    }
    
    public long getCommandesEnAttente() {
        return commandesEnAttente;
    }
    
    public void setCommandesEnAttente(long commandesEnAttente) {
        this.commandesEnAttente = commandesEnAttente;
    }
    
    public long getCommandesPayees() {
        return commandesPayees;
    }
    
    public void setCommandesPayees(long commandesPayees) {
        this.commandesPayees = commandesPayees;
    }
    
    public long getCommandesAnnulees() {
        return commandesAnnulees;
    }
    
    public void setCommandesAnnulees(long commandesAnnulees) {
        this.commandesAnnulees = commandesAnnulees;
    }
    
    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }
    
    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }
}
