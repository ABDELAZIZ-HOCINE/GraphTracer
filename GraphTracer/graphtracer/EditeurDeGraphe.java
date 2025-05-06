// EditeurDeGraphe.java
package graphtracer;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.print.attribute.*;
import javax.print.attribute.standard.*;

// Classe principale de la fenêtre d'édition de graphe
public class EditeurDeGraphe extends JFrame {
    private Graphe graphe; // Le graphe en cours d’édition
    private ZoneDeGraphe ZoneDeGraphe; // Zone d'affichage et d'interaction avec le graphe
    private Tache tacheSelectionnee; // Tâche sélectionnée par l’utilisateur
    private Arc arcSelectionne; // Arc sélectionné par l'utilisateur
    private Point decalageDrag; // Décalage entre l'endroit cliqué et le coin de la tâche (pour drag)

    // Constructeur principal
    public EditeurDeGraphe(Graphe graphe) {
    	setIconImage(Toolkit.getDefaultToolkit().getImage(EditeurDeGraphe.class.getResource("/graphtracer/img/logo.png"))); // Icône de la fenêtre
        this.graphe = graphe;
        initialiserUI(); // Crée la fenêtre et ses composants
        configurerMenu(); // Crée la barre de menu	
        configurerListeners();  // Active les actions liées aux clics et aux interactions utilisateur	
    }

    // Prépare l’interface de la fenêtre
    private void initialiserUI() {
        setTitle("GraphTracer - Éditeur de graphe");
        setSize(1200, 800); // Taille par défaut	
        setLocationRelativeTo(null);  // Centre la fenêtre	
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme uniquement cette fenêtre sans arrêter le programme principal

        Container contentPane = getContentPane();
        SpringLayout springLayout = new SpringLayout();
        contentPane.setLayout(springLayout);

        ZoneDeGraphe = new ZoneDeGraphe(graphe); // Zone de dessin

        ZoneDeGraphe.setBackground(Color.WHITE); // Fond blanc
        // Positionne la ZoneDeGraphe pour qu'elle occupe tout l'espace du contentPane sauf les 100 pixels du bas        
        springLayout.putConstraint(SpringLayout.NORTH, ZoneDeGraphe, 0, SpringLayout.NORTH, contentPane);
        springLayout.putConstraint(SpringLayout.WEST, ZoneDeGraphe, 0, SpringLayout.WEST, contentPane);
        springLayout.putConstraint(SpringLayout.EAST, ZoneDeGraphe, 0, SpringLayout.EAST, contentPane);
        springLayout.putConstraint(SpringLayout.SOUTH, ZoneDeGraphe, -100, SpringLayout.SOUTH, contentPane);
        contentPane.add(ZoneDeGraphe);

        JPanel panneauControle = new JPanel();
        // Configuration de la position et de la taille du panneau de contrôle dans le contentPane
        springLayout.putConstraint(SpringLayout.NORTH, panneauControle, 686, SpringLayout.NORTH, contentPane);
        springLayout.putConstraint(SpringLayout.WEST, panneauControle, 0, SpringLayout.WEST, contentPane);
        springLayout.putConstraint(SpringLayout.EAST, panneauControle, 1184, SpringLayout.WEST, contentPane);
        springLayout.putConstraint(SpringLayout.SOUTH, panneauControle, 0, SpringLayout.SOUTH, contentPane);

        // Apparence du panneau de contrôle : fond clair et bordure
        panneauControle.setBackground(new Color(240, 240, 240));
        panneauControle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.add(panneauControle);

        // Définit le layout du contentPane
        getContentPane().setLayout(springLayout);
    }
    // Configuration de la barre de menu
    private void configurerMenu() {
        JMenuBar barreMenu = new JMenuBar();

        // Menu Fichier : Permet d'enregistrer, ouvrir ou imprimer le graphe
        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem itemEnregistrer = new JMenuItem("Enregistrer");
        JMenuItem itemOuvrir = new JMenuItem("Ouvrir");
        JMenuItem itemImprimer = new JMenuItem("Imprimer");

        itemEnregistrer.addActionListener(e -> sauvegarderGraphe());
        itemOuvrir.addActionListener(e -> chargerGraphe());
        itemImprimer.addActionListener(e -> imprimerGraphe());

        menuFichier.add(itemEnregistrer);
        menuFichier.add(itemOuvrir);
        menuFichier.add(itemImprimer);
        barreMenu.add(menuFichier);

        // Menu Tâche : Permet de gérer les tâches du graphe (ajouter, supprimer, etc.)
        JMenu menuTache = new JMenu("Tâche");
        JMenuItem itemAjouterTache = new JMenuItem("Ajouter");
        JMenuItem itemSupprimerTache = new JMenuItem("Supprimer");
        JMenuItem itemRenommerTache = new JMenuItem("Renommer");
        JMenuItem itemModifierDureeTache = new JMenuItem("Modifier la durée");
        JMenuItem itemModifierCouleurTache = new JMenuItem("Modifier la couleur");
        JMenuItem itemModifiercontrainte = new JMenuItem("Modifier la contrainte");
        
        itemAjouterTache.addActionListener(e -> ajouterTache());
        itemSupprimerTache.addActionListener(e -> supprimerTache());
        itemRenommerTache.addActionListener(e -> renommerTache());
        itemModifierDureeTache.addActionListener(e -> modifierDureeTache());
        itemModifierCouleurTache.addActionListener(e -> modifierCouleurTache());
        itemModifiercontrainte.addActionListener(e -> ModifierContrainte());

        menuTache.add(itemAjouterTache);
        menuTache.add(itemSupprimerTache);
        menuTache.add(itemRenommerTache);
        menuTache.add(itemModifierDureeTache);
        menuTache.add(itemModifierCouleurTache);
        menuTache.add(itemModifiercontrainte);
        
        barreMenu.add(menuTache);

        // Menu Arc : Permet de gérer les arcs du graphe (ajouter, supprimer, etc.)
        JMenu menuArc = new JMenu("Arc");
        JMenuItem itemAjouterArc = new JMenuItem("Ajouter");
        JMenuItem itemSupprimerArc = new JMenuItem("Supprimer");
        JMenuItem itemModifierCouleurArc = new JMenuItem("Modifier la couleur");

        itemAjouterArc.addActionListener(e -> ajouterArc());
        itemSupprimerArc.addActionListener(e -> supprimerArc());
        itemModifierCouleurArc.addActionListener(e -> modifierCouleurArc());

        menuArc.add(itemAjouterArc);
        menuArc.add(itemSupprimerArc);
        menuArc.add(itemModifierCouleurArc);
        barreMenu.add(menuArc);

         // Menu Calcul : Permet de calculer et d'annuler le chemin critique
        JMenu menuCalcul = new JMenu("Calcul");
        JMenuItem itemCheminCritique = new JMenuItem("Calculer chemin critique");
        itemCheminCritique.addActionListener(e -> calculerCheminCritique());
        menuCalcul.add(itemCheminCritique);
        
        JMenuItem itemannulerCheminCritique = new JMenuItem("Annuler la selection du chemin critique");
        itemannulerCheminCritique.addActionListener(e -> annulerCheminCritique());
        menuCalcul.add(itemannulerCheminCritique);
        barreMenu.add(menuCalcul);

        // On associe la barre de menu à la fenêtre
        setJMenuBar(barreMenu);
    }

    // Annule la sélection du chemin critique en réinitialisant les dates et en réinitialisant la couleur des arcs
    private void annulerCheminCritique() {
        graphe.getTaches().forEach(t -> {
            t.setDateAuPlusTot(0);
            t.setDateAuPlusTard(1);
        });
        
        graphe.getArcs().forEach(a -> {
            a.setCouleur(Color.black);
        });
        
        // Forcer le rafraîchissement de l'affichage
        ZoneDeGraphe.repaint();
    }
    // Calcule le chemin critique, l'affiche et rafraîchit la zone de dessin
    private void calculerCheminCritique() {
    	annulerCheminCritique();
        graphe.calculerCheminCritique();
        ZoneDeGraphe.repaint();
        
        // Afficher un message avec la durée totale
        Tache fin = graphe.trouverTacheParNom("fin");
        if (fin != null) {
            JOptionPane.showMessageDialog(this,
                "Chemin critique calculé.\nDurée totale du projet: " + fin.getDateAuPlusTot(),
                "Chemin critique",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    // Permet d'imprimer le graphe en ajustant son échelle à la page
    private void imprimerGraphe() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Impression du graphe");

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(MediaSizeName.ISO_A4);
        attributes.add(new JobName("GraphTracer - Impression", null));

        if (job.printDialog(attributes)) {
            try {
                job.setPrintable(new Printable() {
                    @Override
                    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                            throws PrinterException {

                        if (pageIndex > 0) return NO_SUCH_PAGE;

                        Graphics2D g2d = (Graphics2D) graphics;
                        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                        double scaleX = pageFormat.getImageableWidth() / ZoneDeGraphe.getWidth();
                        double scaleY = pageFormat.getImageableHeight() / ZoneDeGraphe.getHeight();
                        double scale = Math.min(scaleX, scaleY);

                        AffineTransform originalTransform = g2d.getTransform();
                        g2d.scale(scale, scale);

                        ZoneDeGraphe.paint(g2d); // Dessin du graphe à imprimer
                        g2d.setTransform(originalTransform); // Restauration de la transformation initiale

                        return PAGE_EXISTS;
                    }
                });

                job.print(attributes); // Lancement de l'impression
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'impression : " + e.getMessage(),
                        "Erreur d'impression",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Configuration des écouteurs d'événements pour la zone de dessin (clics et déplacements de souris)
    private void configurerListeners() {
        ZoneDeGraphe.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // Ne traite que les clics gauche de la souris
                if (SwingUtilities.isLeftMouseButton(e)) {
                    gererClicSouris(e);
                }
            }
        });

        // Ajoute un écouteur de mouvement de souris pour détecter les glissements (drag)
        ZoneDeGraphe.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                gererDragSouris(e);
            }
        });
    }
    // Gère le clic de souris dans la zone de dessin (sélection de tâche ou d'arc)
    private void gererClicSouris(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) return;

        // Réinitialiser la sélection
        tacheSelectionnee = null;
        arcSelectionne = null;

        // Vérifier si on clique sur une tâche
        for (Tache tache : graphe.getTaches()) {
            if (estSourisSurTache(e.getPoint(), tache)) {
                tacheSelectionnee = tache;
                decalageDrag = new Point(e.getX() - tache.getPx(), e.getY() - tache.getPy());
                break;
            }
        }

        // Si aucune tâche n'est sélectionnée, vérifier les arcs
        if (tacheSelectionnee == null) {
            for (Arc arc : graphe.getArcs()) {
                if (estSourisSurArc(e.getPoint(), arc)) {
                    arcSelectionne = arc;
                    break;
                }
            }
        }

        // Mettre à jour la sélection dans le panel et rafraîchir
        ZoneDeGraphe.setTacheSelectionnee(tacheSelectionnee);
        ZoneDeGraphe.setArcSelectionne(arcSelectionne);
    }
    // Gère le déplacement d'une tâche avec la souris
    private void gererDragSouris(MouseEvent e) {
        if (tacheSelectionnee != null) {
            tacheSelectionnee.setPx(e.getX() - decalageDrag.x);
            tacheSelectionnee.setPy(e.getY() - decalageDrag.y);
            ZoneDeGraphe.repaint();
        }
    }

    // Détecte si la souris est au-dessus d'une tâche (cercle)
    private boolean estSourisSurTache(Point point, Tache tache) {
        int rayon = 25;
        return point.distance(tache.getPx() + rayon, tache.getPy() + rayon) < rayon;
    }

    // Détecte si la souris est proche du centre d'un arc (ligne entre deux tâches)
    private boolean estSourisSurArc(Point point, Arc arc) {
        Point p1 = new Point(arc.getOrigine().getPx() + 25, arc.getOrigine().getPy() + 25);
        Point p2 = new Point(arc.getDestination().getPx() + 25, arc.getDestination().getPy() + 25);
        Point pointMilieu = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
        return point.distance(pointMilieu) < 20;
    }

    // Ajoute une nouvelle tâche au graphe avec éventuellement ses dépendances
    private void ajouterTache() {
        String nom = JOptionPane.showInputDialog(this, "Nom de la nouvelle tâche:");
        if (nom != null && !nom.trim().isEmpty()) {
            if (graphe.trouverTacheParNom(nom) != null) {
                JOptionPane.showMessageDialog(this, "Une tâche avec ce nom existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String dureeStr = JOptionPane.showInputDialog(this, "Durée de la tâche:");
            try {
                long duree = Long.parseLong(dureeStr);
                Tache nouvelleTache = new Tache(nom, duree, 100, 100);
                graphe.ajouterTache(nouvelleTache);             
                
                // Demande les tâches précédentes (contraintes)
        	    String contrainte = JOptionPane.showInputDialog(this, 
        	        "Entrez les noms des tâches achevées (séparés par des virgules) :\n");
        	       	    		
        	    if (contrainte.trim().isEmpty() || contrainte == null) {
        	    	Tache source = graphe.trouverTacheParNom("début");
    	    		graphe.ajouterArc(new Arc(source, nouvelleTache));
        	    } else {  	  
                    String[] sources = contrainte.split(",");
                    List<String> listeSources = new ArrayList<>();
                    for (String s : sources) {
                        if (!s.trim().isEmpty()) {
                        	Tache st = graphe.trouverTacheParNom(s.trim());
            	            if (st != null) {
            	            	listeSources.add(s.trim());
            	            }else {
            	                JOptionPane.showMessageDialog(this, "Il n'existe aucune tâche avec ce nom", "Erreur", JOptionPane.ERROR_MESSAGE);
            	                return;
            	            }
                        }
                    }
                    nouvelleTache.setTachesachevées(listeSources);
        	    }

        	    nouvelleTache.mettreajourarcs(graphe);
        	    
                ZoneDeGraphe.repaint();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Durée invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
        
    }
    

    // Supprime la tâche actuellement sélectionnée
    private void supprimerTache() {
        if (tacheSelectionnee != null) {
            try {
                graphe.supprimerTache(tacheSelectionnee);
                tacheSelectionnee = null;
                ZoneDeGraphe.repaint();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une tâche à supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Renomme la tâche sélectionnée
    private void renommerTache() {
        if (tacheSelectionnee != null) {
        	
            if (tacheSelectionnee.getNom().equals("début") || tacheSelectionnee.getNom().equals("fin")) {
                JOptionPane.showMessageDialog(this, "Impossible de modifier les tâches système.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String nouveauNom = JOptionPane.showInputDialog(this, "Nouveau nom pour la tâche:", tacheSelectionnee.getNom());
            if (nouveauNom != null && !nouveauNom.trim().isEmpty()) {
                if (graphe.trouverTacheParNom(nouveauNom) != null) {
                    JOptionPane.showMessageDialog(this, "Une tâche avec ce nom existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                tacheSelectionnee.setNom(nouveauNom);
                ZoneDeGraphe.repaint();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une tâche à renommer.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Change la durée de la tâche sélectionnée
    private void modifierDureeTache() {
        if (tacheSelectionnee != null) {
            if (tacheSelectionnee.getNom().equals("début") || tacheSelectionnee.getNom().equals("fin")) {
                JOptionPane.showMessageDialog(this, "Impossible de modifier les tâches système.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String nouvelleDureeStr = JOptionPane.showInputDialog(this, "Nouvelle durée pour la tâche:", tacheSelectionnee.getDuree());
            if (nouvelleDureeStr != null && !nouvelleDureeStr.trim().isEmpty()) {
                try {
                    long nouvelleDuree = Long.parseLong(nouvelleDureeStr);
                    tacheSelectionnee.setDuree(nouvelleDuree);
                    ZoneDeGraphe.repaint();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Durée invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une tâche dont vous voulez modifier la durée.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Permet de changer la couleur d'une tâche
    private void modifierCouleurTache() {
        if (tacheSelectionnee != null) {

            Color nouvelleCouleur = JColorChooser.showDialog(this, "Choisir une nouvelle couleur", tacheSelectionnee.getCouleur());
            if (nouvelleCouleur != null) {
                tacheSelectionnee.setCouleur(nouvelleCouleur);
                ZoneDeGraphe.repaint();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une tâche dont vous voulez modifier la couleur.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Modifier les contraintes (tâches précédentes) d'une tâche
    private void ModifierContrainte() {
    	if (tacheSelectionnee != null) {
            if (tacheSelectionnee.getNom().equals("début") || tacheSelectionnee.getNom().equals("fin")) {
                JOptionPane.showMessageDialog(this, "Impossible de modifier les tâches système.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String input = JOptionPane.showInputDialog(this, 
                "Entrez les noms des tâches achevées (séparés par des virgules) :\n");
            
            if (input == null || input.trim().isEmpty()) {
                tacheSelectionnee.setTachesachevées(null);
            } else {  	        
                String[] sources = input.split(",");
                List<String> listeSources = new ArrayList<>();
                for (String s : sources) {
                    if (!s.trim().isEmpty()) {
                        listeSources.add(s.trim());
                    }
                }
                tacheSelectionnee.setTachesachevées(listeSources);
               
                tacheSelectionnee.mettreajourarcs(graphe);
                 
                ZoneDeGraphe.repaint();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Aucune tâche sélectionnée !", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Ajoute un arc (dépendance) entre deux tâches
    private void ajouterArc() {
        String nomOrigine = JOptionPane.showInputDialog(this, "Nom de la tâche d'origine:");
        if (nomOrigine == null || nomOrigine.trim().isEmpty()) return;
        Tache origine = graphe.trouverTacheParNom(nomOrigine);
        if (origine == null) {
            JOptionPane.showMessageDialog(this, "Tâche d'origine introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        

        String nomDestination = JOptionPane.showInputDialog(this, "Nom de la tâche de destination:");
        if (nomDestination == null || nomDestination.trim().isEmpty()) return;
        Tache destination = graphe.trouverTacheParNom(nomDestination);
        if (destination == null) {
            JOptionPane.showMessageDialog(this, "Tâche de destination introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Arc nouvelArc = new Arc(origine, destination);
            graphe.ajouterArc(nouvelArc);
            
            
            // Mise à jour des tâches achevées
            destination.getTachesachevées().add(origine.getNom().trim());       
            
            ZoneDeGraphe.repaint();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Supprime l'arc actuellement sélectionné
    private void supprimerArc() {
        if (arcSelectionne != null) {
            // Récupérer la tâche d'origine avant suppression
            Tache origine = arcSelectionne.getOrigine();
            Tache Destination = arcSelectionne.getDestination();
            String nomDestination = Destination.getNom();
            
            // Supprimer l'arc
            graphe.supprimerArc(arcSelectionne);
            
            // Mettre à jour les tâches achevées
            Destination.getTachesachevées().remove(origine.getNom());
            
            arcSelectionne = null;
            ZoneDeGraphe.repaint();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un arc à supprimer.", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Change la couleur de l'arc sélectionné
    private void modifierCouleurArc() {
        if (arcSelectionne != null) {
            Color nouvelleCouleur = JColorChooser.showDialog(this, "Choisir une nouvelle couleur", arcSelectionne.getCouleur());
            if (nouvelleCouleur != null) {
                arcSelectionne.setCouleur(nouvelleCouleur);
                ZoneDeGraphe.repaint();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un arc dont vous voulez modifier la couleur.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Sauvegarde le graphe dans un fichier
    private void sauvegarderGraphe() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                graphe.sauvegarder(file.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la sauvegarde : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Charge un graphe à partir d'un fichier
    public void chargerGraphe() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Graphe grapheCharge = Graphe.charger(file.getAbsolutePath());
                chargerGraphe(grapheCharge);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Met à jour l'affichage après le chargement d'un graphe
    public void chargerGraphe(Graphe grapheCharge) {
        graphe = grapheCharge;
        ZoneDeGraphe.setGraphe(graphe);
        ZoneDeGraphe.repaint();
    }
}
