/* Class: ServletMain, Project Name: DBAdminAsServlet
 * Description: 
 *   A Database admin designed by Gurpreet Singh.
 *   Functionalities:
 *   1) Connects to Remote Database
 *   2) Create new Table, View Old Tables
 *   3) Insert/Delete any number of Rows in Tables
 *   4) Insert/Delete any no. of Columns in Tables
 *   5) Drop Table 
 * 
 * @param out
 *************
 * Author Info
 *************
 * @author Gurpreet Singh
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import oracle.jdeveloper.layout.XYConstraints;
import oracle.jdeveloper.layout.XYLayout;

public class ServletMain extends HttpServlet {

	private static final long serialVersionUID = 1L;
    PrintWriter out;
	
    public ServletMain() {
        super();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");   
 		out = response.getWriter();
 		InitTemplate();
		String action=request.getParameter("action");
		String tableName=request.getParameter("tableName");
		 
        if(action==null) {
                action="";
                out.println("<hr><br><b>Welcome to Oracle Database admin!--Servlet Version by Gurpreet Singh</b>");
        } else if(action.equals("create") ) {
                try{
             		out.println("<p>Create new Table</p>");
             		CreateTableHandler cmh = new CreateTableHandler();
             		cmh.CreateTableSetUp();
                } catch(Exception e) {
                        out.println("Exception="+e);
                } 
        } else if(action.equals("insert")) {
 	        	try{

 	        	} catch(Exception e) {
	                    out.println("Exception="+e);
	            } finally {
	            }
        } else if(action.equals("delete")) {
        	 	try{
	        		out.println("<br><br>Select Table to <b>DROP</b>: <br> ");
	        		out.println("<FORM METHOD = 'POST' ACTION = 'http://localhost:8080/Project1Servlet2/ServletMain?action=delete' >");
	        		ORA_DB ToShowTables = new ORA_DB(out); 
	           	    
	           	    if(ToShowTables.connected == true){
	           	    	String[] alltables = ToShowTables.showTables("SELECT TABLE_NAME AS NAME FROM USER_TABLES");  
	           	    	String selectTable = "&nbsp;&nbsp;&nbsp; <select name='tableName'>";
	           	    	for(int i=0; i< alltables.length; i++)
	                    { 
	           	    	   selectTable =  selectTable +   "<option value="+alltables[i]+">"+alltables[i]+"</option>";
	                    }
	           	    	selectTable =  selectTable + "</select>";
	           	    	out.println(selectTable);
	           	    }
	           	    out.println("<INPUT TYPE='SUBMIT' VALUE='DROP'>");
	           	    out.println("</form> ");
	        	} catch(Exception e) {
	                    out.println("Exception="+e);
	            } finally {
	            }
          }
        EndTemplate();
	}
	
	public void InitTemplate() {
		String InitialPage = "<html>" +
				" <head>" +
				"    <title> DB servlet </title>" +
				" </head>" +
				" <body bgcolor='#9FCDE4'> " +
        		"    <br>&nbsp;&nbsp; " +
        		"    <a href='http://localhost:8080/Project1Servlet2/ServletMain?action=create' ><img src='http://venus.cs.qc.cuny.edu/~sigu2113/Images_data/Create_tab.PNG' width='100' height='100' alt='Create' /></a>&nbsp;&nbsp;&nbsp;" +
        		"    <a href='http://localhost:8080/Project1Servlet2/ServletMain?action=insert' ><img src='http://venus.cs.qc.cuny.edu/~sigu2113/Images_data/Insert_row.PNG' width='100' height='100' alt='Insert' /></a>&nbsp;&nbsp;&nbsp;" +
        		"    <a href='http://localhost:8080/Project1Servlet2/ServletMain?action=delete' ><img src='http://venus.cs.qc.cuny.edu/~sigu2113/Images_data/Delete_Table_Icon.PNG' width='100' height='100' alt='Delete' />  </a>" +
        		""  ;  
	
		out.println(InitialPage);
		ORA_DB ToShowTables = new ORA_DB(out); 
   	    
   	    if(ToShowTables.connected == true){
   	    	String[] alltables = ToShowTables.showTables("SELECT TABLE_NAME AS NAME FROM USER_TABLES");  
   	    	out.println("<form method='POST'  action='http://localhost:8080/Project1Servlet2/ServletMain?action=ViewAndEditTable'>");
   	    	String selectTable = "&nbsp;&nbsp;&nbsp; <select name='tableNames'>";
   	    	for(int i=0; i< alltables.length; i++)
            { 
   	    	   selectTable =  selectTable +   "<option value="+alltables[i]+">"+alltables[i]+"</option>";
	        }
   	    	selectTable =  selectTable + "</select>";
   	    	out.println(selectTable);
   	    	out.println(" <input type='submit' value='View'> </form>");
   	    }
	   
	}
	
	public void EndTemplate() {
		out.println("</body></html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
 		out = response.getWriter();
 		InitTemplate();
		String action=request.getParameter("action");
		System.out.println("action = "+ action);
		 
        if(action==null) {
                action="";
                out.println("<hr><br><b>Welcome to Oracle Database admin!--Servlet Version by Gurpreet Singh</b>");
        } else if(action.equals("createtable")) {
	            try{
	            	String tableName = request.getParameter("tableName");        	
	            	String col_names[] = new String[4];
	            	String type[] = new String[4];
	            	String length[] = new String[4];
	            	String defalt[] = new String[4];           //defalt = default
	            	String constraints[] = new String[4];
	            	boolean FirstEntry=true;
	            	String StmtQuery = "CREATE TABLE "+ tableName + " ( ";
	            	
	            	for(int i=0; i<4; i++) {
	            		col_names[i]   = request.getParameter("columnName_"+i+"");
	            		type[i]        = request.getParameter("Type_"+i+"");
	            		length[i]      = request.getParameter("length_"+i+"");
	            		defalt[i]      = request.getParameter("default_"+i+"");
	            		constraints[i] = request.getParameter("constraint_"+i+"");
	            	}
                for(int i=0; i<4; i++){ 	
                  	if(!col_names[i].equals("")){
                  		String length_new="";
                  		if(type[i].equals("VARCHAR2") ){
                  			length_new = "(" + length[i] + ") ";
                  		}
                  		
                  		
	                	if(FirstEntry == true){ 		
	       		           StmtQuery = StmtQuery + col_names[i] + " " + type[i] + " " + length_new +" "+defalt[i]+" "+constraints[i];
	       		           FirstEntry = false;
	       		        }else{
	       		           StmtQuery = StmtQuery + " , "+ col_names[i] + "  " + type[i] + " " + length[i] + " "+defalt[i]+" "+constraints[i];
	       		        }
                  	} // if ends here...
                 }   

	           StmtQuery = StmtQuery + ")";
	       	   System.out.println(StmtQuery);
	       	   
	       	   ORA_DB connectToCreateTable = new ORA_DB();
	       	   
	       	   if(connectToCreateTable.createTable(StmtQuery) == 0){
	       	         System.out.println("Table Successfuly Created!!!");
	       	        
	       	   }    
            } catch(Exception e) {
                    out.println("Exception="+e);
            } finally {
                    
            }
        } else if(action.equals("insert")) {
 
	        	try{
	        	   
	            } catch(Exception e) {
	                    out.println("Exception="+e);
	            } finally {
	            }
        } else if(action.equals("delete")) {
        	 	try{
	        		ORA_DB connectToDropTable = new ORA_DB();
	        		if(connectToDropTable.SQL_QueryExecuter("DROP TABLE "+ request.getParameter("tableName")) == true){
	        			out.println("<hr><br><h3>Table Dropped Successfully!</h3>");
	        		}
	            } catch(Exception e) {
	                    out.println("Exception="+e);
	            } finally {
	            }
          } else if(action.equals("ViewAndEditTable")) {
         		try{
 	        		 out.println("View Table: "+ request.getParameter("tableNames")+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");  
 	        		 String table_name =  request.getParameter("tableNames");
 	        		 System.out.println("table_name: "+table_name);
		        	 String getTableQuery = "SELECT* FROM "+ table_name;		
					 ORA_DB getSingleTable = new ORA_DB();  
				     ShowTableData  tableData = getSingleTable.getTable(getTableQuery);               // Getting data from selected Table...
	        	     
				     out.println("<table border='1'>" ); 
				     out.println("<tr>");
				     // Print column names
				     for(int i=0; i < tableData.numberOfColumns; i++){
				    	 out.println("<td><b>"+tableData.column_names[i]+"</b></td>"); 
				     }
				     
				     out.println("</tr>");
				     // Printing Rows...
				     for(int i=0; i < tableData.numberOfRows ; i++) {
				       out.println("<tr>");	 
				       for(int j=0; j < tableData.numberOfColumns; j++){ 
				    	 out.println("<td>"+tableData.allRowsAsStrings[i][j]+"</td>");
				       }
				       out.println("</tr>");
				     }  
				     out.println("</table>"); 
	
         		} catch(Exception e) {
 	                    out.println("Exception="+e);
 	            } finally {
 	            	
 	            }
           }
        
        EndTemplate();
	
	 }    // doPost ends here...
	
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

		public ORA_DB (PrintWriter out){
			 try
		     {
		        Class.forName(oracle_driver);
		        conn = DriverManager.getConnection(dbURL1, userName1, userPassword1);         // Connect to data base
		        connected = true; 
		     }
		     catch (Exception e)  
		     {
		        out.println(e); 
		        connected = false;
		     }
		}
		
		public ORA_DB (){
			 try
		     {
		        Class.forName(oracle_driver);
		        conn = DriverManager.getConnection(dbURL1, userName1, userPassword1);         // Connect to data base
		        connected = true; 
		     }
		     catch (Exception e)  
		     {
		        out.println(e); 
		        connected = false;
		     }
		}
	     
	    public int createTable (String stmtquery) {
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
	            {  tables[i] = new String((String)(rs.getObject(1)));
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
	        {   pstmt = conn.prepareStatement(stmtquery);
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
	}

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
			
		}  // Constructor ends here...
	}// Class ends here...

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
	public class CreateTableHandler {
			 JPanel jpanel;
	 	     JFrame mainFrame;   
	
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
			
 	      // Constructor 
 	      public CreateTableHandler () {
			     mainFrame = new JFrame(); 
				 jpanel = new JPanel();     
		  }
						   
		  public void CreateTableSetUp() throws Exception{
			
			   go.addActionListener(new java.awt.event.ActionListener()
		       {    public void actionPerformed(ActionEvent e)
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
				        	    } // else 
				        	   
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
				        		        }// if
				        		        
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
				        		        
				        		        if(firstEntry == true){
				        		           stmtQuery = stmtQuery + ColumnName + "  " + typeVal + "" + size + " "+defaultVal+" "+indexVal;
				        		           firstEntry = false;
				        		        }else{
				        		           stmtQuery = stmtQuery + " , "+ ColumnName + "  " + typeVal + "" + size + " "+defaultVal+" "+indexVal;
				        		        }
				        		   }  // if
				        	   }  // for
				        	   System.out.println(stmtQuery);
				        	   stmtQuery = stmtQuery + " ) ";
				        	   System.out.println(stmtQuery);
				        	   String CommentsQuery = "COMMENT ON TABLE " +TableNameField.getText()+ " IS '"+table_comments_area.getText()+"'";
				        	   ORA_DB connectToCreateTable = new ORA_DB();
				        	   if(connectToCreateTable.createTable(stmtQuery) == 0){
				        	         out.println("Table Successfuly Created!!!");
				        	        
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

				    // Setting Components of GUI
				    xYLayout1.setWidth(600);    
				    xYLayout1.setHeight(500);  
				     
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
				    
				    out.println("<FORM METHOD = 'POST' ACTION = 'http://localhost:8080/Project1Servlet2/ServletMain?action=createtable' >" +
				    		         "<b>Table Name:</b> &nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; " +
				    		         "<INPUT TYPE='text' NAME='tableName' VALUE='Enter Table name here...'  MAXLENGTH='20'>" +
				    		         "   <br>&nbsp;<b>NumberOfColumns:</b> &nbsp;  " +
				    		         "<INPUT TYPE='text' NAME='numberOfColumns' VALUE='4' MAXLENGTH='4'>" +
				    		         "<br><br> <b>Column Name</b> &nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				    		         "<b>Type</b> &nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				    		         "<b>Length/Values</b> &nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				    		         "<b>Default</b> &nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				    		         "<b>Constraints</b>");
				    
				    for(int i=0; i<4; i++){
				    	out.println("<br><INPUT TYPE='text' NAME='columnName_"+i+"'  MAXLENGTH='15'>");
				    	out.println("<SELECT NAME='Type_"+i+"'> <option value='INT'>INT</option> <option value='CHAR'>CHAR</option> <option value='VARCHAR2'>VARCHAR2</option><option value='FLOAT'>FLOAT</option> </SELECT>");
				    	out.println("<INPUT TYPE='text' NAME='length_"+i+"  MAXLENGTH='15'>");
				    	out.println("<SELECT NAME='default_"+i+"'> <option value=''></option> <option value='NULL'>NULL</option> <option value='NOT NULL'>NOT NULL</option> </SELECT>");
				    	out.println("<SELECT NAME='constraint_"+i+"'> <option value=''></option><option value='PRIMARY KEY'>PRIMARY KEY</option> <option value='FOREIGN KEY'>FOREIGN KEY</option>  </SELECT>");
				    }
				   
				    out.println( "<br><br>&nbsp;&nbsp;&nbsp;" +
				    		         "<INPUT TYPE='submit' Value='Create Table'>" +
				    		      "</FORM>");  
				    
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
      }//Class CreateTableHandler
	
}   // ServletMain Class ends here....
