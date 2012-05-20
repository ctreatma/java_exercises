package toDo;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ToDoManager provides an interface for interacting with a
 * ToDoHandler in order to modify a list of tasks maintained
 * in an XML document.
 * 
 * @author ctreatma
 * @version 1.0
 *
 */
public class ToDoManager extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String addCommand = "add";
    private static final String removeCommand = "remove";
    private static final String removeCompletedCommand = "remove completed";
    private static final String updateCommand = "update";
    private static final String commandParam = "command";
    private static final String descriptionParam = "description";
    private static final String rowParam = "row";
    private static final String completedParam = "completed";
    
    private ToDoHandler toDoHandler;

    public void init(ServletConfig config) throws ServletException {
        try {
            ServletContext context = config.getServletContext();
            toDoHandler = new ToDoHandler(context.getRealPath("/"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        synchronized(toDoHandler) {
            String command = request.getParameter(commandParam);

            if (command != null) {
                if (removeCompletedCommand.equalsIgnoreCase(command)) {
                    toDoHandler.removeCompletedItems();
                } else if (removeCommand.equalsIgnoreCase(command)) {
                    int row = Integer.valueOf(request.getParameter(rowParam));
                    toDoHandler.removeToDoItem(row);
                } else if (addCommand.equalsIgnoreCase(command)) {
                    String description = request.getParameter(descriptionParam);
                    toDoHandler.addToDoItem(description, false);
                } else if (updateCommand.equalsIgnoreCase(command)) {
                    int row = Integer.valueOf(request.getParameter(rowParam));
                    boolean completed = (request.getParameter(completedParam) != null);
                    String description = request.getParameter(descriptionParam);
                    toDoHandler.modifyToDoItem(row, description, completed);
                }
            }

            request.setAttribute("toDos", toDoHandler.getToDoElements());
            RequestDispatcher view = request.getRequestDispatcher("index.jsp");
            view.forward(request, response);
        }
    }
}
