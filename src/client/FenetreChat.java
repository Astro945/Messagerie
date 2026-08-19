package client;

import javax.swing.*;
import java.awt.event.*;

public class FenetreChat extends JFrame { 

    private JTextPane zone = new JTextPane(); // Utilisation de JTextPane pour supporter le format HTML (couleurs)
    private JScrollPane scroll; 
    private int id; 
    private String monPseudo;
    private Timer timer; 

    public FenetreChat(int id, String titre, String pseudo) {
        this.id = id;
        this.monPseudo = pseudo; 
        
        setTitle("Chat: " + titre); 
        setSize(400, 500);
        setLocationRelativeTo(null);
        setLayout(null);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

        JButton bMembres = new JButton("Membres"); // Bouton pour afficher la liste des membres de la discussion
        bMembres.setBounds(10, 10, 100, 25);
        add(bMembres);

        zone.setEditable(false); 
        zone.setContentType("text/html"); // Active le mode HTML pour le rendu avec couleur

        scroll = new JScrollPane(zone); // Ajjout d'une barre de scroll si il y a trop de message
        scroll.setBounds(10, 45, 360, 360);
        add(scroll);

        JTextField tMsg = new JTextField();  // Zone de texte pour écrire un message
        tMsg.setBounds(10, 420, 260, 30);
        add(tMsg);

        JButton bEnv = new JButton("Envoyer"); // Boutton pour envoyer un message
        bEnv.setBounds(280, 420, 90, 30);
        add(bEnv);

        timer = new Timer(2000, new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                charger(); // Rafraîchissement automatique des messages
            }
        });
        timer.start();
        charger(); 

        bEnv.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                if (!tMsg.getText().isEmpty()) { 
                    GestionReseau.envoyerMessage(id, tMsg.getText()); 
                    tMsg.setText(""); 
                    charger(); // Force une mise à jour immédiate après envoi
                }
            }
        });

        bMembres.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                new FenetreMembres(titre, GestionReseau.recupererMembres(id)); // sert à la récupération des messages
            }
        });

        setVisible(true);
    }

    public void dispose() { 
        if (timer != null) timer.stop(); // Arrêt du timer pour libérer les ressources
        super.dispose();
    }

    private void charger() { 
        String rawData = GestionReseau.recupererMessages(id);

        if (rawData == null || rawData.isEmpty() || rawData.equals("ERREUR")) return; // Sécurité anti-crash si données vides

        StringBuilder html = new StringBuilder("<html><body style='font-family:sans-serif; font-size:10px;'>"); // StringBuilder pour pouvoir assembler les différentes parti des messages
        
        String[] messages = rawData.split("__"); // Découpage des messages reçus

        for (String msg : messages) {
            String[] parts = msg.split("::");

            if (parts.length >= 3) { 
                String auteur = parts[0];
                String contenu = parts[1];
                String date = parts[2];
                
                String couleur = "black"; // Couleur par défaut pour les autres utilisateurs
                
                if (auteur.equals(monPseudo)) {
                    couleur = "blue"; // Si c'est moi, on met en bleu
                }

                html.append("<span style='color:").append(couleur).append("'>") // Construction de la ligne HTML colorée
                    .append("<b>").append(auteur).append(" (").append(date).append("): </b>")
                    .append("</span>")
                    .append(contenu)
                    .append("<br>");
            }
        }
        
        html.append("</body></html>");

        if (!zone.getText().equals(html.toString())) { // Rafraichissement seulement si le texte a changé

            int positionActuelle = scroll.getVerticalScrollBar().getValue(); // Mémorise la position actuelle du scroll

            zone.setText(html.toString()); // Applique le nouveau texte

            SwingUtilities.invokeLater(new Runnable() { // Restaure la position une fois l'affichage terminé
                public void run() {
                    scroll.getVerticalScrollBar().setValue(positionActuelle);
                }
            });
        }
    }
}