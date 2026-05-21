/**
 * main.js - Fonctions JavaScript globales pour l'application e-commerce
 * 
 * Ce fichier est OPTIONNEL mais RECOMMANDÉ pour améliorer l'expérience utilisateur.
 * Il apporte de l'interactivité sans recharger la page.
 */

// ===== Initialisation au chargement de la page =====
document.addEventListener('DOMContentLoaded', function() {
    console.log('Application e-commerce chargée');
    
    // Initialiser les tooltips Bootstrap
    initTooltips();
    
    // Initialiser les popovers
    initPopovers();
    
    // Initialiser la confirmation avant action dangereuse
    initDeleteConfirmations();
    
    // Initialiser le tri des produits
    initProductSorting();
    
    // Initialiser l'auto-dismiss des alertes
    initAutoDismissAlerts();
    
    // Initialiser la validation des formulaires
    initFormValidation();
    
    // Initialiser la mise à jour automatique du panier
    initCartUpdate();
});

// ===== Tooltips Bootstrap =====
function initTooltips() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// ===== Popovers Bootstrap =====
function initPopovers() {
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function(popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
}

// ===== Confirmation avant suppression =====
function initDeleteConfirmations() {
    const deleteButtons = document.querySelectorAll('.btn-delete, .delete-action, [data-confirm]');
    
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            const message = this.getAttribute('data-confirm') || 'Êtes-vous sûr de vouloir effectuer cette action ?';
            if (!confirm(message)) {
                e.preventDefault();
                return false;
            }
        });
    });
}

// ===== Tri des produits =====
function initProductSorting() {
    const sortSelect = document.getElementById('sortSelect');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            const productsGrid = document.getElementById('productsGrid');
            if (!productsGrid) return;
            
            const products = Array.from(productsGrid.children);
            const sortValue = this.value;
            
            products.sort((a, b) => {
                const priceA = parseFloat(a.querySelector('.price')?.innerText.replace('€', '') || 0);
                const priceB = parseFloat(b.querySelector('.price')?.innerText.replace('€', '') || 0);
                const nameA = a.querySelector('.card-title')?.innerText || '';
                const nameB = b.querySelector('.card-title')?.innerText || '';
                
                switch(sortValue) {
                    case 'price-asc':
                        return priceA - priceB;
                    case 'price-desc':
                        return priceB - priceA;
                    case 'name-asc':
                        return nameA.localeCompare(nameB);
                    default:
                        return 0;
                }
            });
            
            // Réorganiser le DOM
            products.forEach(product => productsGrid.appendChild(product));
        });
    }
}

// ===== Auto-dismiss des alertes =====
function initAutoDismissAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
}

// ===== Validation des formulaires =====
function initFormValidation() {
    const forms = document.querySelectorAll('.needs-validation');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
}

// ===== Mise à jour automatique du panier =====
function initCartUpdate() {
    const quantityInputs = document.querySelectorAll('.cart-quantity-input');
    
    quantityInputs.forEach(input => {
        input.addEventListener('change', function() {
            const form = this.closest('form');
            if (form) {
                form.submit();
            }
        });
    });
}

// ===== Ajout au panier sans rechargement (AJAX) =====
function addToCart(productId, quantity) {
    fetch(`/panier/ajouter/${productId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `quantite=${quantity}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            updateCartCount(data.cartCount);
            showNotification('Article ajouté au panier !', 'success');
        } else {
            showNotification(data.message || 'Erreur lors de l\'ajout', 'error');
        }
    })
    .catch(error => {
        console.error('Erreur:', error);
        showNotification('Une erreur est survenue', 'error');
    });
}

// ===== Mise à jour du compteur du panier =====
function updateCartCount(count) {
    const cartBadge = document.querySelector('.cart-badge, .position-absolute.badge');
    if (cartBadge) {
        if (count > 0) {
            cartBadge.textContent = count;
            cartBadge.style.display = 'inline-block';
        } else {
            cartBadge.style.display = 'none';
        }
    }
}

// ===== Notifications toast =====
function showNotification(message, type = 'info') {
    // Créer un conteneur de toast s'il n'existe pas
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed bottom-0 end-0 p-3';
        document.body.appendChild(toastContainer);
    }
    
    // Déterminer la classe CSS selon le type
    let bgClass = 'bg-primary';
    let icon = 'fa-info-circle';
    
    switch(type) {
        case 'success':
            bgClass = 'bg-success';
            icon = 'fa-check-circle';
            break;
        case 'error':
            bgClass = 'bg-danger';
            icon = 'fa-exclamation-triangle';
            break;
        case 'warning':
            bgClass = 'bg-warning';
            icon = 'fa-exclamation-circle';
            break;
    }
    
    // Créer le toast
    const toastId = 'toast-' + Date.now();
    const toastHtml = `
        <div id="${toastId}" class="toast" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="true" data-bs-delay="3000">
            <div class="toast-header ${bgClass} text-white">
                <i class="fas ${icon} me-2"></i>
                <strong class="me-auto">Notification</strong>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        </div>
    `;
    
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
    
    // Supprimer le toast après fermeture
    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}

// ===== Filtre par prix (slider) =====
function initPriceFilter() {
    const priceSlider = document.getElementById('priceSlider');
    const priceValue = document.getElementById('priceValue');
    
    if (priceSlider && priceValue) {
        priceSlider.addEventListener('input', function() {
            priceValue.textContent = this.value + '€';
            filterProductsByPrice(this.value);
        });
    }
}

function filterProductsByPrice(maxPrice) {
    const products = document.querySelectorAll('.product-card');
    
    products.forEach(product => {
        const price = parseFloat(product.querySelector('.price')?.innerText.replace('€', '') || 0);
        if (price <= maxPrice) {
            product.closest('.col-md-4')?.style.removeProperty('display');
        } else {
            product.closest('.col-md-4')?.style.display = 'none';
        }
    });
}

// ===== Recherche en temps réel =====
function initLiveSearch() {
    const searchInput = document.getElementById('liveSearch');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(function() {
            const keyword = this.value.toLowerCase();
            const products = document.querySelectorAll('.product-card');
            
            products.forEach(product => {
                const title = product.querySelector('.card-title')?.innerText.toLowerCase() || '';
                const description = product.querySelector('.card-text')?.innerText.toLowerCase() || '';
                
                if (title.includes(keyword) || description.includes(keyword)) {
                    product.closest('.col-md-4')?.style.removeProperty('display');
                } else {
                    product.closest('.col-md-4')?.style.display = 'none';
                }
            });
        }, 300));
    }
}

// ===== Debounce pour optimiser les recherches =====
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func.apply(this, args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// ===== Gestion du mode sombre (optionnel) =====
function initDarkMode() {
    const darkModeToggle = document.getElementById('darkModeToggle');
    if (darkModeToggle) {
        darkModeToggle.addEventListener('click', () => {
            document.body.classList.toggle('dark-mode');
            localStorage.setItem('darkMode', document.body.classList.contains('dark-mode'));
        });
        
        // Charger la préférence
        if (localStorage.getItem('darkMode') === 'true') {
            document.body.classList.add('dark-mode');
        }
    }
}

// ===== Export pour utilisation dans d'autres scripts =====
window.ecommerce = {
    addToCart: addToCart,
    showNotification: showNotification,
    updateCartCount: updateCartCount
};