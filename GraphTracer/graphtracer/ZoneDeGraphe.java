package graphtracer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;

public class ZoneDeGraphe extends JPanel {

    private Graphe graphe;
    private Tache tacheSelectionnee;
    private Arc arcSelectionne;

    public ZoneDeGraphe(Graphe graphe) {
        this.graphe = graphe;
        setBackground(Color.WHITE);
    }

    public void setGraphe(Graphe graphe) {
        this.graphe = graphe;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (graphe != null) {
            dessinerArcs(g2d);
            dessinerTaches(g2d);
        }
    }

    public void setTacheSelectionnee(Tache tacheSelectionnee) {
        this.tacheSelectionnee = tacheSelectionnee;
        repaint();
    }

    public void setArcSelectionne(Arc arcSelectionne) {
        this.arcSelectionne = arcSelectionne;
        repaint();
    }

    private void dessinerArcs(Graphics2D g) {
        List<Arc> arcs = graphe.getArcs();
        for (Arc arc : arcs) {
            Tache origine = arc.getOrigine();
            Tache destination = arc.getDestination();

            if (origine != null && destination != null) {
                Point2D.Double p1 = new Point2D.Double(origine.getPx() + 25, origine.getPy() + 25);
                Point2D.Double p2 = new Point2D.Double(destination.getPx() + 25, destination.getPy() + 25);

                // Dessiner la ligne principale
                g.setColor(arc.getCouleur());
                g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());

                // Dessiner la flèche à la fin
                dessinerFleche(g, p1, p2, 10, 7);

                // Afficher la durée au milieu de l’arc
                g.setColor(Color.black);
                int midX = (int)((p1.getX() + p2.getX()) / 2);
                int midY = (int)((p1.getY() + p2.getY()) / 2);
                g.drawString(String.valueOf(arc.getOrigine().getDuree()), midX, midY);
            }

            if (arcSelectionne != null && arc == arcSelectionne) {
                if (origine != null && destination != null) {
                    Point2D.Double p1 = new Point2D.Double(origine.getPx() + 25, origine.getPy() + 25);
                    Point2D.Double p2 = new Point2D.Double(destination.getPx() + 25, destination.getPy() + 25);
                    g.setColor(Color.green);
                    g.setStroke(new BasicStroke(3));
                    g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
                    dessinerFleche(g, p1, p2, 12, 9);  // flèche plus grosse pour sélection
                    g.setStroke(new BasicStroke(1));
                }
            }
        }
    }

    private void dessinerFleche(Graphics2D g, Point2D.Double p1, Point2D.Double p2, int longueur, int largeur) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double angle = Math.atan2(dy, dx);

        // Décale l'extrémité pour que la flèche arrive au bord du cercle
        double dist = Math.sqrt(dx*dx + dy*dy);
        double ratio = (dist - 25.0) / dist; // 25 = rayon du cercle
        int x = (int) (p1.x + dx * ratio);
        int y = (int) (p1.y + dy * ratio);

        AffineTransform old = g.getTransform();
        g.translate(x, y);
        g.rotate(angle - Math.PI / 2);

        Polygon fleche = new Polygon();
        fleche.addPoint(0, 0);
        fleche.addPoint(-largeur / 2, -longueur);
        fleche.addPoint(largeur / 2, -longueur);

        g.fill(fleche);
        g.setTransform(old);
    }

    public void dessinerContourTacheCritique(Graphics2D g, Tache tache) {
        int x = tache.getPx();
        int y = tache.getPy();
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3));
        g.drawOval(x, y, 50, 50);
        g.setStroke(new BasicStroke(1)); // Réinitialiser à l'épaisseur par défaut
    }

    public void annulerContourTacheCritique(Graphics2D g, Tache tache) {
        int x = tache.getPx();
        int y = tache.getPy();
        g.setColor(Color.black);
        g.setStroke(new BasicStroke(1));
        g.drawOval(x, y, 50, 50);
        g.setStroke(new BasicStroke(1)); // Réinitialiser à l'épaisseur par défaut
    }
    
    private void dessinerTaches(Graphics2D g) {
        List<Tache> taches = graphe.getTaches();
        for (Tache tache : taches) {
            // Remplissage du cercle
            g.setColor(tache.getCouleur());
            int x = tache.getPx();
            int y = tache.getPy();
            g.fillOval(x, y, 50, 50);

            // Contour rouge si tâche critique
            if (tache.estCritique()) {
            	dessinerContourTacheCritique(g,tache);
            } else {
                g.setColor(Color.BLACK);
                g.drawOval(x, y, 50, 50);
                annulerContourTacheCritique(g,tache);
            }

            String nom = tache.getNom();
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(nom);
            int textX = x + (50 - textWidth) / 2;
            int textY = y + (50 + fm.getAscent()) / 2;
            g.drawString(nom, textX, textY);

            if (tacheSelectionnee != null && tache == tacheSelectionnee) {
                g.setColor(Color.green);
                Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                g.setStroke(dashed);
                g.drawOval(x, y, 50, 50);
                g.setStroke(new BasicStroke());
            }
        }
    }
}
