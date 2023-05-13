
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;

public class GUI extends JFrame implements ActionListener {
	
	//buttons
	static JButton stepButton = new JButton("Step ");
	static JButton timerButton = new JButton("start");
	static JButton fileButton = new JButton("load LST");
	
	//labels
	static JLabel wRegLabel = new JLabel("w-Register: 0");
	static JLabel progZählerLabel = new JLabel("PCL : 0");
	static JLabel pclathLabel = new JLabel("PCLATH : 0");
	static JLabel FSRLabel = new JLabel("FSR: 0");
	static JLabel timerLabel = new JLabel("TMR0 : 0");
	static JLabel breakPointLabel = new JLabel("Hier breakpoint eingeben(pc, dezimal)");
	static JTextField breakPointTextField = new JTextField();
	{
		breakPointTextField.setMaximumSize(new Dimension(80,20));
	}
	
	static JLabel statusLabel = new JLabel("Status : IRP  RP0  RP1   T0   PD    Z   DC    C");
	static JLabel statusDataLabel = new JLabel();
	static JLabel optionLabel = new JLabel("Option : RPu  IEg  TCs  TSe  PSA  PS2  PS1  PS0");
	static JLabel optionDataLabel = new JLabel();
	static JLabel intconLabel = new JLabel("Intcon : GIE  EIE  TIE   IE  RIE  TIF   IF  RIF ");
	static JLabel intconDataLabel = new JLabel();
	
	
	//ProgrammSpeicher erstellen
    static ProgrammSpeicher progSpeicher = new ProgrammSpeicher();
    
    //RA Ports checkboxen
    static JCheckBox[] RAports = new JCheckBox[5];
    {
    	for(int i=0; i<5; i++) {
    	RAports[i] = new JCheckBox("RA" + Integer.toString(i));
    	}
    }
    
    //RB PORTS checkboxen
    static JCheckBox[] RBports = new JCheckBox[8];
    {
    	for(int i=0; i<8; i++) {
    	RBports[i] = new JCheckBox("RB" + Integer.toString(i)+"                                 ") ;
    	}
    }
    
    //decoder
    Decoder decoder = new Decoder();
    
    //RAM tabelle
    static Object[][] RAMText = new Object[32][9];
    {
    	int count = 0;
    	for(int i=0; i<32; i++) {
    		for(int j=0; j<9; j++) {		
    			if(j==0) {
    				if(count < 16) {
    					RAMText[i][j] = "0" + Integer.toHexString(i*8) + "h";
    				} else {
    					RAMText[i][j] =  Integer.toHexString(i*8) + "h";
    				}	
    			} else {
    				RAMText[i][j] = "0";
    				count++;
    			}
    		}
    		
    	}
    }
    
	static String[] RAMcolumnNames = {" ","+0","+1","+2","+3","+4","+5","+6","+7"};
    static DefaultTableModel ramTableModel = new DefaultTableModel();
    static JTable ramTable = new JTable(ramTableModel);
    {
    	ramTableModel.setDataVector(RAMText, RAMcolumnNames);
    }
    static JScrollPane RAMtableScroll = new JScrollPane(ramTable);
	
    
    //LST tabelle 
    static Object[][] LSTtext = new Object[400][2];   
	static String[] LSTcolumnNames = {"ZeilenNR ", "Inhalt"};
    static DefaultTableModel LSTtableModel = new DefaultTableModel();
    static JTable LSTtable = new JTable(LSTtableModel);
    {
    	LSTtable.setModel(LSTtableModel);
    	LSTtableModel.setDataVector(LSTtext, LSTcolumnNames);
    }
    static JScrollPane LSTtableScroll = new JScrollPane(LSTtable);
    
    //timer for stepbutton
	Timer timer = new Timer(500, new ActionListener() {
		  @Override
		  public void actionPerformed(ActionEvent arg0) {
		    stepButton.doClick();
		  }
		});
	{timer.setRepeats(true);}

    public GUI() {
    	
    	//actionlistener hinzufügen
    	stepButton.addActionListener(this);
    	timerButton.addActionListener(this); 
    	fileButton.addActionListener(this);
        
		//change ramTable size to fit
    	for(int i=0; i<9; i++) {
    		ramTable.getColumnModel().getColumn(i).setMaxWidth(30);
    		ramTableModel.fireTableStructureChanged();
    		RAMtableScroll.setPreferredSize(new Dimension(270, 300));
    	}
        
        //panel initialisierung + Layouts
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
        JPanel pinPanelA = new JPanel();
        JPanel pinPanelB = new JPanel();
        pinPanelA.setLayout(new BoxLayout(pinPanelA,BoxLayout.Y_AXIS));
        pinPanelB.setLayout(new BoxLayout(pinPanelB,BoxLayout.Y_AXIS));
        JPanel infoPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
        JPanel labelPanel1 = new JPanel();
        labelPanel1.setLayout(new BoxLayout(labelPanel1,BoxLayout.Y_AXIS));
        infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
        JPanel bedienungsPanel = new JPanel();
        bedienungsPanel.setLayout(new BoxLayout(bedienungsPanel,BoxLayout.X_AXIS));
        JPanel RAMPanel = new JPanel();
        JPanel containerPanel1 = new JPanel();
        containerPanel1.setLayout(new BoxLayout(containerPanel1,BoxLayout.X_AXIS));
        JPanel containerPanel2 = new JPanel();
        containerPanel2.setLayout(new BoxLayout(containerPanel2,BoxLayout.Y_AXIS));
        JPanel labelPanel2 = new JPanel();
        labelPanel2.setLayout(new BoxLayout(labelPanel2, BoxLayout.Y_AXIS));
        
        //add PORTA to pinPanelA
        for(int i=0; i<5; i++) {
        	pinPanelA.add(RAports[i]);
        	int bitVal = (int) Math.pow(2, i);
        	RAports[i].addItemListener(new ItemListener() {    
                public void itemStateChanged(ItemEvent e) {     
                if((decoder.getF(5)&bitVal) == 0) {
                	decoder.addToF(5, bitVal); 
                } else {
                	decoder.addToF(5, -bitVal); 
                }
                 updateTableData();
                 }    
              });    
        }
        
        //add PORTB to pinPanelB
        for(int i=0; i<8; i++) {
        	pinPanelB.add(RBports[i]);
        	int bitVal = (int) Math.pow(2, i);
        	RBports[i].addItemListener(new ItemListener() {    
                public void itemStateChanged(ItemEvent e) {                 
                    if((decoder.getF(6)&bitVal) == 0) {
                    	decoder.addToF(6, bitVal); 
                    } else {
                    	decoder.addToF(6, -bitVal); 
                    }
                    updateTableData();
                 }    
              });  
        	
        }
        
        //add stuff to Panels
        RAMPanel.add(RAMtableScroll);
        buttonPanel.add(stepButton);
        buttonPanel.add(timerButton);
        buttonPanel.add(fileButton);
        labelPanel1.add(wRegLabel);
        labelPanel1.add(progZählerLabel);
        labelPanel1.add(pclathLabel);
        labelPanel1.add(timerLabel);
        labelPanel1.add(FSRLabel);
        labelPanel2.add(statusLabel);
        labelPanel2.add(statusDataLabel);
        labelPanel2.add(intconLabel);
        labelPanel2.add(intconDataLabel);
        labelPanel2.add(optionLabel);
        labelPanel2.add(optionDataLabel);
        labelPanel2.add(breakPointLabel);
        labelPanel2.add(breakPointTextField);
        bedienungsPanel.add(pinPanelA);
        bedienungsPanel.add(pinPanelB);
        bedienungsPanel.add(buttonPanel);
        containerPanel1.add(RAMPanel);
        containerPanel1.add(bedienungsPanel);
        containerPanel2.add(containerPanel1);
        containerPanel2.add(labelPanel1);
        containerPanel2.add(labelPanel2);
        mainPanel.add(LSTtableScroll);
        mainPanel.add(containerPanel2);
        ramTable.setModel(ramTableModel);
        
        // set frame site
        this.setMinimumSize(new Dimension(1400, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // display it
        this.pack();
        this.setContentPane(mainPanel);
        this.setVisible(true);
    	
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
    
    	GUI myGUI = new GUI();
    	
    }
    
    //update table data for RAM table
    void updateTableData() {
	    int count = 0;
	    for(int i=0; i<32; i++) {
	    	for(int j=0; j<9; j++) {		
	    		if(j==0) {
	    			if(count < 16) {
	    				RAMText[i][j] = "0" + Integer.toHexString(i*8) + "h";
	    			} else {
	    				RAMText[i][j] = Integer.toHexString(i*8) + "h";
	    			}	
	    		} else {
	    			RAMText[i][j] =  Integer.toHexString(decoder.getF(count));
	    			count++;
	    		}
	    	}
	    }
	    ramTableModel.setDataVector(RAMText, RAMcolumnNames);
		ramTableModel.fireTableDataChanged();
    }
    
    //shows where the PC is in the LST window
    void updatePCinLST() {	
    	int count = 0;
    	for(int i=0;i<400; i++) {
    		if(LSTtext[i][1] == null) {
    			break;
    		}
    		
    		String pcString = Integer.toHexString(decoder.getProgrammzähler());
    		String lstString = LSTtext[i][1].toString().substring(1, 4).toLowerCase();
    		while(pcString.length()<3) {
    			pcString = "0" + pcString;
    		}

    		if(lstString.equals(pcString)) {
    			LSTtext[i][0] += " >>>";
    		} else if(LSTtext[i][0].toString().contains(" >>>")) {
    			LSTtext[i][0] = LSTtext[i][0].toString().substring(0, 8);
    		}
    		count = i;
    	}
    	LSTtableModel.setDataVector(LSTtext, LSTcolumnNames);
    	LSTtableModel.fireTableDataChanged();
    	LSTtableModel.setRowCount(count+5);
    	LSTtableModel.fireTableStructureChanged();
    	LSTtable.setModel(LSTtableModel);
    	LSTtable.getColumnModel().getColumn(0).setMinWidth(100);
    	LSTtable.getColumnModel().getColumn(0).setMaxWidth(100);
    	LSTtable.getColumnModel().getColumn(1).setMinWidth(700);
    	LSTtableScroll.setPreferredSize(new Dimension(800, 600));
    }
    
    public void chooseFile() {
      	 JFileChooser chooser = new JFileChooser();
      	 FileNameExtensionFilter filter = new FileNameExtensionFilter("LST FILES","LST");
      	 chooser.setFileFilter(filter);
      	 int returnVal = chooser.showOpenDialog(this);
      	    if(returnVal == JFileChooser.APPROVE_OPTION) {
      	       progSpeicher.befehleLaden(chooser.getSelectedFile().getAbsolutePath());
      	       loadFileToLST(chooser.getSelectedFile().getAbsolutePath());
      	    }
    }
    //write a string for status option and intcon bits into data labels
    void updateDataLabels() {
    	String[] statusBits = new String[8];
    	String[] optionBits = new String[8];
    	String[] intconBits = new String[8];
    	
    	for(int i=0; i<8; i++) {
    		statusBits[i] = Integer.toString(decoder.getF(3,i));
    	}
    	
    	for(int i=0; i<8; i++) {
    		optionBits[i] = Integer.toString(decoder.getF(129,i));
    	}
    	
    	for(int i=0; i<8; i++) {
    		intconBits[i] = Integer.toString(decoder.getF(11,i));
    	}
    	
    	statusDataLabel.setText("                 " + statusBits[7]+"      "+statusBits[6]+"       "
    	+statusBits[5]+"        "+statusBits[4]+"     "+statusBits[3]+"      "+statusBits[2]+
    			"     "+statusBits[1]+"     "+statusBits[0]);
    	
    	optionDataLabel.setText("                 " + optionBits[7]+"       "+optionBits[6]+"       "
    	+optionBits[5]+"        "+optionBits[4]+"     "+optionBits[3]+"        "+optionBits[2]+
    			"       "+optionBits[1]+"       "+optionBits[0]);
    	
    	intconDataLabel.setText("                 " + intconBits[7]+"      "+intconBits[6]+"    "
    	+intconBits[5]+"     "+intconBits[4]+"      "+intconBits[3]+"     "+intconBits[2]+
    	"   "+intconBits[1]+"     "+intconBits[0]);
    }
    
    //load file to lst window
    void loadFileToLST(String filePath) {
    	Reader fileReader;
		try {
			fileReader = new Reader(filePath);
			int count = 0;
	    	for(int i=0; i<400; i++) {
	    		if(fileReader.getLine(i) == null) {
					break;
				}
	    		for(int j=0; j<2; j++) {		
	    			if(j==0) {
	    				LSTtext[i][j] = "Zeile " + Integer.toString(i) +" ";
	    			} else {
	    				LSTtext[i][j] = fileReader.getLine(i);
	    			}
	    		}
	    		count ++;
	    	}
	    	LSTtableModel.setDataVector(LSTtext, LSTcolumnNames);
	    	LSTtableModel.fireTableDataChanged();
	    	LSTtableModel.setRowCount(count+5);
	    	LSTtableModel.fireTableStructureChanged();
	    	LSTtable.setModel(LSTtableModel);
	    	LSTtable.getColumnModel().getColumn(0).setMinWidth(100);
	    	LSTtable.getColumnModel().getColumn(0).setMaxWidth(100);
	    	LSTtable.getColumnModel().getColumn(1).setMinWidth(700);
	    	LSTtableScroll.setPreferredSize(new Dimension(800, 600));
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Fehler beim einlesen");
			e1.printStackTrace();
		}
    }
    
    //action event handling
    public void actionPerformed(ActionEvent e){  
    	if(e.getSource() == stepButton) {
    		if(breakPointTextField.getText().equals(Integer.toString(decoder.getProgrammzähler()))) {
    			timer.stop();
    		}
			decoder.decode(progSpeicher.getBefehl(decoder.getF(2)));
    		wRegLabel.setText("W-Register : " + Integer.toHexString(decoder.getW()));
    		progZählerLabel.setText("Programmzähler : " + decoder.getF(2));
    		timerLabel.setText("TMR0 : " + decoder.getF(1));
    		FSRLabel.setText("FSR : " + decoder.getF(4));
    		updateTableData();
    		updatePCinLST();
    		updateDataLabels();
    	}
    	if(e.getSource() == timerButton) {
    		if(timer.isRunning()) {
    			timer.stop();
    		} else {
    			timer.start();
    		}
    	}
    	if(e.getSource() == fileButton) {
    		chooseFile();
    		updateTableData();
    	}

    }  
}
    