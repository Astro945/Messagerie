package serveur;

import tools.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceAuth {

    private static Connection getConn() throws SQLException {       // Ouvre une connexion à la base
        return Database.getConnection();
    }

    public static boolean verifierIdentifiants(String pseudo, String mdp) {     // Vérifie si un utilisateur existe avec ce pseudo et mot de passe
        // Modification : On a retiré MD5() car le mot de passe arrive déjà chiffré du client
        String sql = "SELECT COUNT(*) FROM UTILISATEUR WHERE pseudo = ? AND mot_de_passe = ?";      // Compte le nombre d'utilisateurs correspondant

        try (Connection conn = getConn();       // Ouverture de la connexion
             PreparedStatement ps = conn.prepareStatement(sql)) {       // Préparation de la requête

            ps.setString(1, pseudo);        // Remplacement des paramètres
            ps.setString(2, mdp);

            ResultSet rs = ps.executeQuery();       // Exécution de la requête SELECT
            return rs.next() && rs.getInt(1) > 0;       // On récupère la valeur de COUNT(*)

        } catch (SQLException e) {      // En cas d'erreur
            System.err.println("Erreur vérification identifiants : " + e.getMessage());
            return false;
        }
    }

    public static boolean inscrireUtilisateur(String pseudo, String mdp) {      // Inscrit un nouvel utilisateur dans la base
        // Modification : On a retiré MD5() car le mot de passe arrive déjà chiffré du client
        String sql = "INSERT INTO UTILISATEUR (pseudo, mot_de_passe) VALUES (?, ?)";       // Insertion du pseudo et mot de passe

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pseudo);        
            ps.setString(2, mdp);

            return ps.executeUpdate() > 0;      // executeUpdate() renvoie le nombre de lignes affectées

        } catch (SQLException e) {      
            System.err.println("Échec inscription : " + e.getMessage());
            return false;
        }
    }
    
    public static List<String> getAllUsers() {
        List<String> users = new ArrayList<>();			// Création liste vide contenant les pseudos trouvés
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT pseudo FROM UTILISATEUR ORDER BY pseudo")) {			// Récupération pseudos triés par ordre alphabétique

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(rs.getString("pseudo"));			// On récupère la colonne "pseudo" pour chaque ligne
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static boolean estAdmin(String pseudo) {     // Vérifie si un utilisateur est admin
        String sql = "SELECT est_admin FROM UTILISATEUR WHERE pseudo = ?";      // On récupère la colonne est_admin

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pseudo);

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getBoolean("est_admin");     // On récupère true/false

        } catch (SQLException e) {
            System.err.println("Erreur vérification admin : " + e.getMessage());
            return false;
        }
    }
}