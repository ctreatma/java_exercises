//Charles Treatman
//CS 311 Databases
//Fussers Frame Class

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TenantPanel extends JPanel {
    Connection conn;
    JPanel panel;
    JLabel landlabel;
    JComboBox landlords;
    JScrollPane directory;
    JTextArea dirview;

    public TenantPanel(Connection c) {
	this.setName("Tenant");
	conn = c;
	
	panel = new JPanel();
	landlabel = new JLabel("Display Tenants For: ");
	landlords = new JComboBox();
	directory = new JScrollPane();
	directory.setPreferredSize(new Dimension(600, 400));
	dirview = new JTextArea();
	dirview.setSize(new Dimension(600, 400));
	directory.setViewportView(dirview);

	try {
	    Statement stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery("SELECT firstname, lastname FROM Landlord");
	    while (rs.next()) {
		landlords.addItem(makeObj(rs.getString("firstname") + " "
					  + rs.getString("lastname")));
	    }
	}
	catch (SQLException e) {
	    e.printStackTrace();
	}
	    
	panel.add(landlabel);
	panel.add(landlords);
	this.add(panel);
    }

    public void submitAction() {
	try {
	    Statement stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery("SELECT firstname, lastname FROM Student NATURAL JOIN Lease");
	}
	catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private Object makeObj(final String item)  {
	return new Object() { public String toString() { return item; } };
    }
      
}
