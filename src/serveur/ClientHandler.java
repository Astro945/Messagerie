package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


//Classe qui reçoit les messages de GestionReseau
public class ClientHandler implements Runnable {
    private Socket s;
    private String pseudo = null;
    private boolean isAdmin = false;

    public ClientHandler(Socket socket) { this.s = socket; }

    @Override
    public void run() {
        try (BufferedReader lire = new BufferedReader(new InputStreamReader(s.getInputStream())); // Initialisation de lecture
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {  // Initialisation de l'écriture

            System.out.println("Client connecté : " + s.getInetAddress()); // Indique la connexion d'un client 
            
            String ligne;

            while ((ligne = lire.readLine()) != null) { // Dés que l'on lis quelque chose ( envoyé par GestionReseau ) 
                String[] parts = ligne.split("::", 5); // Séparé en 5 bloc maximum le message reçu avec comme séparation les "::"
                String commande = parts[0]; // Dire que la commande correspond à la partie 1 ( on commence par 0 )

                System.out.println("CMD [" + (pseudo != null ? pseudo : "Inconnu") + "]: " + commande); // Envoie le nom de la commande dans la console avec le nom de l'utilisateur ou inconnu. ( pour développement )

                switch (commande) { // définir toutes les commandes possibles 
                    case "LOGIN": // dans le cas où c'est LOGIN 
                        if (parts.length == 3 && ServiceAuth.verifierIdentifiants(parts[1], parts[2])) { //Vérifie qu'il contient que le nom de cmd + username + pswd & vérifie dans la BDD leur ressemblance ou non.
                            this.pseudo = parts[1]; // Mémorise son pseudo
                            this.isAdmin = ServiceAuth.estAdmin(pseudo); // Mémorise s'il est admin ou non
                            out.println("AUTH_OK"); // Envoie AUTH_OK à GestionReseau.
                        } else out.println("AUTH_FAIL"); // Sinon envoie AUTH_FAIL à GestionReseau
                        break; // fin du cas 

                    case "REGISTER": // dans le cas où c'est REGISTRER
                        boolean regOk = (parts.length == 3) && ServiceAuth.inscrireUtilisateur(parts[1], parts[2]); // Envoie true si il contient que le nom de cmd + username + pswd & qu'il a reussi a créer un compte
                        out.println(regOk ? "REG_OK" : "REG_FAIL"); // Si il a reçu true, il envoie REG_OK à GestionReseau, sinon il envoie REG_FAIL.
                        break; // fin du cas

                    case "GET_DISCUSSIONS": // dans le cas où c'est GET_DISCUSSION
                        out.println("LISTE::" + ServiceDiscussion.getListeDiscussions(pseudo, isAdmin));// Renvoie la liste des discussions en fonctions de son username et s'il est admin ou non
                        break; // fin du cas

                    case "GET_MESSAGES": // dans le cas où c'est GET_MESSAGE
                        if (parts.length == 2) out.println("HISTORIQUE::" + ServiceMessage.getHistorique(Integer.parseInt(parts[1]))); // Si la commande comporte bien un id de discussion, alors on recupére l'historique d'une discussion avec sont ID qui est convertie en INT au lieu d'un string
                        break; // fin du cas

                    case "SEND_MSG": // dans le cas où c'est SEND_MSG
                        if (parts.length == 3 && pseudo != null) ServiceMessage.enregistrerMessage(Integer.parseInt(parts[1]), pseudo, parts[2]); // Si la commande comporte bien un ID de discussion et un message & que l'utilisateur est bien connecté, alors on envoie un message avec l'id de la discussion, le pseudo de la personne, et le contenu du message 
                        break; // fin du cas
                        
                    case "GET_USERS":
                        // Récupération de tous les pseudos depuis la BDD
                        out.println("USERS::" + String.join(",", ServiceAuth.getAllUsers()));
                        break;

                    case "CREATE_DISC": // dans le cas où c'est CREATE_DISC
                        if (parts.length >= 4) { // Vérifie que l'on a minimum le nom, date et message de la discussion 
                            String membres = (parts.length == 5) ? parts[4] : ""; // Si il y a un cinquième champs, on l'associe aux membres du groupe, et sinon personne
                            boolean ok = ServiceDiscussion.creerDiscussionComplete(parts[1], parts[2], parts[3], membres); // Envoie True si la discussion à été créé avec succès
                            out.println(ok ? "CREATE_OK" : "CREATE_FAIL"); // Si True avant, alors on envoie à GestionReseau, CREATE_OK, et sinon on envoie CREATE_FAIL
                        }
                        break; // fin du cas

                    case "GET_MEMBERS": // dans le cas où c'est GET_MEMBERS
                        if (parts.length == 2 && pseudo != null) { // Si la commande comporte bien un ID de discussion et un message & que l'utilisateur est bien connecté
                            int id = Integer.parseInt(parts[1]); // Récupère l'id de la discussion qui est sous string et le met en int.
                            out.println("MEMBERS::" + ServiceDiscussion.getMembres(id));  // Envoie à GestionReseau la liste des membres correspondant avec l'ID de la discussion
                        	}
                        break; // fin du cas
                }
            }
        } catch (IOException e) { // Si tout ca ne marche pas/plus
            System.out.println("Client déconnecté (" + pseudo + ")"); // pas besoin d'expliquer cela, c'est d'une simplicité sans nom
        } 
        finally { try { if (s != null) s.close(); } catch (IOException e) {} } // Si erreur avant, on ferme quans meme le socket
    }
}