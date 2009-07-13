import SQLite.*;
import SQLite.Exception;
import java.util.Vector;
import java.util.Iterator;
import java.sql.Time;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.awt.Color;
import java.util.HashMap;
import java.io.*;

/**
 * Classe qui permet de stoquer toutes les informations de l'application dans une base de données SQLite
 * @author Flandre Guillaume / Schilis Vivien
*/

public class FileRSS {

	/** Nom  du fichier SQLite */
	String fileName;
	/** Base de Donnee SQLite */
	SQLite.Database db;
	
	/** Repère d'indexation */
	static String word;
	
	/** table de hachage des "mots vides" a exclure de la base */
	HashMap motsVides;
	
	/** Construit la hashmap de mot vides a partir d'un fichier */
	public HashMap recupererMotsVides(String file) {
		BufferedReader lecteur =  null;
		String ligne;
		HashMap hash = new HashMap();
		try {
			lecteur = new BufferedReader(new FileReader(file));		
		    while ((ligne = lecteur.readLine()) != null) {
		    	hash.put(ligne.trim(),1);
		    }
		    lecteur.close();
	    	return hash;
	   }catch(FileNotFoundException exc){
			System.out.println("fichier non trouve");
			return null;
		}catch (IOException e) {return null;}
	}
	
	/** Retourne le mot indexé a un instant donné 
		@return mot indexé lors de la phase l'indexation
	*/
	public String getWord() {
		return word;
	}
	
	/** Affecte le mot en cours d'indexation 
		@param str mot en cours d'indexation
	*/
	public void setWord(String str){
		word = str;
	}
	
	/** Initialisation des attributs et creation de la base de donnée 
		@param fileName nom du fichier de la BD
	*/
	public FileRSS(String fileName) throws Exception{
		this.fileName = fileName;
		db = new SQLite.Database();

		// verification de l'existance de la base
		java.io.File f = new java.io.File(fileName);
		if (f.exists()){
			db.open(fileName, 0666);
		}else{
			db.open(fileName, 0666);
			create();
		}
		db.exec("pragma synchronous=0",null);
	}
	/** Fermeture de la base SQLite */
	protected void close() throws Exception {
		db.close();
	}

	/** Retourne le nom du fichier de la BD 
		@return nom du fichier de la BD
	*/
	public String getFileName(){
		return fileName;
	}
	
	
	//***************************************CREATION DE LA BASE****************************
	/** Creation des tables SQLite */
	public void create() throws Exception  {

		//*******************TABLE SUB***************************
		//System.out.println("CREATION DE LA TABLE tbSUB");
		db.exec("CREATE TABLE tbSub (id_sub INTEGER PRIMARY KEY,"+
				"linkSub TEXT,"+"dateSub TEXT,"+
				"title TEXT,"+"link TEXT,"+
				"description TEXT,"+"copyright TEXT,"+
				"pubDate INTEGER,"+"imgUrl TEXT,"+
				"imgHeight INTEGER,"+"imgWidth INTEGER,"+
				"imgTitle VARCHAR(60),"+"imgLink TEXT"+
				")",null);

		//***********************TABLE Item***********************
		//System.out.println("CREATION DE LA TABLE tbItem");
		db.exec("CREATE TABLE tbItem ("+
				"id_item INTEGER PRIMARY KEY,"+"id_sub INTEGER,"+
				"title TEXT,"+"link TEXT,"+"description TEXT,"+
				"pubDate INTEGER,"+"guid TEXT,"+"read INTEGER"+
				")",null);
		
		//System.out.println("CREATION DE LA TABLE tbIndex");
		db.exec("CREATE TABLE tbIndex (id_index INTEGER PRIMARY KEY,"+
				"keyword TEXT,"+
				"frequency INTEGER)",null);
		
		//System.out.println("CREATION DE LA TABLE tbEstDansItem");
		db.exec("CREATE TABLE tbEstDansItem (id_index INTEGER,"+
				"id_item INTEGER)",null);

		db.exec("CREATE TABLE tbColorSub (id_Sub INTEGER,"+
				"Rcolor INTEGER, Gcolor INTEGER, Bcolor INTEGER)",null);
	}

	//**************************ENREGISTREMENT DANS LA BASE************************************
	
	/** Enregistrement de la couleur d'un abonnement a partir d'une structure Subscription
		@param sub abonnement qui se voit modier sa couleur
		@param color couleur de l'abonnement
	*/
	public void saveCorlorSubscription(Subscription sub,Color color) throws Exception {

				TableResult tb = db.get_table("SELECT id_sub FROM tbSub WHERE linkSub='"+sub.getLink()+"'");

				if (!tb.rows.isEmpty()) {
					saveColorSub(Integer.parseInt( ((String[])tb.rows.elementAt(0))[0]),color);
				}
	}
	
	/** Enregistrement de la couleur d'un abonnement a partir d'un identifiant SQLite
		@param id_sub identifiant de l'abonnement qui se voit modier sa couleur
		@param color couleur de l'abonnement
	*/
	private void saveColorSub(int id_sub, Color color) throws Exception{
		TableResult tb = db.get_table("SELECT id_sub FROM tbColorSub WHERE id_sub="+id_sub);
		if (tb.rows.isEmpty()) {
			db.exec("INSERT INTO tbColorSub VALUES ("+id_sub+","+
				color.getRed()+","+color.getGreen()+","+color.getBlue()+")",null);
			
				
		}
		else {
				db.exec("UPDATE tbColorSub SET id_sub="+id_sub+", Rcolor="+
							color.getRed()+", Gcolor="+color.getGreen()+",Bcolor="+color.getBlue(),null);
		}
	}
	
	/** Retourne la couleur d'un abonnement 
		@return couleur de l'abonnement
	*/
	public Color getColorSubscription(int id_sub) throws Exception{
				String[] row;
				TableResult tb = db.get_table("SELECT * FROM tbColorSub WHERE id_sub="+id_sub);
			
				if (tb.rows.isEmpty())
					return null;
				else {
					row = (String[])tb.rows.elementAt(0);
					return new Color(Integer.parseInt(row[1]),Integer.parseInt(row[2]),Integer.parseInt(row[3]));
				}
	}
	
	/** Sauvegarde un catalogue complet dans la base de donnée 
		@param c a sauvegarder
	*/
	public void saveFile(Catalogue c) throws Exception{
			recupererMotsVides("mots-vides.txt");
			Iterator it = (c.getSesSubscriptions()).iterator();
			while (it.hasNext()) {
				saveFileSub((Subscription)it.next());
			}
			word = null;


	}

	/** Sauvegarde un abonnement dans la base de donnée 
		@param sub abonnement a sauvegarder
	*/
	public  void saveFileSub(Subscription sub) throws Exception {
		if (motsVides ==null) motsVides = recupererMotsVides("mots-vides.txt");
		long  feed_date;
		if ( (sub.getFeed()).getPubDate() == null)
			feed_date=0;
		else
			feed_date=((sub.getFeed()).getPubDate()).getTime();
		
		TableResult tb = db.get_table("SELECT id_sub FROM tbSub WHERE linkSub='"+sub.getLink()+"'");
		if ( tb.rows.isEmpty() ) {
			db.exec ("INSERT INTO tbSub VALUES (NULL,'"+
					sub.getLink()+"','"+
					sub.getDate()+"','"+
					(sub.getFeed()).getTitle()+"','"+
					(sub.getFeed()).getLink()+"','"+
					(sub.getFeed()).getDescription()+"','"+
					(sub.getFeed()).getCopyright()+"','"+
					feed_date+"','"+
					((sub.getFeed()).getSonImage()).getURL()+"',"+
					((sub.getFeed()).getSonImage()).getHeight()+","+
					((sub.getFeed()).getSonImage()).getWidth()+",'"+
					((sub.getFeed()).getSonImage()).getTitle()+"','"+
					((sub.getFeed()).getSonImage()).getLink()+"'"+
					")",null);
		}

		tb =db.get_table("SELECT id_sub FROM tbSub WHERE linkSub='"+sub.getLink()+"'");
		int idSub= Integer.parseInt( ((String[])(tb.rows.lastElement()))[0]);
		
		saveFileFeed(sub.getFeed(),idSub); // On ajoute les nouveaux items
	}

	/** Sauvegarde un flux dans la base de donnée 
		@param feed flux a sauvegarder
		@param id identifiant du flux
	*/
	protected void saveFileFeed(Feed feed, int id) throws Exception {
		Item item_courant;
		TableResult tb;
		Iterator it = (feed.getSesItems()).iterator();
		while (it.hasNext())
		{
			item_courant = (Item)(it.next());
			tb =  db.get_table("SELECT * FROM tbItem WHERE Title='"+
					item_courant.getTitle()+"' AND pubDate="+
					item_courant.getPubDate().getTime());
			if (tb.rows.isEmpty())
				saveFileItem(item_courant,id);
//			else
//				updateFileItem(item_courant);			
		}
	}

	/** Sauvegarde un item dans la base de donnée 
		@param item item a sauvegarder 
		@param id_sub identifiant du l'abonnement rataché @ l'item
	*/
	protected void saveFileItem(Item item,int id_sub) throws Exception {
		int read;
		if(item.getRead()) read=1;
		else read=0;
		System.out.print("hiiiiiii");
		db.exec("INSERT INTO tbItem "+
				"(id_item,id_Sub,title,link,description,pubDate,guid,read) VALUES (NULL,"+
				id_sub+",'"+
				item.getTitle()+"','"+
				item.getLink()+"','"+
				item.getDescription()+"','"+
				(item.getPubDate()).getTime()+"','"+
				item.getGuid()+"',"+
				read+	
				")",null);
		TableResult tb = db.get_table("SELECT id_item FROM tbItem WHERE title='"+item.getTitle()+"'");
		int id_item = Integer.parseInt(((String[])(tb.rows.elementAt(0)))[0]);
		indexItem(item,id_item);
		
	}
	/** Met a jour un item 
		@param item item à mettre à jour
	*/
	protected void updateFileItem (Item item) throws Exception {

		db.exec("UPDATE tbItem SET description='"+item.getDescription()+
				"' WHERE Title='"+item.getTitle()+"'",null);	
	}

	//******************************RECUPERATION DE LA BASE************************************
	
	
	/** Recupere le catalogue par flux dans la base SQLite */
	public Catalogue getFile(){
		Catalogue c =  new Catalogue();
		Subscription sub;
		TableResult tb;
		try{
			tb =  db.get_table("SELECT id_sub FROM tbSub");
			for(int i=1;i<=tb.nrows;i++) {
				c.addSesSubscriptions(getFileSubscription(i));
			}
			return c;
		} catch (java.lang.Exception e) {
			return null;
		}
	}
	
	/** Recupere un abonnement de la base SQLite 
		@param id identifiant de l'abonnement dans la base 
	*/
	protected Subscription getFileSubscription(int id){
		TableResult tb;
		Subscription sub = new Subscription();
		Feed feed = new Feed();
		Image img = new Image();
		Item item;
		String[] row;
		try {
			tb =  db.get_table("SELECT * FROM tbSub WHERE id_sub ="+id);
			if (!tb.rows.isEmpty()) {
				row = (String[])tb.rows.elementAt(0);
				sub.setLink(row[1]);
				sub.setColor(getColorSubscription(Integer.parseInt(row[0])));
				//sub.setDate(row[2]);
				feed.setTitle(row[3]);
				feed.setLink(row[4]);
				feed.setDescription(row[5]);
				feed.setCopyright(row[6]);
				feed.setPubDate(new Time(Long.parseLong(row[7])));
				img.setURL(row[8]);
				img.setHeight(Integer.parseInt(row[9]));
				img.setWidth(Integer.parseInt(row[10]));
				img.setTitle(row[11]);
				img.setLink(row[12]);
				feed.setSonImage(img);
				// on recupere les items
				tb = db.get_table("SELECT * FROM tbItem WHERE id_sub="+id+" ORDER BY pubDate DESC");
				for(int i=0;i<tb.nrows;i++) {
					row = (String[])tb.rows.elementAt(i);
					item = new Item();
					item.setID(row[0]);
					item.setTitle(row[2]);
					item.setLink(row[3]);
					item.setDescription(row[4]);
					item.setPubDate(new Time(Long.parseLong(row[5])));
					item.setGuid(row[6]);
					item.setSonFeed(feed);
					if(Integer.parseInt(row[7]) == 1)
						item.setRead(true);
					else
						item.setRead(false);
					feed.addItem(item);
				}
				feed.setSonSubscription(sub);
				sub.setFeed(feed);
				
			}
			
			return sub;
		} catch (java.lang.Exception e) {
			return null;
		}	
	}
	//***************************************************************************** new non lue
		/** Recupere les Items non lus du catalogue par flux dans la base SQLite */
		public Catalogue getFileUnRead(){
		Catalogue c =  new Catalogue();
		Subscription sub;
		TableResult tb;
		try{
			tb =  db.get_table("SELECT id_sub FROM tbSub");
			for(int i=1;i<=tb.nrows;i++) {
				c.addSesSubscriptions(getFileSubscriptionUnRead(i));
			}
			return c;
		} catch (java.lang.Exception e) {
			return null;
		}
	}
	
	/** Recupere  le items non lu d'un abonnement de la base SQLite a partir d'une structure Subscription
		@param sub abonnement à rechercher
	*/
	public Subscription getSubscription(Subscription sub) {
		TableResult tb;
		try{
			tb =  db.get_table("SELECT id_sub FROM tbSub WHERE linkSub='"+sub.getLink()+"'");
			return getFileSubscription(Integer.parseInt(((String[])(tb.rows.elementAt(0)))[0]));
		} catch (java.lang.Exception e) {
			return null;
		}
	}
	
	/** Recupere un abonnement associé a un item de la base SQLite a partir d'un identifiant de la base
		@param id_item identifiant d'un item dans la base 
	*/
	public int  getSonSubscription(int id_item) throws Exception {
		TableResult	tb =  db.get_table("SELECT tbItem.id_sub FROM tbSub,tbItem WHERE tbSub.id_sub = tbItem.id_sub AND tbItem.id_item="+id_item);
		if (!tb.rows.isEmpty()) 
			return Integer.parseInt( ((String[])tb.rows.elementAt(0))[0]);
		else return 0;
	}
	
	/** Recupere un abonnement sans ses items pour etre lié a un item (et non l'inverse)
		@param id_sub identifiant d'un abonnement dans la base SQLite 
	*/
	protected Subscription getSonFileSubscription(int id_sub) throws Exception{
		TableResult tb;
		Subscription sub = new Subscription();
		Feed feed = new Feed();
		Image img = new Image();
		Item item;
		String[] row;
			tb =  db.get_table("SELECT * FROM tbSub WHERE id_sub="+id_sub);
			if (!tb.rows.isEmpty()) {
				row = (String[])tb.rows.elementAt(0);
				sub.setLink(row[1]);
				sub.setColor(getColorSubscription(Integer.parseInt(row[0])));
				//sub.setDate(row[2]);
				feed.setTitle(row[3]);
				feed.setLink(row[4]);
				feed.setDescription(row[5]);
				feed.setCopyright(row[6]);
				feed.setPubDate(new Time(Long.parseLong(row[7])));
				img.setURL(row[8]);
				img.setHeight(Integer.parseInt(row[9]));
				img.setWidth(Integer.parseInt(row[10]));
				img.setTitle(row[11]);
				img.setLink(row[12]);
				feed.setSonImage(img);
				// on recupere les items
				feed.setSonSubscription(sub);
				sub.setFeed(feed);
				return sub;
			}
		return null;
	}
	
	/** Recupere les abonnements des news non lues 
		@param id identifiant d'un abonnement dans la base SQLite
	*/
	protected Subscription getFileSubscriptionUnRead(int id){
		TableResult tb;
		Subscription sub = new Subscription();
		Feed feed = new Feed();
		Image img = new Image();
		Item item;
		String[] row;

		try {
			tb =  db.get_table("SELECT * FROM tbSub WHERE id_sub ="+id);
			if (!tb.rows.isEmpty()) {
				row = (String[])tb.rows.elementAt(0);
				sub.setLink(row[1]);
				sub.setColor(getColorSubscription(Integer.parseInt(row[0])));
				//sub.setDate(row[2]);
				feed.setTitle(row[3]);
				feed.setLink(row[4]);
				feed.setDescription(row[5]);
				feed.setCopyright(row[6]);
				feed.setPubDate(new Time(Long.parseLong(row[7])));
				img.setURL(row[8]);
				img.setHeight(Integer.parseInt(row[9]));
				img.setWidth(Integer.parseInt(row[10]));
				img.setTitle(row[11]);
				img.setLink(row[12]);
				feed.setSonImage(img);
				// on recupere les items
				tb = db.get_table("SELECT * FROM tbItem WHERE id_sub="+id+" AND read=0 ORDER BY pubDate DESC");
				for(int i=0;i<tb.nrows;i++) {
					row = (String[])tb.rows.elementAt(i);
					item = new Item();
					item.setID(row[0]);
					item.setTitle(row[2]);
					item.setLink(row[3]);
					item.setDescription(row[4]);
					item.setPubDate(new Time(Long.parseLong(row[5])));
					item.setGuid(row[6]);
					item.setSonFeed(feed);
					if(Integer.parseInt(row[7]) == 1)
						item.setRead(true);
					else
						item.setRead(false);
					feed.addItem(item);
				}
				feed.setSonSubscription(sub);
				sub.setFeed(feed);
				
			}
			
			return sub;
		} catch (java.lang.Exception e) {
			return null;
		}	
	}
	
	/** Retourne le nombre d'occurences d'un mot clé dans une chaine 
		@param keyword mot clé a comparer
		@param chaine dans la quel se trouve le mot clé
	*/
	protected int compteurKeyword(String keyword, String chaine) {
			int i =-1;
			int pos = -1;
			do {
				i++;
				pos = chaine.indexOf(keyword,pos+1);
			}while( pos+1 < chaine.length() && pos != -1);
			return i;
	}
	
	/** Supprime un item dans la base SQLite a partir d'une structure Item 
		@param item item à supprimer de la base
	*/
	public void deleteFileItem(Item item)throws Exception  {
		int cpt,i;
		String titre, description,id, keyword,frequency;

		TableResult tb = db.get_table("SELECT id_item,title,description FROM tbItem WHERE title='"+item.getTitle()+"'");
		if(!tb.rows.isEmpty()) {
			id = ((String[])tb.rows.elementAt(0))[0];
			titre =((String[])tb.rows.elementAt(0))[1];
			description =((String[])tb.rows.elementAt(0))[2];
			
			tb = db.get_table("SELECT  keyword,frequency,id_item FROM tbIndex,tbEstDansItem WHERE tbIndex.id_index = tbEstDansItem.id_index "+
				" AND tbEstDansItem.id_item="+id);

			if(!tb.rows.isEmpty()) {
				i=0;
				cpt=0;
				while (i < tb.nrows) {
					keyword = ((String[])tb.rows.elementAt(i))[0];
					frequency= ((String[])tb.rows.elementAt(i))[1];
					cpt += compteurKeyword(keyword,description.toLowerCase());
					cpt += compteurKeyword(keyword,titre.toLowerCase());

					if (Integer.parseInt(frequency) - cpt <=0)
						db.exec("DELETE FROM tbIndex WHERE keyword='"+keyword+"'",null);
					else
						db.exec("UPDATE tbIndex SET frequency="+(Integer.parseInt(frequency) - cpt)+" WHERE keyword='"+keyword+"'",null);
					i++;
				}
			}
			db.exec("DELETE FROM tbItem WHERE id_item="+id,null);
			db.exec("DELETE FROM tbEstDansItem WHERE id_item="+id,null);
		}
	}

	/** Supprime un abonnement dans la base SQLite a partir d'une structure Subscription 
		@param sub abonnement à supprimer de la base
	*/
	public void deleteFileSubscription(Subscription sub)throws Exception  {
		Iterator it = sub.getFeed().getSesItems().iterator();
		while (it.hasNext()){	
			deleteFileItem((Item)it.next());
		}
		
		TableResult tb = db.get_table("SELECT id_sub FROM tbSub WHERE linkSub='"+sub.getLink()+"'");
		if (!tb.rows.isEmpty()) {
			db.exec("DELETE FROM tbSub WHERE id_sub='"+((String[])tb.rows.elementAt(0))[0]+"'",null);
			db.exec("DELETE FROM tbItem WHERE id_sub='"+((String[])tb.rows.elementAt(0))[0]+"'",null);
			db.exec("DELETE FROM tbColorSub WHERE id_sub='"+((String[])tb.rows.elementAt(0))[0]+"'",null);
		}

	}

	/** Change l'adresse de l'abonnement, du fichier XML 
		@param sub abonnement a modifier
		@param newLink nouvelle adresse de l'abonnement
	*/
	public void changeSubURL(Subscription sub,String newLink) throws Exception {
		db.exec("UPDATE tbSub SET linkSub='"+newLink+"' WHERE title='"+sub.getFeed().getTitle()+"'",null);
	}
	
	/** Modifie un item en tant que lu 
		@param item definit comme lu
	*/
	public void saveFileReadItem(Item item) throws Exception{
				db.exec("UPDATE tbItem SET read=1 WHERE title='"+item.getTitle()+"'",null);
	}
	/** Cherche a savoir s'il existe déja un abonnement d'une url donnée 
		@return true si l'adresse existe sinon false
		@param url adresse d'un abonnment a comparer dans la base
	*/
	public boolean isURLSubscriptionExist(String url) throws Exception{
		TableResult tb = db.get_table	("SELECT linkSub FROM tbSub WHERE linkSub='"+url+"'");
		return !tb.rows.isEmpty();
	}
	
	/** Split une chaine en un tableau de mots supposés indexables
		@param str chaine a spliter
		@return tableau de mots supposés indexables
	*/
	private String[] formatToIndex(String str){
	
		str = str.replaceAll("\\bhttp://[a-z0-9\\p{Punct}]+\\b"," ");
		str = str.replaceAll("\\p{Punct}"," ");
		str = str.replace("."," ");
		return str.split(" ");
	}
	
	/** Index un item selon son identifiant dans la base 
		@param item item a indexer dans la base
		@param id_item identifiant de l'item dans la base
	*/
	public void indexItem(Item item,int id_item) throws Exception{
		indexKeywords(formatToIndex(item.getDescription()),id_item);
		indexKeywords(formatToIndex(item.getTitle()),id_item);
	}
	
	/** Index un tableau de mots clés 
		@param str tableau de mots clé à indexer
		@param id_item identifiant de l'item dans la base 
	*/
	public void indexKeywords(String[] str,int id_item) throws Exception{
		int id_index;
		TableResult tb = null;
		TableResult tb2 = null;
		for (int i=0;i<str.length;i++)
		{
			if (str[i].length()>2 && motsVides.get(str[i].trim().toLowerCase()) == null) {
				word = str[i]; // on identifie le mot indexe
				tb = db.get_table("SELECT id_index, frequency FROM tbIndex WHERE keyword='"+str[i].trim().toLowerCase()+"'");
				if (tb.rows.isEmpty()){
					db.exec("INSERT INTO tbIndex VALUES (NULL,'"+str[i].trim().toLowerCase()+"',0)",null);
					tb = db.get_table("SELECT id_index, frequency FROM tbIndex WHERE keyword='"+str[i].trim().toLowerCase()+"'");
				}
				id_index = Integer.parseInt(((String[])(tb.rows.elementAt(0)))[0]);
				tb2 = db.get_table("SELECT id_index FROM tbEstDansItem WHERE id_index="+id_index+" AND id_item="+id_item);
				if(tb2.rows.isEmpty())
					db.exec("INSERT INTO tbEstDansItem VALUES ("+id_index+","+id_item+")",null);
				int freq=Integer.parseInt(((String[])(tb.rows.elementAt(0)))[1])+1;
				db.exec("UPDATE tbIndex SET frequency="+freq+" WHERE id_index="+id_index,null);
			}
		}
	}
	
			
	/** Retourne un catalogue tri par un abonnement virtuel , trié par date 
		@return catalogue trié par date
	*/
	public Catalogue getFileByDate() throws Exception{
	try {
		Catalogue catalogue = new Catalogue();
		Subscription vsub = new Subscription();
		Subscription sub;
		Item item;
		Feed feed = new Feed();
		String[] row;
		GregorianCalendar c1,c2;
		
		int i=0;
		TableResult tb = db.get_table(" SELECT * FROM tbItem ORDER BY pubDate DESC ");
	
		if (tb.rows.isEmpty()) return null;
		
		while(i<tb.nrows) {
				row = (String[])tb.rows.elementAt(i);
				
				c2 = new GregorianCalendar();
				c2.setTime(new Date(Long.parseLong(row[5])));
				
				item = new Item();
				item.setPubDate(new Time(Long.parseLong(row[5])));
				item.setID(row[0]);
				item.setTitle(row[2]);
				item.setLink(row[3]);
				item.setDescription(row[4]);
				item.setGuid(row[6]);
				sub = getSonFileSubscription(Integer.parseInt(row[1]));
				item.setSonFeed(sub.getFeed());
				item.getSonFeed().setSonSubscription(sub);
				if(Integer.parseInt(row[7]) == 1)
					item.setRead(true);
				else
					item.setRead(false);
				feed.addItem(item);
				i++;
				
				if (i>= tb.nrows) {
					feed.setTitle(Subscription.timeToRSSDate(item.getPubDate()));
					vsub.setFeed(feed);
					catalogue.addSesSubscriptions(vsub);
					continue;
				}
				row = (String[])tb.rows.elementAt(i);
				c1 = new GregorianCalendar();
				c1.setTime(new Date(Long.parseLong(row[5])));
																
				if((c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH) || 
				 c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH) ||
				 c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR) )&&
				  i < tb.nrows) {
					feed.setTitle(Subscription.timeToRSSDate(item.getPubDate()));
					vsub.setFeed(feed);
					catalogue.addSesSubscriptions(vsub);
					vsub= new Subscription();
					feed = new Feed();
					c2 = c1;				
				}
			}
			return catalogue;
		}catch (Exception e) {
			return null;
		}
	}

	/** Retourne un catalogue trié par index et par frequence dans un subscripton virtuel 
		@return catalogue trié par index et fréquence
	*/
	public Catalogue getFileByIndexAndFrequency()  throws Exception{
		return getFileByIndex("ORDER BY frequency DESC, keyword");
	}
	/** Retourne un catalogue trié par index et par ordre alphabetique dans un subscripton virtuel 
		@return catalogue trié par index et ordre alphabetique
	*/
	public Catalogue getFileByIndexAndKeyword()  throws Exception{
		return getFileByIndex("ORDER BY keyword, tbEstDansItem.id_item");
	}
	
	/** Retourne un catalogue trié par index suivant un ordre non définit 
		@param order ordre de requete 
		@return catalogue trié par index
	*/
	protected Catalogue getFileByIndex(String order) throws Exception {
				Catalogue c = new Catalogue();
				String [] row;
				Subscription vsub,sub	;
				Feed feed;
				Item item;
				String keyword , frequency, id_item;	
				TableResult tb = db.get_table("SELECT *  FROM tbIndex,tbEstDansItem WHERE "+
									  " tbIndex.id_index = tbEstDansItem.id_index "+ order);

				if (tb.rows.isEmpty())
					return null;
		
				int i=0;
				while(i < tb.nrows)
				{
					vsub = new Subscription();
					feed = new Feed();
					row = (String[])tb.rows.elementAt(i);
					keyword = row[1];
					frequency =	row[2];
					while (row[1].equals(keyword) && i < tb.nrows) {
						id_item = row[4];
						item = getFileItem(Integer.parseInt(id_item));
						if( item == null ) {i++; continue;}
						sub = getSonFileSubscription(getSonSubscription(Integer.parseInt(id_item)));
						feed.setSonSubscription(sub);
						item.setSonFeed(sub.getFeed());
						feed.addItem(item);
						i++;	
						if(i < tb.nrows)
							row = (String[])tb.rows.elementAt(i);
						
						}
					feed.setTitle("<span class='freq' style='font-size:"+(9+Integer.parseInt(frequency))+"px'>"+keyword+"</span>" + " ("+frequency+")");
					vsub.setFeed(feed);
					c.addSesSubscriptions(vsub);
				}
				return c;
				
	}
	
	/** Retourne un item identifié par son identifiant dans la base
		@return item correspondant a l'identifiant dans la base
	*/
	protected Item getFileItem(int id_item) throws Exception{
			String [] row;
			Item item;
			TableResult tb = db.get_table("SELECT *  FROM tbItem WHERE id_item="+id_item);

			if(tb.rows.isEmpty()) {
				//System.out.println(id_item);
			return null;
			}
			row = (String[])tb.rows.elementAt(0);
			item = new Item();
			item.setID(row[0]);
			item.setTitle(row[2]);
			item.setLink(row[3]);
			item.setPubDate(new Time(Long.parseLong(row[5])));
			item.setDescription(row[4]);
			item.setGuid(row[6]);
			if(Integer.parseInt(row[7]) == 1)
				item.setRead(true);
			else
				item.setRead(false);
			return item;
	}
	
	/** Retourne un catalogue apres recherche d'un mot clé d'apres l'index 
		@param keyword mot clé recherché
		@return catalogue d'une recherche
	*/
	public Catalogue searchKeyword(String keyword) throws Exception {
		keyword = keyword.toLowerCase();
		TableResult tb = db.get_table("SELECT *  FROM tbItem,tbIndex,tbEstDansItem,tbSub WHERE "+
				" tbIndex.id_index = tbEstDansItem.id_index AND "+
				" tbEstDansItem.id_item = tbItem.id_item AND "+
				" tbItem.id_sub = tbSub.id_sub AND "+
				" tbIndex.keyword='"+keyword+"'");

		if (tb.rows.isEmpty())
			return null;
		else {

			String[] row;
			int  id_sub,id_item;
			int i = 0;
			Catalogue c = new Catalogue();
			Subscription sub, vsub;
			Image img;
			Feed feed;
			Item item;
			
 			while(i < tb.nrows) {
				row = (String[])tb.rows.elementAt(i);
				
				vsub = new Subscription();
				vsub.setLink(row[14]);
				img = new Image();
				feed = new Feed();
				
  				//sub.setDate(row[14]);
  				feed.setTitle(row[16]);
				feed.setLink(row[17]);
				feed.setDescription(row[18]);
				feed.setCopyright(row[19]);
				feed.setPubDate(new Time(Long.parseLong(row[20])));
				// image
				img.setURL(row[21]);
				img.setHeight(Integer.parseInt(row[22]));
				img.setWidth(Integer.parseInt(row[23]));
				img.setTitle(row[24]);
				img.setLink(row[25]);
				feed.setSonImage(img);
				
 				id_sub = Integer.parseInt(row[1]);
 				id_item=-1;
				vsub.setColor(getColorSubscription(id_sub));
 				while(Integer.parseInt(row[1]) == id_sub && i < tb.nrows) {
						if ( id_item != Integer.parseInt(row[0])) {
									id_item = Integer.parseInt(row[0]);
									item = new Item();
									item.setID(row[1]);
									item.setTitle(row[2]);
									item.setLink(row[3]);
									item.setDescription(row[4]);
									item.setPubDate(new Time(Long.parseLong(row[5])));
									item.setGuid(row[6]);
									sub = getFileSubscription(Integer.parseInt(row[1]));
									item.setSonFeed(sub.getFeed());
									sub.getFeed().setSonSubscription(sub);
									if(Integer.parseInt(row[7]) == 1)
										item.setRead(true);
									else
										item.setRead(false);

									feed.addItem(item);
						}
						i++;
						if (i>= tb.nrows) continue;
						row = (String[])tb.rows.elementAt(i);
  				}
  				vsub.setFeed(feed);

  				c.addSesSubscriptions(vsub);
 			}
			return c;
		}
	}
	
	
}
