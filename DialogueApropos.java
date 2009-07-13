import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Classe définissant la fenêtre Apropos*/

public class DialogueApropos extends JDialog implements ActionListener {

    JButton b_ok = null;   
    
    public DialogueApropos () {

	    setTitle("A propos");
	    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			JLabel  jl1 = new JLabel (" Dites meRSSi a ");
            jl1.setBounds(0,0,200,100);
            JLabel  jl2 = new JLabel (" Vivien Schilis");
            jl2.setBounds(10,30,200,100);
            JLabel  jl3 = new JLabel (" Guillaume Flandre");
            jl3.setBounds(10,50,200,100);
            
            b_ok = new JButton("meRSSi");
            b_ok.setFont(new Font("Dialog", Font.BOLD, 12));
            setBounds(500, 500, 400, 150);
            b_ok.setBounds(300,50, 80, 50);
            b_ok.addActionListener(this);
            
            getContentPane().setLayout(null);
            getContentPane().add(jl1);
            getContentPane().add(jl2);
            getContentPane().add(jl3);
            getContentPane().add(b_ok);
            setVisible(true);
    }
    
    public void actionPerformed (ActionEvent e) {
            Object source = e.getSource();
            if (source == b_ok) {
                dispose();
            }
    }

}
