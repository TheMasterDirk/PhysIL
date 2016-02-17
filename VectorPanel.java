import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.BorderLayout;
import java.util.ArrayList;

public class VectorPanel extends JPanel implements MouseListener, KeyListener
{
    JFrame frame = new JFrame("Select A Vector");
    Figure f;
    JList variables = new JList();
    Button ok;
    JTextField search = new JTextField();
    ArrayList<String> optionsList;

    public VectorPanel(Figure f)
    {
        this.f = f;
        optionsList = f.getAllVectorAttributes();
        // FRAME
        frame.setResizable(false);
        frame.setVisible(true);

        setVisible(true);

        ok = new Button("Select");
        ok.setPreferredSize(new Dimension(200,30));
        ok.addMouseListener(this); // Why is this necessary?
        variables.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        variables.addMouseListener(this);
        variables.setLayoutOrientation(JList.VERTICAL);
        variables.setVisibleRowCount(-1);
        JScrollPane listScroll = new JScrollPane(variables); 
        listScroll.setPreferredSize(new Dimension(200,150));
        search = new JTextField();
        search.setPreferredSize(new Dimension(200,30));
        search.addKeyListener(this);

        // PANEL
        setLayout(new BorderLayout());
        add(search, BorderLayout.NORTH);
        add(listScroll, BorderLayout.CENTER);
        add(ok, BorderLayout.SOUTH);

        // Look how non-invasive this was
        new GhostText(search, "Search");

        // LAYOUT
        frame.add(this);
        frame.pack();
        setVisible(true);
        variables.setListData(refineSearch(search.getText()).toArray());
    }

    /**
     * Search Method to narrow attribute list based on query
     * 
     * @param query The term to be searched for
     * @return tmp The new arraylist of attributes
     */
    public ArrayList<String> refineSearch(String query)
    {
        if(query.equals("Search")) query = "";
        ArrayList<String> tmp = new ArrayList<String>();
        for(String str : optionsList)
        {
            if(str.split(" -")[0].toLowerCase().contains(query.toLowerCase()))
            {   
                tmp.add(str.split(" -")[0]);
            }
        }
        return tmp;
    }

    public void mouseExited(MouseEvent e){}

    public void mouseClicked(MouseEvent e){}

    public void mousePressed(MouseEvent e){}

    public void mouseReleased(MouseEvent e)
    {
        try{
            if(e.getSource() instanceof JButton)
            {
                f.drawVectors(variables.getSelectedValue().toString());
                frame.dispose();
            }
            else
            {
                search.setText(variables.getSelectedValue().toString());
            }
        }catch(Exception ex){ex.printStackTrace();}
    }

    public void mouseEntered(MouseEvent e){}

    public void keyPressed(KeyEvent e){variables.setListData(refineSearch(search.getText()).toArray());}

    public void keyTyped(KeyEvent e){variables.setListData(refineSearch(search.getText()).toArray());}

    public void keyReleased(KeyEvent e){variables.setListData(refineSearch(search.getText()).toArray());}
}