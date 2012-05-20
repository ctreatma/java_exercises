//Charles Treatman
//CS 311 Databases
//JDBC GUI

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HousingFrame extends JFrame implements ActionListener {
    static final String SUBMIT = "Submit", QUIT = "Quit";
    Connection conn;
    final JButton submit = new JButton(SUBMIT);
    final JButton quitbutton = new JButton(QUIT);
    JPanel buttonpanel = new JPanel();
    JTabbedPane pane = new JTabbedPane();    

    public HousingFrame(Connection c) {
	conn = c;

	try {
	    jbInit();
	}
	catch(Exception e) {
	    e.printStackTrace();
	}
    }
   
    void jbInit() throws Exception {
	this.setSize(new Dimension(800, 600));
	Container contents = getContentPane();

	submit.addActionListener(this);
	quitbutton.addActionListener(this);
	submit.setActionCommand(SUBMIT);
	quitbutton.setActionCommand(QUIT);
	buttonpanel.add(quitbutton);
	buttonpanel.add(submit);

	pane.add("Fussers", new FussersPanel(conn));
	pane.add("Add Student", new StudentPanel(conn));
	pane.add("Vacancy Search", new VacancyPanel(conn));
	pane.add("Sign a Lease", new LeasePanel(conn));
	pane.add("Change Ownership", new HousePanel(conn));

	contents.add(pane, "Center");
	contents.add(buttonpanel, "South");
    }

    protected void processWindowEvent(WindowEvent e) {
	super.processWindowEvent(e);
	if (e.getID() == WindowEvent.WINDOW_CLOSING) {
	    System.exit(0);
	}
    }

    public void actionPerformed(ActionEvent e) {
	final String command = e.getActionCommand();
	if (command.equals(QUIT)) {
	    System.exit(0);
	}
	else if (command.equals(SUBMIT)) {
	    Component current = pane.getSelectedComponent();
	    if (current.getName().equals("Fussers")) {
		((FussersPanel) current).submitAction();
	    }
	    else if (current.getName().equals("Student")) {
		((StudentPanel) current).submitAction();
	    }
	    else if (current.getName().equals("Vacancy")) {
		((VacancyPanel) current).submitAction();
	    }
	    else if (current.getName().equals("Lease")) {
		((LeasePanel) current).submitAction();
	    }
	    else if (current.getName().equals("House")) {
		((HousePanel) current).submitAction();
	    }
	}
	else {
	    System.out.println("Unexpected command " + command +
			       " received.");
	}
    }
}
