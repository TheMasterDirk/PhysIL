import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphSelectionPanel extends JPanel implements MouseListener, KeyListener
{
    JFrame frame = new JFrame("Create a Graph");
    JList variables = new JList();
    JList figures = new JList();
    JTextField search = new JTextField();
    JPanel variablePanel = new JPanel();
    Button ok;
    String ghostText = "Search";
    ArrayList<Figure> figuresOnField = Container.figuresOnField;
    Map<String, ImageIcon> imageMap = createImageMap();
    ArrayList<String> item = new ArrayList<String>();

    public GraphSelectionPanel()
    {
        // FRAME
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setSize(400,400);

        variables.setPreferredSize(new Dimension(180,400));
        variables.setVisible(true);

        String[] figArray = new String[figuresOnField.size()];
        for(int i = 0; i < figArray.length; i++)
        {
            figArray[i] = (i+1)+"";
        }
        figures = new JList(figArray);
        figures.setCellRenderer(new ListRenderer());
        figures.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        figures.addMouseListener(this);
        figures.setLayoutOrientation(JList.VERTICAL);
        figures.setVisibleRowCount(-1);
        JScrollPane figScroll = new JScrollPane(figures); 
        figScroll.setPreferredSize(new Dimension(200,200));
        ok = new Button("GRAPH");
        ok.setPreferredSize(new Dimension(200,30));
        ok.addMouseListener(this); // Why is this necessary?
        variables = new JList();
        variables.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        variables.addMouseListener(this);
        variables.setLayoutOrientation(JList.VERTICAL);
        variables.setVisibleRowCount(-1);
        JScrollPane listScroll = new JScrollPane(variables); 
        listScroll.setPreferredSize(new Dimension(200,200));
        search = new JTextField();
        search.setPreferredSize(new Dimension(200,30));
        search.addKeyListener(this);

        // VAR PANEL
        variablePanel.setLayout(new BorderLayout());
        variablePanel.add(search, BorderLayout.NORTH);
        variablePanel.add(listScroll, BorderLayout.CENTER);
        variablePanel.add(ok, BorderLayout.SOUTH);
        variablePanel.setVisible(true);
        variablePanel.setPreferredSize(new Dimension(180,400));

        // Look how non-invasive this was
        new GhostText(search, ghostText);

        // LAYOUT
        setLayout(new BorderLayout());
        add(figScroll, BorderLayout.WEST);
        add(variablePanel, BorderLayout.EAST);
        frame.add(this);
        setVisible(true);
    }

    /**
     * Search Method to narrow attribute list based on query
     * 
     * @param query The term to be searched for
     * @return tmp The new arraylist of attributes
     */
    public ArrayList<String> refineSearch(String query)
    {
        if(query.equals(ghostText)) query = "";
        ArrayList<String> tmp = new ArrayList<String>();

        for(String str : item) // Runs through all names
        {
            if(str.contains(query.toLowerCase())) //If any are equal to query
                tmp.add(str.split(" -")[0]); //Add them
        }
        return tmp;
    }

    /**
     * Type casts an Image to a BufferedImage
     * 
     * @param img the Image object
     * @return img the BufferedImage object
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if(img instanceof BufferedImage)
            return (BufferedImage)img;
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    public void mouseClicked(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseExited(MouseEvent e){}

    public void mousePressed(MouseEvent e)
    {
        if(e.getSource().equals(figures)){
            item = figuresOnField.get(figures.getSelectedIndex()).getNumericalAttributes();
            variables.setListData(item.toArray(new String[item.size()]));
            ArrayList<String> tmp = refineSearch(search.getText());
            variables.setListData(tmp.toArray(new String[tmp.size()]));
        }
    }

    public void mouseReleased(MouseEvent e)
    {
        if(e.getSource().equals(ok))
        {
            frame.dispose();
            try{
                Container.addGraph(new GraphPanel(Container.t.timePanel,figuresOnField.get(figures.getSelectedIndex()), figuresOnField.get(figures.getSelectedIndex()).getVar(variables.getSelectedValue().toString())));
            }catch(Exception e2){}
        }
    }

    public void keyPressed(KeyEvent e)
    {
        ArrayList<String> tmp = refineSearch(search.getText());
        variables.setListData(tmp.toArray(new String[tmp.size()]));
    }

    public void keyReleased(KeyEvent e)
    {
        ArrayList<String> tmp = refineSearch(search.getText());
        variables.setListData(tmp.toArray(new String[tmp.size()]));
    }

    public void keyTyped(KeyEvent e)
    {
        ArrayList<String> tmp = refineSearch(search.getText());
        variables.setListData(tmp.toArray(new String[tmp.size()]));
    }

    private Map<String, ImageIcon> createImageMap() 
    {
        Map<String, ImageIcon> map = new HashMap<>();
        int i = 1;
        for(Figure f : figuresOnField)
        {
            map.put(i+"", new ImageIcon(f.getImage()));
            i++;
        }   
        return map;
    }

    public class ListRenderer extends DefaultListCellRenderer 
    {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
        {
            JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setIcon(imageMap.get((String)value));
            label.setText("");
            return label;
        }
    }

}