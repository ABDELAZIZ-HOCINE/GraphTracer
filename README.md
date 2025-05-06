# GraphTracer
L’application Java GraphTracer, un outil graphique permettant de modéliser et de suivre l’avancement d’un projet sous forme de graphe orienté. L’utilisateur peut créer, modifier, supprimer et déplacer des tâches (sommets) et des dépendances (arcs), sauvegarder et charger des graphes, et visualiser le chemin critique. 

  - **Gestion des tâches**
  - Ajout/suppression de tâches
  - Modification des propriétés (nom, durée)
  - Déplacement visuel des nœuds
  - Tâches spéciales Début/Fin intégrées

- **Gestion des dépendances**
  - Création de liens entre tâches
  - Visualisation des durées sur les arcs
  - Suppression de relations

- **Persistance des données**
  - Sauvegarde/chargement de projets (.graph)
  - Exportation des configurations

- **Interface utilisateur**
  - Canvas interactif avec AWT/Swing
  - Système de sélection intuitif
  - Palettes de couleurs personnalisables


GraphTracer/
├── GraphTracer.exe # Exécutable Windows
├── README.txt  # Documentation

│
├──# Code source
│ └── graphtracer/
│ ├── Graphe.java # Modèle de graphe
│ ├── Tache.java # Représentation des taches
│ ├── Arc.java # Représentation des arcs
│ └── ... # Autres composants
│
└── img/ # Ressources graphiques
├── graphe_oriente.png # Exemple de graphe orienté
├── graphe_non_or.png # Exemple de graphe non orienté
└── logo.png # Logo de l'application

## 🚀 Installation

### Prérequis
- JDK 17+ ([Téléchargement Oracle](https://www.oracle.com/java/))
- Système d'exploitation : Windows/Linux/macOS

### Compilation du projet :
Pour compiler le projet, placez-vous dans le dossier (GraphTracer) et exécutez la commande suivante dans le terminal :
    % javac graphtracer/*.java
Ensuite, pour lancer l'application, utilisez la commande :
    % java graphtracer/GraphTracer

### Exécution
Version exécutable (Windows)
GraphTracer.exe

## 🖥️ Utilisation

1. **Créer un nouveau projet**
   - Menu : Fichier → Nouveau
   - Bouton : Créer un graphe

2. **Ajouter des tâches**
   - Clic droit → "Ajouter tâche"
   - Saisir nom et durée

3. **Créer des dépendances**
   - Sélectionner tâche source
   - Clic droit → "Créer lien"
   - Sélectionner tâche cible

4. **Exporter le projet**
   - Menu : Fichier → Exporter
   - Formats supportés : .graph (native)

