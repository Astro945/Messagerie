/* TABLE UTILISATEUR gère les comptes. Le mot de passe sera stocké en MD5 via Java. */
CREATE TABLE UTILISATEUR (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    pseudo VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    /* FALSE = Utilisateur, TRUE = Admin (accès global) */
    est_admin BOOLEAN DEFAULT FALSE 
);

/* TABLE DISCUSSION gère les canaux.*/
CREATE TABLE DISCUSSION (
    id_discussion INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(50) NOT NULL,
    date_expiration TIMESTAMP NULL DEFAULT NULL, 
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP /* Heure actuelle */
);

/* TABLE MEMBRE_DISCUSSION (Table d'association) */
CREATE TABLE MEMBRE_DISCUSSION (
    id_user INT,
    id_discussion INT,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (id_user, id_discussion),
    
    /* Clés étrangères : Liens vers les autres tables */
    CONSTRAINT fk_membre_user FOREIGN KEY (id_user) REFERENCES UTILISATEUR(id_user) ON DELETE CASCADE, /* Pour simplifier la suppression de discussion */
    CONSTRAINT fk_membre_disc FOREIGN KEY (id_discussion) REFERENCES DISCUSSION(id_discussion) ON DELETE CASCADE
);

/* TABLE MESSAGE stocke l'historique des échanges. */
CREATE TABLE MESSAGE (
    id_message INT AUTO_INCREMENT PRIMARY KEY,
    contenu TEXT NOT NULL,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    id_expediteur INT NOT NULL,
    id_discussion INT NOT NULL,
    
    /* Clés étrangères */
    CONSTRAINT fk_msg_user FOREIGN KEY (id_expediteur) REFERENCES UTILISATEUR(id_user),
    CONSTRAINT fk_msg_disc FOREIGN KEY (id_discussion) REFERENCES DISCUSSION(id_discussion) ON DELETE CASCADE
);