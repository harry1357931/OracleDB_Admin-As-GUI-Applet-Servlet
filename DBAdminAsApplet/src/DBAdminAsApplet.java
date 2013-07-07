/* DBAdminAsApplet: Contains all other Supporting Classes
 * Description: 
 *   A Database admin designed by Gurpreet Singh.
 *   Functionalities:
 *   1) Connects to Remote Database
 *   2) Create new Table, View Old Tables
 *   3) Insert/Delete any number of Rows in Tables
 *   4) Insert/Delete any no. of Columns in Tables
 *   5) Drop Table 
 ************
 * Parameters
 ************
 * @param MainPanelHomeArea 
 * @param MainPanel 
 * @param ControlPanel
 * @param MenuPanel
 *
 *************
 * Author Info
 *************
 * @author Gurpreet Singh
 */
import javax.swing.*;        
import javax.swing.border.Border;
import oracle.jdeveloper.layout.XYConstraints;
import oracle.jdeveloper.layout.XYLayout;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.net.MalformedURLException;
import java.net.URL;

public class DBAdminAsApplet extends JApplet {         // JApplet
	 
	 private static final long serialVersionUID = 1L;
	 JTextArea MainPanelHomeArea;
	 JPanel MainPanel, ControlPanel, MenuPanel;    
	 boolean isStandalone = false;     
	 
	 public static void main(String[] args) {
		   DBAdminAsApplet display = new DBAdminAsApplet();
		   display.init(); 
	 }	
	 
	 // Constructor
	 public DBAdminAsApplet() {
     
	 } // Constructor
	 
     private void createFileMenu() {
	      JMenuItem   item;
	      JMenuBar    menuBar  = new JMenuBar();
	      JMenu       fileMenu = new JMenu("File");
	      FileMenuHandler fmh  = new FileMenuHandler();

	      item = new JMenuItem("Open");    //Open...
	      item.addActionListener( fmh );
	      fileMenu.add( item );
          fileMenu.addSeparator();           //add a horizontal separator line
	      setJMenuBar(menuBar);
	      menuBar.add(fileMenu);
	   
     } //createFileMenu

     public void createCreateMenu() {
	      JMenuItem   item2;
	      JMenuBar    menuBar  = this.getJMenuBar();
	      JMenu       editMenu = new JMenu("Create");
	      CreateTableHandler emh  = new CreateTableHandler(this);

	      item2 = new JMenuItem("Create New Table");    //Open...
	      item2.addActionListener( emh );
	      editMenu.add(item2);
          editMenu.addSeparator();           //add a horizontal separator line
	      
	      setJMenuBar(menuBar);
	      menuBar.add(editMenu);
	   } //createEditMenu
     
     private void createInsertMenu() {
	      JMenuItem   item3;
	      JMenuBar    menuBar  = this.getJMenuBar();
	      JMenu       editMenu = new JMenu("Insert");
	      
	      TransferToShowTableAndInsertRowHandler emh  = new TransferToShowTableAndInsertRowHandler(this);   
	      item3 = new JMenuItem("Insert data Into Table");    //Open...
	      item3.addActionListener(emh);
	      editMenu.add(item3);
          editMenu.addSeparator();           //add a horizontal separator line
	      setJMenuBar(menuBar);
	      menuBar.add(editMenu);
	    
	   } //createEditMenu
  
     private void createDeleteMenu() {
	      JMenuItem   item4;
	      JMenuBar    menuBar  = this.getJMenuBar();
	      JMenu       editMenu = new JMenu("Delete");
	 	  DeleteTableOrRowHandler emh  = new DeleteTableOrRowHandler(this);
 	      item4 = new JMenuItem("Delete data from Table");    //Open...
	      item4.addActionListener( emh );
	      editMenu.add(item4);
          editMenu.addSeparator();           //add a horizontal separator line
          setJMenuBar(menuBar);
	      menuBar.add(editMenu);
	 } //createEditMenu

     
     public void ShowTablesComboBoxInControlPanel(){
    	 ControlPanel.removeAll();    	     	 
    	 JButton try_againButton = new JButton();        // refresh button for Internet Connection...
    	 try_againButton.addActionListener(new java.awt.event.ActionListener()
		  {
		        public void actionPerformed(ActionEvent e)
		        {   
		        	ShowTablesComboBoxInControlPanel();
		        }
		   } 
		   );

    	 ORA_DB ToShowTables = new ORA_DB(); 
    	 if(ToShowTables.connected == false){
    		  JTextArea message2 = new JTextArea(" Unable to list all Tables.\n" +
    		  		           " Please Check your Internet\n Connection:");
    		  ControlPanel.add(message2, new XYConstraints(5, 100, 180, 60)); 
    		  try_againButton.setText("Try Again"); 
    		  ControlPanel.add(try_againButton, new XYConstraints(5, 165, 100, 20));     		  
    		  ControlPanel.setVisible(false);
    		  ControlPanel.setVisible(true);
    		  return;
    	 }
    	 
    	 JLabel LabelTables = new JLabel("All Tables:", JLabel.CENTER); 
    	 LabelTables.setBackground(new Color(35, 90, 150));           // 100, 100, 100
    	 LabelTables.setFont(new Font("Dialog", 1, 13)); 
    	 LabelTables.setForeground(new Color(250, 250, 250));
    	 LabelTables.setOpaque(true);
	     Border border =  BorderFactory.createLineBorder(new Color(170, 170, 170));       //BorderFactory.createLineBorder(Color.blue);
	     LabelTables.setBorder(border);
	     ControlPanel.add(LabelTables, new XYConstraints(10, 48, 80, 20)); 
    	 
	     String[] alltables = ToShowTables.showTables("SELECT TABLE_NAME AS NAME FROM USER_TABLES");        // show tables and get tables are two different methods in ORA_DBTest
    	     	 
    	 JComboBox showTables = new JComboBox(alltables); 
    	 showTables.setSelectedIndex(0);
    	 showTables.setBackground(new Color(255, 255, 255));
    	 ControlPanel.add(showTables, new XYConstraints(10, 75, 160, 25));
    	 
    	 ViewInsertDeleteHandler vidh  = new ViewInsertDeleteHandler(this);   
    	 showTables.addActionListener(vidh);
    	 JTextArea message1 = new JTextArea(" You can view and edit any \n table" +
		           " just by selecting it!");
    	 message1.setBackground(new Color(250, 250, 250));
	     ControlPanel.add(message1, new XYConstraints(10, 105, 160, 60)); 
    	 ControlPanel.setVisible(false);
    	 ControlPanel.setVisible(true);
     }
     
     public class TransferToShowTableAndInsertRowHandler implements ActionListener{
    	 DBAdminAsApplet mainFrame;
    	 JPanel mainPanel;
    	 public TransferToShowTableAndInsertRowHandler (DBAdminAsApplet main_frame) {
    		 mainFrame = main_frame;
    		 mainPanel = main_frame.MainPanel;
		 }
		   
		public void actionPerformed(ActionEvent event) {
		      String menuName = event.getActionCommand();
		      int index = event.getID();
		      InsertDataStartupTableSelector();
		} //actionPerformed
		
		public void InsertDataStartupTableSelector(){
			 mainPanel.removeAll();    	     	
		     mainPanel.setBackground(new Color(255, 255, 254));   
		     JLabel NameLabel = new JLabel("INSERT", JLabel.CENTER); 
		     NameLabel.setBackground(new Color(35, 90, 150));           // 100, 100, 100
		     NameLabel.setFont(new Font("Dialog", 1, 14)); 
		     NameLabel.setForeground(new Color(250, 250, 250));
		     NameLabel.setOpaque(true);
		     Border border =  BorderFactory.createLineBorder(new Color(170, 170, 170));       //BorderFactory.createLineBorder(Color.blue);
		     NameLabel.setBorder(border);
		     mainPanel.add(NameLabel, new XYConstraints(100, 70, 100, 20)); 

		     JPanel InsertRowMenuPanel = new JPanel(new XYLayout());                     // 
		     InsertRowMenuPanel.removeAll();
		     InsertRowMenuPanel.setBackground(new Color(250, 250, 250));
		     mainPanel.add(InsertRowMenuPanel, new XYConstraints(80, 80, 350, 200));
		     
	    	 JButton try_againButton = new JButton();        // refresh button for Internet Connection...
	    	 try_againButton.addActionListener(new java.awt.event.ActionListener()              // Edit
			  {
			        public void actionPerformed(ActionEvent e)
			        {   
			        	InsertDataStartupTableSelector();
			        }
			   } 
			   );

	    	 ORA_DB ToShowTables = new ORA_DB(); 
	    	 
	    	 if(ToShowTables.connected == false){
	    		  
	    		  JTextArea message2 = new JTextArea(" Unable to list all Tables.\n" +
	    		  		           " Please Check your Internet\n Connection:");
	    		  InsertRowMenuPanel.add(message2, new XYConstraints(20, 20, 180, 60)); 
	    		  try_againButton.setText("Try Again"); 
	    		  InsertRowMenuPanel.add(try_againButton, new XYConstraints(20, 85, 100, 20));  
	    		  
	    		  mainPanel.setVisible(false);
	 	    	  mainPanel.setVisible(true);
	    		  return;
	    	 }
	    
	    	 JLabel selectTable = new JLabel(" SELECT Table to INSERT Rows: ");
	    	 selectTable.setOpaque(true); 
	    	 InsertRowMenuPanel.add(selectTable, new XYConstraints(20, 20, 300, 23)); 
	    	 
	    	 String[] alltables = ToShowTables.showTables("SELECT TABLE_NAME AS NAME FROM USER_TABLES");        // show tables and get tables are two different methods in ORA_DBTest
	    	 final JComboBox showTables = new JComboBox(alltables); 
	    	 
	    	 showTables.setSelectedIndex(0);
	    	 showTables.setBackground(new Color(255, 255, 255));
	    	 InsertRowMenuPanel.add(showTables, new XYConstraints(20, 50, 300, 25));
	    	 
	    	 JLabel insertLabel = new JLabel(" INSERT: ");
	    	 insertLabel.setOpaque(true);
	    	 InsertRowMenuPanel.add(insertLabel, new XYConstraints(21, 90, 53, 23)); 
	    	 
	    	 final JTextField num_rows = new JTextField(16);
	    	 InsertRowMenuPanel.add(num_rows, new XYConstraints(75, 90, 50, 23));
	    	 
	    	 JLabel rowsLabel = new JLabel("new row(s)");
	    	 rowsLabel.setOpaque(true);
	    	 InsertRowMenuPanel.add(rowsLabel, new XYConstraints(125, 90, 190, 23)); 
	    	 
	    	 JButton goButton = new JButton("Go"); 
	    	 InsertRowMenuPanel.add(goButton, new XYConstraints(20, 130, 50, 23));
	    	 
	    	 goButton.addActionListener(new java.awt.event.ActionListener()
			    {
			        public void actionPerformed(ActionEvent e)
			        {    String table_name = (String)(showTables.getSelectedItem());   
			        	 String getTableQuery = "SELECT* FROM "+ table_name;		
						 ORA_DB getSingleTable = new ORA_DB();  
					     ShowTableData  tableData = getSingleTable.getTable(getTableQuery);               // Getting data from selected Table...
		        	     
					     InsertNewRowsHandler idh  = new InsertNewRowsHandler(mainFrame, tableData.column_names, table_name, tableData.numberOfColumns, tableData.column_class_types); 
			        	 idh.numberOfNewRows = Integer.parseInt(num_rows.getText());
			        	 idh.InsertNewRows();
			        }
			     } 
			    );
	    	 
	    	 mainPanel.setVisible(false);
	    	 mainPanel.setVisible(true);
		 }
     }
	 
     public void InsertDataButtonInMenuPanel(){
        String imageLink = "http://venus.cs.qc.cuny.edu/~sigu2113/Images_data/Insert_row.PNG";
        JButton Insert_dataButton=null;
		try {
			Insert_dataButton = new JButton(new ImageIcon(((getImage(new URL(imageLink)))).getScaledInstance(78, 83, java.awt.Image.SCALE_SMOOTH)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 Insert_dataButton.setBorderPainted(true);  
    	 Insert_dataButton.setFocusPainted(true);  
    	 Insert_dataButton.setContentAreaFilled(true); 
    	 Insert_dataButton.setToolTipText("Insert data into Table");
         
    	 TransferToShowTableAndInsertRowHandler emh  = new TransferToShowTableAndInsertRowHandler(this);   
         Insert_dataButton.addActionListener(emh);                         // Edit 
         MenuPanel.add(Insert_dataButton, new XYConstraints(105, 10, 78, 83));
     }
     
     public void DeleteDataButtonInMenuPanel(){
        String imageLink = "http://venus.cs.qc.cuny.edu/~sigu2113/Images_data/Delete_Table_Icon.PNG";
        JButton delete_dataButton=null;
		try {
			delete_dataButton = new JButton(new ImageIcon(((getImage(new URL(imageLink)))).getScaledInstance(78, 83, java.awt.Image.SCALE_SMOOTH)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 delete_dataButton.setBorderPainted(true);  
    	 delete_dataButton.setFocusPainted(true);  
    	 delete_dataButton.setContentAreaFilled(true); 
    	 delete_dataButton.setToolTipText("Delete data");
    	 DeleteTableOrRowHandler emh  = new DeleteTableOrRowHandler(this);    
         delete_dataButton.addActionListener(emh);                           
    	 MenuPanel.add(delete_dataButton, new XYConstraints(195, 10, 78, 83));   
      }
     
     public class DeleteTableOrRowHandler implements ActionListener{
    	 DBAdminAsApplet mainFrame;
    	 JPanel mainPanel;
    	 public DeleteTableOrRowHandler(DBAdminAsApplet main_frame) {
			 mainFrame = main_frame;
    		 mainPanel = main_frame.MainPanel;
		 }
		   
		public void actionPerformed(ActionEvent event) {
		      String menuName = event.getActionCommand();
		      int index = event.getID();	      
		      deleteRowOrTableSelector();
	    } //actionPerformed  
		
		public void deleteRowOrTableSelector(){
			 mainPanel.removeAll();    	     	
		     mainPanel.setBackground(new Color(255, 255, 254));   
		     JLabel NameLabel = new JLabel("DELETE", JLabel.CENTER); 
		     NameLabel.setBackground(new Color(35, 90, 150));           // 100, 100, 100
		     NameLabel.setFont(new Font("Dialog", 1, 14)); 
		     NameLabel.setForeground(new Color(250, 250, 250));
		     NameLabel.setOpaque(true);
		     Border border =  BorderFactory.createLineBorder(new Color(170, 170, 170));       //BorderFactory.createLineBorder(Color.blue);
		     NameLabel.setBorder(border);
		     mainPanel.add(NameLabel, new XYConstraints(100, 70, 100, 20)); 

		     JPanel InsertRowMenuPanel = new JPanel(new XYLayout());                     // 
		     InsertRowMenuPanel.removeAll();
		     InsertRowMenuPanel.setBackground(new Color(250, 250, 250));
		     mainPanel.add(InsertRowMenuPanel, new XYConstraints(80, 80, 350, 200));
		     
	    	 JButton try_againButton = new JButton();        // refresh button for Internet Connection...
	    	 try_againButton.addActionListener(new java.awt.event.ActionListener()              // Edit
			  {
			        public void actionPerformed(ActionEvent e)
			        {   
			        	deleteRowOrTableSelector();
			        }
			   } 
			   );

	    	 ORA_DB ToShowTables = new ORA_DB(); 
	    	 if(ToShowTables.connected == false){
	    		  JTextArea message2 = new JTextArea(" Unable to list all Tables.\n" +
	    		  		           " Please Check your Internet\n Connection:");
	    		  InsertRowMenuPanel.add(message2, new XYConstraints(20, 20, 180, 60)); 
	    		  try_againButton.setText("Try Again"); 
	    		  InsertRowMenuPanel.add(try_againButton, new XYConstraints(20, 85, 100, 20));  
	    		  mainPanel.setVisible(false);
	 	    	  mainPanel.setVisible(true);
	    		  return;
	    	 }
	    	 JLabel selectTable = new JLabel(" SELECT Table: ");
	    	 selectTable.setOpaque(true); 
	    	 InsertRowMenuPanel.add(selectTable, new XYConstraints(20, 20, 300, 23)); 
	    	 String[] alltables = ToShowTables.showTables("SELECT TABLE_NAME AS NAME FROM USER_TABLES");        // show tables and get tables are two different methods in ORA_DBTest
	    	 final JComboBox showTables = new JComboBox(alltables); 
	    	 showTables.setSelectedIndex(0);
	    	 showTables.setBackground(new Color(255, 255, 255));
	    	 InsertRowMenuPanel.add(showTables, new XYConstraints(20, 50, 300, 25));
	    	 
	    	 JTextArea message = new JTextArea("For following options: \n   Delete Row \n   Delete Column  \n   Drop this Table");
	    	 message.setBackground(new Color(250, 250, 250));
	    	 message.setFont(new Font("Dialog", 1, 12));
	    	 message.setForeground(new Color(135, 29 ,26));
	    	 InsertRowMenuPanel.add(message, new XYConstraints(20, 80, 300, 80)); 
	    	 
	    	 JButton goButton = new JButton("Go"); 
	    	 InsertRowMenuPanel.add(goButton, new XYConstraints(20, 160, 50, 23));
	    	 
	    	 final ViewInsertDeleteHandler vidh  = new ViewInsertDeleteHandler(mainFrame);    
	    	 goButton.addActionListener(new java.awt.event.ActionListener()
			    {
			        public void actionPerformed(ActionEvent e)
			        {    
			        	try { vidh.showSelectedTable((String)(showTables.getSelectedItem()));
			        	}
			        	catch(Exception ex){
			        		JOptionPane.showMessageDialog(null, ex.getMessage());
			        	}
			        	mainPanel.setVisible(false);
				    	mainPanel.setVisible(true);

			        }
			     } 
			    );
	    	 mainPanel.setVisible(false);
	    	 mainPanel.setVisible(true);
		 }
	 }
    
     public void createTableButtonInMenuPanel() {  
         String imageLink = "http://venus.cs.qc.cuny.edu/~sigu2113/Images_data/Create_tab.PNG";
         JButton create_tableButton=null;
		try {
			create_tableButton = new JButton(new ImageIcon(((getImage(new URL(imageLink)))).getScaledInstance(78, 83, java.awt.Image.SCALE_SMOOTH)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		 create_tableButton.setBorderPainted(true);  
         create_tableButton.setFocusPainted(true);  
         create_tableButton.setContentAreaFilled(true); 
         create_tableButton.setToolTipText("Create Table");   
         CreateTableHandler cmh  = new CreateTableHandler(this);  
         create_tableButton.addActionListener(cmh); 
    	 MenuPanel.add(create_tableButton, new XYConstraints(15, 10, 78, 83)); 
    }
    
     public String getParameter(String key, String def) {
    	    if (isStandalone) {
    	      return System.getProperty(key, def);
    	    }
    	   if (getParameter(key) != null) {
    	      return getParameter(key);
    	   }
    	    return def;
    }
     
     public void init() {
    	    try  { 	    	
    	 	   ControlPanel = new JPanel(new XYLayout());
        	   ControlPanel.setPreferredSize(new Dimension(200, 580));
        	   ControlPanel.setBackground(new Color(250, 250, 250));
        	   ControlPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
    		   MainPanel = new JPanel(new XYLayout());
    		   MainPanel.setPreferredSize(new Dimension(700, 1500));
    		   MainPanel.setBackground(new Color(255, 255, 255));   
    		   
    		   MainPanelHomeArea = new JTextArea("\n   Welcome to Gurpreet's Oracle Database Admin!");
    		   MainPanelHomeArea.append("\n\n   Here you can Create, Insert and Delete Tables...\n   Use Menu and MenuIcons to do stuff here...");
    		   MainPanelHomeArea.setFont(new Font("Dialog", 1, 14));
    		   MainPanelHomeArea.setBackground(new Color(250, 250, 250));
    		   MainPanel.add(MainPanelHomeArea, new XYConstraints(1, 1, 1000, 700));
    		   MenuPanel = new JPanel(new XYLayout());
    		   getContentPane().setLayout(new XYLayout());            // XY Layout
    	       
    		   getContentPane().add(new JScrollPane(ControlPanel), new XYConstraints(5, 105, 200, 600 ));
    	       JScrollPane main = new JScrollPane(MainPanel);
    	       main.setPreferredSize(new Dimension(800,650));
    	       getContentPane().add(main, new XYConstraints(210, 105, 1065, 600 ));
    	       getContentPane().add(MenuPanel, new XYConstraints(0, 0, 1170, 100));
    	        setSize(950, 750);   // width, height
    	        setLocation(200,30);
    	       
    	        createFileMenu();
    		    createCreateMenu();
    		    createInsertMenu();
    		    createDeleteMenu();
    		    createTableButtonInMenuPanel();
    		    InsertDataButtonInMenuPanel();
    		    DeleteDataButtonInMenuPanel();
    		    ShowTablesComboBoxInControlPanel();
    		    //setDefaultCloseOperation(EXIT_ON_CLOSE);  
    	        setVisible(true);	        
    	        
    	    }
    	    catch (Exception e) {
    	      e.printStackTrace();
    	    }
    	    
    	  }
      
     /**
      * start
      */
     public void start() {
    
     }    
   

     /**
      * stop
      */
     public void stop() {
     }

     /**
      * destroy
      */
     public void destroy() {
     }

     /**
      * getAppletInfo
      * @return java.lang.String
      */
     public String getAppletInfo() {
       return "Applet Information";
     }

     /**
      * getParameterInfo
      * @return java.lang.String[][]
      */
     public String[][] getParameterInfo() {
       return null;
     }
     
     /* CreateTableHandler
      * Description:
      *   1) Gets Activated whenever 'Create New Table' is selected
      *   2) Sets up the Interface for Creating New Table
      *   3) After user entered Table Name and Column Names, 
      *      it checks for Errors & Warnings, if no error is found
      *      it connects to database, and creates Table 
      * **********
      * Parameters
      * **********
      * @param jpanel
      * @param mainFrame
      * @param xYLayout1
      * @param TableName JLabel for Table Name
      * @param TableNameField JTextField for TableName
      * @param Total JLabel
      * @param TotalField It lets the user enter total no. of Columns user wants to create. 
      * @param TotalColumnFields default value for initial no. of columns
      * @param columns
      * @param go JButton to display specified no. of column
      * @param structure
      * @param Name JLabel to display String 'Name' as Label
      * @param Type JLabel to display String 'Types' as Label
      * @param Length JLabel to display String 'Length' as Label
      * @param Default JLabel to display String 'Default' as Label
      * @param Constraints JLabel to display String 'Constraints' as Label
      * @param nameEntries 1-D array of Type JTextField, default size is 4
      * @param type 1-D array of Type JTextField, default size is 4
      * @param length 1-D array of Type JTextField, default size is 4
      * @param defaultValue 1-D array of Type JTextField, default size is 4
      * @param constraints 1-D array of Type JTextField, default size is 4
      * @param table_comments 
      * @param table_comments_area
      * @param createButton JButton to create Table
      * @param cancelButton JButton to cancel create table setup
      * 
      * @author GurpreetSingh
      */
     
     public class CreateTableHandler implements ActionListener {
  			  JPanel jpanel;
  			  DBAdminAsApplet mainFrame;   
  			  
  			  XYLayout xYLayout1 = new XYLayout(); 
  			  JLabel TableName = new JLabel();           
  			  JTextField TableNameField = new JTextField(16);
  			  JLabel Total = new JLabel();           
 			  JTextField TotalField = new JTextField(16);
 			  int TotalColumnFields = 4;
 			  JLabel columns = new JLabel();
 			  JButton go = new JButton("<html><b>Go</b></html>");
 			  JLabel structure = new JLabel();
 			  
  			  JLabel Name = new JLabel();
  			  JLabel Type = new JLabel();
  			  JLabel Length = new JLabel();
  			  JLabel Default = new JLabel();
  			  JLabel Constraints = new JLabel();
  			  
  			  JTextField[] nameEntries = new JTextField[4];
  			  JComboBox[] type = new JComboBox[4];
  			  JTextField[] length = new JTextField[4];
  			  JComboBox[] defaultValue = new JComboBox[4];
  			  JComboBox[]  constraints = new JComboBox[4];
  			  
     	      JLabel table_comments = new JLabel();
     	      JTextField table_comments_area = new JTextField();
     	      JButton createButton = new JButton("<html><b>Create Table</b></html>");   
     	      JButton cancelButton = new JButton("<html><b>Cancel</b></html>");        
  			
     	      public CreateTableHandler (DBAdminAsApplet actualFrame) {
  			     mainFrame = actualFrame; 
  				 jpanel = actualFrame.MainPanel;
  			 }
  			   
  			 public void actionPerformed(ActionEvent event) {
  			      String menuName = event.getActionCommand();
  			      if (menuName.equals("Create New Table")){
  			             try {
  			            	 CreateTableSetUp();       // size of add column = 0 
  			             }catch(Exception ex){
  			            	 
  			             }
  			      
  			      }else if (menuName.equals("")){        // for Create Table button in Menu Panel 
  			    	 try {
 			            	 CreateTableSetUp();       // size of add column = 0 
 			             }catch(Exception ex){
 			            	 
 			             }
  			      }  
  			   } //actionPerformed
  			   
  			public void CreateTableSetUp() throws Exception{
  				    jpanel.removeAll();
  				    go.addActionListener(new java.awt.event.ActionListener()
 				     {
 				        public void actionPerformed(ActionEvent e)
 				        {      
 				        	   int size = Integer.parseInt(TotalField.getText());
 				        	   if(size < 1){
 				        		   JOptionPane.showMessageDialog(null, "Total Columns should be >= 1 !", "Message", JOptionPane.WARNING_MESSAGE);
 				        	       return;
 				        	   }
 				        	   
 				        	   jpanel.removeAll();
 				        	   nameEntries = new JTextField[size];  
 				 			   type = new JComboBox[size]; 
 				 			   length = new JTextField[size];
 				 			   defaultValue = new JComboBox[size];
 				 			   constraints = new JComboBox[size];
 				 			   
 				 			   TotalColumnFields = size;
 				 	            try {
                             	   CreateTableSetUp();
                                }catch (Exception ex){
                             	   
                                }        
                           }
 				     }
 				    );
            
  				    createButton.addActionListener(new java.awt.event.ActionListener()
 				     {
 				        public void actionPerformed(ActionEvent e)
 				        {	   
 				        	   if(TableNameField.getText().equalsIgnoreCase("")){
 				        		    JOptionPane.showMessageDialog(null, "Table Name is Not Set!", "Message", JOptionPane.WARNING_MESSAGE);
 		                            return;
 				        	   }
 				        	   else{
 				        		   int TotalColumns = 0;
 				        		   
 					        	   for(int i=0; i<nameEntries.length; i++){
 					        		   if(!nameEntries[i].getText().equalsIgnoreCase("") ) {
 					        			   TotalColumns++; 
 					        		   }
 					        	   } 
 					        	   if(TotalColumns == 0){             // Check if #columns > 0 or not
 					        		   JOptionPane.showMessageDialog(null, "Add atleast 1 Column!", "Message", JOptionPane.ERROR_MESSAGE);
 					        		   return;
 					        	   }   
 				        	    } // else ends here
 				    
 				        	   // To check whether there are two columns with same name or not...
 				        	   for(int i=0; i< nameEntries.length; i++){
 				        		   for(int j=0; j< nameEntries.length; j++){
 					        		   if(!nameEntries[j].getText().equalsIgnoreCase("")){
 					        			   if( i!=j && nameEntries[i].getText().equalsIgnoreCase(nameEntries[j].getText())){
 					        				   JOptionPane.showMessageDialog(null, "Duplicate Column name exists!", "Inane error", JOptionPane.ERROR_MESSAGE);
 					        				   return;
 					        			   }
 					        		   }
 					        	   }   
 				        	   }
 				        	   
 				        	   String[] checkSizeSet = {"NVARCHAR2", "VARCHAR2"}; 
 				        	   String[] Types = { "BFILE", "BINARY_DOUBLE", "BINARY_FLOAT", "BLOB", "BOOLEAN", "BINARY_INTEGER", "CHAR", "CLOB",
 						    			  "DATE", "DEC", "DECIMAL", "DOUBLE", "FLOAT", "INT", "INTEGER", "LONG RAW", "MLSLABEL",
 						    			  "NATURAL", "NATURALN", "NCHAR", "NCLOB", "NUMBER", "NUMERIC", "NVARCHAR2", "PLS_INTEGER", "POSITIVE", "POSITIVEN", 
 						    			   "RAW", "REAL", "ROWID", "SIGNTYPE", "SMALLINT", "STRING", "TIMESTAMP", "TIMESTAMP WITH TIME ZONE", 
 							    		 "TIMESTAMP WITH LOCAL TIME ZONE", "UROWID", "VARCHAR2", "XMLTYPE",   "YEAR" };    
 				        	 
 			                   String ColumnName, typeVal, size = "", defaultVal, indexVal;
 			                   String stmtQuery = "CREATE TABLE "+ TableNameField.getText() + " ( ";
 			                   boolean firstEntry = true;
 				        	   
 				        	   for(int i=0; i<nameEntries.length; i++){
 				        		   if(!nameEntries[i].getText().equalsIgnoreCase("") ) {
 				        		       
 				        			   ColumnName = nameEntries[i].getText();
 				        			   int index1 = type[i].getSelectedIndex();
 				        		       typeVal = Types[index1]; 
 				        			   
 				        		        boolean setSize = false;
 				        		        for(int j=0; j < checkSizeSet.length; j++){ 
 				        		    	    if(Types[index1].equalsIgnoreCase(checkSizeSet[j])){
 				        		    	    	
 				        		    	    	if(length[i].getText().equals("")){
 				        		    	    	  JOptionPane.showMessageDialog(null, "'Length/Value' field can't be empty", "Warning", JOptionPane.WARNING_MESSAGE);	
 				        		    	    	  return;	
 				        		    	    	}
 				        		    	    	 else {
 				        		    	    		try{
 				        		    	    		   int checksize1 = Integer.parseInt(length[i].getText());
 				        		    	    		   size = "("+checksize1+")";  
 				        		    	    		   setSize = true;
 				        		    	    		}catch (Exception ex){
 				        		    	    			JOptionPane.showMessageDialog(null, "Value of Length/Values must be an 'integer' type!", "Error", JOptionPane.ERROR_MESSAGE);
 				        		    	    			return;
 						        		    	    }
 				        		    	    	 }
 				        		    	         break;
 				        		    	      }
 				        		    	}  // for j      
 				        		       
 				        		        if(setSize == false){
 				        		        	if(length[i].getText().equals(""))
 				        		        		size = "";
 				        		        	else{
 				        		        		 try{
 				        		        			int sizeVal = Integer.parseInt(length[i].getText());
 				        		        		    size = "("+sizeVal+")";	 
 				        		        		 } 
 				        		        		 catch(Exception ex){
 				        		        			 JOptionPane.showMessageDialog(null, "Value of Length/Values must be an 'integer' type!", "Inane error", JOptionPane.ERROR_MESSAGE);
 				        		    	    	     return;
 				        		        		 }
 				        		        	}  
 				        		        }// if setSize ends here...
 				        		        
 				        		        String[] defaults2 = { "None", "NOT NULL", "NULL", "CURRENT_TIMESTAMP"};
 				        		        if(defaultValue[i].getSelectedIndex() == 0)
 				        		        	defaultVal = "";
 				        		        else{
 				        		           defaultVal = defaults2[defaultValue[i].getSelectedIndex()] ; 
 				        		        }
 				        		        
 				        		        String[] indexes2 = { "---", "PRIMARY KEY", "FOREIGN KEY", "UNIQUE", "INDEX", "BITMAP"};
 				        		        if(constraints[i].getSelectedIndex() == 0)
 				        		        	  indexVal = "";
 				        		        else{
 				        		           indexVal = indexes2[constraints[i].getSelectedIndex()] ; 
 				        		        }
 				        		        
 				        		        //ColumnName, typeVal, size, defaultVal, indexVal
 				        		        
 				        		        if(firstEntry == true){
 				        		           stmtQuery = stmtQuery + ColumnName + "  " + typeVal + "" + size + " "+defaultVal+" "+indexVal;
 				        		           firstEntry = false;
 				        		        }else{
 				        		           stmtQuery = stmtQuery + " , "+ ColumnName + "  " + typeVal + "" + size + " "+defaultVal+" "+indexVal;
 				        		        }
 				        		      
 				        		   
 				        		   }  // if ends here...
 				        		   
 				        	   }  // for i
 				        	   
 				        	   System.out.println(stmtQuery);
 				        	   
 				        	   stmtQuery = stmtQuery + " ) ";
 				        	   System.out.println(stmtQuery);
 				        	   String CommentsQuery = "COMMENT ON TABLE " +TableNameField.getText()+ " IS '"+table_comments_area.getText()+"'";
 				        	   
 				        	   ORA_DB connectToCreateTable = new ORA_DB();
 				        	   
 				        	   if(connectToCreateTable.createTable(stmtQuery, CommentsQuery) == 0){
 				        	        JOptionPane.showMessageDialog(null, "Table Successfuly Created!!!");
 				        	        
 				        	        mainFrame.ControlPanel.removeAll(); 
 			        				mainFrame.ShowTablesComboBoxInControlPanel();
 			        				mainFrame.ControlPanel.setVisible(false);
 			        				mainFrame.ControlPanel.setVisible(true);    
 				        	        
 				        	   }    
 				        	  
 				 			  
                             try {
                          	   CreateTableSetUp();
                             }catch (Exception ex){
                          	   
                             }        
                        }
 				     }
 				    );

  				  
  				 cancelButton.addActionListener(new java.awt.event.ActionListener()       // Modify this method
 			     { 
 			        public void actionPerformed(ActionEvent e)
 			        {
 			 			jpanel.removeAll();
                         try {
                      	   CreateTableSetUp();
                         }catch (Exception ex){
                      	   
                         }        
                    }
 			     }
 			    );

  				    xYLayout1.setWidth(600);    //429
  				    xYLayout1.setHeight(500);   //394
  				    TableName.setText("Table name: ");
  				     TableName.setBackground(new Color(245, 245, 245));
  				     TableName.setOpaque(true);
  				     Total.setText("Total: ");
  				     Total.setBackground(new Color(245, 245, 245));
  				     Total.setOpaque(true);
  				     TotalField.setText(""+TotalColumnFields+"");
  				    
  				     columns.setText("column(s)");
  				     columns.setBackground(new Color(245, 245, 245));
 				     columns.setOpaque(true);   
  				     structure.setFont(new Font("Dialog", 1, 13));
  				     structure.setBackground(new Color(240, 240, 240));
 				     structure.setOpaque(true);
  				     structure.setText("                                         " +
  				     		"                                   Structure");
  				     
  				     Name.setFont(new Font("Dialog", 1, 12));
 				     Name.setBackground(new Color(220, 220, 220));
 				     Name.setOpaque(true);
  				     Name.setText("Name");
  				     
  				     Type.setFont(new Font("Dialog", 1, 12));
 				     Type.setBackground(new Color(220, 220, 220));
 				     Type.setOpaque(true);
  				     Type.setText("Type");
  				     
  				     Length.setFont(new Font("Dialog", 1, 12));              
 				     Length.setBackground(new Color(220, 220, 220));          
 				     Length.setOpaque(true);
  				     Length.setText("Length/Values");
  				     
  				     Default.setFont(new Font("Dialog", 1, 12));
 				     Default.setBackground(new Color(220, 220, 220));
 				     Default.setOpaque(true);
  				     Default.setText("Default");
  				     
  				     Constraints.setFont(new Font("Dialog", 1, 12));
 				     Constraints.setBackground(new Color(220, 220, 220));
 				     Constraints.setOpaque(true);
  				     Constraints.setText("Constraints");
  				     
  				     table_comments.setFont(new Font("Dialog", 1, 13));
  				     table_comments.setBackground(new Color(245, 245, 245));
  				     table_comments.setOpaque(true);
  				     table_comments.setText("Table comments: ");
  				    
  				    jpanel.setLayout(xYLayout1);
  				    jpanel.setBackground(SystemColor.control);    
  				    
  				    jpanel.add(TableName, new XYConstraints(9, 15, 75, 25));            // label TableName
  				    jpanel.add(TableNameField, new XYConstraints(84, 15, 250, 25));     // Table Name Text Field
  				    jpanel.add(Total, new XYConstraints(360, 15, 40, 25));
  				    jpanel.add(TotalField, new XYConstraints(401, 15, 40, 25));            // text field
  				    jpanel.add(columns, new XYConstraints(443, 15, 60, 25));
  				    jpanel.add(go, new XYConstraints(510, 15, 50, 25));
  				    jpanel.add(structure, new XYConstraints(5, 50, 690, 25));
  				    jpanel.add(Name, new XYConstraints(5, 75, 130, 25));                // all labels
  				    jpanel.add(Type, new XYConstraints(137, 75, 130, 25));
  				    jpanel.add(Length, new XYConstraints(269, 75, 130, 25));
  				    jpanel.add(Default, new XYConstraints(401, 75, 130, 25));
  				    jpanel.add(Constraints, new XYConstraints(533, 75, 162, 25));
  				    
  				    // Type
 			    	String[] types = { "BFILE", "BINARY_DOUBLE", "BINARY_FLOAT", "BLOB", "BOOLEAN", "BINARY_INTEGER", "CHAR", "CLOB",
 			    			  "DATE", "DEC", "DECIMAL", "DOUBLE", "FLOAT", "INT", "INTEGER", "LONG RAW", "MLSLABEL",
 			    			  "NATURAL", "NATURALN", "NCHAR", "NCLOB", "NUMBER", "NUMERIC", "NVARCHAR2", "PLS_INTEGER", "POSITIVE", "POSITIVEN", 
 			                  "RAW", "REAL", "ROWID", "SIGNTYPE", "SMALLINT", "STRING", "TIMESTAMP", "TIMESTAMP WITH TIME ZONE", 
 				    		 "TIMESTAMP WITH LOCAL TIME ZONE", "UROWID", "VARCHAR2", "XMLTYPE",   "YEAR" };    
 			        
 			    	// Default
 			    	String[] defaults = { "None", "NOT NULL", "NULL", "CURRENT_TIMESTAMP"};
  				    //Index
 			    	String[] indexes = { "---", "PRIMARY KEY", "FOREIGN KEY", "UNIQUE", "INDEX", "BITMAP"};
  				    int totalcolumns = nameEntries.length;
  				    
  				    for(int i=0; i < totalcolumns; i++){
  				    	// names
  				    	nameEntries[i] = new JTextField(); 
  				    	if(i%2!=0)
  				    		nameEntries[i].setBackground(new Color(240, 240, 240));
 				    	jpanel.add(nameEntries[i], new XYConstraints(10, 75+((i+1)*35), 120, 25));
 				    	
 				    	// Type		      
 				        type[i] = new JComboBox(types);
 				    	type[i].setSelectedIndex(14);
 				    	type[i].setBackground(new Color(250, 250, 250));
 				    	jpanel.add(type[i], new XYConstraints(142, 75+((i+1)*35), 120, 25));
 				    
 				    	// Length 
 				    	length[i] = new JTextField(); 
  				    	if(i%2!=0)
  				    		length[i].setBackground(new Color(240, 240, 240));
 				    	jpanel.add(length[i], new XYConstraints(274, 75+((i+1)*35), 120, 25));   
 				    	
 				    	// Default
 				        defaultValue[i] = new JComboBox(defaults);
 				    	defaultValue[i].setSelectedIndex(0);
 				    	defaultValue[i].setBackground(new Color(250, 250, 250));
 				    	jpanel.add(defaultValue[i], new XYConstraints(405, 75+((i+1)*35), 120, 25));
 				    	
 				    	//Index
 				    	constraints[i] = new JComboBox(indexes);
 				    	constraints[i].setSelectedIndex(0);
 				    	constraints[i].setBackground(new Color(250, 250, 250));
 				    	jpanel.add(constraints[i], new XYConstraints(537, 75+((i+1)*35), 130, 25));
 				    	
  				    }            
  				    
  				    jpanel.add(table_comments, new XYConstraints(5, 75+((totalcolumns+1)*35)+33, 250, 40));
  				    jpanel.add(table_comments_area, new XYConstraints(5, 75+((totalcolumns+1)*35)+75, 240, 25));
  				    
  				    JPanel buttonPanel = new JPanel(new XYLayout());             // Panel for Buttons
  				    buttonPanel.setPreferredSize(new Dimension(670, 50));
  				    buttonPanel.setBackground(new Color(240, 240, 240));
  				    jpanel.add(buttonPanel, new XYConstraints(5, 75+((totalcolumns+1)*35)+136, 670, 50));
  				    jpanel.setPreferredSize(new Dimension(700, 75+((totalcolumns+1)*35)+136+ 200));
  				    buttonPanel.add(createButton, new XYConstraints(400, 10, 120, 20));
  				    buttonPanel.add(cancelButton, new XYConstraints(525, 10, 100, 20));
 	 				jpanel.setBackground(new Color(255, 255, 255));      // for jpanel color
 	 				jpanel.setVisible(false); 
 	 				jpanel.setVisible(true); 
 	 				
  			   }
  			    
          }//Class CreateTableHandler ends here
     
     
     
     /*FileMenuHandler Class--To Handle Events for 'Exit'.
      *Implements ActionListener to Handle Events
      *Open calls- Choose file from JFile Chooser, Read File and Save them in Unsorted and SortedLists 
      * and then Display them
      *SaveAs--Saves the List on a TextFile
      *Exit- Exit From Program
      *@param jframe
      *@author GurpreetSingh   
      */
    public class FileMenuHandler implements ActionListener {
    	   JFrame jframe;
    	 
    	   public StringTokenizer myTokens;
    	   
    	   /* FileMenuHandler-Constructor  
    	    */
     	    public FileMenuHandler () {
    	      
    	    }
    	    /* Method-- ActionPerformed 
    		 * Calls specific methods according to specific events
    		 * Event handles through ActionEvent
    		 * @param menuName To Save the name of action event
    		 * @param event  the event that user chooses
    		 */
    	    public void actionPerformed(ActionEvent event) {
    	      String menuName = event.getActionCommand();
    	      if (menuName.equals("Exit"))
    	          System.exit(0);    
    	      
    	   } //actionPerformed
           
    }//FileMenu Class ends here
         
    
    /* InsertNewRowsHandler
     * Description:
     *   This class handles events related to Insertion of New Rows in existing Tables
     * 
     * @param table_name Table Name in which to insert rows
     * @param num_of_Columns Number of Columns that table have
     * @param ColumnNames Names of those Columns
     * @param ColumnTypes Data Types of Columns like int, string, date etc.
     * @param numberOfNewRows Number of Rows to Insert, default value is 1
     * 
     * @author GurpreetSingh 
     */
    
    public class InsertNewRowsHandler implements ActionListener {
      	   DBAdminAsApplet mainFrame;
      	   JPanel mainPanel;
      	   
      	   // Information Required
      	   String table_name;
      	   int num_of_Columns;
      	   String[] ColumnNames;
      	   String[] ColumnTypes;   
      	   int numberOfNewRows = 1;
      	   
      	   public InsertNewRowsHandler (DBAdminAsApplet main_frame, String[] colum_names, String tabl_name, int no_Of_columns, String[] column_types) {
      	      mainFrame = main_frame;
      	      mainPanel = mainFrame.MainPanel;
      	      
      	       table_name = tabl_name;
      		   num_of_Columns = no_Of_columns;
      		   ColumnNames = colum_names;
      		   ColumnTypes = column_types;
      	   }
            
             public void actionPerformed(ActionEvent event) {
      	      String menuName = event.getActionCommand();

      	      if (menuName.equals("")){                    // for insert data button in View Table--->Option
      	           JOptionPane.showMessageDialog(null, "Inserting Rows");
      	           InsertNewRows();
      	      }    
      	      else if (menuName.equals("SaveAs")){
      	             
      	      }     
      	      
      	   } //actionPerformed
             
           public void InsertNewRows(){
          	   mainPanel.removeAll();
          	   JLabel insertLabel = new JLabel("INSERT Rows in '"+table_name+"'", JLabel.CENTER);
          	   insertLabel.setForeground(new Color(35, 90, 129));
          	   insertLabel.setBackground(new Color(245, 245, 245));
          	   insertLabel.setOpaque(true); 
          	   mainPanel.add(insertLabel, new XYConstraints(2, 5, 715, 22));  
          	   JPanel[] NewRows = new JPanel[numberOfNewRows];  
          	   final JTextField[][] all_Textfields = new JTextField[numberOfNewRows][num_of_Columns]; 
          	   JLabel[][] labelsAsBackground = new JLabel[numberOfNewRows][num_of_Columns];
          	   JLabel[] columnName= new JLabel[num_of_Columns];
          	   JLabel[] columnType= new JLabel[num_of_Columns];
          	   JButton[] goButtons = new JButton[numberOfNewRows];              
          	   JLabel[] button_backgrounds = new JLabel[numberOfNewRows];           
          	   
          	   for(int i=0; i<numberOfNewRows; i++){
          	           NewRows[i] = new JPanel(new XYLayout());
      		           NewRows[i].setBackground(new Color(255, 255, 255));
      		    	   JLabel column_names = new JLabel("Column", JLabel.CENTER); 
      		    	   column_names.setOpaque(true);
      		    	   JLabel column_type = new JLabel("Type", JLabel.CENTER); 
      		    	   column_type.setOpaque(true);
      		    	   JLabel value = new JLabel("Value         ", JLabel.CENTER); 
      		    	   value.setOpaque(true);
      		    	   NewRows[i].add(column_names, new XYConstraints(2, 0, 150, 22));
      		    	   NewRows[i].add(column_type, new XYConstraints(153, 0, 150, 22));
      		    	   NewRows[i].add(value, new XYConstraints(304, 0, 300, 22));
      		    	   
      		    	   for(int j=0; j<num_of_Columns; j++){
      		    		   columnName[j] = new JLabel(ColumnNames[j], JLabel.CENTER);
      		    		   columnName[j].setFont(new Font("", Font.PLAIN, 12)); 
      		    		   NewRows[i].add(columnName[j], new XYConstraints(2, 22+3+28*j, 150, 22)); 
      		    		   
      		    		   columnType[j] = new JLabel(ColumnTypes[j], JLabel.CENTER);
      		    		   columnType[j].setFont(new Font("", Font.PLAIN, 12)); 
      		    		   NewRows[i].add(columnType[j], new XYConstraints(2+150, 22+3+28*j, 150, 22)); 
      		    				   
      		    		   all_Textfields[i][j] = new JTextField(25);  
      		    		   NewRows[i].add(all_Textfields[i][j], new XYConstraints(375, 22+3+28*j, 150, 22));
      		    		   labelsAsBackground[i][j] = new JLabel();
      		    		   labelsAsBackground[i][j].setOpaque(true);                
      		    		   
      		    		   if(j%2==0){
      		    			   labelsAsBackground[i][j].setBackground(new Color(255,255,255));
      		    		       columnName[j].setBackground(new Color(255,255,255));
      		    		       columnType[j].setBackground(new Color(255,255,255));
      		    		   }else{
      		    			   labelsAsBackground[i][j].setBackground(new Color(245,245,245));
      		    			   columnName[j].setBackground(new Color(245,245,245));
      		    			   columnType[j].setBackground(new Color(245,245,245));
      		    		   }
      		    		   
      		    		   NewRows[i].add(labelsAsBackground[i][j], new XYConstraints(2, 22+28*j, 604, 28));
      		    		
      		    	   }
      		   
      		    	   goButtons[i] = new JButton("<html><b>Go</b></html>");
      	    	       NewRows[i].add(goButtons[i], new XYConstraints(30, 22+28*num_of_Columns+2, 50, 18));
      	    	       button_backgrounds[i] = new JLabel();
      	    	       button_backgrounds[i].setOpaque(true);
      	    	       button_backgrounds[i].setBackground(new Color(211, 220, 227));
      	    	       NewRows[i].add(button_backgrounds[i], new XYConstraints(2, 22+28*num_of_Columns, 604, 22 ));
      		    	   mainPanel.add(NewRows[i], new XYConstraints(2, 50+(22+(num_of_Columns*28)+22+50)*i, 604, 22+num_of_Columns*28+22));
                 
                 }
                 
          	    for(int i=0; i<goButtons.length; i++){            
          	       	final int id = i;
                
          	       	goButtons[id].addActionListener(new java.awt.event.ActionListener()
        			     {
        			        public void actionPerformed(ActionEvent e)  
        			        {   
        			        	String col_names_string = "";
        			        	for(int i=0; i<num_of_Columns; i++){
        			        		if(i==0)
        			        		   col_names_string = col_names_string + " "+ColumnNames[i];
        			        		else
        			        			col_names_string = col_names_string + ", "+ColumnNames[i];
        			        	}
        			        	
        			        	String column_field_values = "";
        			        	for(int j=0; j<num_of_Columns; j++){
        			        		if(j==0)
        			        			column_field_values = column_field_values + " '"+all_Textfields[id][j].getText()+"'";
        			        		else
        			        			column_field_values = column_field_values + ", '"+all_Textfields[id][j].getText()+"'";
        			        	}
        			
        			        	String insertRowQuery = "INSERT INTO "+table_name+" ( "+col_names_string+" ) VALUES ( " + column_field_values + " )";
        			        	int dialogResult = JOptionPane.showConfirmDialog (null, "Do you really want to: \n "+ insertRowQuery ,"Are You Sure ?", JOptionPane.YES_OPTION);
        			        	
        			        	if(dialogResult == JOptionPane.YES_OPTION){
        			        		ORA_DB insertRow = new ORA_DB();
        			        		boolean rowAdded = insertRow.SQL_QueryExecuter(insertRowQuery);   
        			        		if(rowAdded){
        			        			JOptionPane.showMessageDialog(null, "Row Added Successfully!");
        			        			
        			        		}else{
        			        			JOptionPane.showMessageDialog(null, "Unsuccessful!");
        			        		}
        			        		
        			        	}else{	
        			        	}  			       
        			        }
        			     } 
        			    );
                  }         
          	   mainPanel.setVisible(false);
          	   mainPanel.setVisible(true);
          	   mainFrame.setVisible(true);
          	   
             } // method ends here
                            
        }    //  InsertNewRowsHandler
         
      
    /* ORA_DB
     * Description:
     *   1) Connects to Oracle DB
     *   2) Contains methods to fetch rows, columns, and tables from Oracle DB
     * @param jdbcDriver 
     * @param dbURL1 Connection String for connection to database
     * @param userName1 User name of user
     * @param userPassword1 Password of user
     * @param conn
     * @param pstmt Prepared Statement for prepared SQL Query
     * @param rs Result Set
     * @param connected this boolean variable tells the status of connection with Oracle DB
     * 
     * @author GurpreetSingh 
     */    
       public class ORA_DB {
        		
        		String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
        	    String dbURL1 =  "jdbc:oracle:thin:@bonnet19.cs.qc.edu:1521:dminor";
        	    String userName1 =  "lackner";
        	    String userPassword1 = "guest";
        	    String oracle_driver = "oracle.jdbc.driver.OracleDriver";   
        	    Connection conn;               // to connect to database
        	    PreparedStatement pstmt;       // for Prepared SQL Query
        	    ResultSet rs;                  // A table of data representing a database Result Set
        		int flag = 0;
        		String newTime;
        		boolean connected;

        		public ORA_DB (){
        			 try
        		     {
        		        Class.forName(oracle_driver);
        		        conn = DriverManager.getConnection(dbURL1, userName1, userPassword1);         // Connect to data base
        		        connected = true; 
        		     }
        		     catch (Exception e)  
        		     {
        		        System.out.println(e.getMessage()); 
        		        connected = false;
        		     }
        		}
        	     
        	    public int createTable (String stmtquery, String commentQuery) {
        	     
        	        try
        	        {        
        	            pstmt = conn.prepareStatement(stmtquery);
        	            rs = pstmt.executeQuery();
        	            pstmt = conn.prepareStatement(commentQuery);
        	            rs = pstmt.executeQuery();
        	            rs.close();
        	            pstmt.close();
        	            
        	            try
        	            {
        	              conn.close();
        	            } 
        	            catch (SQLException e) { };
        	            
        	        }
        	        catch (SQLException e)
        	        {
        	          System.out.println(e.getMessage());
        	          JOptionPane.showMessageDialog(null, e.getMessage(),"Inane error", JOptionPane.ERROR_MESSAGE); 
        	          flag = -1;
        	        }
        	        
        	    	return flag;
        	    }
        	    
        	    public String[] showTables(String stmtquery) {
        	        
        	    	String[] tables = new String[100];       // need to Edit in here...
        	    	try
        	        {        
        	            pstmt = conn.prepareStatement(stmtquery);
        	            rs = pstmt.executeQuery(); 
        	            int i=0;
        	            while (rs.next()) 	
        	            {   tables[i] = new String((String)(rs.getObject(1)));
        	            	i++;
        	            }
        	            
        	            rs.close();
        	            pstmt.close();
        	            
        	            try
        	            {
        	              conn.close();
        	            } 
        	            catch (SQLException e) { }; 
        	            
        	        }
        	        catch (SQLException e)
        	        {
        	          System.out.println(e.getMessage());
        	          JOptionPane.showMessageDialog(null, e.getMessage(),"Inane error", JOptionPane.ERROR_MESSAGE); 
        	          flag = -1;
        	        }
        	         
        	         return tables;
        	    }
        	    
        	public ShowTableData getTable(String stmtquery) {
        	       
        		    ShowTableData table_data = null;
        		    try
        	        {        
        	            pstmt = conn.prepareStatement(stmtquery);
        	            rs = pstmt.executeQuery(); 
        	            table_data = new ShowTableData(rs);
        	            rs.close();
        	            pstmt.close();
        	            
        	            try
        	            {
        	              conn.close();
        	            } 
        	            catch (SQLException e) { }; 
        	        }
        	        catch (SQLException e)
        	        {
        	          System.out.println(e.getMessage());
        	          JOptionPane.showMessageDialog(null, e.getMessage(),"Inane error", JOptionPane.ERROR_MESSAGE); 
        	          flag = -1;
        	        }
        	         
        	        return table_data;
        	    }
        		
        	    public boolean SQL_QueryExecuter(String stmtquery){
        	    	try
        	        {        
        	            pstmt = conn.prepareStatement(stmtquery);
        	            rs = pstmt.executeQuery(); 
        	            rs.close();
        	            pstmt.close();
        	            
        	            try
        	            {
        	              conn.close();
        	            } 
        	            catch (SQLException e) { }; 
        	            
        	        }
        	        catch (SQLException e)
        	        {
        	          System.out.println(e.getMessage());
        	          JOptionPane.showMessageDialog(null, e.getMessage()+"\n Try again ","Inane error", JOptionPane.ERROR_MESSAGE); 
        	          flag = -1;
        	          return false;
        	        }
        	         
        	    	return true;
        	    }
        		
        } // ORA_DB

       /* ShowTableData
        * Description:
        *   Extracts Rows, Columns from ResultSet returned by SQL Query
        *   and saves them in corresponding arrays
        * @param rsMetaData
        * @param rs1
        * @param numberOfColumns
        * @param numberOfRows
        * @param column_names
        * @param column_class_types
        * @param allRowsAsStrings
        *  
        * @author GurpreetSingh 
        */
        public class ShowTableData {
        	    
        		ResultSetMetaData rsMetaData;
        		ResultSet rs1;
        		int numberOfColumns;
        		int numberOfRows = 0;
        		String[] column_names;
        		String[] column_class_types;
        		String[][] allRowsAsStrings;               
        		
        		public ShowTableData(ResultSet rs) throws SQLException{
        			
        			try{
        				rs1 = rs;
        				
        				rsMetaData = rs.getMetaData();
        		        numberOfColumns = rsMetaData.getColumnCount();
        		        
        		        column_names = new String[numberOfColumns];
        		        column_class_types = new String[numberOfColumns];
        		        for(int i=0; i < column_names.length; i++){
        		        	column_names[i] = rsMetaData.getColumnName(i+1);      // as Column index starts from 1... 
        		        	column_class_types[i] = rsMetaData.getColumnTypeName(i+1);      // as Column index starts from 1...
        		        }
        		        
        		        String[][] roughRowsAsStrings = new String[100][numberOfColumns];       // can take maximum of 100 rows... 
        		        
        		        while (rs.next())                             // this while loop to count number of rows
        		        {           
        		        	for(int col=0; col < numberOfColumns; col++ ){
        		        	     roughRowsAsStrings[numberOfRows][col] = rs.getString(col+1);
        		        	}
        		            numberOfRows++;  
        		        } 
        		        
        		        allRowsAsStrings = new String[numberOfRows][numberOfColumns];
        		        for(int i=0; i< numberOfRows; i++ ){
        		        	for(int j=0; j< numberOfColumns; j++){
        		        		allRowsAsStrings[i][j] = roughRowsAsStrings[i][j];  
        		        	}
        		        }
        			}
        			catch(SQLException e){
        				System.out.println(e.getMessage());
        		        JOptionPane.showMessageDialog(null, e.getMessage(),"Inane error", JOptionPane.ERROR_MESSAGE);
        			}
        			
        		}  // Constructor

   }   // ShowTableData

        
/* ViewInsertDeleteHandler
 * Description:
 *   1) This class lets you View Table
 *   2) This class contains methods to Insert Rows into selected Table
 *   3) This class contains methods to Delete Columns/Rows in a Table
 *   4) This class let you Drop Table
 ************
 * Parameters
 ************
 * @param mainFrame
 * @param mainPanel		  
 * @param NameLabel
 * @param InsertDataPanel
 * @param insert
 * @param numberOfRows
 * @param rows_text
 * @param insertButton 
 * @param DeleteTablePanel
 * @param dropTable
 * @param TableToDrop
 * @param dropButton 
 * @param ColumnNames  
 * @param deleteColumnButtons
 * @param deleteColumnbuttonbackgrounds 
 * @param rows
 * @param deleteRowsButtons 
 * @param deleteRowsbuttonbackgrounds
 *    
 * @author GurpreetSingh 
 */

public class ViewInsertDeleteHandler implements ActionListener {
		   
	      DBAdminAsApplet mainFrame;
		  JPanel mainPanel;
		  JLabel NameLabel;
		  JPanel InsertDataPanel;
		  JLabel insert;
		  JTextField numberOfRows;
		  JLabel rows_text;
		  JButton insertButton;
		  JPanel DeleteTablePanel;
		  JLabel dropTable;
		  JLabel TableToDrop;
		  JButton dropButton;
		  JLabel[] ColumnNames;  
	      JButton[] deleteColumnButtons;
	      JLabel[] deleteColumnbuttonbackgrounds; 
	      JLabel[][]  rows;  
	      JButton[] deleteRowsButtons; 
	      JLabel[] deleteRowsbuttonbackgrounds;
		  
		  
	      public ViewInsertDeleteHandler (DBAdminAsApplet main_frame) {                 // Constructor 
		      mainFrame = main_frame;
		      mainPanel = main_frame.MainPanel;
		  }
		   
	       public void actionPerformed(ActionEvent event) {
		      
	    	  String menuName = event.getActionCommand();
	          if (menuName.equals("comboBoxChanged")){ 
		        	  
		        	  JComboBox MainComboBoxInPanel = (JComboBox)(event.getSource());
		        	  try{
		        	    showSelectedTable((String)(MainComboBoxInPanel.getSelectedItem()));   // passing name of the Selected table Name...
		        	  }catch(Exception ex){
		              }
		      }		 
           }  // actionPerformed
	   
	   public void showSelectedTable(String TableName) throws Exception {                  
		   final String table_name = TableName;
		    mainPanel.setVisible(false);
		    mainPanel.setVisible(true);
		    String getTableQuery = "SELECT* FROM "+ TableName;
			System.out.println(getTableQuery);
			
			ORA_DB getSingleTable = new ORA_DB();  
		    final ShowTableData  tableData = getSingleTable.getTable(getTableQuery);               // Getting data from selected Table...
		    
		    // GUI Part for Showing Table Starts here...
		    mainPanel.removeAll();
		    //mainFrame.setTitle(TableName);      
		    mainPanel.setBackground(new Color(255, 255, 254));   
		    		    
		    NameLabel = new JLabel(TableName, JLabel.CENTER); 
		    NameLabel.setBackground(new Color(35, 90, 150));           // 100, 100, 100
		    NameLabel.setFont(new Font("Dialog", 1, 14)); 
		    NameLabel.setForeground(new Color(250, 250, 250));
		    NameLabel.setOpaque(true);
		    Border border =  BorderFactory.createLineBorder(new Color(170, 170, 170));       //BorderFactory.createLineBorder(Color.blue);
		    NameLabel.setBorder(border);
		    mainPanel.add(NameLabel, new XYConstraints(40, 10, 150, 20)); 

		    InsertDataPanel = new JPanel(new XYLayout());                     // 
		    InsertDataPanel.removeAll();
		    InsertDataPanel.setBackground(new Color(245, 245, 245));
		    mainPanel.add(InsertDataPanel, new XYConstraints(3, 20, 710, 40));
		    
		    insert = new JLabel("INSERT: ");
		    insert.setFont(new Font("Dialog", 1, 13));
		    insert.setForeground(new Color(35, 90, 129));              // 153,0,153
		    InsertDataPanel.add(insert, new XYConstraints(45, 15, 60, 20));
		    
		    numberOfRows = new JTextField(16);
		    numberOfRows.setText(""+1+"");
		    InsertDataPanel.add(numberOfRows, new XYConstraints(105, 15, 40, 24));
		    
		    rows_text = new JLabel(" new row(s) into Table '"+TableName+"'");
		    rows_text.setFont(new Font("Dialog", 1, 13));
		    rows_text.setForeground(new Color(0, 128, 0));
		    InsertDataPanel.add(rows_text, new XYConstraints(147, 15, 250, 20));
		    
		    String imageLink = "http://venus.cs.qc.cuny.edu/~sigu2113/Images_data/Insert_row.PNG";
	    	insertButton=null;
			try {
				insertButton = new JButton(new ImageIcon(((getImage(new URL(imageLink)))).getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));
			   
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		    insertButton.setToolTipText("Insert new rows");
		    InsertDataPanel.add(insertButton, new XYConstraints(20, 16, 15, 15));
		    final InsertNewRowsHandler idh  = new InsertNewRowsHandler(mainFrame, tableData.column_names, TableName, tableData.numberOfColumns, tableData.column_class_types);   
		    insertButton.addActionListener(idh);   
		    insertButton.addActionListener(new java.awt.event.ActionListener()
		    {
		        public void actionPerformed(ActionEvent e)
		        {       
		        	idh.numberOfNewRows = Integer.parseInt(numberOfRows.getText());  
		        }
		     } 
		    );

		    DeleteTablePanel = new JPanel(new XYLayout());                      //
		    DeleteTablePanel.removeAll();
		    DeleteTablePanel.setBackground(new Color(235, 235, 235));
		    mainPanel.add(DeleteTablePanel, new XYConstraints(3, 60, 710, 40));
		    dropTable = new JLabel("DROP TABLE ");
		    dropTable.setFont(new Font("Dialog", 1, 13));
		    dropTable.setForeground(new Color(35, 90, 129));
		    DeleteTablePanel.add(dropTable, new XYConstraints(45, 12, 90, 20));
		    
		    TableToDrop = new JLabel("'"+ TableName + "'");
		    TableToDrop.setFont(new Font("Dialog", 1, 13));
		    TableToDrop.setForeground(new Color(0, 128, 0));
		    DeleteTablePanel.add(TableToDrop, new XYConstraints(135, 12, 100, 20));             
		    
		    String imageLink2 = "http://venus.cs.qc.cuny.edu/~sigu2113/Images_data/Delete_Table_Icon.PNG";
	        dropButton=null;
			
	        try {
				dropButton = new JButton(new ImageIcon(((getImage(new URL(imageLink2)))).getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));
			   
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
		    dropButton.setToolTipText("Drop table '"+TableName+"'");
		    DeleteTablePanel.add(dropButton, new XYConstraints(20, 15, 15, 15)); 
		    
		    dropButton.addActionListener(new java.awt.event.ActionListener()
		     {
		        public void actionPerformed(ActionEvent e)
		        {       
		        	String dropTableQuery = "DROP TABLE "+table_name;
		        	int dialogResult = JOptionPane.showConfirmDialog (null, "Do you really want to: \n "+ dropTableQuery ,"Are You Sure ?", JOptionPane.YES_OPTION);
		        	
		        	if(dialogResult == JOptionPane.YES_OPTION){
		        		ORA_DB dropTable = new ORA_DB();
		        		boolean dropped = dropTable.SQL_QueryExecuter(dropTableQuery); 
		        		if(dropped){
		        			JOptionPane.showMessageDialog(null, "Table Dropped Successfully!");
		        			mainPanel.removeAll();
		        			try {
		        				
		        				JTextArea message = new JTextArea();
		        				message.append("Table Dropped Successfully !\n\n You can do following things: \n\n    1) Create New Table \n    2) View and Edit Table by selecting it from Left Window ");
		        		        message.setOpaque(true);
		        				mainPanel.add(message, new XYConstraints(30, 30, 800, 800));
		        				mainFrame.ControlPanel.removeAll(); 
		        				mainFrame.ShowTablesComboBoxInControlPanel();
		        				mainFrame.ControlPanel.setVisible(false);
		        				mainFrame.ControlPanel.setVisible(true);
		        				mainPanel.setVisible(false);
		        				mainPanel.setVisible(true);
		        				
		        			}catch(Exception ex){
		        			 } 
		        			
		        		}else{
		        			JOptionPane.showMessageDialog(null, "Unsuccessfull!");
		        		}
		        		
		        	}else{
		        		
		        	}
		        }
		     } 
		    );
       
		    JPanel showingSelectFromTablePanel = new JPanel(new XYLayout());
		    showingSelectFromTablePanel.setOpaque(true);
		    showingSelectFromTablePanel.setBackground(new Color(235, 235, 235));
		    mainPanel.add(showingSelectFromTablePanel, new XYConstraints(2, 150, 710, 30)); 
		    
		    JLabel selectFrom = new JLabel("SELECT* FROM ");
		    selectFrom.setFont(new Font("Dialog", 1, 12));
		    selectFrom.setForeground(new Color(153, 0 ,153));           //(35, 90, 129));              // 153,0,153
		    showingSelectFromTablePanel.add(selectFrom, new XYConstraints(10, 5, 90, 20));
		    
		    JLabel tableName = new JLabel("'"+ TableName + "'");
		    tableName.setFont(new Font("Dialog", 1, 12));
		    tableName.setForeground(new Color(0, 128 ,0));              // 153,0,153
		    showingSelectFromTablePanel.add(tableName, new XYConstraints(100, 5, 100, 20));
		    
		    JLabel leftCorner = new JLabel();
		    leftCorner.setOpaque(true);
		    leftCorner.setBackground(new Color(245, 245, 245));
		    mainPanel.add(leftCorner, new XYConstraints(2, 177+50, 120, 45));
		    
		    ColumnNames = new JLabel[tableData.numberOfColumns];  
		    deleteColumnButtons = new JButton[tableData.numberOfColumns];
		    deleteColumnbuttonbackgrounds = new JLabel[tableData.numberOfColumns]; 
		    rows = new JLabel[tableData.numberOfRows][tableData.numberOfColumns];  
		    deleteRowsButtons = new JButton[tableData.numberOfRows]; 
		    deleteRowsbuttonbackgrounds = new JLabel[tableData.numberOfRows];
		    
		    for(int i=0; i<tableData.numberOfColumns; i++){
            	String imageLink3 = "http://venus.cs.qc.cuny.edu/~sigu2113/Images_data/Delete_GarbageCan_LightUp.PNG";
		    	deleteColumnButtons[i]=null;
				try {
					deleteColumnButtons[i] = new JButton(new ImageIcon(((getImage(new URL(imageLink3)))).getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));
				   
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    
				deleteColumnButtons[i].setToolTipText("Delete Column '"+tableData.column_names[i]+"'");
		    	Border borderDelButton =  BorderFactory.createLineBorder(new Color(220, 220, 220));
		    	deleteColumnButtons[i].setBorder(borderDelButton);
		    	mainPanel.add(deleteColumnButtons[i], new XYConstraints(121*(i+1)+2+50, 177+50, 20, 20));
		    	
		    	deleteColumnbuttonbackgrounds[i] = new JLabel();
			    deleteColumnbuttonbackgrounds[i].setBackground(new Color(245, 245, 245));
			    deleteColumnbuttonbackgrounds[i].setOpaque(true);
			    mainPanel.add(deleteColumnbuttonbackgrounds[i], new XYConstraints(121*(i+1)+2, 177+50, 120, 22));
		    	
		    	ColumnNames[i] = new JLabel(tableData.column_names[i], JLabel.CENTER);
		    	ColumnNames[i].setOpaque(true);
		    	ColumnNames[i].setBackground(new Color(235, 235, 235));
		    	ColumnNames[i].setForeground(new Color(35, 90, 129));  
		    	mainPanel.add(ColumnNames[i], new XYConstraints(121*(i+1)+2, 200+50, 120, 22));
		    	
		    }
            
            for(int i=0; i<tableData.numberOfRows; i++){
		      for(int j=0; j<tableData.numberOfColumns; j++){	
            	
		    	rows[i][j] = new JLabel(tableData.allRowsAsStrings[i][j], JLabel.CENTER);  
            	rows[i][j].setOpaque(true);
            	rows[i][j].setFont(new Font("", Font.PLAIN, 14));
            	
            	if(i%2==0){
            		rows[i][j].setBackground(new Color(250, 250, 250));
            	}else {
            		rows[i][j].setBackground(new Color(240, 240, 240));
            	}
            	   	
		    	mainPanel.add(rows[i][j], new XYConstraints(121*(j+1)+2, 222+(23*i)+1+50, 120, 22));
		      }
		      
		      String imageLink4 = "http://venus.cs.qc.cuny.edu/~sigu2113/Images_data/Delete_GarbageCan_LightUp.PNG";
		    	 
		      deleteRowsButtons[i]=null;
				try {
					deleteRowsButtons[i] = new JButton(new ImageIcon(((getImage(new URL(imageLink4)))).getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH)));   
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		      
		        deleteRowsButtons[i].setToolTipText("Delete this row");
		    	Border borderDelButton =  BorderFactory.createLineBorder(new Color(220, 220, 220));
		    	deleteRowsButtons[i].setBorder(borderDelButton);
		    	mainPanel.add(deleteRowsButtons[i], new XYConstraints(50, 222+(23*i)+1+50, 20, 20));
		        deleteRowsbuttonbackgrounds[i] = new JLabel();
		      
		      if(i%2==0){
		          deleteRowsbuttonbackgrounds[i].setBackground(new Color(250, 250, 250));
		      }else {
		    	  deleteRowsbuttonbackgrounds[i].setBackground(new Color(240, 240, 240));
		      }
		      deleteRowsbuttonbackgrounds[i].setOpaque(true);
		      mainPanel.add(deleteRowsbuttonbackgrounds[i], new XYConstraints(2, 222+(23*i)+1+50, 120, 22));
		   
           }
            
            mainFrame.setVisible(true); 
            
            for(int i=0; i<deleteColumnButtons.length; i++){            
	        	final int id=i;
            	final String column_name = tableData.column_names[id];
	            deleteColumnButtons[id].addActionListener(new java.awt.event.ActionListener()
			     {
			        public void actionPerformed(ActionEvent e)
			        {       
			        	String dropColumnQuery = "ALTER TABLE "+table_name+ " DROP COLUMN "+column_name;
			        	int dialogResult = JOptionPane.showConfirmDialog (null, "Do you really want to: \n "+ dropColumnQuery ,"Are You Sure ?", JOptionPane.YES_OPTION);
			        	
			        	if(dialogResult == JOptionPane.YES_OPTION){
			        		ORA_DB dropColumn = new ORA_DB();
			        		boolean dropped = dropColumn.SQL_QueryExecuter(dropColumnQuery);
			        		if(dropped){
			        			JOptionPane.showMessageDialog(null, "Column Dropped Successfully!");
			        			mainPanel.removeAll();
			        			try {
			        				
			        				showSelectedTable(table_name); 
			        				
			        			}catch(Exception ex){
			        			  } 
			        			
			        		}else{
			        			JOptionPane.showMessageDialog(null, "Unsuccessfull!");
			        		}
			        		
			        	}else{
			        		
			        	}
			        }
			     } 
			    );
	            
            }   // for loop ends here...
            
            for(int i=0; i<deleteRowsButtons.length; i++){            
   	        	final int id=i;
            	deleteRowsButtons[id].addActionListener(new java.awt.event.ActionListener()
			     {
			        public void actionPerformed(ActionEvent e)
			        {   String addQuery= "";    	
			        	for(int j=0; j<tableData.numberOfColumns; j++){
			        		if(j==0)
			        		   addQuery = addQuery + " "+tableData.column_names[j] +"='"+ tableData.allRowsAsStrings[id][j]+"'"; 
			        		else
			        		   addQuery = addQuery + " AND "+tableData.column_names[j] +"='"+ tableData.allRowsAsStrings[id][j]+"'";
			        	}
			        	String dropRowQuery = "DELETE FROM "+table_name+ " WHERE( "+addQuery+" )";
			        	
			        	int dialogResult = JOptionPane.showConfirmDialog (null, "Do you really want to: \n "+ dropRowQuery ,"Are You Sure ?", JOptionPane.YES_OPTION);
			        	
			        	if(dialogResult == JOptionPane.YES_OPTION){
			        		ORA_DB dropRow = new ORA_DB();
			        		boolean dropped = dropRow.SQL_QueryExecuter(dropRowQuery);
			        		if(dropped){
			        			JOptionPane.showMessageDialog(null, "Row Dropped Successfully!");
			        			mainPanel.removeAll();
			        			try {
			        				
			        				showSelectedTable(table_name); 
			        				
			        			}catch(Exception ex){
			        			  } 
			        			
			        		}else{
			        			JOptionPane.showMessageDialog(null, "Unsuccessfull!");
			        		}
			        	}
			        }
			     } 
			    );
            } 
	   }    
	   
  }//  ViewInsertDeleteHandler Class ends here
      
 
}// DBAdminAsApplet ends here
