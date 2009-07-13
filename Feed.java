
import java.util.Iterator;
import java.util.Vector;
import java.sql.Time;
import java.util.Date;

/**
*Classe qui permet de stoquer un flux d'information RSS, à savoir l'ensemble des items qu'il contient ainsi que les informations associé au flux
 * @author Flandre Guillaume / Schilis Vivien
 */
 
public class Feed {

	/** Titre d'un flux */
	String title;
	
	/** Lien d'unf flux */
	String link;
	
	/** Description du flux */
	String description;
	
	/** Copyright d'un flux */
	String copyright;
	
	/** Date de la dernière publication / mise a jour du flux */
	Time pubDate;
	
	/** Image associé aux flux */
	Image sonImage;
	
	/** Lien vers les items du flux */
	Vector sesItems;
	
	/** Lien vers l'abonnement auquel il appartient */
	Subscription sonSubscription;

	/** Initialise les attributs de la classe */
	public Feed(){
		title =
		link  =
		description =	
		copyright ="";
		sonImage = new Image();
		sesItems = new Vector();
		pubDate = new Time((new Date()).getTime());
	}
	
	//*****************************
	
	/** Retourne l'abonnement lié au flux 
		@return abonnement lié au flux RSS
	*/
	public Subscription getSonSubscription() {
		return sonSubscription;
	}
	
	/** Affecte un abonnement au flux 
		@param s abonnement souscrit
	*/
	public void setSonSubscription(Subscription s) {
		sonSubscription = s;
	}
	
	/** Retourne le titre du flux 
		@return titre du flux
	*/
	public String getTitle(){
		return title;
	}
	
	/** Affecte un titre a un flux 
		@param t titre du flux
	*/
	public void setTitle(String t){
		title = t;
	}
	
	/** Retourne le lien du flux
		@return lien du flux
	*/
	public String getLink(){
		return link;
	}
	
	/** Affecte un lien au flux 
		@param l lien du flux
	*/
	public void setLink(String l){
		link = l;
	}
	
	/** Retourne la description d'un flux 
		@return la description d'un flux
	*/
	public String getDescription(){
		return description;
	}
	
	/** Affecte la description d'un flux
		@param d description d'un flux
	*/
	public void setDescription(String d){
		description = d;
	}
	
	/** Retourne le copyright 
		@return copyright du flux
	*/
	public String getCopyright(){
		return copyright;
	}
	
	/** Affecte un copyright aux flux
		@param c  copyright d'un flux
	*/
	public void setCopyright(String c){
		copyright = c;
	}
	
	/** Retourne la date de publication 
	*	@return date de publication du flux
	*/
	public  Time getPubDate(){
		return pubDate;
	}
	
	/** Affecte la date de publication 
		@param pd date de publication 
	*/
	public void setPubDate(Time pd){
		pubDate = pd;
	}
	
	/** Retourne l'image associé au flux
		@return image du flux
	*/
	public Image getSonImage(){
		return sonImage;
	}
	
	/** Affecte une image a un flux 
		@param i image d'un flux
	*/
	public void setSonImage(Image i){
		sonImage = i;
	}
	
	/** Retourne les items associé a flux
		@return Listes des items du flux
	*/
	public Vector getSesItems(){
		return sesItems;
	}
	
	/** Retourne le nombre d'items
		@return nombre d'items
	*/
	public int nItems() {
		return sesItems.size();
	}
	
	/** Retourne le nombre d'items non lue 
		@return nombre d'items non lue
	*/
	public int nNewItems() {
		int n=0;
		Iterator it = sesItems.iterator();
		while(it.hasNext()) 
			if( ((Item)(it.next())).getRead()==false) n++;
		return n;
	}
	

	//*******************************
	/**  Formate les attributs pour la sauvegarde des news
	*/
	public void format() {
		title = Item.formatToHtml(title);
		description = Item.formatToHtml(description);
		copyright = Item.formatToHtml(copyright);
	}
	
	/** Affiche le flux en mode console*/
	public void afficher(){
		System.out.println(title.toUpperCase());
		System.out.println("lien : "+link);
		System.out.println("description : "+description);
		System.out.println("copyright : (c) "+copyright);
		System.out.println("date de publication : "+pubDate);
		System.out.println();
		sonImage.afficher();
		System.out.println();
		listSesItems();
	}
	
	/** Ajouter un item au flux 
	*/
	public void addItem(Item item){
		sesItems.add(item);
	}
	
	/** Supprimer un item 
		@param num numero de l'item à supprimer
	*/
	public void delItem(int num){
		sesItems.removeElementAt(num);
	}
	
	/** Affichage  de l'ensemble des news 
	*/
	public void listSesItems(){
		Iterator it = sesItems.iterator();
		while(it.hasNext()){
			((Item)(it.next())).afficher();
		}
	}
	
}
