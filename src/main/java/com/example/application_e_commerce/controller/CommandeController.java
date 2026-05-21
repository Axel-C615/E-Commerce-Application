package com.example.application_e_commerce.controller;

import com.example.application_e_commerce.models.Commande;
import com.example.application_e_commerce.models.Utilisateur;
import com.example.application_e_commerce.service.CommandeService;
import com.example.application_e_commerce.service.PanierService;
import com.example.application_e_commerce.service.UtilisateurService;
import com.example.application_e_commerce.payment.PaymentDetails;
import com.example.application_e_commerce.payment.PaymentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/commande")
public class CommandeController {

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private PanierService panierService;

    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        if (panierService.getItems().isEmpty()) {
            return "redirect:/panier";
        }
        model.addAttribute("items", panierService.getItems());         // ✅ manquant avant
        model.addAttribute("total", panierService.getTotal());
        model.addAttribute("nombreArticles", panierService.getNombreArticles());
        return "commande/checkout";
    }

    @PostMapping("/creer")
    public String creerCommande(@RequestParam String modePaiement, Model model) {
        if (panierService.getItems().isEmpty()) {
            return "redirect:/panier";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Utilisateur utilisateur = utilisateurService.getUtilisateurByEmail(auth.getName());

        Commande commande = commandeService.creerCommande(
            utilisateur,
            panierService.convertirEnLignesCommande(),
            panierService.getTotal(),
            modePaiement
        );

        model.addAttribute("commande", commande);
        model.addAttribute("montantTotal", commande.getMontantTotal());
        model.addAttribute("modePaiement", modePaiement);

        return "commande/paiement";
    }

    @PostMapping("/payer/{commandeId}")
    public String traiterPaiement(@PathVariable String commandeId,
                                  @RequestParam(required = false) String cardNumber,
                                  @RequestParam(required = false) String cardExpiry,
                                  @RequestParam(required = false) String cardCvv,
                                  @RequestParam(required = false) String paypalEmail,
                                  HttpSession session,
                                  Model model) {

        Commande commande = commandeService.getCommandeByNumero(commandeId);

        PaymentDetails details = new PaymentDetails();
        details.setCardNumber(cardNumber);
        details.setCardExpiry(cardExpiry);
        details.setCardCvv(cardCvv);
        details.setPaypalEmail(paypalEmail);

        String baseUrl = "http://localhost:8080";
        details.setReturnUrl(baseUrl + "/commande/success/" + commande.getNumeroCommande());
        details.setCancelUrl(baseUrl + "/commande/cancel/" + commande.getNumeroCommande());

        PaymentResult result = commandeService.traiterPaiement(commande, details);

        if (result.isSuccess()) {
            if (result.getPaymentUrl() != null) {
                return "redirect:" + result.getPaymentUrl();
            }
            panierService.viderPanier();
            model.addAttribute("commande", commande);
            model.addAttribute("transactionId", result.getTransactionId());
            return "commande/succes";
        } else {
            model.addAttribute("erreur", result.getMessage());
            model.addAttribute("commande", commande);
            return "commande/echec";
        }
    }

    @GetMapping("/success/{numeroCommande}")
    public String paiementSuccess(@PathVariable String numeroCommande, Model model) {
        Commande commande = commandeService.getCommandeByNumero(numeroCommande);
        panierService.viderPanier();
        model.addAttribute("commande", commande);
        return "commande/succes";
    }

    @GetMapping("/cancel/{numeroCommande}")
    public String paiementCancel(@PathVariable String numeroCommande, Model model) {
        model.addAttribute("commande", commandeService.getCommandeByNumero(numeroCommande));
        return "commande/echec";
    }

    @GetMapping("/historique")
    public String historiqueCommandes(Authentication auth, Model model) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurByEmail(auth.getName());
        model.addAttribute("commandes", commandeService.getCommandesByUtilisateur(utilisateur));
        return "commande/historique";
    }
}
