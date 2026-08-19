package partage;

import java.io.Serializable;

public class Utilisateur implements Serializable { // Objet représentant un utilisateur, sérialisable pour être envoyer via les Socket

    private int id; // Identifiant unique de l'utilisateur en BDD
    private String pseudo; // Le nom de l'utilisateur qui sera affiché

    public Utilisateur(int id, String pseudo) {
        this.id = id;
        this.pseudo = pseudo;
    }

    public int getId() {
        return id;
    }

    public String getPseudo() {
        return pseudo;
    }

    @Override
    public String toString() {
        return pseudo; // Permet d'afficher juste le pseudo dans les listes de l'interface
    }
}