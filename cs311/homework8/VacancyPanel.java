//Charles Treatman
//CS 311 Databases
//Fussers Frame Class

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class VacancyPanel extends JPanel {
    Connection conn;

    JPanel panel;
    JTextField numRooms;
    JLabel nrlabel;
    JScrollPane directory;
    JTextArea dirview;

    public VacancyPanel(Connection c) {
	this.setName("Vacancy");
	conn = c;

	panel = new JPanel();
	nrlabel = new JLabel("Number in your group: ");
	numRooms = new JTextField(2);
	directory = new JScrollPane();
	directory.setPreferredSize(new Dimension(600, 400));
	dirview = new JTextArea();
	dirview.setSize(new Dimension(600, 400));
	directory.setViewportView(dirview);	

	panel.add(nrlabel);
	panel.add(numRooms);
	this.add(panel);
	this.add(directory);
    }
      

    public void submitAction() {
	dirview.setText("");
	try {
	    Statement stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery("SELECT * from Available WHERE numbeds - numtaken >= " + numRooms.getText());
	    if (rs.next()) {
		do {
		    dirview.insert(rs.getString("street") + "\t" +
				   rs.getInt("numbeds") + "\t" +
				   rs.getInt("numtaken") + "\n", 0);
		}while (rs.next());
		dirview.insert("Street\t\t# Bedrooms\t# Tenants\n", 0);
	    }
	    else
		dirview.insert("No vacancies found for your group size.\n", 0);
	}
	catch (SQLException e) {
	    dirview.insert(e.getMessage(), 0);
	}
    }
}
