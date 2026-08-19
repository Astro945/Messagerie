package serveur;

import tools.Database;
import java.sql.*;

public class ServiceDiscussion {

    public static String getListeDiscussions(String pseudo, boolean isAdmin) {      // Récupération de la liste des discussions visibles par un utilisateur
        StringBuilder resultat = new StringBuilder();

        String sql = isAdmin ? // On vérifie si admin ou non
            // Si oui
            "SELECT id_discussion, titre FROM DISCUSSION" :
            // Sinon
            "SELECT d.id_discussion, d.titre FROM DISCUSSION d " +
            "JOIN MEMBRE_DISCUSSION md ON d.id_discussion = md.id_discussion " +
            "JOIN UTILISATEUR u ON md.id_user = u.id_user " +
            "WHERE u.pseudo = ? AND (d.date_expiration IS NULL OR d.date_expiration > NOW())";

        try (Connection conn = Database.getConnection();        // Connexion
             PreparedStatement ps = conn.prepareStatement(sql)) {       // Préparation de la requête

            if (!isAdmin) ps.setString(1, pseudo);

            ResultSet rs = ps.executeQuery();       // Exécution de la requête
            while (rs.next()) {     // On parcourt les discussions trouvées
                resultat.append(rs.getInt("id_discussion"))
                        .append(":")
                        .append(rs.getString("titre"))
                        .append("__");
            }

        } catch (SQLException e) {
            System.err.println("Erreur getListeDiscussions : " + e.getMessage());
            return "ERREUR";
        }

        return resultat.toString();
    }

    public static boolean creerDiscussionComplete(String titre, String createur, String dateFin, String membres) {      // Création d'une discussion + ajout créateur et membres
        boolean hasDate = !dateFin.equals("NULL");
        String sql = "INSERT INTO DISCUSSION (titre, date_expiration) VALUES (?, " + (hasDate ? "?" : "NULL") + ")";        // Insertion titre et date d'expiration de la discussion

        try (Connection conn = Database.getConnection();        // Connexion
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {      // Préparation de la requête et récupération clé

            ps.setString(1, titre);     // Remplacement des paramètres
            if (hasDate) ps.setString(2, dateFin);
            ps.executeUpdate();     // On exécute l'insertion

            ResultSet rs = ps.getGeneratedKeys();       // On récupère l'ID de la discussion
            if (!rs.next()) return false;

            int idDisc = rs.getInt(1);

            ajouterMembre(conn, idDisc, createur);      // Ajout du créateur comme membre

            if (!membres.isEmpty()) {       // Ajout des membres invités
                for (String invite : membres.split(",")) {
                    ajouterMembre(conn, idDisc, invite.trim());
                }
            }

            return true;

        } catch (Exception e) {
            System.err.println("Erreur creerDiscussionComplete : " + e.getMessage());
            return false;
        }
    }

    private static void ajouterMembre(Connection conn, int idDisc, String pseudo) throws SQLException {     // Ajout membre dans une discussion
        String sql = "INSERT INTO MEMBRE_DISCUSSION (id_discussion, id_user) " +        // On insère l'ID de la discussion et de l'utilisateur
                     "SELECT ?, id_user FROM UTILISATEUR WHERE pseudo = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {       //Préparation de la requête
            ps.setInt(1, idDisc);
            ps.setString(2, pseudo);
            ps.executeUpdate();
        }
    }

    public static String getMembres(int idDiscussion) {     // Récupération liste des membres d'une discussion
        StringBuilder resultat = new StringBuilder();

        String sql = "SELECT u.pseudo FROM UTILISATEUR u " +        // On récupère tous les pseudos des membres
                     "JOIN MEMBRE_DISCUSSION md ON u.id_user = md.id_user " +
                     "WHERE md.id_discussion = ? " +
                     "ORDER BY u.pseudo ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDiscussion);     // On met l'ID de la discussion

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (resultat.length() > 0) resultat.append(",");
                resultat.append(rs.getString("pseudo"));
            }

        } catch (SQLException e) {
            System.err.println("Erreur getMembres : " + e.getMessage());
            return "";
        }

        return resultat.toString();     // Retour sous forme : "Alice,Bob,Charlie"
    }
}
