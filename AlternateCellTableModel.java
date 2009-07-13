 import java.awt.Color;
 import java.awt.Component;
 import java.io.Serializable;
  
 import javax.swing.JLabel;
 import javax.swing.JTable;
 import javax.swing.UIManager;
 import javax.swing.border.Border;
 import javax.swing.border.EmptyBorder;
 import javax.swing.table.TableCellRenderer;
  
 public class AlternateCellTableModel extends JLabel
     implements TableCellRenderer, Serializable
 {
  
     protected static Border noFocusBorder;
     
     private Color unselectedForeground;
     private Color unselectedBackground;
     private Color unselectedForegroundAlt;
     private Color unselectedBackgroundAlt;
     private Color selectedForeground;
     private Color selectedBackground;
     private int alternateInc;
  
     public AlternateCellTableModel()
     {
      super();
         noFocusBorder = new EmptyBorder(1, 2, 1, 2);
      setOpaque(true);
         setBorder(noFocusBorder);
         alternateInc = 0;
     }
     
     public void setInterval( int i )
     {
         alternateInc = i;
     }
  
     public void setColorsSel( Color back, Color fore )
     {
         selectedForeground = fore;
         selectedBackground = back;
     }
  
     public void setColorsAlt( Color back, Color fore )
     {
         unselectedForegroundAlt = fore;
         unselectedBackgroundAlt = back;
     }
     
     public void setColors( Color back, Color fore )
     {
         unselectedForeground = fore;
         unselectedBackground = back;
     }
     
     public void setForeground(Color c){
         super.setForeground(c);
         unselectedForeground = c;
     }
     
     public void setBackground(Color c) {
         super.setBackground(c);
         unselectedBackground = c;
     }
  
     public void updateUI(){
        	super.updateUI();
      		setForeground(null);
      		setBackground(null);
     }
     
     public Component getTableCellRendererComponent(JTable table, Object value,
                           boolean isSelected, boolean hasFocus, int row, int column)
     {

         Item item = (Item)value;
         try
         {
 	     boolean alternateColor = false; // on demare par la couleur de fon
         // init l interval pour le suivi des ligne en fonction du scroll
         float dec = (float)( row ) / (float)( alternateInc * 2 );
         dec -= (int)dec; // me rest que les parti decimal
         dec *= ( alternateInc * 2 ); // g un resulta entre 0 et ( intervalAlternate * 2 )
         dec += .5; // pour eviter le movai bornage de larondi ;o)
         int interval = (int) dec;

         if ( interval >= alternateInc ) {
	          if ( alternateInc != 0 ) { alternateColor = !alternateColor;}
    	      interval -= alternateInc;
    	  }
         // fin de linit du decalage de l intevale
		
			setFont( table.getFont() );

			if ( hasFocus ) {
				setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
				if ( table.isCellEditable( row, column ) ){
			    		super.setForeground( UIManager.getColor("Table.focusCellForeground") );
			      		super.setBackground( UIManager.getColor("Table.focusCellBackground") );
			    }
			}
			else{
				setBorder( noFocusBorder );
		    }
		    
		    if((!isSelected) && (item.getSonFeed().getSonSubscription().getColor() ==null))
		    {
                      if ( alternateColor )
                        {
                        super.setForeground( ( unselectedForeground != null) ? unselectedForegroundAlt : table.getForeground() );
                        super.setBackground( ( unselectedBackgroundAlt != null) ? unselectedBackgroundAlt : table.getBackground() );
                        }
                        else
                        {
                        super.setForeground( ( unselectedForeground != null) ? unselectedForeground : table.getForeground() );
                        super.setBackground( ( unselectedBackground != null) ? unselectedBackground : table.getBackground() );
                        }
	   		}
	   		else if((!isSelected) && (item.getSonFeed().getSonSubscription().getColor() !=null)) {
				 super.setBackground(item.getSonFeed().getSonSubscription().getColor());
				if(item.getRead())

	   			      super.setForeground(Color.black);
	   			else
	   					super.setForeground(Color.white);
		    }
		    else if(isSelected){
		    	   		super.setBackground(selectedBackground);
		   			  	super.setForeground(selectedForeground);	
		   	}
		    
		    this.setHorizontalAlignment( JLabel.LEFT);
		    if (column==0)
		    	if(item.getRead() || isSelected)
					setValue( "<html><body>"+item.getTitle()+"</html></body>");
				else
					setValue( "<html><body style='font-weight:bold;'>"+item.getTitle()+"</html></body>");
		    if (column==1)
		    	if(item.getRead() || isSelected)	
			    	setValue( Subscription.timeToRSSDate(item.getPubDate() )+" "+item.getPubDate().toString());
			    else
			    	setValue("<html><body style='font-weight:bold;'>"+Subscription.timeToRSSDate(item.getPubDate() ) + " "+item.getPubDate().toString() +"</html></body>");
		    }
      catch (Exception e)
      { 
      		System.out.println("AlternateCellTableModel getTableCellRendererComponent() : "+e); }
       		return this;
     }
     
     protected void setValue( Object value )
     {
      	setText( ( value == null ) ? "" : value.toString() );
     }
  
 }
