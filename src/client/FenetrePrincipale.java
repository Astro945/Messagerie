package client;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList; // Import nécessaire pour la liste cachée

public class FenetrePrincipale extends JFrame { // Fenêtre principale : Liste les discussion de l'utilisateur

    private DefaultListModel<String> model = new DefaultListModel<>(); // Modèle de données pour la liste (Affichage)
    private ArrayList<Integer> listeIds = new ArrayList<>(); // Liste qui Stocke les IDs correspondant au discussions affichées
    private String pseudo; // Mémorise qui est connecté
    private JList<String> liste; // On déclare la liste ici pour y accéder dans le refresh

    public FenetrePrincipale(String pseudo) {
        this.pseudo = pseudo;
        setTitle("Discussions de " + pseudo); // Personnalise le titre de la fenetre
        setSize(350, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Ferme l'application complète ici
        setLocationRelativeTo(null);
        setLayout(null);

        liste = new JList<>(model); // Composant graphique affichant les lignes
        JScrollPane scroll = new JScrollPane(liste); // Ajoute le scroll si la liste est longue
        scroll.setBounds(20, 20, 290, 300);
        add(scroll);

        JButton bOuvrir = new JButton("Ouvrir"); // Bouton pour entrer dans un chat
        bOuvrir.setBounds(20, 340, 130, 30);
        add(bOuvrir);

        JButton bCreer = new JButton("Nouveau"); // Bouton pour créer un groupe
        bCreer.setBounds(160, 340, 150, 30);
        add(bCreer);
        
        // Système de rafraîchissement automatique toutes les 3 secondes
        new Timer(3000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refresh(); // Met à jour la liste des discussions
            }
        }).start();
        refresh(); // Premier appel immédiat au lancement

        bOuvrir.addActionListener(new ActionListener() { // Action "Ouvrir"
            public void actionPerformed(ActionEvent e) {
                int index = liste.getSelectedIndex(); // On récupère la position du clic/de la ligne (0, 1, 2...)
                
                if (index != -1) { // Si quelque chose est sélectionné (on commence à -1 pour un index car 0 peux être une ligne sélectionné)
                    int id = listeIds.get(index); // On récupère l'ID caché correspondant à la position
                    String titre = model.getElementAt(index); // On récupère le nom affiché
                    
                    new FenetreChat(id, titre, pseudo); // Ouvre le chat avec les bonnes infos
                }
            }
        });

        bCreer.addActionListener(new ActionListener() { // Action "Nouveau"
            public void actionPerformed(ActionEvent e) {
                new FenetreCreation(pseudo); // Ouvre le formulaire de création d'une discussion
                refresh(); // Force un rafraîchissement pour voir le nouveau groupe
            }
        });

        setVisible(true);
    }

    private void refresh() { // Méthode qui interroge le serveur pour avoir la liste à jour
        String data = GestionReseau.recupererDiscussions(); // Réception chaîne brute
        if (data.isEmpty() || data.equals("ERREUR")) return; // Si data est vide ou que data contient "ERREUR" alors on ne retourne rien pour ne pas créer de problème de rafraichissement 

        String selection = liste.getSelectedValue(); // On sauvegarde la sélection actuelle (le titre)

        model.clear(); // Vide la liste actuelle (affichage)
        listeIds.clear(); // Vide la liste cachée (IDs)
        
        String[] tab = data.split("__"); // Le serveur sépare les discussions par "__"
        for (String s : tab) {
            // Le serveur envoie "id:titre". On coupe en deux.
            String[] parts = s.split(":");
            if (parts.length == 2) {
                int id = Integer.parseInt(parts[0]); // On transforme l'ID en entier pour puvoir ouvrir fenetreChat (une ch^^ine de caractère à la base)
                String titre = parts[1]; // Partie droite : Titre de la discussion
                
                model.addElement(titre); // On affiche juste le titre (PROPRE)
                listeIds.add(id); // On stocke l'ID dans une autre liste pour pas l'afficher
            }
        }

        if (selection != null) {
            liste.setSelectedValue(selection, true); // On remet la sélection si elle existe toujours
        }
    }
}