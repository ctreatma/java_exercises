import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class Browser extends JPanel implements ActionListener, HyperlinkListener {
    private static final long serialVersionUID = 1L;
    private JTextField urlBox;
    private JTextArea headerWindow;
    private JEditorPane browserWindow;
    
    public Browser() {        
        super(new GridBagLayout());
        
        // Set preferred size so window pops up
        // at a reasonable size
        setPreferredSize(new Dimension(800,600));
        
        urlBox = new JTextField();
        
        JButton goBtn = new JButton("Go");
        goBtn.addActionListener(this);

        headerWindow = new JTextArea();
        headerWindow.setEditable(false);
        JScrollPane headerScrollPane = new JScrollPane(headerWindow);

        browserWindow = new JEditorPane();
        browserWindow.setEditable(false);
        browserWindow.addHyperlinkListener(this);
        JScrollPane browserScrollPane = new JScrollPane(browserWindow);

        GridBagConstraints c = new GridBagConstraints();

        // Set constraints so urlBox occupies
        // top left of window and top 1%
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.9;
        c.weighty = 0.01;
        c.gridx = 0;
        c.gridy = 0;
        add(urlBox, c);

        // Set constraints so urlBox occupies
        // top right of window and top 1%
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.1;
        c.weighty = 0.01;
        c.gridx = 1;
        c.gridy = 0;
        add(goBtn,c);

        // Set constraints so headerScrollPane
        // occupies middle 39% of window
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 2;
        c.weighty = 0.38;
        c.gridx = 0;
        c.gridy = 1;
        add(headerScrollPane, c);

        // Set constraints so browserScrollPane
        // occupies bottom 60% of window
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 2;
        c.weighty = 0.6;
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.PAGE_END;
        add(browserScrollPane, c);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        Browser browserPane = new Browser();
        browserPane.setOpaque(true);
        
        JFrame frame = new JFrame("Browser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setContentPane(browserPane);
        
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent evt) {
        // Only care if the user clicked a link
        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            urlBox.setText(evt.getURL().toString());
            updatePage();
        }
    }
    @Override
    public void actionPerformed(ActionEvent evt) {
        updatePage();
    }
    
    private void updatePage() {
        try {
            URL url = new URL(urlBox.getText());
            URLConnection c = url.openConnection();
            // Print out status and header fields
            StringBuffer header = new StringBuffer();
            header.append(c.getHeaderField(0) + "\n");
            String value = "";
            String key = "";
            int n = 1;
            while (true) {
                value = c.getHeaderField(n);
                if (value == null) break;
                key = c.getHeaderFieldKey(n);
                header.append(key + ": " + value + "\n");
                n++;
            }
            headerWindow.setText(header.toString());
            browserWindow.setPage(url);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
