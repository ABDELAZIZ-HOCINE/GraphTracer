// ChargeurDeGraphe.java
package graphtracer;

// Importation de la classe nécessaire pour gérer les événements d'action (ex : clic sur un bouton).
import java.awt.event.ActionEvent;

// Interface ChargeurDeGraphe : définit une méthode pour charger un graphe en réponse à un événement.
public interface ChargeurDeGraphe {

	// Méthode qui sera implémentée par les classes chargées de gérer le chargement du graphe.
    // Elle prend un événement (ActionEvent) comme paramètre pour associer le chargement à une action utilisateur.
    void chargerGraphe(ActionEvent e);
}
