// FenetrePrincipale.java
package graphtracer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

// Classe principale de la fenêtre de l'application qui gère l'interface graphique
public class FenetrePrincipale extends JFrame implements ChargeurDeGraphe {

    private JPanel panneauPrincipal; // Panneau principal de la fenêtre
    private EditeurDeGraphe editeurDeGraphe; // Éditeur de graphes pour l'ajout et la modification

    // Constructeur de la fenêtre principale, initialise l'interface
    public FenetrePrincipale() {
        initialiser(); // Appel de la méthode pour initialiser la fenêtre
    }

    // Méthode pour initialiser les composants de la fenêtre
    private void initialiser() {
        setIconImage(chargerImage(Constantes.CHEMIN_LOGO)); // Chargement de l'icône de l'application
        setTitle(Constantes.TITRE_APPLICATION); // Définir le titre de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fermer l'application à la fermeture de la fenêtre
        setMinimumSize(new Dimension(Constantes.LARGEUR_FENETRE, Constantes.HAUTEUR_FENETRE)); // Taille minimale de la fenêtre
        setLocationRelativeTo(null); // Centrer la fenêtre à l'écran


        panneauPrincipal = new JPanel(new BorderLayout()); // Utilisation d'un layout BorderLayout pour le panneau principal
        panneauPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Ajout d'une marge

        JLabel titreLabel = new JLabel("Bienvenue dans GraphTracer", SwingConstants.CENTER); // Titre principal
        titreLabel.setFont(Constantes.POLICE_TITRE); // Définir la police du titre
        panneauPrincipal.add(titreLabel, BorderLayout.NORTH); // Ajouter le titre en haut du panneau


        JPanel panneauImages = creerPanneauImages(); // Création du panneau contenant les images
        panneauPrincipal.add(panneauImages, BorderLayout.SOUTH); // Ajouter les images en bas

        JPanel panneauBoutons = creerPanneauBoutons(); // Création du panneau avec les boutons  
        panneauPrincipal.add(panneauBoutons, BorderLayout.CENTER);  // Ajouter les boutons au centre

        setContentPane(panneauPrincipal); // Définir le contenu de la fenêtre
        pack(); // Ajuster la taille de la fenêtre en fonction du contenu
        setVisible(true); // Afficher la fenêtre
    }

    // Méthode pour créer le panneau avec les images du graphe orienté et non orienté
    private JPanel creerPanneauImages() {
        JPanel panneauImages = new JPanel(new GridLayout(1, 2, 1, 0)); // Utilisation d'un GridLayout
        panneauImages.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Ajout d'une marge

        // Chargement et affichage de l'image du graphe orienté
        ImageIcon iconeOriente = new ImageIcon(chargerImage(Constantes.CHEMIN_IMAGE_ORIENTE));
        JLabel labelOriente = new JLabel(new ImageIcon(iconeOriente.getImage().getScaledInstance(600, 500, Image.SCALE_SMOOTH)));
        labelOriente.setHorizontalAlignment(SwingConstants.CENTER); // Centrer l'image
        panneauImages.add(labelOriente);  // Ajouter l'image au panneau

        // Chargement et affichage de l'image du graphe non orienté
        ImageIcon iconeNonOriente = new ImageIcon(chargerImage(Constantes.CHEMIN_IMAGE_NON_ORIENTE));
        JLabel labelNonOriente = new JLabel(new ImageIcon(iconeNonOriente.getImage().getScaledInstance(600, 500, Image.SCALE_SMOOTH)));
        labelNonOriente.setHorizontalAlignment(SwingConstants.CENTER); // Centrer l'image
        panneauImages.add(labelNonOriente);  // Ajouter l'image au panneau

        return panneauImages; // Retourner le panneau des images
    }

    // Méthode pour créer le panneau avec les boutons de création et de modification de graphe
    private JPanel creerPanneauBoutons() {
        JPanel panneauBoutons = new JPanel(new GridLayout(1, 2, 20, 0));
        panneauBoutons.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        // Création du bouton pour créer un nouveau graphe
        JButton boutonCreer = creerBouton("Créer un graphe", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Création d'un nouveau graphe et ouverture de l'éditeur
                Graphe graphe = new Graphe();
                editeurDeGraphe = new EditeurDeGraphe(graphe);
                editeurDeGraphe.setVisible(true); 
            }
        });
        panneauBoutons.add(boutonCreer); // Ajouter le bouton au panneau

        // Création du bouton pour modifier un graphe existant
        JButton boutonModifier = creerBouton("Modifier un graphe", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Appel de la méthode pour charger un graphe
                chargerGraphe(e);
            }
        });
        panneauBoutons.add(boutonModifier); // Ajouter le bouton au panneau

        return panneauBoutons; // Retourner le panneau des boutons
    }

     // Méthode pour créer un bouton avec une action associée
    private JButton creerBouton(String texte, ActionListener action) {
        JButton bouton = new JButton(texte); // Création du bouton avec le texte
        bouton.addActionListener(action); // Ajouter l'action au bouton
        bouton.setBackground(Constantes.COULEUR_BOUTON); // Définir la couleur du bouton
        bouton.setPreferredSize(new Dimension(100, 100)); // Définir la taille du bouton
        bouton.setMinimumSize(new Dimension(100, 100)); 
        bouton.setMaximumSize(new Dimension(100, 100));
        bouton.setFont(Constantes.POLICE_BOUTON); // Définir la police du bouton
        return bouton; // Retourner le bouton
    }

    // Méthode pour charger une image à partir du chemin donné
    private Image chargerImage(String chemin) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(chemin)); // Chargement de l'image
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                throw new RuntimeException("Image non trouvée: " + chemin); // Gestion des erreurs
            }
            return icon.getImage(); // Retourner l'image
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
            return creerImagePlaceholder(); // Retourner une image par défaut en cas d'erreur
        }
    }

    // Méthode pour créer une image par défaut (placeholder) en cas d'échec de chargement
    private Image creerImagePlaceholder() {
        java.awt.image.BufferedImage placeholder = new java.awt.image.BufferedImage(400, 300, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = placeholder.createGraphics();
        g.setColor(Color.LIGHT_GRAY); // Définir la couleur de fond
        g.fillRect(0, 0, 400, 300); // Dessiner un rectangle de fond
        g.setColor(Color.BLACK); // Définir la couleur du texte
        g.drawString("Image non chargée", 150, 150);
        g.dispose(); // Libérer les ressources graphiques
        return placeholder; // Retourner l'image placeholder
    }
    // Méthode pour charger un graphe depuis un fichier
    @Override
    public void chargerGraphe(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(); // Choisir un fichier
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fichier = fileChooser.getSelectedFile(); // Obtenir le fichier sélectionné
            try {
                Graphe grapheCharge = Graphe.charger(fichier.getAbsolutePath()); // Charger le graphe à partir du fichier
                if (editeurDeGraphe == null || !editeurDeGraphe.isVisible()) {
                    editeurDeGraphe = new EditeurDeGraphe(grapheCharge); // Créer un nouvel éditeur si nécessaire
                } else {
                    editeurDeGraphe.chargerGraphe(grapheCharge); // Mettre à jour l'éditeur avec le graphe chargé
                }
                editeurDeGraphe.setVisible(true); // Afficher l'éditeur
            } catch (Exception ex) {
                // Afficher un message d'erreur si le chargement échoue
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
