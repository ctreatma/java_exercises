//Charles Treatman
//CS 311 Databases
//Fussers Frame Class

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentPanel extends JPanel {
    Connection conn;

    JPanel fpanel, lpanel, tpanel, epanel, phpanel;
    JPanel dpanel, rpanel, ypanel, opanel, vpanel;
    JLabel fnlabel, lnlabel, tlabel, elabel, phlabel;
    JLabel dormlabel, rmlabel, yrlabel, olabel;
    JComboBox yearchoice;
    JTextField fname, lname, tnumber, email, phone, room, ocmr, dorm;
    JTextArea display;
    JScrollPane viewer;

    public StudentPanel(Connection c) {
	super();
	this.setName("Student");
	conn = c;
	
	fpanel = new JPanel();
	lpanel = new JPanel();
	tpanel = new JPanel();
	epanel = new JPanel();
	phpanel = new JPanel();
	dpanel = new JPanel();
	rpanel = new JPanel();
	ypanel = new JPanel();
	opanel = new JPanel();
	vpanel = new JPanel();

	fnlabel = new JLabel("First Name: ");
	lnlabel = new JLabel("Last Name: ");
	tlabel = new JLabel("T-number: (Required field) ");
	elabel = new JLabel("e-mail Address: ");
	phlabel = new JLabel("Phone Number: ");
	dormlabel = new JLabel("Dorm: ");
	rmlabel = new JLabel("Room Number: ");
	yrlabel = new JLabel("Year: ");
	olabel = new JLabel("OCMR: ");
	viewer = new JScrollPane();
	viewer.setPreferredSize(new Dimension(600, 100));
	display = new JTextArea();
	display.setSize(new Dimension(600, 100));
	viewer.setViewportView(display);

	yearchoice = new JComboBox();
	yearchoice.addItem(makeObj("Freshman"));
	yearchoice.addItem(makeObj("Sophomore"));
	yearchoice.addItem(makeObj("Junior"));
	yearchoice.addItem(makeObj("Senior"));
	yearchoice.addItem(makeObj("5th Year"));	

	fname = new JTextField(20);
	lname = new JTextField(20);
	tnumber = new JTextField(20);
	email = new JTextField(50);
	phone = new JTextField(10);
	dorm = new JTextField(20);
	room = new JTextField(3);
	ocmr = new JTextField(4);

	fpanel.add(fnlabel);
	fpanel.add(fname);
	fpanel.add(lnlabel);
	fpanel.add(lname);
	tpanel.add(tlabel);
	tpanel.add(tnumber);
	tpanel.add(yrlabel);
	tpanel.add(yearchoice);
	epanel.add(elabel);
	epanel.add(email);
	phpanel.add(phlabel);
	phpanel.add(phone);
	dpanel.add(dormlabel);
	dpanel.add(dorm);
	dpanel.add(rmlabel);
	dpanel.add(room);
	opanel.add(olabel);
	opanel.add(ocmr);
	vpanel.add(viewer);

	this.add(fpanel);
	this.add(lpanel);
	this.add(tpanel);
	this.add(epanel);
	this.add(phpanel);
	this.add(dpanel);
	this.add(rpanel);
	this.add(opanel);
	this.add(ypanel);
	this.add(vpanel);
    }

    public void submitAction() {
	try {
	    Statement stmt = conn.createStatement();
	    stmt.executeUpdate("INSERT into Student VALUES" +
			       "('" + tnumber.getText() + "', '" + 
			       fname.getText() + "', '" +
			       lname.getText() + "', '" +
			       phone.getText() + "', " +
			       ocmr.getText() + ", '" +
			       yearchoice.getSelectedItem().toString() +
			       "', '" + email.getText() + "', '" +
			       dorm.getText() + "', " + room.getText() + ")");
	    display.insert("Student " + fname.getText() + " " +
			   lname.getText() + " successful.\n", 0);
	}
	catch (SQLException e) {
	    display.insert(e.getMessage(), 0);
	    display.insert("Add Student failed for Student: " +
			   fname.getText() + " " + lname.getText() +
			   " with T-number " + tnumber.getText() + ".\n", 0);
	}
    }
    
    private Object makeObj(final String item)  {
	return new Object() { public String toString() { return item; } };
    }
}
