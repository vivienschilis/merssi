

 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Date;
import java.net.MalformedURLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.sql.Time;
import java.sql.Timestamp;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.awt.Color;

/**
 *Classe qui permet de stoquer un abonnement comprenant son flux et l'adresse du fichier XML /RSS
 * @author Flandre Guillaume / Schilis Vivien
*/

public class Subscription {

	/** Date de la creation de l'abonnement */
	Date date;
	
	/** Lien direct du flux RSS du type XML */
	String link;
	
	/** Flux de l'abonnement */
	Feed feed;	
	
	/** Couleur affecté à l'abonnement */
	Color couleur;
	

	/** Classe principale de test */
	public static void main(String[] argv) throws Exception {
	  	FileRSS f = new FileRSS("database.db");
		Subscription sub = new Subscription("http://rss.macgeneration.com");
		Catalogue c = f.getFileByIndexAndFrequency();
		c.afficher();
	}
	
	/** Affecte une couleur a l'abonnement 
		@param c couleur de l'abonnement
	*/
	public void setColor(Color c) {
		couleur = c;
	}
	
	/** Retourne la couleur de l'abonnement */
	public Color getColor() {
		return couleur;
	}
	
	/** Retourne le Flux de l'abonnement
		@return flux associé à l'abonnement 
	*/
	public Feed getFeed(){
		return feed;
	}
	
	/** Affecte un flux à l'abonnement 
		@param feed flux de l'abonnement
	*/
	public void setFeed(Feed feed) {
		this.feed=feed; 
	}

	/** Retourne le lien du flux RSS 
		@return lien du flux
	*/
	public String getLink(){
		return link;
	}
	
	/** Affecte un lien a l'abonnement 
		@param lien lien RSS du flux
	*/
	public void setLink(String lien) {
		link = lien;
	}

	/**  Retourne la date de l'inscription 
		@return date de l'inscription
	*/
	public Date getDate() {
		return date;
	}
	
	/** Affecte une date à l'abonnement
		@param d date de l'inscription
	*/
	public void setDate(Date d) {
		date =  d;
	}

	/** Affiche en mode console un Abonnement 
	*/
	public void afficher() {
		System.out.println("Lien : "+link);
		System.out.println("Date : "+date.toLocaleString());
		feed.afficher();
	}

	//*****************Constructeur******************
	/** Initialise les attributs de la classe */
	public Subscription (){
		feed = new Feed();
		setDate(new Date());
	}
	
	/** Abonnement a un flux, automatiquement parser
		@param url lien du flux RSS
	*/	
	public Subscription(String url) throws UnknownHostException,IOException,SAXParseException{
		this();
		setLink(url);
		refresh();
	}
	
	// *****************methodes************
	/** Actualise un Abonnement et toute ses news associées */
	public void refresh()  throws UnknownHostException,IOException,SAXParseException{
	try {
		Node root;
		feed = new Feed();
		feed.setTitle("Actu en cours ...");
		root = recupererArbre(recupererSourceXML());
		if (root != null) {
			recupererFeed(root,feed);
			feed.setSonSubscription(this);
			feed.format();
		}
	 }catch(MalformedURLException e)
	 {System.out.println("ca plait  pas ");}

	}
	/** Récupère un arbre DOM d'un flux RSS / XML 
		@return racine du noeur de l'abre
	*/
	protected static Node recupererArbre(InputStream is) throws MalformedURLException,UnknownHostException,IOException,SAXParseException{

		if (is == null) return null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Node root= null;

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(is);
			root = document.getDocumentElement();

		} catch (SAXParseException spe) {
			throw spe;
		} catch (SAXException sxe) {
			return null;
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			return null;
		} catch (IOException ioe) {
			// I/O error
			return null;
		}
		return root;
	}

	/** Recupère un stream apres l'affectation de l'attribut link */
	protected InputStream recupererSourceXML() throws MalformedURLException,UnknownHostException,IOException{
		InputStream source;
		URL  monUrl;
		try {
			try { 
				monUrl = new URL (link);
			}catch(MalformedURLException e) {
				return null;
			}
			source = monUrl.openStream();
		}catch(UnknownHostException e){
				throw e;
		}catch(IOException e){
			return null;
		}

		return source;	
	}

	/** Enregistrement du flux dans la structure de données 
		@param node noeud de l'abre DOM
		@param feed  structure ou l'on sauvegarde les données 
	*/
	public static void recupererFeed(Node node, Feed feed) {

		switch (node.getNodeType())
		{
			case org.w3c.dom.Node.ELEMENT_NODE:
				//dans le channel
				if (node.getParentNode().getNodeName()== "channel"){

					if(node.getNodeName()=="title"){
						feed.setTitle(node.getTextContent());
					}
					if(node.getNodeName()=="copyright"){
						feed.setCopyright(node.getTextContent());
					}
					if(node.getNodeName()=="description"){
						feed.setDescription(node.getTextContent());
					}
					if(node.getNodeName()=="link"){
						feed.setLink(node.getTextContent());
					}
					if(node.getNodeName()=="pubDate"){
						feed.setPubDate(Subscription.sqliteDate(node.getTextContent()));
					}
				}		
				// dans l'image
				if (node.getParentNode().getNodeName()=="image"){

					if(node.getNodeName()=="title"){
						feed.getSonImage().setTitle(node.getTextContent());
					}
					if(node.getNodeName()=="width"){
						feed.getSonImage().setWidth(Integer.parseInt(node.getTextContent()));
					}
					if(node.getNodeName()=="height"){
						feed.getSonImage().setHeight(Integer.parseInt(node.getTextContent()));
					}
					if(node.getNodeName()=="link"){
						feed.getSonImage().setLink(node.getTextContent());
					}
					if(node.getNodeName()=="url"){
						feed.getSonImage().setURL(node.getTextContent());
					}
				}    		
				//dans les items
				if (node.getNodeName()=="item"){
					Item item = new Item();
					recupererItem(node,item);
					item.setSonFeed(feed);
					item.format();
					feed.addItem(item);
				}

		}

		//appel recursif
		if (node.hasChildNodes()) {
			NodeList nodeList = node.getChildNodes();
			int size = nodeList.getLength();
			for (int i = 0; i < size; i++) {
				recupererFeed(nodeList.item(i),feed);
			}
		}

	}
	
	/** Retourne un affichage String de la date 
		@param t date a transformer
	*/
	public static String timeToRSSDate(Time t){
		GregorianCalendar c = new GregorianCalendar();
		int year, month, day;	
		String strDay="";
		c.setTime(new Date(t.getTime()));
							
		month = c.get(Calendar.MONTH);
		day      = c.get(Calendar.DAY_OF_MONTH);
		switch (month){
			case 1 : strDay ="Janvier";break;
			case 2 : strDay ="Fevrier";break; 
			case 3 : strDay ="Mars";break; 
			case 4 : strDay ="Avril";break; 
			case 5 : strDay ="Mai";break; 
			case 6 : strDay ="Juin";break; 
			case 7 : strDay ="Juillet";break; 
			case 8 : strDay ="Août";break; 
			case 9 : strDay ="Septembre";break; 
			case 10 : strDay ="Octobre";break; 
			case 11 : strDay ="Novembre";break; 
			case 12: strDay ="Decembre";break;  
		}
		year = c.get(Calendar.YEAR)-1900;
		return day+" "+strDay+" "+year;
	}
	
	/** Enregistrement de l'item dans la structure de données 
		@param node noeud de l'abre DOM
		@param item  structure ou l'on sauvegarde les données 
	*/
	public static void recupererItem(Node node,Item item){

		switch (node.getNodeType())
		{
			case org.w3c.dom.Node.ELEMENT_NODE:
				if(node.getNodeName()=="title"){
					item.setTitle(node.getTextContent());
				}
				if(node.getNodeName()=="description"){
				  item.setDescription(node.getTextContent());
				}
				if(node.getNodeName()=="link"){
				  item.setLink(node.getTextContent());
				}
				if(node.getNodeName()=="pubDate"){
				  item.setPubDate(Subscription.sqliteDate(node.getTextContent()));
				}
				if(node.getNodeName()=="guid"){
				  item.setGuid(node.getTextContent());
				}

		}
		//appel recursif
		if (node.hasChildNodes()) {
		  NodeList nodeList = node.getChildNodes();
		  int size = nodeList.getLength();
		  for (int i = 0; i < size; i++) {
		    recupererItem(nodeList.item(i),item);
		  }
		}
	}

	/** Transforme une string RSS en un type Time 
		@param date date à convertir
	*/
	static public Time sqliteDate (String date) {
		
		String year,month,day,hour,min,sec;
		year = month = day =  hour = min = sec = "";
		year = date.substring(12,16);
		String strmonth =date.substring(8,11);

		if (strmonth.equals("jan")) month ="01"; 
			else if(strmonth.equals("Feb")) month ="02";
			else if(strmonth.equals("Mar")) month ="03"; 
			else if( strmonth.equals("Apr"))  month ="04"; 
			else if( strmonth.equals("may")) month ="05";
			else if( strmonth.equals("Jun")) month ="06";
			else if( strmonth.equals("Jul")) month ="07"; 
			else if( strmonth.equals("Aug")) month ="08"; 
			else if( strmonth.equals("Sep")) month ="09";
			else if( strmonth.equals("Oct")) month ="10";
			else if( strmonth.equals("Nov")) month ="11"; 
			else if( strmonth.equals("Dec")) month ="12"; 
			
		day =date.substring(5,7);
		hour=date.substring(17,19);
		min = date.substring(20,22);
		sec = date.substring(23,25);
		
		Timestamp t = new Timestamp
		(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day),Integer.parseInt(hour),Integer.parseInt(min),Integer.parseInt(sec),0);
		
		return new Time(t.getTime());

	}

}
