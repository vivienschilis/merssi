/**
	*Classe qui permet de stoquer une News en gardant ses principales informations / balises 
 * @author Flandre Guillaume / Schilis Vivien
*/
 
public class Image {
	
	/** Largeur de l'image*/
	int width;

	/** Longeur de l'image*/
	int height;
	
	/** Adresse de l'image*/
	String url;
	
	/** Titre de l'image*/
	String title;
	
	/** lien de l'image */
	String link;
	
	//*********************
	
	/** Initialisation des attributs */
	public Image(){
		width = height = 0;
		url = title = link ="";
	}
	
	//*********************
	
	/** Retourne le titre 
		@return titre de l'image
	*/
	public String getTitle(){
		return title;
	}
	
	/** Affecte un titre à l'image 
		@param t titre de l'image
	*/
	public void setTitle(String t){
		title = t;
	}
	
	/** Returne le lien de l'image 
		@return lien de l'image
	*/
	public String getLink(){
		return link;
	}
	
	/** Affecte un lien à une image 
		@param l lien de l'image
	*/
	public void setLink(String l){
		link = l;
	}
	
	/** Retourne l'adresse de l'image 
		@return adresse de l'image
	*/
	public String getURL(){
		return url;
	}
	
	/** Affecte une adresse a une image
		@param u 
	*/
	public void setURL(String u){
		url = u;
	}
	
	/** Retourne  la largeur de l'image 
		@return largeur de l'image
	*/
	public int getWidth(){
		return width;
	}
	
	/** Affecte une largeur à l'image 
		@param w largeur de l'image
	*/
	public void setWidth(int w){
		width = w;
	}
	
	/** Retourne  la longeur de l'image 
		@return longeur de l'image
	*/
	public int getHeight(){
		return height;
	}
	
	/** Affecte une longeur à l'image 
		@param h longeur de l'image
	*/	
	public void setHeight(int h){
		height = h;
	}
	
	//*************************
	
	/** Affichage console de l'image
	*/
	public void afficher(){
		System.out.println("IMAGE");
		System.out.println("largeur : "+width);
		System.out.println("hauteur : "+height);
		System.out.println("url : "+url);
		System.out.println("titre : "+title);
		System.out.println("lien : "+link);
	}
	
}
