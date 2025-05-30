package modele;

import javax.swing.JPanel;

import controleur.Controle;
import controleur.Global;
import outils.connexion.Connection;

/**
 * Gestion du jeu côté client
 */
public class JeuClient extends Jeu implements Global {
	
	/**
	 * objet de connexion pour communiquer avec le serveur
	 */
	private Connection connection;
	/**
	 * Etat des murs
	 */
	private Boolean mursOk = false;
	
	/**
	 * Controleur
	 * @param controle instance du contrôleur pour les échanges
	 */
	public JeuClient(Controle controle) {
		super.controle = controle;
	}
	
	@Override
	public void connexion(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void reception(Connection connection, Object info) {
		if(info instanceof JPanel) {
			if(!this.mursOk) {
				// arrivée du panel des murs
				this.controle.evenementJeuClient(AJOUTPANELMURS, info);
				this.mursOk = true;
			} else {
				// arrivée du panel de jeu
				this.controle.evenementJeuClient(MODIFPANELJEU, info);
			}
		} else if(info instanceof String) {
			this.controle.evenementJeuClient(MODIFTCHAT, info);
		} else if (info instanceof Integer) {
			this.controle.evenementJeuClient(JOUESON,  info);
		}
	}
	
	@Override
	public void deconnexion(Connection connection) {
		System.exit(0);
	}

	/**
	 * Envoi d'une information vers le serveur
	 * fais appel une fois à l'envoi dans la classe Jeu
	 * @param info information à envoyer au serveur
	 */
	public void envoi(String info) {
		super.envoi(this.connection, info);
	}

}
