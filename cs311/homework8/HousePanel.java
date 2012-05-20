//Charles Treatman
//CS 311 Databases
//Fussers Frame Class

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class HousePanel extends JPanel {
    Connection conn;
    JPanel ssnpanel, strpanel;
    JLabel ssnlabel, strlabel;
    JTextField ssn, street;
    JScrollPane viewer1, viewer2;
    JTextArea errors, owns;

    public HousePanel(Connection c) {
	this.setName("House");
	conn = c;

	ssnpanel = new JPanel();
	strpanel = new JPanel();
	ssnlabel = new JLabel("Owner's Social Security Number: ");
	strlabel = new JLabel("Street Address: ");
	ssn = new JTextField(9);
	street = new JTextField(20);
	viewer1 = new JScrollPane();
	viewer2 = new JScrollPane();
	viewer1.setPreferredSize(new Dimension(600, 300));
	viewer2.setPreferredSize(new Dimension(600, 100));
	errors = new JTextArea();
	owns = new JTextArea();
	errors.setSize(new Dimension(600, 100));
	owns.setSize(new Dimension(600, 300));	
	viewer1.setViewportView(owns);
	viewer2.setViewportView(errors);

	ssnpanel.add(ssnlabel);
	ssnpanel.add(ssn);
	strpanel.add(strlabel);
	strpanel.add(street);
	this.add(ssnpanel);
	this.add(strpanel);
	this.add(viewer1);
	this.add(viewer2);
	errors.insert("Error Messages:\n", 0);
	try {
	    Statement stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery("SELECT lastname, firstname, street FROM Landlord NATURAL JOIN Owns ORDER BY lastname DESC");
	    while (rs.next()) {
		owns.insert(rs.getString("lastname") + "\t" +
			       rs.getString("firstname") + "\t" +
			       rs.getString("street") + "\n", 0);
	    }
	    owns.insert("Last\tFirst\tStreet\n", 0);
	}
	catch (SQLException e) {
	    errors.insert(e.getMessage() + "\n", 0);
	}	    
    }
    
    public void submitAction() {
	owns.setText("");
	try {
	    Statement stmt = conn.createStatement();
	    stmt.executeUpdate("DELETE from Owns where street = '" +
			       street.getText() + "'");
	    stmt.executeUpdate("INSERT into Owns values ('" +
			       ssn.getText() + "', '" +
			       street.getText() + "')");
	    ResultSet rs = stmt.executeQuery("SELECT lastname, firstname, street FROM Landlord NATURAL JOIN Owns ORDER BY lastname DESC");
	    while (rs.next()) {
		owns.insert(rs.getString("lastname") + "\t" +
			       rs.getString("firstname") + "\t" +
			       rs.getString("street") + "\n", 0);
	    }
	    owns.insert("Last\tFirst\tStreet\n", 0);
	}
	catch (SQLException e) {
	    errors.insert(e.getMessage() + "\n", 0);
	}
    }

}
