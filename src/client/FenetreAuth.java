package client;

import javax.swing.*;
import java.awt.event.*;

public class FenetreAuth extends JFrame { // Hérite de JFrame pour créer une fenêtre graphique

    public FenetreAuth() {
        setTitle("Connexion"); // Titre de la fenêtre
        setSize(300, 250); // Dimensions (largeur, hauteur)
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Ferme le programme quand on clique sur la croix
        setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        setLayout(null); // Désactive le placement automatique

        JLabel l1 = new JLabel("Pseudo:"); // Texte d'affichage simple
        l1.setBounds(20, 20, 80, 25); // Position (x, y) et taille (largeur, hauteur)
        add(l1); // Ajoute l'élément à la fenêtre

        JTextField tPseudo = new JTextField(); // Champ de saisie pour le pseudo
        tPseudo.setBounds(100, 20, 160, 25);
        add(tPseudo);

        JLabel l2 = new JLabel("Mdp:");
        l2.setBounds(20, 60, 80, 25);
        add(l2);

        JPasswordField tMdp = new JPasswordField(); // Champ mot de passe (caractères masqués)
        tMdp.setBounds(100, 60, 160, 25);
        add(tMdp);

        JButton bConnect = new JButton("Connexion"); // Bouton pour valider
        bConnect.setBounds(20, 110, 120, 30);
        add(bConnect);

        JButton bInscrip = new JButton("S'inscrire"); // Bouton pour aller s'inscrire
        bInscrip.setBounds(150, 110, 110, 30);
        add(bInscrip);

        bConnect.addActionListener(new ActionListener() { // Action quand on clique sur Connexion
            public void actionPerformed(ActionEvent e) {
                // Envoie pseudo/mdp au serveur. Si true , on se connecte.
                String mHash = GestionReseau.hasherMotDePasseMD5(new String(tMdp.getPassword())); // Hashage MD5 pour ne pas envoyer le mot de passe en clair sur le réseau

                if (GestionReseau.tenterConnexion(tPseudo.getText(), mHash)) { // Connexion : Envoi du pseudo et du hash au serveur, renvoie true si validé
                    dispose(); // Ferme la fenêtre de connexion
                    new FenetrePrincipale(tPseudo.getText()); // Ouvre la fenêtre principale
                } else {
                    JOptionPane.showMessageDialog(null, "Erreur login"); // Affiche une popup d'erreur sinon
                }
            }
        });

        bInscrip.addActionListener(new ActionListener() { // Action quand on clique sur S'inscrire
            public void actionPerformed(ActionEvent e) {
                dispose(); // Ferme la fenêtre actuelle
                new FenetreInscription(); // Ouvre la fenêtre d'inscription
            }
        });

        setVisible(true); // Rend la fenêtre visible pour l'utilisateur
    }
}