package client;

import javax.swing.*;

public class FenetreMembres extends JFrame { // Petite fenêtre affichant la liste des participants

    public FenetreMembres(String titre, String membres) {
        setTitle("Membres: " + titre); // Affiche le nom du groupe dans le titre
        setSize(250, 300);
        setLocationRelativeTo(null);
        setLayout(null);

        DefaultListModel<String> model = new DefaultListModel<>(); // Modèle de données pour la liste graphique
        if(membres != null && !membres.isEmpty()) { // Vérifie qu'il y a bien des données
            for(String s : membres.split(",")) model.addElement(s); // Découpe la chaîne reçue (ex: "naim,juline") et remplit la liste
        }

        JScrollPane sc = new JScrollPane(new JList<>(model)); // Crée la liste visuelle avec barre de défilement
        sc.setBounds(10, 10, 215, 200);
        add(sc);

        JButton bFermer = new JButton("Fermer");
        bFermer.setBounds(70, 220, 100, 30);
        add(bFermer);
        
        bFermer.addActionListener(e -> dispose()); // Ferme la fenêtre au clic

        setVisible(true);
    }
}