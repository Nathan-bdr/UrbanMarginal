package controleur;

import vue.EntreeJeu;

public class Controle {
	
	private EntreeJeu frmEntreeJeu ;

	public static void main(String[] args) {
		new Controle();

	}
	
	/**
	 * Constructeur
	 */
	private Controle() {
		this.frmEntreeJeu = new EntreeJeu() ;
		this.frmEntreeJeu.setVisible(true);
	}

}
