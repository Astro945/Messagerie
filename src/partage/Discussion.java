package partage;

import java.io.Serializable;
import java.util.Date;

public class Discussion implements Serializable { // Serializable permet à l'objet de voyager via les Socket

    private int id; // Identifiant unique de la discussion en BDD
    private String titre; // Nom du groupe ou de la conversation
    private Date dateFin; // Date d'expiration (si null, la discussion est permanente)

    public Discussion(int id, String titre, Date dateFin) {
        this.id = id;
        this.titre = titre;
        this.dateFin = dateFin;
    }

    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public Date getDateFin() {
        return dateFin;
    }
    public boolean isEphemere() { // Permet de savoir facilement si c'est une discussion temporaire
        return dateFin != null;
    }

    @Override
    public String toString() {
        return titre; // Permet d'afficher le nom directement dans les JList graphique
    }
}