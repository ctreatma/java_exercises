package toDo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

/**
 * ToDoManager provides an interface for interacting with a
 * ToDoHandler in order to modify a list of tasks maintained
 * in an XML document.
 * 
 * @author ctreatma
 * @version 1.0
 *
 */
public class ToDoManager extends JPanel implements ActionListener, TableModelListener {
    private static final long serialVersionUID = 1L;
    private static final String addCommand = "add";
    private static final String removeCommand = "remove";
    private static final String removeCompletedCommand = "removeCompleted";
    private static final int descriptionColumn = 0;
    private static final int completedColumn = 1;
    
    private JTable table;
    private ToDoHandler toDoHandler;

    /**
     * Creates a ToDoManager object, which provides the interface
     * and associated handlers for interacting with a ToDoHandler.
     * 
     */
    public ToDoManager() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        toDoHandler = new ToDoHandler();
        table = new JTable(new DefaultTableModel(toDoHandler.getToDoElements(),toDoHandler.getToDoColumns()));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.getModel().addTableModelListener(this);
        add(scrollPane);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
        
        JButton add, remove, removeCompleted;
        JPanel buttonPanel = new JPanel();
        
        add = new JButton("Create New To Do");
        remove = new JButton("Remove Selected To Do");
        removeCompleted = new JButton("Remove Completed To Dos");
        
        add.setActionCommand(addCommand);
        remove.setActionCommand(removeCommand);
        removeCompleted.setActionCommand(removeCompletedCommand);
        
        add.addActionListener(this);
        remove.addActionListener(this);
        removeCompleted.addActionListener(this);
        
        buttonPanel.add(add);
        buttonPanel.add(remove);
        buttonPanel.add(removeCompleted);
        
        inputPanel.add(buttonPanel);
        
        add(inputPanel);
    }
    
    /**
     * Handles events triggered by the user clicking on a button
     * in the ToDoManager interface.  Adds or deletes tasks in
     * the ToDoHandler depending on which button was pressed.
     *
     * @param  e  the event triggered by the user
     * 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (addCommand.equals(e.getActionCommand())) {
            String description = JOptionPane.showInputDialog(this, "What do you need to do?");
            model.addRow(new Object[]{description,false});
            toDoHandler.addToDoItem(description, false);
        } else if (removeCommand.equals(e.getActionCommand())) {
            int row = table.getSelectedRow();
            toDoHandler.removeToDoItem(row);
            model.removeRow(row);
        } else if (removeCompletedCommand.equals(e.getActionCommand())) {
            int numRows = table.getRowCount();
            for (int i = numRows - 1; i >= 0;--i) {
                Boolean completed = Boolean.valueOf(model.getValueAt(i,completedColumn).toString());
                if (completed.equals(Boolean.TRUE)) {
                    toDoHandler.removeToDoItem(i);
                    model.removeRow(i);
                }
            }
        }
    }

    /**
     * Handles events triggered by the user interacting with
     * the display of existing tasks.  Modifies tasks in the
     * ToDoHandler.
     *
     * @param  e  the event triggered by the user
     *
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            TableModel model = table.getModel();
            String description = model.getValueAt(row, descriptionColumn).toString();
            Boolean completed = Boolean.valueOf(model.getValueAt(row,completedColumn).toString());
            toDoHandler.modifyToDoItem(row, description, completed);
        }
    }

    /**
     * @param args
     */   
    public static void main(String[] args) {
        ToDoManager managerPane = new ToDoManager();
        managerPane.setOpaque(true);
        
        JFrame frame = new JFrame("ToDoManager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setContentPane(managerPane);

        frame.pack();
        frame.setVisible(true);
    }
}
