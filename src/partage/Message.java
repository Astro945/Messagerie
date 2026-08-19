package partage;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable { // Sérialisable pour être envoyé via les Socket

    private int id; // Identifiant unique du message en BDD
    private String contenu; // Le texte du message
    private Date dateEnvoi; // Date et heure de l'envoi
    private Utilisateur auteur; // Objet contenant les infos de l'expéditeur
    private int idDiscussion; // Permet de savoir à quelle conversation appartient ce message

    public Message(int id, String contenu, Date dateEnvoi, Utilisateur auteur, int idDiscussion) { // Constructeur complet (lecture depuis la BDD)
        this.id = id;
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
        this.auteur = auteur;
        this.idDiscussion = idDiscussion;
    }

    public Message(String contenu, Utilisateur auteur, int idDiscussion) { // Constructeur simplifié (création nouveau message)
        this.contenu = contenu;
        this.auteur = auteur;
        this.idDiscussion = idDiscussion;
        this.dateEnvoi = new Date(); // Assigne automatiquement la date/heure actuelle
    }

    public int getId() {
        return id;
    }

    public String getContenu() {
        return contenu;
    }

    public Date getDateEnvoi() {
        return dateEnvoi;
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public int getIdDiscussion() {
        return idDiscussion;
    }

    @Override
    public String toString() {
        return auteur.getPseudo() + " : " + contenu; // Format d'affichage par défaut (Pseudo : Texte)
    }
}