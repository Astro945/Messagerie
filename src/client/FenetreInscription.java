package client;

import javax.swing.*;
import java.awt.event.*;

public class FenetreInscription extends JFrame { // Fenêtre permettant de créer un nouveau compte

    public FenetreInscription() {
        setTitle("Inscription");
        setSize(300, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // Layout null pour positionnement absolu

        JLabel l1 = new JLabel("Pseudo:");
        l1.setBounds(20, 20, 100, 25);
        add(l1);
        JTextField tPseudo = new JTextField(); // Champ de saisie du pseudo
        tPseudo.setBounds(120, 20, 140, 25);
        add(tPseudo);

        JLabel l2 = new JLabel("Mdp:");
        l2.setBounds(20, 60, 100, 25);
        add(l2);
        JPasswordField tMdp = new JPasswordField(); // Champ de saisi du mot de passe
        tMdp.setBounds(120, 60, 140, 25);
        add(tMdp);

        JLabel l3 = new JLabel("Confirmer:");
        l3.setBounds(20, 100, 100, 25);
        add(l3);
        JPasswordField tConfirm = new JPasswordField(); // Champ de confirmation du mot de passe pour éviter les fautes
        tConfirm.setBounds(120, 100, 140, 25);
        add(tConfirm);

        JButton bValider = new JButton("Valider"); // Lance l'inscription
        bValider.setBounds(50, 150, 180, 30);
        add(bValider);

        JButton bRetour = new JButton("Retour"); // Annule et revient au login
        bRetour.setBounds(50, 190, 180, 30);
        add(bRetour);

        bValider.addActionListener(new ActionListener() { // Logique de validation du formulaire
            public void actionPerformed(ActionEvent e) {
                String p = tPseudo.getText();
                String m = new String(tMdp.getPassword());
                String c = new String(tConfirm.getPassword());

                if (p.isEmpty()) { // Vérification pseudo non vide
                    JOptionPane.showMessageDialog(null, "Le pseudo ne peut pas être vide");
                    return;
                }

                if (!m.equals(c)) { // Vérification mot de passe identique
                    JOptionPane.showMessageDialog(null, "Les mots de passe ne correspondent pas");
                    return;
                }
     
                if (m.length() < 10) { // Vérification longueur
                    JOptionPane.showMessageDialog(null,
                        "Le mot de passe doit contenir au minimum 10 caractères");
                    return;
                }
                
                if (!m.matches(".*[a-z].*")) { // Vérification minuscule
                    JOptionPane.showMessageDialog(null,
                        "Le mot de passe doit contenir au moins une minuscule");
                    return;
                }

                if (!m.matches(".*[A-Z].*")) { // Vérification majuscule
                    JOptionPane.showMessageDialog(null,
                        "Le mot de passe doit contenir au moins une majuscule");
                    return;
                }

                if (!m.matches(".*[0-9].*")) {  // Vérification chiffre
                    JOptionPane.showMessageDialog(null,
                        "Le mot de passe doit contenir au moins un chiffre");
                    return;
                }

                String mHash = GestionReseau.hasherMotDePasseMD5(m); //Hashage MD5 pour ne pas envoyer le mot de passe en clair sur le réseau

                if (GestionReseau.demanderInscription(p, mHash)) { // Inscription : Envoi du pseudo et du hash au serveur, renvoie true si le compte est créé
                    JOptionPane.showMessageDialog(null, "Compte créé"); // Succès
                    dispose(); // Ferme la fenêtre
                    new FenetreAuth();  // Retour à la connexion
                } else {
                    JOptionPane.showMessageDialog(null, "Pseudo pris");  // Erreur si le pseudo existe déjà
                }
            }
        });


        bRetour.addActionListener(new ActionListener() { // Bouton retour simple
            public void actionPerformed(ActionEvent e) {
                dispose();
                new FenetreAuth();
            }
        });

        setVisible(true);
    }
}