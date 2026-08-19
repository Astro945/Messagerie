package client;

import javax.swing.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FenetreCreation extends JFrame { // Fenêtre permettant de créer un nouveau groupe de discussion

    private DefaultListModel<String> model = new DefaultListModel<>(); // Liste locale pour stocker les pseudos ajoutés

    public FenetreCreation(String createur) {
        setTitle("Créer Groupe"); // Titre de la fenêtre
        setSize(350, 450);
        setLocationRelativeTo(null);
        setLayout(null); // Layout null pour placer les éléments précisément

        JLabel l1 = new JLabel("Nom:");
        l1.setBounds(20, 20, 50, 25);
        add(l1);
        JTextField tNom = new JTextField(); // Champ pour choisir le nom du groupe
        tNom.setBounds(80, 20, 200, 25);
        add(tNom);

        JCheckBox cEph = new JCheckBox("Éphémère ?"); // Case pour activer la date de fin
        cEph.setBounds(20, 60, 100, 25);
        add(cEph);

        JSpinner spin = new JSpinner(new SpinnerDateModel()); // Sélecteur de date et heure (pour les discussions éphémères)
        spin.setEditor(new JSpinner.DateEditor(spin, "dd/MM/yyyy HH:mm")); // Format d'affichage jour/mois/année
        spin.setBounds(130, 60, 150, 25);
        add(spin);

        String[] users = GestionReseau.recupererUtilisateurs(); // Récupère tous les pseudos en BDD
        JComboBox<String> comboUsers = new JComboBox<>(users); // Liste déroulante des utilisateurs
        comboUsers.setBounds(20, 110, 200, 25);
        add(comboUsers);

        JButton bAdd = new JButton("+"); // Bouton pour ajouter le membre à la liste
        bAdd.setBounds(230, 110, 50, 25);
        add(bAdd);

        JScrollPane sc = new JScrollPane(new JList<>(model)); // Liste des membres ajoutés
        sc.setBounds(20, 150, 260, 150);
        add(sc);

        JButton bOk = new JButton("Créer"); // Bouton final pour valider la création
        bOk.setBounds(100, 320, 100, 30);
        add(bOk);

        bAdd.addActionListener(new ActionListener() { // Action du bouton "+"
            public void actionPerformed(ActionEvent e) {
                String pseudo = (String) comboUsers.getSelectedItem(); // Récupère le pseudo sélectionné

                if (pseudo != null && !model.contains(pseudo)) { // Vérifie si pas vide + pas déjà ajouté
                    model.addElement(pseudo); // Ajoute à la liste visuelle
                }
            }
        });

        bOk.addActionListener(new ActionListener() { // Action du bouton "Créer"
            public void actionPerformed(ActionEvent e) {
                String date = "NULL"; // Par défaut, la discussion est permanente
                if(cEph.isSelected()) { // Si la case éphémère est cochée
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = sdf.format((Date)spin.getValue()); // On formate la date pour la base de données
                }

                String membres = ""; // On va transformer la liste en une seule chaîne de caractères
                for(int i=0; i<model.size(); i++) membres += model.get(i) + ",";

                if(GestionReseau.creerDiscussion(tNom.getText(), createur, date, membres)) { // Envoi au serveur
                    dispose(); // Ferme la fenêtre si succès
                } else {
                    JOptionPane.showMessageDialog(null, "Erreur"); // Affiche une erreur si échec
                }
            }
        });

        setVisible(true);
    }
}
