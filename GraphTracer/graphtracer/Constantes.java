// Constantes.java
package graphtracer;

import java.awt.*;

// Classe qui regroupe toutes les constantes utilisées dans l'application
public class Constantes {

    // Dimensions de la fenêtre principale de l'application
    public static final int LARGEUR_FENETRE = 1200;
    public static final int HAUTEUR_FENETRE = 900;

    // Titre de l'application affiché dans la fenêtre
    public static final String TITRE_APPLICATION = "GraphTracer - Tableau de bord";

    // Chemins vers les ressources d'images utilisées dans l'application
    public static final String CHEMIN_LOGO = "/graphtracer/img/logo.png";
    public static final String CHEMIN_IMAGE_ORIENTE = "/graphtracer/img/graphe_oriente.png";
    public static final String CHEMIN_IMAGE_NON_ORIENTE = "/graphtracer/img/graphe_non_or.png";

    // Polices utilisées pour le titre et les boutons de l'interface
    public static final Font POLICE_TITRE = new Font("Arial", Font.BOLD, 24);
    public static final Font POLICE_BOUTON = new Font("Arial", Font.BOLD, 32);

    // Couleur utilisée pour les boutons dans l'application
    public static final Color COULEUR_BOUTON = SystemColor.activeCaptionBorder;
}
