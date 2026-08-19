package client;

import java.io.*;
import java.net.Socket;

public class GestionReseau {

    private static Socket s;
    private static PrintWriter ecrire;
    private static BufferedReader lire;
    
    private synchronized static String send(String c, boolean r) {
        try {
            if (s == null || s.isClosed()) {                                         //
                s = new Socket("localhost", 5000);                                   // si il n'y a plus ou pas de socket, on en réouvre un, avec lecture et écriture 
                ecrire = new PrintWriter(s.getOutputStream(), true);                      //
                lire = new BufferedReader(new InputStreamReader(s.getInputStream()));   //
            }
            ecrire.println(c);  // envoie la chaine de caractère associé à la méthode demandé par les autres fichiers
            return r ? lire.readLine() : null; // savoir si on attends une réponse ou pas. Si oui, sur écoute, sinon null.
        } catch (IOException e) {
            // Si le socket plante, on le force à null pour qu'il se recrée propre au prochain appel
            try { s.close(); } catch (Exception ex) {}
            s = null;
            return null;
        }
    }

    public static boolean tenterConnexion(String u, String p) {      // tentative de connexion avec user et pswd
        return "AUTH_OK".equals(send("LOGIN::" + u + "::" + p, true));  // envoie user et pswd au serveur et retourne true uniquement si le serveur confirme la connexion avec AUTH_OK
    }

    public static boolean demanderInscription(String u, String p) {     // tentative de création de user avec user de pswd
        return "REG_OK".equals(send("REGISTER::" + u + "::" + p, true)); // envoie user et pswd au serveur et retourne true uniquement si le serveur confirme la connexion avec AUTH_OK
    }

    public static String recupererDiscussions() {  // tentative de récupérer les discussions d'un USER précis
        String r = send("GET_DISCUSSIONS", true);  // envoie la commande get_discussion au serveur et attends une réponse avec le true
        return r != null && r.startsWith("LISTE::") ? r.substring(7) : ""; // si le serveur réponds, et qu'il réponds avec un "liste::" au début, alors on retire les 7 premiers caractère (LISTE::), sinon rien
    }

    public static String recupererMessages(int id) {  // tentative de récupérer les messages d'une discussion avec son ID
        String r = send("GET_MESSAGES::" + id, true);  // envoie la commande get_message + id au serveur et attends une réponse avec le true
        return r != null && r.startsWith("HISTORIQUE::") ? r.substring(12) : "";  // si le serveur réponds, et qu'il réponds avec un "historique::" au début, alors on retire les 12 premiers caractère (HISTORIQUE::), sinon rien
    }

    public static void envoyerMessage(int id, String m) {  // tentative d'envoyer un message avec comme variable id de discussion et le contennu du message 
        send("SEND_MSG::" + id + "::" + m, false);   // envoie du message sous forme de "SEND_MSG::id::message" avec un false donc n'attends pas de réponse
    }

    public static boolean creerDiscussion(String name, String createur, String date, String membre) {  // tentative de création d'une discussion avec son nom, sois même, les autres membres et la date d'expiration ou non
        return "CREATE_OK".equals(send("CREATE_DISC::" + name + "::" + createur + "::" + date + "::" + membre, true)); // envoie les infos au serveur et retourne true uniquement si le serveur confirme la connexion avec CREATE_OK
    }
    
    public static String[] recupererUtilisateurs() {
        String rep = send("GET_USERS", true);
        if (rep != null && rep.startsWith("USERS::")) {
            return rep.substring(7).split(",");
        }
        return new String[0];
    }

    public static String recupererMembres(int id) { // tentative de recupération des membres de la discussion souhaité
        String r = send("GET_MEMBERS::" + id, true); // envoie la commande get_member + id au serveur et attends une réponse avec le true
        return r != null && r.startsWith("MEMBERS::") ? r.substring(9) : ""; // si le serveur réponds, et qu'il réponds avec un "MEMBERS::" au début, alors on retire les 9 premiers caractère (MEMBERS::), sinon rien
    }
    
    // fonction qui s'occupe du hachage 
    public static String hasherMotDePasseMD5(String motDePasse) {  
        try {    
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");  // Création de l'outil de hachage utilisant l'algorithme MD5
            byte[] array = md.digest(motDePasse.getBytes());  //Prends le mot de passe, et le met sous forme de tableau
            StringBuffer sb = new StringBuffer();   // Buffer permettant de construire la chaîne finale du hash
            for (int i = 0; i < array.length; ++i) {   // Parcours de chaque octet du hash binaire
                sb.append(Integer.toHexString((array[i] & 0xFF)|0x100).substring(1, 3));  // Convertit chaque octet du hash binaire en 2 caractères hexadécimaux afin de construire le hash MD5 lisible final                                             
            }
            return sb.toString();  // Retourne le hash MD5 final sous forme de String
        } catch (java.security.NoSuchAlgorithmException e) { 
            return null; 
        }
    }
}