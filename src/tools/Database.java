package tools;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

public class Database {

    private static String url;      // Variables contenant les informations de connexion
    private static String user;
    private static String pwd;

    static {        // Bloc statique exécuté au chargement de la classe
        try {
            Properties p = new Properties();        
            try (FileInputStream fis = new FileInputStream("properties/configuration.properties")) {        // Lit le fichier de configuration
                p.load(fis);        // Chargement des valeurs
            }

            url  = "jdbc:mariadb://" + p.getProperty("db_host") + "/" + p.getProperty("db_name");       // Construction de l'URL JDBC
            user = p.getProperty("db_user");
            pwd  = p.getProperty("db_pwd");

            Class.forName("org.mariadb.jdbc.Driver");       // Chargement du driver MariaDB

        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la base de données : " + e.getMessage());
        }
    }

    public static Connection getConnection() {      // Ouvre connexion à la base de données
        try {
            return DriverManager.getConnection(url, user, pwd);     // On utilise les valeurs chargées dans le bloc statique
        } catch (SQLException e) {
            System.err.println("Erreur connexion BDD : " + e.getMessage());
            return null;
        }
    }
}
