package modele;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import controleur.Global;

/**
 * Gestion des joueurs
 */
public class Joueur extends Objet implements Global {

	/**
	 * pseudo saisi
	 */
	private String pseudo ;
	/**
	 * n° correspondant au personnage (avatar) pour le fichier correspondant
	 */
	private int numPerso ; 
	/**
	 * message qui s'affiche sous le personnage (contenant pseudo et vie)
	 */
	private JLabel message;
	/**
	 * instance de JeuServeur pour communiquer avec lui
	 */
	private JeuServeur jeuServeur ;
	/**
	 * numéro d'étape dans l'animation (de la marche, touché ou mort)
	 */
	private int etape ;
	/**
	 * la boule du joueur
	 */
	private Boule boule ;
	/**
	 * vie restante du joueur
	 */
	private int vie ; 
	/**
	 * tourné vers la gauche (0) ou vers la droite (1)
	 */
	private int orientation ;
	/**
	 * Nombre de boules du joueur
	 */
	private int nbBoules;
	
	/**
	 * Constructeur : récupératon de jeuServeur et initialisaton de certaines propriétés
	 * @param jeuServeur instance de JeuServeur pour lui envoyer des informations
	 */
	public Joueur(JeuServeur jeuServeur) {
		this.jeuServeur = jeuServeur;
		this.vie = MAXVIE;
		this.etape = 1;
		this.orientation = DROITE;
	}

	/**
	 * Obtenir le pseudo du joueur
	 * @return the pseudo
	 */
	public String getPseudo() {
		return pseudo;
	}
	
	/**
	 * Obtenir l'orientation du personnage
	 * @return the orientation
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * Initialisation d'un joueur (pseudo et numéro, calcul de la 1ère position, affichage, création de la boule)
	 * @param pseudo pseudo du joueur
	 * @param numPerso numéro du personnage
	 * @param lesJoueurs collection contenant tous les joueurs
	 * @param lesMurs collection contenant les murs
	 */
	public void initPerso(String pseudo, int numPerso, Collection lesJoueurs, Collection lesMurs) {
		this.pseudo = pseudo;
		this.numPerso = numPerso;
		this.nbBoules = 10;
		System.out.println("joueur "+pseudo+" - num perso "+numPerso+" créé");
		// création du label du personnage
		super.jLabel = new JLabel();
		// création du label du message sous le personnage
		this.message = new JLabel();
		message.setHorizontalAlignment(SwingConstants.CENTER);
		message.setFont(new Font("Dialog", Font.PLAIN, 8));
		// création de la boule
		this.boule = new Boule(this.jeuServeur);
		// calcul de la première position du personnage
		this.premierePosition(lesJoueurs, lesMurs);
		// demande d'ajout du label du personnage, du message et de la boule dans l'arène du serveur
		this.jeuServeur.ajoutJLabelJeuArene(jLabel);
		this.jeuServeur.ajoutJLabelJeuArene(message);
		this.jeuServeur.ajoutJLabelJeuArene(boule.getjLabel());
		// demande d'affichage du personnage
		this.affiche(MARCHE, this.etape);
	}

	/**
	 * Calcul de la première position aléatoire du joueur (sans chevaucher un autre joueur ou un mur)
	 * @param lesJoueurs collection contenant tous les joueurs
	 * @param lesMurs collection contenant les murs
	 */
	private void premierePosition(Collection lesJoueurs, Collection lesMurs) {
		jLabel.setBounds(0, 0, LARGEURPERSO, HAUTEURPERSO);
		do {
			posX = (int) Math.round(Math.random() * (LARGEURARENE - LARGEURPERSO)) ;
			posY = (int) Math.round(Math.random() * (HAUTEURARENE - HAUTEURPERSO - HAUTEURMESSAGE)) ;
		}while(toucheCollectionObjets(lesJoueurs) != null || toucheCollectionObjets(lesMurs) != null);
	}
	
	/**
	 * Affiche le personnage et son message
	 * @param etape Etape dans le mouvement du personnage
	 * @param etat etat du personnage : "marche", "touche", "mort"
	 */
	public void affiche(String etat, int etape) {
		// positionnement du personnage et affectation de la bonne image
		super.jLabel.setBounds(posX, posY, LARGEURPERSO, HAUTEURPERSO);
		String chemin = CHEMINPERSONNAGES+PERSO+this.numPerso+etat+etape+"d"+this.orientation+EXTFICHIERPERSO;
		URL resource = getClass().getClassLoader().getResource(chemin);
		super.jLabel.setIcon(new ImageIcon(resource));
		// positionnement et remplissage du message sous le perosnnage
		this.message.setBounds(posX-10, posY+HAUTEURPERSO, LARGEURPERSO+10, HAUTEURMESSAGE);
		this.message.setText(pseudo+" : "+vie+" | "+nbBoules);
		// demande d'envoi à tous des modifications d'affichage
		this.jeuServeur.envoiJeuATous();
	}
	
	/**
	 * Obtenir le nombre de boules du joueur
	 * @return nb de boules
	 */
	public int getNbBoules() {
		return nbBoules;
	}
	
	/**
	 * Le joueur perd une boule
	 */
	public void perdBoule() {
		this.nbBoules = Math.max(0,  this.nbBoules - 1);
	}
	
	/**
	 * Le joueur gagne un certain nombre de boules
	 * @param nb nombre de boules gagnées
	 */
	public void gainBoules(int nb) {
		this.nbBoules += nb;
	}

	/**
	 * Gère une action reçue et qu'il faut afficher (déplacement, tire de boule...)
	 * @param action action à exécuter (déplacement ou tire de boule)
	 * @param lesJoueurs collection de joueurs
	 * @param lesMurs collection de murs
	 */
	public void action(Integer action, Collection lesJoueurs, Collection lesMurs) {
		if (!this.estMort()) {
			switch (action) {
			case KeyEvent.VK_LEFT:
				orientation = GAUCHE;
				posX = deplace(posX, action, -PAS, LARGEURARENE - LARGEURPERSO, lesJoueurs, lesMurs);
				break;
			case KeyEvent.VK_RIGHT:
				orientation = DROITE;
				posX = deplace(posX, action, PAS, LARGEURARENE - LARGEURPERSO, lesJoueurs, lesMurs);
				break;
			case KeyEvent.VK_UP:
				posY = deplace(posY, action, -PAS, HAUTEURARENE - HAUTEURPERSO - HAUTEURMESSAGE, lesJoueurs, lesMurs);
				break;
			case KeyEvent.VK_DOWN:
				posY = deplace(posY, action, PAS, HAUTEURARENE - HAUTEURPERSO - HAUTEURMESSAGE, lesJoueurs, lesMurs);
				break;
			case KeyEvent.VK_SPACE:
				if (!this.boule.getjLabel().isVisible() && this.nbBoules > 0) {
					this.boule.tireBoule(this, lesMurs);
					this.perdBoule();
					this.affiche(MARCHE,  this.etape);
				}
				break;
			}
			this.affiche(MARCHE, this.etape);
		}
	}

	/**
	 * Gère le déplacement du personnage
	 */
	private int deplace(int position, int action, int lepas, int max, Collection lesJoueurs, Collection lesMurs) { 
		int ancpos = position;
		position += lepas;
		if (action==KeyEvent.VK_LEFT || action==KeyEvent.VK_RIGHT) {
			if (position < 0) {
				position = max;
			} else if (position > max) {
				position = 0;
			}
			posX = position;
		} else if (action==KeyEvent.VK_UP || action==KeyEvent.VK_DOWN) {
			if (position < 0) {
				position = max;
			} else if (position > max) {
				position = 0;
			}
			posY = position;
		}
		// contrôle s'il y a des collisions, et dans ce cas le personnage reste sur place
		if (toucheCollectionObjets(lesJoueurs) != null || toucheCollectionObjets(lesMurs) != null) {
			position = ancpos;
		}
		// passe à l'étape suivante de l'animation de marche
		etape = (etape % NBETAPESMARCHE) + 1;
		return position;
	}
	
	/**
	 * Gain de points de vie après avoir touché un joueur
	 */
	public void gainVie() {
		this.vie += GAIN;
		affiche(MARCHE, etape);
	}
	
	/**
	 * Perte de points de vie après avoir été touché 
	 */
	public void perteVie() {
		this.vie = Math.max(0,  this.vie - PERTE);
		affiche(MARCHE, etape);
	}
	
	/**
	 * vrai si la vie est à 0
	 * @return true si vie = 0
	 */
	public Boolean estMort() {
		return (this.vie == 0);
	}
	
	/**
	 * Le joueur disparait (ainsi que son message et sa boule)
	 */
	public void departJoueur() {
		if(super.jLabel != null) {
			super.jLabel.setVisible(false);
			this.message.setVisible(false);
			this.boule.getjLabel().setVisible(false);
			this.jeuServeur.envoiJeuATous();
		}
	}
	
}
