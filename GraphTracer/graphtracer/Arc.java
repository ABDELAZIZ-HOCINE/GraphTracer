// Arc.java
package graphtracer;

// Importation des classes nécessaires pour la sérialisation et les couleurs.
import java.io.Serializable;// Permet de sérialiser les objets de la classe
import java.awt.Color;// Permet de définir des couleurs pour les arcs

// Déclaration de la classe Arc qui implémente l'interface Serializable.
public class Arc implements Serializable {

    // Déclaration des attributs privés de la classe Arc.
    private Tache origine; // La tâche d'origine de l'arc
    private Tache destination; // La tâche de destination de l'arc
    private Color couleur; // La couleur de l'arc

    // Constructeur pour créer un arc entre deux tâches.
    public Arc(Tache origine, Tache destination) {
        this.origine = origine;
        this.destination = destination;
        this.couleur = Color.BLACK; // La couleur par défaut est noire

        // Ajoute l'arc aux tâches respectives (sortant pour l'origine et entrant pour la destination)
        origine.ajouterArcSortant(this);
        destination.ajouterArcEntrant(this);
    }

    // Getter pour récupérer l'origine de l'arc
    public Tache getOrigine() { return origine; }

    // Getter pour récupérer la destination de l'arc
    public Tache getDestination() { return destination; }

    // Getter pour récupérer la couleur de l'arc
    public Color getCouleur() { return couleur; }

    // Setter pour définir la couleur de l'arc
    public void setCouleur(Color couleur) { this.couleur = couleur; }

    // Méthode pour supprimer l'arc et ses relations avec les tâches
    public void supprimer() {
        // Retire l'arc des tâches concernées
        origine.supprimerArcSortant(this);
        destination.supprimerArcEntrant(this);
        origine = null; // Annule la référence à l'origine
        destination = null; // Annule la référence à la destination
    }
}
