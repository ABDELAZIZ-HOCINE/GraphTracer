package graphtracer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Graphe implements Serializable {
    private List<Tache> taches;
    private List<Arc> arcs;

    public Graphe() {
        taches = new ArrayList<>();
        arcs = new ArrayList<>();
        ajouterTachesSpeciales();
    }

    private void ajouterTachesSpeciales() {
        ajouterTache(new Tache("début", 0, 100, 100));
        ajouterTache(new Tache("fin", 0, 500, 300));
    }

    public void ajouterTache(Tache tache) {
        if (tache.getNom().equals("début") || tache.getNom().equals("fin")) {
            if (taches.stream().noneMatch(t -> t.getNom().equals(tache.getNom()))) {
                taches.add(tache);
            }
        } else {
            taches.add(tache);
        }
    }

    public void supprimerTache(Tache tache) {
        if (tache.getNom().equals("début") || tache.getNom().equals("fin")) {
            throw new IllegalArgumentException("Impossible de supprimer les tâches système");
        }

        new ArrayList<>(arcs).forEach(arc -> {
            if (arc.getOrigine() == tache || arc.getDestination() == tache) {
                supprimerArc(arc);
            }
        });

        taches.remove(tache);
    }

    public void ajouterArc(Arc arc) {
        if (arc.getOrigine() == null || arc.getDestination() == null) {
            throw new IllegalArgumentException("L'arc doit avoir une origine et une destination.");
        }
        if (arc.getOrigine() == arc.getDestination()) {
            throw new IllegalArgumentException("Une tâche ne peut dépendre d'elle-même");
        }
        arcs.add(arc);
    }

    public void supprimerArc(Arc arc) {
        arcs.remove(arc);
        arc.supprimer();
    }

    public Tache trouverTacheParNom(String nom) {
        return taches.stream()
                .filter(tache -> tache.getNom().equals(nom))
                .findFirst()
                .orElse(null);
    }

    public List<Tache> getTaches() { return new ArrayList<>(taches); }
    public List<Arc> getArcs() { return new ArrayList<>(arcs); }


    public void calculerCheminCritique() {
        calculerDatesAuPlusTot();
        calculerDatesAuPlusTard();
        colorierCheminCritique();
    }


    private List<Tache> trouverCheminCritiqueComplet() {
    	    List<Tache> chemin = new ArrayList<>();
    	    Tache fin = trouverTacheParNom("fin");
    	    
    	    if (fin == null || fin.getDateAuPlusTot() == 0) {
    	        return chemin; // Pas de chemin critique calculé
    	    }
    	    
    	    // On part de la fin et on remonte
    	    Tache current = fin;
    	    chemin.add(current);
    	    
    	    while (current != null && !current.getNom().equals("début")) {
    	        Tache precedentCritique = null;
    	        
    	        // Chercher parmi les prédécesseurs
    	        for (Arc arc : current.getArcsEntrants()) {
    	            Tache precedent = arc.getOrigine();
    	            if (precedent.estCritique() && 
    	                current.getDateAuPlusTot() == precedent.getDateAuPlusTot() + (int)precedent.getDuree()) {
    	                precedentCritique = precedent;
    	                break; // On prend le premier prédécesseur critique trouvé
    	            }
    	        }
    	        
    	        if (precedentCritique == null) {
    	            // Cas où on ne trouve pas de prédécesseur critique
    	            if (current.getArcsEntrants().isEmpty()) {
    	                // La tâche n'a aucun prédécesseur (anomalie)
    	                System.err.println("Avertissement : la tâche " + current.getNom() 
    	                    + " n'a pas de prédécesseur dans le chemin critique");
    	            } else {
    	                // La tâche a des prédécesseurs mais aucun ne fait partie du chemin critique
    	                System.err.println("Avertissement : aucun prédécesseur critique trouvé pour " 
    	                    + current.getNom());
    	            }
    	            break;
    	        }
    	        
    	        chemin.add(precedentCritique);
    	        current = precedentCritique;
    	    }
    	    
    	    // Vérifier si on est bien arrivé au début
    	    if (current == null || !current.getNom().equals("début")) {
    	        System.err.println("Avertissement : chemin critique incomplet. Dernière tâche : " 
    	            + (current != null ? current.getNom() : "null"));
    	    }
    	    
    	    Collections.reverse(chemin);
    	    return chemin;
    	}
    
    private void calculerDatesAuPlusTot() {
        // Réinitialisation
        taches.forEach(t -> t.setDateAuPlusTot(0));
        
        // On traite d'abord les tâches sans prérequis (connectées à début)
        Tache debut = trouverTacheParNom("début");
        if (debut != null) {
            for (Arc arc : debut.getArcsSortants()) {
                Tache destination = arc.getDestination();
                destination.setDateAuPlusTot((int)debut.getDuree()); // 0
            }
        }

        // Tri topologique personnalisé basé sur les tâchesachevées
        List<Tache> tachesTraitees = new ArrayList<>();
        Queue<Tache> file = new LinkedList<>();
        
        // On commence par les tâches sans prérequis (déjà connectées à début)
        taches.stream()
              .filter(t -> t.getTachesachevées().isEmpty())
              .forEach(file::add);

        while (!file.isEmpty()) {
            Tache tache = file.poll();
            tachesTraitees.add(tache);

            // Pour chaque tâche qui a cette tâche comme prérequis
            for (Tache successeur : taches) {
                if (successeur.getTachesachevées().contains(tache.getNom())) {
                    // Calcul de la nouvelle date potentielle
                    int nouvelleDate = tache.getDateAuPlusTot() + (int)tache.getDuree();
                    
                    // Mise à jour si plus grande date trouvée
                    if (nouvelleDate > successeur.getDateAuPlusTot()) {
                        successeur.setDateAuPlusTot(nouvelleDate);
                    }

                    // Vérifie si tous les prérequis sont traités
                    boolean tousPrerequisTraites = successeur.getTachesachevées().stream()
                        .allMatch(nom -> tachesTraitees.contains(trouverTacheParNom(nom)));
                    
                    if (tousPrerequisTraites && !file.contains(successeur)) {
                        file.add(successeur);
                    }
                }
            }
        }

        // Finalement, calculer la date de fin
        Tache fin = trouverTacheParNom("fin");
        if (fin != null) {
            int maxDate = 0;
            for (Arc arc : fin.getArcsEntrants()) {
                Tache source = arc.getOrigine();
                int date = source.getDateAuPlusTot() + (int)source.getDuree();
                if (date > maxDate) {
                    maxDate = date;
                }
            }
            fin.setDateAuPlusTot(maxDate);
        }
    }

    private void calculerDatesAuPlusTard() {
        // Initialisation avec la tâche fin
        Tache fin = trouverTacheParNom("fin");
        if (fin != null) {
            fin.setDateAuPlusTard(fin.getDateAuPlusTot());
        }

        // Création d'une liste des tâches dans l'ordre inverse de traitement
        List<Tache> tachesInverse = new ArrayList<>(taches);
        Collections.reverse(tachesInverse);

        // On retire "début" et "fin" pour les traiter séparément
        tachesInverse.removeIf(t -> t.getNom().equals("début") || t.getNom().equals("fin"));

        for (Tache tache : tachesInverse) {
            // Trouver tous les successeurs (tâches qui ont cette tâche dans leurs tachesachevées)
            List<Tache> successeurs = taches.stream()
                .filter(t -> t.getTachesachevées().contains(tache.getNom()))
                .collect(Collectors.toList());

            if (successeurs.isEmpty()) {
                // Si pas de successeurs, c'est une tâche terminale (connectée à fin)
                tache.setDateAuPlusTard(fin.getDateAuPlusTard() - (int)tache.getDuree());
            } else {
                // Calculer le minimum (date au plus tard du successeur - durée de la tâche actuelle)
                int minDate = Integer.MAX_VALUE;
                for (Tache successeur : successeurs) {
                    int date = successeur.getDateAuPlusTard() - (int)tache.getDuree();
                    if (date < minDate) {
                        minDate = date;
                    }
                }
                tache.setDateAuPlusTard(minDate);
            }
        }

        // Traitement spécial pour "début"
        Tache debut = trouverTacheParNom("début");
        if (debut != null) {
            // Le début doit avoir la même date au plus tôt et au plus tard (0)
            debut.setDateAuPlusTard(0);
        }
    }

    private void colorierCheminCritique() {

        
        List<Tache> cheminComplet = trouverCheminCritiqueComplet();
        
        if (cheminComplet.isEmpty()) {
            return;
        }
        
        // Colorier les arcs entre tâches consécutives
        for (int i = 0; i < cheminComplet.size() - 1; i++) {
            Tache source = cheminComplet.get(i);
            Tache destination = cheminComplet.get(i+1);
            
            for (Arc arc : arcs) {
                if (arc.getOrigine().equals(source) && arc.getDestination().equals(destination)) {
                    arc.setCouleur(Color.RED);
                    break;
                }
            }
        }
    }
    
    public void sauvegarder(String cheminFichier) throws java.io.IOException {
        if (!cheminFichier.toLowerCase().endsWith(".ser")) {
            cheminFichier += ".ser";
        }

        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream(cheminFichier))) {
            oos.writeObject(this);
        }
    }

    public static Graphe charger(String cheminFichier) throws java.io.IOException, ClassNotFoundException {
        if (!cheminFichier.toLowerCase().endsWith(".ser")) {
            cheminFichier += ".ser";
        }
        try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                new java.io.FileInputStream(cheminFichier))) {
            return (Graphe) ois.readObject();
        }
    }
}