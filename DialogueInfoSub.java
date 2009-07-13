import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class DialogueInfoSub  extends JDialog {
	
	private String url = null;
    private JTextField adresse;
    private FileRSS file;
    private Subscription subscription;
    
    
	public DialogueInfoSub(Subscription sub){
	subscription = sub;
		WindowListener wl = new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				setVisible(false);
			}
		};
		
		addWindowListener(wl);
		
		//********************************************
		//principal
		//éléments du dialogue
		adresse = new JTextField(12);
		adresse.setText(sub.getLink());
		Object[] elements ={sub.getFeed().getTitle()," ","Abonnement fait le "+sub.getDate(),
		"Dernière publication : "+sub.getFeed().getPubDate()," ","Adresse du flux RSS",adresse};

		//création du type de dialogue
		final JOptionPane optionPane = new JOptionPane(
				elements,
				JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);

		setContentPane(optionPane);
		setDefaultCloseOperation(
				JDialog.DO_NOTHING_ON_CLOSE);

		//****************************************************
		//événements
		optionPane.addPropertyChangeListener(
			    new PropertyChangeListener() {
			        public void propertyChange(PropertyChangeEvent e) {
			            String prop = e.getPropertyName();

			            if (isVisible() 
			             && (e.getSource() == optionPane)
			             && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
//			            	si boutons appuyés

			        		int value = ((Integer)optionPane.getValue()).intValue();
			                	int numVal=0;
			                if (value == JOptionPane.OK_OPTION){
			                	url= adresse.getText();
			                	Pattern pattern=Pattern.compile("\\bhttp://[a-z0-9\\p{Punct}]+\\b",Pattern.CASE_INSENSITIVE);
			                	Matcher matcher = pattern.matcher(url);
			                	if (matcher.matches()){
			                		try {
			                			file = new FileRSS("database.db");
										file. changeSubURL(subscription,url);
										subscription.setLink(url);
									}catch(Exception ee) {
										System.out.println(ee);	
									}
			                	}else{
			                		//message d'erreur
			                		adresse.selectAll();
			                		JOptionPane.showMessageDialog(
		                                    null,
		                                    "Désolé, \"" + url + "\" "
		                                    + "n'est pas une adresse de flux RSS\n"
		                                    + "meRSSi d'entrer une adresse valide","Ajout de flux",
		                                    JOptionPane.ERROR_MESSAGE);
			                		url = null;
			                		adresse.requestFocusInWindow();
			                	}
			                	
			                }else if (numVal == JOptionPane.CANCEL_OPTION) {
			        		   //faire qqch
			        		}
			                setVisible(false);
			            }
			        }
			    });	
			 
				//		********************************************
		// centrage de la fenêtre
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height) {
		frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
		frameSize.width = screenSize.width;
		}
		this.setLocation( (screenSize.width - frameSize.width) / 2,
		(screenSize.height - frameSize.height) / 2);
	
		//********************************************
		// construction
		pack();
		setVisible(true);
		setResizable(false);
		setTitle("Infomation flux");
		setSize(400,250);
		validate();
		
	}
	
	
	public String getURL(){
		return url;
	}

}
