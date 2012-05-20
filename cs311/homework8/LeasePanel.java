//Charles Treatman
//CS 311 Databases
//Fussers Frame Class

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LeasePanel extends JPanel {
    Connection conn;
    JPanel tpanel, spanel;
    JLabel tlabel, slabel;
    JTextField tnumber, street;
    JScrollPane viewer1, viewer2;
    JTextArea errors, lease;

    public LeasePanel(Connection c) {
	this.setName("Lease");
	conn = c;
	
	tpanel = new JPanel();
	spanel = new JPanel();
	tlabel = new JLabel("Tnumber: ");
	slabel = new JLabel("Street Address: ");
	tnumber = new JTextField(20);
	street = new JTextField(20);
	viewer1 = new JScrollPane();
	viewer2 = new JScrollPane();
	errors = new JTextArea();
	lease = new JTextArea();
	viewer1.setPreferredSize(new Dimension(600, 300));
	viewer2.setPreferredSize(new Dimension(600, 100));
	errors = new JTextArea();
	lease = new JTextArea();
	errors.setSize(new Dimension(600, 100));
	lease.setSize(new Dimension(600, 300));	
	viewer1.setViewportView(lease);
	viewer2.setViewportView(errors);

	tpanel.add(tlabel);
	tpanel.add(tnumber);
	spanel.add(slabel);
	spanel.add(street);
	this.add(tpanel);
	this.add(spanel);
	this.add(viewer1);
	this.add(viewer2);
	errors.insert("Error Messages:\n", 0);
	try {
	    Statement stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery("SELECT lastname, firstname, street FROM Student NATURAL JOIN Rents ORDER BY lastname DESC");
	    while (rs.next()) {
		lease.insert(rs.getString("lastname") + "\t" +
			     rs.getString("firstname") + "\t" +
			     rs.getString("street") + "\n", 0);
	    }
	    lease.insert("Last\tFirst\tStreet\n", 0);
	}
	catch (SQLException e) {
	    errors.insert(e.getMessage() + "\n", 0);
	}
    }
      

    public void submitAction() {
	try {
	    Statement stmt = conn.createStatement();
	    stmt.executeUpdate("INSERT INTO Rents VALUES ('" +
			       street.getText() + "', '" +
			       tnumber.getText() + "')");
	    ResultSet rs = stmt.executeQuery("SELECT lastname, firstname, street FROM Student NATURAL JOIN Rents ORDER BY lastname DESC");
	    while (rs.next()) {
		lease.insert(rs.getString("lastname") + "\t" +
			    rs.getString("firstname") + "\t" +
			    rs.getString("street") + "\n", 0);
	    }
	    lease.insert("Last\tFirst\tStreet\n", 0);
	}
	catch (SQLException e) {
	    errors.insert(e.getMessage() + "\n", 0);
	}
    }
}
