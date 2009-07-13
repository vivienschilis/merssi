
import javax.swing.colorchooser.*;
import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Vector;
import java.lang.Thread;
import java.awt.event.*;
import java.io.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.net.*;
import java.io.IOException;
import javax.swing.table.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JOptionPane;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
	*View.java est la Classe principale du projet, a savoir l'interface graphique. 
	*Toutes les fonctionalités des autres classes sont utilisées pour donner un programme fini et utilisable.
 * @author Flandre Guillaume / Schilis Vivien
*/

public class View extends JFrame{
	
	//catalogue
	private Catalogue catalogue = new Catalogue();
	private FileRSS file;
	
	//menu
	private JMenuBar menuBar;
	private JMenu menuFichier;
	private JMenuItem jmiQuitter;
	private JMenu menuFlux;
	private JMenuItem jmiAjout;
	private JMenuItem jmiSuppr;
	private JMenuItem jmiUpdate;
	private JMenu menuAff;
	private JRadioButtonMenuItem jmiListe;
	private JRadioButtonMenuItem jmiNonLu;
	private JRadioButtonMenuItem jmiDate;
	private JRadioButtonMenuItem jmiIndex;
	private JCheckBoxMenuItem jmiRech;
	private JMenu menuAide;
	private JMenuItem jmiApropos;
	private JMenuItem jmiSupprNews;
	//Popup
	private JMenuItem jppActu;
	private JMenuItem jppInfo;
	private JPopupMenu pop;
	private JMenuItem jppColor;
	
	//Recherche
	private JPanel topBar;
	private JLabel label;
	private JTextField champRecherche;
	private JButton lancerRecherche;
	
	//Composants
	private JPanel paneNews;
	private JSplitPane paneDroite;
	private JPanel paneGauche;
	private JScrollPane listeScroller;
	private JSplitPane jsp;
	private JPanel bottomBar;
	private JPanel bbGauche;
	private JPanel bbDroite;
	
	
	//boutons bas
	private JButton boutonAjout;
	private JButton boutonSuppr;
	private JButton boutonSupprNews;
	
	//feeds list
	private JList listeFeeds = new JList();
	
	private Object[] selectedFeeds;
	private int[] selectedFeedsIndex;
	private DefaultListModel listModel= new DefaultListModel();
	private Vector data = new Vector();
	private JPanel paneNewsList;
	private JScrollPane newsListScroll;
	private JLabel msg ;
	
	//news list
	private JTable newsTable=new JTable();
	private Vector columnNames=new Vector();	
	private ListSelectionModel rowSM;
	private ListSelectionListener lsl ;
	private int[] selectedNews;
	private TableSorter sorter;
	private int[] sortingStatus={TableSorter.NOT_SORTED,TableSorter.NOT_SORTED};
	
	//news
	private Item news;
	private JEditorPane html;
	private Thread t,thread_maj;
	private int modeAffichage=0;
	//**************************************
	//Constructeur
	//**************************************
	
	/** Creation des composants de l'interface et de ses évènements */
	public View()  throws Exception, IOException, UnknownHostException{
		super("meRSSi");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.growbox.intrudes","false");
		
		WindowListener wl = new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if (t!=null) {	
				setVisible(false);
				JOptionPane.showMessageDialog(View.this, "L'application est encore en etat de sauvegarde", "avertissement", JOptionPane.WARNING_MESSAGE);
					try {
						if(t!=null)
						t.join();
					}catch(Exception ee){
						System.out.print(ee);
					};
				}
				else
					System.exit(0);
			}
		};
		addWindowListener(wl);
		
		//*******************************
		//Evénements
		
		//événements de liste
		
		lsl = new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if(e.getSource()==listeFeeds){
					selectedFeeds = listeFeeds.getSelectedValues();
					selectedFeedsIndex = listeFeeds.getSelectedIndices();
					if (selectedFeedsIndex.length>0)
						showListNews(selectedFeedsIndex[0]);
				}
				
				if(e.getSource()==rowSM){
					if (e.getValueIsAdjusting()) return;
					
					ListSelectionModel lsm =
						(ListSelectionModel)e.getSource();
					if (lsm.isSelectionEmpty()) {
						//pas de sélection
					} else {
						selectedNews = newsTable.getSelectedRows();
						//selectedRow est sélectionné
						showNews(selectedNews[0]);

					}
				}
			}
		};
		
		//événements de liens
		HyperlinkListener hl = new HyperlinkListener(){
			public void hyperlinkUpdate(HyperlinkEvent e) {			
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					html = (JEditorPane) e.getSource();
					try {
						String os = System.getProperty("os.name").toLowerCase();
						if ((os.indexOf("windows 95") != -1) ||
							(os.indexOf("windows 98") != -1) ||
							(os.indexOf("windows me") != -1)) {
							String[] command = new String[]{"command.com", "/c", "start", "\"\"", '"' + e.getURL().toString() + '"'};
							Runtime.getRuntime().exec(command);
						} else if (os.indexOf("windows") != -1) {
							String[] command = new String[]{"cmd.exe", "/c", "start", "\"\"", '"' + e.getURL().toString() + '"'};
							Runtime.getRuntime().exec(command);
						} else{
							html.setPage(e.getURL());
						}
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}	
		};
		
		//événements d'action
		MouseListener popup = new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}
			
			public void mouseEntered(MouseEvent e) {
			}
			
			public void mouseExited(MouseEvent e) {
			}
			
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger() && modeAffichage==0) {
					if(!listeFeeds.isSelectionEmpty())
						showPopup(e);
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					if(!listeFeeds.isSelectionEmpty())
						showPopup(e);
				}
			}
			
			private void showPopup(MouseEvent e) {
				pop.show(e.getComponent(), e.getX(), e.getY());
			}
		};
		
		// Action listener
		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==jmiQuitter){
					if (t!=null) System.out.print("vous devez attendre la fin de la sauvegarde");
					else
						System.exit(0);
					
				}
				else if (e.getSource()==jmiAjout||e.getSource()==boutonAjout){
					if (modeAffichage==0)	addFeed();
					else if(modeAffichage==3){
						boutonSuppr.setEnabled(true); jmiSuppr.setEnabled(true);
						boutonAjout.setEnabled(false); jmiAjout.setEnabled(false);
						organiserParIndexEtKeyword();
					}
				}
				else if (e.getSource()==jmiSuppr||e.getSource()==boutonSuppr){
					if (modeAffichage==0) remFeed();
					else if(modeAffichage==3) {
					boutonSuppr.setEnabled(false);jmiSuppr.setEnabled(false);
					boutonAjout.setEnabled(true); jmiAjout.setEnabled(true);
					organiserParIndex();}

				}
				if(e.getSource()==jmiSupprNews||e.getSource()==boutonSupprNews){
					remNews();
				}
				else if(e.getSource()==jmiApropos){
					apropos();
				}
				else if(e.getSource()==jmiListe){
					 menuFlux.setEnabled(true);
					boutonAjout.setEnabled(true);
					boutonSuppr.setEnabled(true);
					boutonAjout.setText("+");
					boutonSuppr.setText("X");
					modeAffichage=0;
					organiserParFeed();
				}
				else if(e.getSource()==jmiNonLu){
					menuFlux.setEnabled(false);
					boutonAjout.setEnabled(false);
					boutonSuppr.setEnabled(false);
					modeAffichage=1;
					organiserParFeedNonLu();
				}
				else if(e.getSource()==jmiDate){
					menuFlux.setEnabled(false);
					boutonAjout.setEnabled(false);
					boutonSuppr.setEnabled(false);
					modeAffichage=2;
					organiserParDate();
				}
				else if(e.getSource()==jmiIndex){
					 menuFlux.setEnabled(false);
					boutonAjout.setEnabled(true);
					boutonSuppr.setEnabled(false);			
					boutonAjout.setText("Mot");
					boutonSuppr.setText("Freq");
					jmiAjout.setText("Trie par mots clés");
					jmiSuppr.setText("Trie par frequences");
					modeAffichage=3;
					organiserParIndex();
				}
				
				else if(e.getSource()==lancerRecherche){
					showListResearch(champRecherche.getText());
				}
				else if(e.getSource()==jmiRech){
					if(jmiRech.getState())
						topBar.setVisible(true);
					else
						topBar.setVisible(false);
				}
				else if(e.getSource()==jppActu){
					t = new Thread() {
						public void run() {
							actualiserFeed();
						}
					};
					t.start();
				}
				else if(e.getSource()==jppInfo){
					if(!listeFeeds.isSelectionEmpty()) {
						DialogueInfoSub f = new DialogueInfoSub( (Subscription)listModel.getElementAt(listeFeeds.getSelectedIndex() ));
					}
				}
				else if(e.getSource()==jmiUpdate){
					if (t !=null) System.out.println("attention une mise a jour est en cour!!");
					else {
						t = new Thread() {
							public void run() {
								UpdateListe();
							}
						};
						t.start();
					}
				}
			}
		};
		
		//
		file = new FileRSS("database.db");
		catalogue = file.getFile();
		
		
		//*******************************
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.width*3/4,screenSize.height*3/4);
		
		//affichage au centre de l'écran
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		this.setLocation( (screenSize.width - frameSize.width) / 2,
						  (screenSize.height - frameSize.height) / 2);
		
		//*********************************
		// Menu
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//
		menuFichier = new JMenu("Fichier"); 
		menuFichier.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menuFichier);
		jmiQuitter = new JMenuItem("Quitter");
		jmiQuitter.addActionListener(al);
		jmiQuitter.setMnemonic('q');
		
		menuFichier.add(jmiQuitter);
		
		//
		menuFlux = new JMenu("Flux"); 
		menuBar.add(menuFlux);
		
		jmiUpdate = new JMenuItem("Actualiser");
		jmiUpdate.addActionListener(al);
		
		jmiAjout = new JMenuItem("Ajouter un flux");
		jmiAjout.addActionListener(al);
		
		jmiSuppr = new JMenuItem("Supprimer les flux sélectionnés");
		jmiSuppr.addActionListener(al);
		
		jmiSupprNews = new JMenuItem("Supprimer les news sélectionnées");
		jmiSupprNews.addActionListener(al);
		
		menuFlux.add(jmiUpdate);
		menuFlux.addSeparator();
		menuFlux.add(jmiAjout);
		menuFlux.add(jmiSuppr);
		menuFlux.addSeparator();
		menuFlux.add(jmiSupprNews);
		//
		menuAff = new JMenu("Affichage");
		menuBar.add(menuAff);
		
		ButtonGroup group = new ButtonGroup();
		
		jmiListe = new  JRadioButtonMenuItem("Liste Simple",true);
		jmiListe.addActionListener(al);
		
		jmiNonLu = new  JRadioButtonMenuItem("Non Lues",true);
		jmiNonLu.addActionListener(al);
		
		jmiDate = new JRadioButtonMenuItem("Liste par date");
		jmiDate.addActionListener(al);
		
		jmiIndex = new JRadioButtonMenuItem("Liste par index");
		jmiIndex.addActionListener(al);
		
		jmiRech = new JCheckBoxMenuItem("Rechercher",false);
		jmiRech.addActionListener(al);
		
		group.add(jmiListe);
		menuAff.add(jmiListe);
		group.add(jmiNonLu);
		menuAff.add(jmiNonLu);
		group.add(jmiDate);
		menuAff.add(jmiDate);
		group.add(jmiIndex);
		menuAff.add(jmiIndex);
		
		menuAff.addSeparator();
		menuAff.add(jmiRech);
		
		// 
		menuAide = new JMenu("Aide");
		menuBar.add(menuAide);
		jmiApropos = new JMenuItem("Apropos");
		jmiApropos.addActionListener(al);
		menuAide.add(jmiApropos);
		//*******************************
		
		
		// Recherche
		topBar = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		label = new JLabel("Rechercher : ");
		champRecherche = new JTextField(8);
		lancerRecherche = new JButton("OK");
		lancerRecherche.addActionListener(al);
		topBar.add(label);
		topBar.add(champRecherche);
		topBar.add(lancerRecherche);
		topBar.setVisible(false);
		//*******************************
		// Main Windows
		
		//liste des news d'un flux
		paneNewsList = new JPanel(new BorderLayout());
		
		columnNames.add("Titre");
		columnNames.add("Date");
		newsTable = new JTable(data,columnNames);
		newsTable.setShowGrid(false);
		newsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		newsListScroll = new JScrollPane(newsTable);
		
		paneNewsList.add(newsListScroll,BorderLayout.CENTER);
		paneNewsList.validate();
		paneNewsList.setBackground(Color.white);
		
		//news elle-même affichée ici :
		paneNews = new JPanel(new BorderLayout());
		paneNews.setBackground(Color.white);
		//paneNews.setMinimumSize(new Dimension(paneNews.getWidth(),this.getHeight()/2));
		html = new JEditorPane();
		html.setEditable(false);
		html.addHyperlinkListener(hl);
		JScrollPane html_scroll = new JScrollPane(html);
		paneNews.add(html_scroll,BorderLayout.CENTER);
		
		//séparation des deux sous-fenêtres de droite
		paneDroite = new JSplitPane(JSplitPane.VERTICAL_SPLIT,paneNewsList,paneNews);
		paneDroite.setOneTouchExpandable(true);
		
		
		//		sous-fenêtre de gauche
		paneGauche = new JPanel(new BorderLayout());
		//showListFeeds();
		
		//		ajout d'éléments à la liste
		
		listeFeeds = new JList(listModel);
		listeFeeds.setCellRenderer(new MonRenderer()); 		
		listeFeeds.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listeFeeds.setLayoutOrientation(JList.VERTICAL);
		listeFeeds.setVisibleRowCount(-1);
		//		le scroller de la liste
		listeScroller = new JScrollPane(listeFeeds);
		paneGauche.add(listeScroller);
		
		
		listeFeeds.addListSelectionListener(lsl);
		
		//	séparation des sous-fenêtres : paneGauche(liste des flux) et paneDroite
		jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,paneGauche,paneDroite);
		jsp.setDividerLocation(100);
		jsp.setDividerSize(8);
		jsp.setOneTouchExpandable(true);
		
		//********************************
		// Boutons bas
		
		bottomBar = new JPanel(new BorderLayout());
		bbGauche = new JPanel(new FlowLayout(FlowLayout.LEADING,10,5));
		
		//ajouter un flux
		boutonAjout = new JButton("+");
		boutonAjout.setSize(10,10);
		boutonAjout.addActionListener(al);
		
		//supprimer un flux
		boutonSuppr = new JButton("X");
		boutonSuppr.addActionListener(al);
		
		bbGauche.add(boutonAjout);
		bbGauche.add(boutonSuppr);
		
		bbDroite = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		boutonSupprNews = new JButton("X");
		boutonSupprNews.addActionListener(al);
		
		bbDroite.add(boutonSupprNews);
		
		bottomBar.add(bbGauche,BorderLayout.LINE_START);
		bottomBar.add(bbDroite,BorderLayout.LINE_END);
		
		// PopMenu
		pop = new JPopupMenu();
		jppActu = new JMenuItem("Actualiser");
		jppActu.addActionListener(al);
		pop.add(jppActu);
		
		pop.addSeparator();
		
		jppInfo = new JMenuItem("Info");
		jppInfo.addActionListener(al);
		pop.add(jppInfo);

		Color initialColor = Color.white;
   	   	JColorChooser chooser = new JColorChooser(initialColor);
		jppColor = new JMenuItem("Attribuer couleur");
		
		jppColor.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Color newColor = JColorChooser.showDialog(
                                                View.this,
                                                "Choissiez une couleur pour ce flux",
                                                Color.white);
                    if (newColor != null) {
                    			if(!listeFeeds.isSelectionEmpty()){
									((Subscription)catalogue.getSesSubscriptions().elementAt(selectedFeedsIndex[0])).setColor(newColor);
									showListNews(listeFeeds.getSelectedIndex());
									try {
									file.saveCorlorSubscription((Subscription)catalogue.getSesSubscriptions().elementAt(selectedFeedsIndex[0]),newColor);
									}
									catch(Exception ee){
										System.out.println(ee);
									}
								}
                    }
                }
            }
        );
		pop.add(jppColor);
		
		listeFeeds.addMouseListener(popup);
		
		//********************************
		// Et Dieu créa la JFrame
		
		//	ajout d'éléments à la liste
		showListFeeds();
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(topBar,BorderLayout.PAGE_START);
		getContentPane().add(jsp,BorderLayout.CENTER);
		getContentPane().add(bottomBar,BorderLayout.PAGE_END);

		maj_background();
	//	thread_maj.start();
		
		setLocationRelativeTo(null);
		setVisible(true);
		setSize(600,500);
		validate();
		
	}
	
	/** Met a jour les flux en arrière plan */
	public void maj_background() {
		thread_maj = new Thread () {
				public void run() {
					Iterator it = catalogue.getSesSubscriptions().iterator();
					boolean cond = true;
					while(cond) {
						try{
							Thread.sleep(10000);
					  		if(t==null && catalogue !=null && modeAffichage==0) {
						  		catalogue.refresh();
								file.saveFile(catalogue);
								catalogue=file.getFile();
								actualiserFeed();
							}
						}catch(Exception e){
							System.out.println(e);
						}
					}
				}
			};
	}
	
	/** Affichage des news organiser par date */
	private void organiserParDate() {
		try {
			catalogue = file.getFileByDate();
			actualiserListFeeds();
			if(listeFeeds.isSelectionEmpty())
				showListNews(0);
			else showListNews(listeFeeds.getSelectedIndex());
		}catch (Exception ee) {
			System.out.println(ee);
		};
	}
		/** Affichage des news organiser par flux */
	private void organiserParFeed() {
		try {
			catalogue = file.getFile();
			actualiserListFeeds();
			if(listeFeeds.isSelectionEmpty())
				showListNews(0);
			else showListNews(listeFeeds.getSelectedIndex());
		}catch (Exception ee) {
			System.out.println(ee);
		};
	}
		/** Affichage des news non lus organiser par flux */
	private void organiserParFeedNonLu() {
		try {
			catalogue = file.getFileUnRead();
			actualiserListFeeds();
			if(listeFeeds.isSelectionEmpty())
				showListNews(0);
			else showListNews(listeFeeds.getSelectedIndex());
		}catch (Exception ee) {
			System.out.println(ee);
		};
	}
	/** Affichage des news organiser par index/frequence */
	private void organiserParIndex() {
		try {
			catalogue = file.getFileByIndexAndFrequency();
			actualiserListFeeds();
			if(listeFeeds.isSelectionEmpty())
				showListNews(0);
			else showListNews(listeFeeds.getSelectedIndex());
		}catch (Exception ee) {
			System.out.println(ee);
		};
	}
		/** Affichage des news organiser par index/mot clé */
	private void organiserParIndexEtKeyword() {
		try {
			catalogue = file.getFileByIndexAndKeyword();
			actualiserListFeeds();
			if(listeFeeds.isSelectionEmpty())
				showListNews(0);
			else showListNews(listeFeeds.getSelectedIndex());
		}catch (Exception ee) {
			System.out.println(ee);
		};
	}

	/** Suppression des news selectionés */
	public void remFeed(){
		if (!(listeFeeds.isSelectionEmpty())){
			int reponse = JOptionPane.showConfirmDialog(
														View.this,
														"Etes vous sûr de supprimer les flux sélectionnés ?",
														"Suppression de Flux",
														JOptionPane.YES_NO_OPTION);
			
			if (reponse==JOptionPane.YES_OPTION){
				try {

				Object[] selTemp = selectedFeeds;
				for (int i=0;i<selTemp.length;i++){
					Subscription sub =(Subscription)selTemp[i];
					file.deleteFileSubscription(sub);
					listModel.removeElement(selTemp[i]);
					catalogue.getSesSubscriptions().remove(selTemp[i]);
				}
				data.removeAllElements();
				newsTable = new JTable(data,columnNames);
				newsTable.setShowGrid(false);
				newsListScroll = new JScrollPane(newsTable);
				paneNewsList.removeAll();
				paneNewsList.add(newsListScroll,BorderLayout.CENTER);
				paneNewsList.validate();
				paneNews.removeAll();
				paneNews.add(new JLabel(""));
				paneNews.validate();
				}catch(Exception e){
					System.out.print(e);
				}
			}	
		}
	}
	
	/** Dialogue d'ajout d'un flux au catalogue */
	public void addFeed(){
		DialogueAjoutFeed dial = new DialogueAjoutFeed(this);
		
	}
	
	/** Ajout d'un flux dans la JList 
		@param url adresse du flux
	*/
	public void addFeedInList(String url) throws Exception{
		try{
		catalogue.addSesSubscriptions(new Subscription(url));
		t = new Thread() {
			public void run() {
				try {
					file.saveFileSub((Subscription)catalogue.getSesSubscriptions().lastElement());
					t=null;
				}catch(Exception e) {
					System.out.println(e);
				};
			}
		};
		listModel.addElement
				((Subscription)catalogue.getSesSubscriptions().lastElement());
		t.start();
		
		}catch (UnknownHostException e)
			{ JOptionPane.showMessageDialog(	
								View.this, "Echec de la connexion","Erreur", JOptionPane.ERROR_MESSAGE);
			}
		    catch (IOException e)
			{  JOptionPane.showMessageDialog(	
								View.this, "Echec de la connexion","Erreur", JOptionPane.ERROR_MESSAGE);}
			catch (SAXParseException e)
			{  JOptionPane.showMessageDialog(	
								View.this, "Echec du Paser \n Vérifiez que le flux est de type XML","Erreur", JOptionPane.ERROR_MESSAGE);
		}
			
		

		
	}
	
	/** Affichage de la liste des Abonnements dans la JList */
	public void showListFeeds(){

		if(catalogue!= null && catalogue.getSesSubscriptions()!=null) {
				Iterator it = catalogue.getSesSubscriptions().iterator();
				while(it.hasNext()){
					listModel.addElement((Subscription)it.next());
				}
		}
	}
	
	/** Affichage d'une liste de news dans le JTable 
		@param i indice de l'abonnement du catalogue
	*/
	
	public void showListNews(int i){

		if (catalogue!=null && catalogue.getSesSubscriptions().size()>i) {
				Iterator it = 
				((Subscription)catalogue.getSesSubscriptions().elementAt(i)).getFeed().getSesItems().iterator();
				Item item;
				data.removeAllElements();
				while (it.hasNext()){
					item = (Item)it.next();
					Vector v = new Vector();
					v.add(item);
					v.add(item);
					data.add(v);
				}
				
				AbstractTableModel model = new AbstractTableModel(){
					
					public int getRowCount() {
						return data.size();
					}
					
					public int getColumnCount() {
						return columnNames.size();
					}
					
					public String getColumnName(int arg0){
						return (String)columnNames.elementAt(arg0);
					}
					
					public Object getValueAt(int arg0, int arg1) {
						return ((Vector)data.elementAt(arg0)).elementAt(arg1);
					}
					
					public boolean isCellEditable(int arg0,int arg1){
						return false;
					}
					
				};
				
				newsTable = new JTable(data,columnNames);
				newsTable.setShowGrid(false);
				//tri
				sorter = new TableSorter(model);
				newsTable.setModel(sorter);
				AlternateCellTableModel renderer = new AlternateCellTableModel();
				renderer.setColorsSel(new Color(111,143,149),Color.white);
				renderer.setColors(Color.white,Color.black);
				TableColumn col;
				
				for (int k=0;k<newsTable.getColumnCount();k++){//restitution du statut
					((TableSorter) newsTable.getModel()).setSortingStatus(k,sortingStatus[k]);
					col =  newsTable.getColumnModel().getColumn(k);
					col.setCellRenderer(renderer);
					if (k==1) col.setPreferredWidth(10);

				}
				sorter.setTableHeader(newsTable.getTableHeader());

				//
				rowSM = newsTable.getSelectionModel();
				rowSM.addListSelectionListener(lsl);
				newsListScroll = new JScrollPane(newsTable);
				paneNewsList.removeAll();
				paneNewsList.add(newsListScroll,BorderLayout.CENTER);
				paneNewsList.validate();
		}
	}
	
	/** Affichage d'une News dans le JPaneEditor 
		@param i indice de la news dans un flux
	*/
	
	public void showNews(int i){          try {
			news = ((Item)newsTable.getValueAt(newsTable.getSelectedRow(),0));
	        news.setRead(true);
	        file.saveFileReadItem(news);
	        listeFeeds.repaint();
			paneNews.removeAll();
			makeHTML(news);
			html = new JEditorPane();	
			html.setPage("file:news.html");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
		html.setEditable(false);
		html.validate();
		JScrollPane html_scroll = new JScrollPane(html);
		paneNews.add(html_scroll,BorderLayout.CENTER);
		paneNews.validate();
	}
	
	/** Formatage d'une news au format HTML pour la lecteur de la news dans le JPaneEditor */
	public void makeHTML( Item item){
		Feed feed = item.getSonFeed();
		String dateNews = 	Subscription.timeToRSSDate(item.getPubDate())+ " a "+((item.getPubDate().toString()).substring(0,5)).replace(":","h");
		String titreNews = item.getTitle();
		String lienNews = item.getLink();
		String descriptionNews = item.getDescription();
		String guidNews = item.getGuid();
		String titreFeed = feed.getTitle();
		String lienFeed= feed.getLink();
		String descriptionFeed = feed.getDescription();
		String copyrightFeed = feed.getCopyright();
		String dateFeed = feed.getPubDate().toString();
		String sonImageFeed = feed.getSonImage().getURL().trim();
		String page;
		
		page=
			"<html><head><style type=\"text/css\">"+
			"body{margin:0;padding:0;color: #000;font-family:Verdana, Geneva, Arial, Helvetica, sans-serif;background:url(body.gif);}"+
			"#header{margin:0;padding:5px;font-size:115%;background:#6F8F95;color:white;}" +
			"#infos{background:#BFD1DB;border-color:#999999;border-width:1px;border-style:solid ;" +
			"margin-top:10px;margin-left:10px;margin-right:10px;padding:5px;}" +
			"table{margin:0;padding:0;} #desc{font-weight:bold;text-align:center;}" +
			"td{text-align:center;} " +
			"#news{background:#BFD1DB;border-color:#999999;border-width:1px;border-style:solid ;" +
			"margin-top:20px;margin-right:10px;margin-left:10px;margin-bottom:5px;padding-top:10px;padding-right:5px;padding-left:5px;padding-bottom:10px;}" +
			"#titre{color:#465B60;font-weight:bold;font-size:110%;margin:0;text-align:center;text-decoration:underline;}"+
			".freq{color:red;}"+
			"</style></head><body>" +
			"<div id=\"header\">"+
			"<table><tr><td>";
		if(!sonImageFeed.equals("")){
			page +="<img src=\""+sonImageFeed+"\">";
		}
		page+="</td>"+
			"<td>"+titreFeed+"</td></tr></table></div>"+
			"<div id=\"infos\"><table width=\"100%\"><tr><td><a href=\""+lienFeed+"\">Visiter le site</a></td>"+
			"<td>"+copyrightFeed+"</td>"+
			"<td>le : "+dateFeed+"</td></tr></table>"+
			"<div id=\"desc\">"+descriptionFeed+"</div></div>"+
			"<div id=\"news\"><div id=\"titre\">"+titreNews+"</div><br>"+
			"<table width=\"100%\"><tr><td><a href=\""+guidNews+"\">Permalien</a></td>"+
			"<td>publiée le : "+dateNews+"</td></tr></table><br>"+
			"<br>"+descriptionNews+"<br>" +
			"<a href=\""+lienNews+"\">Lire la suite</a><br>"+
			"</div></body></html>";
		
		//écriture du fichier
		try {
			FileWriter fluxFichEcriture = new FileWriter("news.html",false);
			BufferedWriter tampEcriture = new BufferedWriter(fluxFichEcriture);
			tampEcriture.write(page);
			tampEcriture.close();
		} catch (FileNotFoundException e){e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		
	}
	
	/** Supression des news sélectionnées */
	public void remNews(){
		Item item;
		if(selectedNews!=null){
			int reponse = JOptionPane.showConfirmDialog(
														View.this,
														"Etes vous sûr de supprimer les news sélectionnées ?",
														"Suppression de News",
														JOptionPane.YES_NO_OPTION);
			if (reponse==JOptionPane.YES_OPTION){
				
				int taille = selectedNews.length;
				int[] selTemp = (int[]) selectedNews.clone();
				
				for (int k=0;k<newsTable.getColumnCount();k++){//mémorisation du statut
					sortingStatus[k]= sorter.getSortingStatus(k);
				}
				
				for (int i=0;i<taille;i++){
					try {
						item =(Item)newsTable.getValueAt(selTemp[i],0);
						file.deleteFileItem(item);
						((Item)newsTable.getValueAt(selTemp[i],0)).getSonFeed().getSesItems().remove(item);
					}catch(Exception e) {	
								System.out.println(e);
					}
					
					for (int k=0;k<newsTable.getColumnCount();k++){//restitution du statut
						((TableSorter) newsTable.getModel()).setSortingStatus(k,sortingStatus[k]);
					}
					newsTable.setModel(sorter);
				}
			
				newsTable.setModel(sorter);
				paneNews.removeAll();
				paneNews.add(new JLabel(""));
				paneNews.validate();
				if(!listeFeeds.isSelectionEmpty())
					showListNews(listeFeeds.getSelectedIndex());

				listeFeeds.repaint();
			}
		}
	}
	
	
	/** Met a jour l'ensemble des flux avec affichage de l'indexation */
	public void UpdateListe() {
	    JFrame jf = new JFrame("Indexation");
		
	    JPanel jp =  new JPanel();
	    JProgressBar jpBar = new JProgressBar();
		jf.getContentPane().setLayout(new BorderLayout());
		msg=new JLabel(); 
		jpBar.setIndeterminate(true);
		
	    jp.add(jpBar);
		jf.getContentPane().add(jp,BorderLayout.CENTER);
		jf.getContentPane().add(msg,BorderLayout.PAGE_END);
		
		jf.setBounds(500, 500, 200, 80);
		jf.setResizable(false);
		
		Iterator it = catalogue.getSesSubscriptions().iterator();
		while(it.hasNext()) {
			try {
				((Subscription)it.next()).refresh();
			}catch (UnknownHostException e)
			{ JOptionPane.showMessageDialog(	
								View.this, "Echec de la connexion","Erreur", JOptionPane.ERROR_MESSAGE);}
		    catch (IOException e)
			{  JOptionPane.showMessageDialog(	
								View.this, "Echec de la connexion","Erreur", JOptionPane.ERROR_MESSAGE);}
			catch (SAXParseException e)
			{  JOptionPane.showMessageDialog(	
								View.this, "Echec du Paser \n Vérifiez que le flux est de type XML","Erreur", JOptionPane.ERROR_MESSAGE);}
			}
		
		file.setWord("Indexation des nouveaux mots");
		try{
			t = new Thread(){
				public void run(){
					try {
						FileRSS f = new FileRSS("database.db");
						String word;
						while ( (word=file.getWord()) != null ) {
							msg.setText("Indexation de : "+word);
						}
					}catch(Exception e) {
						System.out.println(e);
					}
				}
			};
			t.start();
			jf.setVisible(true);
			file.saveFile(catalogue);
			jf.dispose();
			catalogue=file.getFile();
			if(t!=null)
			t.join();
			actualiserFeed();
			if(!listeFeeds.isSelectionEmpty())
					showListNews(listeFeeds.getSelectedIndex());
			listeFeeds.repaint();
			t=null;
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	/** Affichage des news selon la recherche dans l'index par mot clé
		@param keyword mot clé a rechercher dans l'index
	*/
	public void showListResearch(String keyword){
		try {
			if (keyword.equals("")){
				catalogue = file.getFile();
			}
			else{
				Catalogue c = file.searchKeyword(keyword);
				if ( c== null) {
					JOptionPane.showMessageDialog(	
								View.this, "Le mot "+keyword+"ne fait partie d'aucune news","Recherche", JOptionPane.INFORMATION_MESSAGE);
				}else {
					catalogue = c;
					paneNewsList.removeAll();
				}
			}
			
			actualiserListFeeds();
			if(listeFeeds.isSelectionEmpty())
				showListNews(0);
			else showListNews(listeFeeds.getSelectedIndex());
			
		}catch(Exception e){
			System.out.print(e);
		}
	}
	
	/** Affichage de la fenetre de dialogue A propos */
	public void apropos() {
		DialogueApropos dial = new DialogueApropos();
	}
	
	/** Actualise un flux selectionné */
	public void actualiserFeed() {
		try {

			if(!listeFeeds.isSelectionEmpty()) {
				int id = listeFeeds.getSelectedIndex();
				Subscription sub = (Subscription)listModel.getElementAt(listeFeeds.getSelectedIndex());
				try{
					sub.refresh();
				}catch (UnknownHostException e)
				{ JOptionPane.showMessageDialog(	
							View.this, "Echec de la connexion","Erreur", JOptionPane.ERROR_MESSAGE);}
		    	catch (IOException e)
				{  JOptionPane.showMessageDialog(	
									View.this, "Echec de la connexion","Erreur", JOptionPane.ERROR_MESSAGE);}
				catch (SAXParseException e)
				{  JOptionPane.showMessageDialog(	
									View.this, "Echec du Paser \n Vérifiez que le flux est de type XML","Erreur", JOptionPane.ERROR_MESSAGE);}
				
				file.saveFileSub(sub);
				catalogue = file.getFile();	
				actualiserListFeeds();
				listeFeeds.repaint();
				if(listModel.getSize()>id)
					showListNews(id);
				t=null;
			}
		}catch(Exception e){
			System.out.print(e);
		}
	}
	
	/** Actualise l'affichage de la liste des flux */
	public void actualiserListFeeds() {
		listModel.removeAllElements();
		showListFeeds();
	}
	
	// Render pour les icons!!************************************************************

    class MonRenderer extends JLabel implements ListCellRenderer
	{
		public MonRenderer() {
            setOpaque(true);
        }
		public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	        Subscription sub = (Subscription)value;

			if(isSelected) {
				setBackground(new Color(111,143,149));
				setForeground(Color.white);

				setText("<html><body>"+sub.getFeed().getTitle()+"<br>"+
						"<span style='font-size:10;font-family:Verdana, Geneva, Arial, Helvetica, sans-serif;'>non lues : "+
						sub.getFeed().nNewItems()+"/"+sub.getFeed().nItems()+"<br>");
			}
			else {
					if(sub.getColor()!=null) {
						setForeground(Color.white);
						setBackground(sub.getColor());
						setText("<html><body>"+sub.getFeed().getTitle()+"<br>"+
							"<span style='font-size:10;font-family:Verdana, Geneva, Arial, Helvetica, sans-serif;'>non lues : "+
							sub.getFeed().nNewItems()+"/"+sub.getFeed().nItems()+"<br>");
					
					}else {
						setForeground(Color.black);
						setBackground(Color.white);
						setText("<html><body>"+sub.getFeed().getTitle()+"<br>"+
							"<span style='font-size:10;font-family:Verdana, Geneva, Arial, Helvetica, sans-serif;color:#6F8F95;'>non lues : "+
							sub.getFeed().nNewItems()+"/"+sub.getFeed().nItems()+"<br>");
				}
			}
			try {
		        if(sub.getFeed().getSonImage().getURL().equals(""))
					setIcon(new ImageIcon("sub.png"));
			    else {
		  	        URL url =new URL(sub.getFeed().getSonImage().getURL());
					ImageIcon img = new ImageIcon(url);

					if(img.getIconWidth() < 60 && img.getIconHeight()< 60)
							setIcon(img);
					else
					setIcon(new ImageIcon("sub.png"));
				}
			}catch(IOException e) {
				return this;
			};
	        return this;
		}
	}
    //Render du Table ****************************************************************************	/** Procedure de lancement de l'application */
	public static void main (String[] arguments) {
		View view;
		try {
			UIManager.setLookAndFeel(
									 UIManager.getCrossPlatformLookAndFeelClassName());
			System.setProperty("http.proxyHost","proxy-web");
			System.setProperty("http.proxyPort","3128");
			view = new View();
		} catch (Exception e) { 
			JOptionPane.showMessageDialog(	
								null,
								 "Une erreur s'est produite lors de la consultation\n de la base de donnnée SQLite","Ajout de flux",
		                                    JOptionPane.ERROR_MESSAGE);

		}
		
		
	}
	
}
