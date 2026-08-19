package partage;
import java.io.Serializable;

public enum TypeMessage implements Serializable { // Enumération définissant le vocabulaire du protocole réseau
    LOGIN, INSCRIPTION, TEXTE, DECONNEXION, INFO_SERVEUR, REFRESH_LIST // Liste des commandes reconnues par le serveur et le client
}