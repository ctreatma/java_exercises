package toDo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;


/**
 * ToDoHandler acts as a wrapper around a DOM Document
 * object containing a list of tasks and their status
 * (completed or not).  ToDoHandler also provides
 * methods for adding, editing, and removing tasks
 * contained in the Document.
 * 
 * @author ctreatma
 * @version 1.0
 *
 */
public class ToDoHandler {
    private static final String toDoFilename = "toDo.xml";
    private static final String toDoDocType = "toDo.dtd";
    private static final String toDoElement = "toDo";
    private static final String toDoRootElement = "toDos";
    private static final String descriptionElement = "description";
    private static final String completedAttr = "completed";
    private static final String[] toDoColumns = {descriptionElement, completedAttr};
    
    private Document toDoFile;
    private String realPath;
    
    /**
     * Creates a new ToDoHandler object and loads the XML document
     * containing the To Dos into the ToDoHandler's Document.
     *
     */
    public ToDoHandler(String realPath) {
        this.realPath = realPath;
        initializeToDoList();
    }
    
    /**
     * Returns a 2D array of Objects.  Each row of the array returned is
     * an array containing the [description, completed] values for a toDo
     * in the Document contained in the ToDoHandler. 
     *
     * @return      a 2D array of the property values for all toDo elements
     * 
     */
    public Object[][] getToDoElements() {
        ArrayList<ArrayList<Object>> toDoArrayList = getToDoElementsList();
        
        ArrayList<Object[]> transform = new ArrayList<Object[]>();
        for (int i = 0; i < toDoArrayList.size(); i++) {
            ArrayList<Object> element = toDoArrayList.get(i);
            transform.add(element.toArray(new Object[toDoColumns.length]));
        }
        
        if (transform.size() == 0) {
            return new Object[0][toDoColumns.length];
        }
        else {
            return transform.toArray(new Object[transform.size()][toDoColumns.length]);
        }
    }
    
    /**
     * Returns a 2D ArrayList of Objects.  Each inner ArrayList
     * contains the [description, completed] values for a toDo
     * in the Document contained in the ToDoHandler. 
     *
     * @return      a 2D ArrayList of the property values for all toDo elements
     * 
     */
    public ArrayList<ArrayList<Object>> getToDoElementsList() {
        NodeList toDos = toDoFile.getElementsByTagName(toDoElement);
        ArrayList<ArrayList<Object>> toDoArrayList = new ArrayList<ArrayList<Object>>();
        for (int i = 0; i < toDos.getLength(); i++) {
            ArrayList<Object> toDoProperties = getToDoNodeProperties(toDos.item(i));
            toDoArrayList.add(toDoProperties);
        }
        
        return toDoArrayList;
    }
    
    /**
     * Returns an array of Objects that are strings representing the
     * names of the properties that a toDo element can have.
     *
     * @return      an array of Objects representing column names
     *
     */
    public Object[] getToDoColumns() {
        return toDoColumns;
    }
    
    /**
     * Adds a task with the specified property values to the
     * Document contained in the ToDoHandler.
     *
     * @param  description  a description of the task to be added
     * @param  completed    a boolean indicating whether the task is finished
     *
     */
    public void addToDoItem(String description, boolean completed) {
        Node newNode = createToDoNode(description, completed);
        
        toDoFile.getDocumentElement().appendChild(newNode); 
        saveToDoList();  
    }
    
    /**
     * Removes a specific task from the Document contained in the
     * ToDoHandler
     *
     * @param  index  the position of the task to be removed
     * 
     */
    public void removeToDoItem(int index) {
        NodeList toDos = toDoFile.getElementsByTagName(toDoElement);
        Node toDelete = toDos.item(index);
        toDelete.getParentNode().removeChild(toDelete);
        saveToDoList();
    }
    
    /**
     * Replaces the specified task with a new task that has the
     * specified property values. 
     *
     * @param  index  the position of the task to be replaced
     * @param  description  a description of the task to be added
     * @param  completed    a boolean indicating whether the task is finished
     *
     */
    public void modifyToDoItem(int index, String description, boolean completed) {
        Node newNode = createToDoNode(description, completed);
        
        NodeList toDos = toDoFile.getElementsByTagName(toDoElement);
        Node toDelete = toDos.item(index);
        toDelete.getParentNode().replaceChild(newNode, toDelete);
        saveToDoList();
    }
    
    public void removeCompletedItems() {
        NodeList toDos = toDoFile.getElementsByTagName(toDoElement);
        int numRows = toDos.getLength();
        for (int i = numRows - 1; i >= 0;--i) {
            Node toDo = toDos.item(i);
            Boolean completed = new Boolean(toDo.getAttributes().item(0).getNodeValue());
            if (completed.equals(Boolean.TRUE)) {
                toDo.getParentNode().removeChild(toDo);
            }
        }
        saveToDoList();
    }

    private Node createToDoNode(String description, boolean completed) {
        Element newToDo = toDoFile.createElement(toDoElement);
        newToDo.setAttribute(completedAttr, Boolean.toString(completed));
        Node newDescription = toDoFile.createElement(descriptionElement);
        newDescription.appendChild(toDoFile.createTextNode(description));
        newToDo.appendChild(newDescription);
        return newToDo;
    }
    
    private ArrayList<Object> getToDoNodeProperties(Node toDo) {
        ArrayList<Object> toDoProperties = new ArrayList<Object>();
        NodeList children = toDo.getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
            if (children.item(j).getNodeType() != Node.TEXT_NODE) {
                NodeList grandChildren = children.item(j).getChildNodes();
                String value = grandChildren.item(0).getNodeValue();
                
                toDoProperties.add(value);
            }
        }
        NamedNodeMap attributes = toDo.getAttributes();
        for (int j = 0; j < attributes.getLength(); j++) {
            Boolean value = new Boolean(attributes.item(j).getNodeValue());
            
            toDoProperties.add(value);
        }
        
        return toDoProperties;
    }
    
    private void initializeToDoList() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            try {
                toDoFile = builder.parse(realPath + toDoFilename);
            }
            catch (FileNotFoundException ex) {
                toDoFile = builder.newDocument();
                toDoFile.appendChild(toDoFile.createElement(toDoRootElement));
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        } 
    }
    
    private void saveToDoList() {
        try {            
            Source source = new DOMSource(toDoFile);
    
            File file = new File(realPath + toDoFilename);
            Result result = new StreamResult(file);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, toDoDocType);
            
            transformer.transform(source, result);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
