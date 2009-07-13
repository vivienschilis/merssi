import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Classe qui permet de stoquer une News en gardant ses principales informations / balises 
 * @author Flandre Guillaume / Schilis Vivien
 */

public class Item {

	/** Identifiant de l'item */
	String id;

	/** Titre de la news */
	String title;

	/** Lien html */
	String link;

	/** Contenu de la news */
	String description;

	/** Date de publication */
	Time pubDate;

	/** Guid de la news */
	String guid;
	
	/** Lien vers son Flux d'information */
	Feed sonFeed;
	
	/** Information pour savoir si la news déja été lue */
	boolean read;
	
	//***********************
	
	/** Initialise les attributs de la classe */
	public Item(){
		
		id= title = link = description =  guid = "";
		pubDate = new Time((new Date()).getTime());
		read = false;
	}
	
	//***********************
	
	/** Retourne l'identifiant de l'item 
			@return Identifiant de l'item
	*/
	public String getID(){
		return id;
	}
	
	/** Affecte un identifiant 
		@param id Identifiant de l'item
	*/
	public void setID(String id){
		this.id = id;
	}
	
	/** Retoure le titre d'un item 
		@return titre de l'item
	*/
	public String getTitle(){
		return title;
	}
	/** Affecte un titre à la l'item
		@param t titre de l'item
	 */
	public void setTitle(String t){
		title = t;
	}
	
	/** retourne le lien de l'item 
		@return lien de l'item
	*/
	public String getLink(){
		return link;
	}
	
	/** Affecte un lien à l'item
		@param l lien d'un item
	*/
	public void setLink(String l){
		link = l;
	}
	
	/** Retourne la description, corps principal de l'Item qui contient l'information de la news 
		@return news de l'item
	*/
	public String getDescription(){
		return description;
	}
	
	/** Affecte le coprs principal de l'item, c'est à dire l'information de la news 
		@param d description de l'item
	*/
	public void setDescription(String d){
		description = d;
	}
	
	/** Retourne la date de publication de la news 
		@return date de pulication
	*/
	public Time getPubDate(){
		return pubDate;
	}
	
	/** Affecte une date de publication à un item 
		@param t date de publication de la news
	*/
	public void setPubDate(Time t){
		pubDate = t;
	}
	
	/** Retourne le guid d'un Item 
		@return guid d'un item
	*/
	public String getGuid(){
		return guid;
	}
	
	/** Affecte un guid à un item 
		@param g guid d'un item
	*/
	public void setGuid(String g){
		guid = g;
	}
	
	/** Retourne le flux associé à l'item 
		@return flux auquel appartient l'item
	*/
	public Feed getSonFeed() {
		return sonFeed;
	}
	
	/** Affecte un flux a l'item
		@param f flux d'où provient l'item
	*/
	public void setSonFeed(Feed f){
		sonFeed = f;
	}
	
	/** Positionne l'item comme lu 
		@param b vrai si lu sinon faux
	*/
	public void setRead(boolean b) {
		read = b;
	}
	
	/** Permet de savoir si un Item à été lue ou non
		@return true si lue false sinon
	*/
	public boolean getRead(){
		return read;
	}
	
	/** Convertie une chaine de caractere dans un format necessaire à son enregistrement dans la base de donnée 
		@param str chaine a convertir 
		@return chaine convertie au format voulu
	*/
	public static String formatToHtml(String str){
		str = str.replace("\'","&#39;");		
		str = str.replace("\"","&#34;");
		return str;
	}
	/** Formate les champs utiles */
	public void format (){
		description = formatToHtml(description);
		title       = formatToHtml(title);
	}
	//*****************************
	/** Affiche un item en mode console */
	public void afficher(){
		System.out.println("ITEM");
		System.out.println("titre : "+title);
		System.out.println("lien : "+link);
		System.out.println("description : "+description);
		System.out.println("date de publication : "+pubDate);
		System.out.println("guid : "+guid);
		System.out.println();
	}
	
}
