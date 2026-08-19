package serveur;

import tools.Database;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ServiceMessage {

    public static String getHistorique(int idDiscussion) {      // Récupération historique des messages d'une discussion
        StringBuilder resultat = new StringBuilder();
        String sql = "SELECT u.pseudo, m.contenu, m.date_envoi " +      // On récupère pseudo, contenu et date du message
                      "FROM MESSAGE m " +
                      "JOIN UTILISATEUR u ON m.id_expediteur = u.id_user " +
                      "WHERE m.id_discussion = ? " +
                      "ORDER BY m.date_envoi ASC";

        try (Connection conn = Database.getConnection();        // Connexion
             PreparedStatement ps = conn.prepareStatement(sql)) {       // Préparation de la requète

            ps.setInt(1, idDiscussion);     // Remplacement de la variable
            ResultSet rs = ps.executeQuery();       // Exécution de la requète

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {     // On parcourt tous les messages trouvés
                resultat.append(rs.getString("pseudo"))
                        .append("::") 
                        .append(rs.getString("contenu"))
                        .append("::") 
                        .append(sdf.format(rs.getTimestamp("date_envoi")))
                        .append("__");
            }		// Changement des séparateurs en ::

        } catch (SQLException e) {
            System.err.println("Erreur getHistorique : " + e.getMessage());
            return "";
        }

        return resultat.toString();
    }

    public static boolean enregistrerMessage(int idDiscussion, String pseudoExpediteur, String contenu) {       // Enregistre un message dans la base
        String sql = "INSERT INTO MESSAGE (contenu, id_expediteur, id_discussion, date_envoi) " +       // Insertion des données associées au message
                      "VALUES (?, (SELECT id_user FROM UTILISATEUR WHERE pseudo = ?), ?, NOW())";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contenu);       // Remplacement des paramètres
            ps.setString(2, pseudoExpediteur);
            ps.setInt(3, idDiscussion);

            return ps.executeUpdate() > 0;      // Si plusieurs lignes alors OK

        } catch (SQLException e) {
            System.err.println("Erreur enregistrerMessage : " + e.getMessage());
            return false;
        }
    }
}