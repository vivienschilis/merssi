
import java.util.Iterator;
import java.util.Vector;

/**
 * Classe qui permet de stoquer une News en gardant ses principales informations / balises 
 * @author Flandre Guillaume / Schilis Vivien
*/
 
public class Catalogue {
	/** Nom du catalogue */
	String name;
	
	/** Liste des abonnements */
	Vector sesSubscriptions;

	public static void main(String[] argv) throws Exception {
		FileRSS archive;

		archive =  new FileRSS("database.db");
		Catalogue c;
		//c.addSesSubscriptions(new Subscription("http://rss.macgeneration.com"));
		//c.addSesSubscriptions(new Subscription("http://www.lemonde.fr/rss/sequence/0,2-3208,1-0,0.xml"));
		//archive.saveFile(c);
		//Catalogue c2;
		c = archive.getFile();	
		//c2.afficher();
		//archive.printL();
		//c = archive.searchKeyword("Promotion");
		c.afficher();
	}

	/** Initialisation des attributs */
	public Catalogue() {
		name = "RSS Catalogue";
		sesSubscriptions = new Vector();
	}
	
	/** Retourne la liste des abonnements 
		@return liste ds abonnements
	*/
	public Vector getSesSubscriptions(){
		return sesSubscriptions;
	}
	
	
	/** Ajoute un abonnement au catalogue 
		@param sub abonnement à insérer
	*/
	public void addSesSubscriptions(Subscription sub){
		sesSubscriptions.add(sub);
	}
	
	/** Supprime un abonnement du catalogue 
		@param i indice de l'abonnement à supprimer
	*/
	public void delSesSubscriptions (int i){
		sesSubscriptions.removeElementAt(i);
	}
	
	/** Affichage console d'un catalogue */
	public void afficher() {
		Subscription sub;

		System.out.println(name);
		Iterator it = sesSubscriptions.iterator();
		while(it.hasNext()) {
			System.out.println("> Abonnement :");
			((Subscription)it.next()).afficher();
		}
	}
	/** Met à jour l'ensemble des abonnements du catalogue */
	public void refresh () {
			try {
				Iterator it = sesSubscriptions.iterator();
		  		while(it.hasNext()) {
					((Subscription)it.next()).refresh();
				}
			}catch(Exception e){
				System.out.println(e);
			}
	}
}
