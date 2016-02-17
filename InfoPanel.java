import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.text.DecimalFormat;
import java.io.File;

import util.ReadTextFile;

public class InfoPanel extends JPanel implements MouseListener, KeyListener
{
    ArrayList<String> watchList = new ArrayList<String>();
    ArrayList<String> optionsList = getAllAttributes();
    String ghostText = "Search";
    JTextField search;
    Figure figure;
    JList options, watchOptions;
    String title;
    private final String indent = "        =";
    private Calculate c;
    private Information i;

    public InfoPanel(Information i, Figure f, String title)
    {
        this.i = i;
        this.title = title;
        figure = f;
        c = new Calculate(f);
        setLayout(new BorderLayout());
        setBackground(Container.THEME);

        watchOptions = new JList(watchList.toArray());
        watchOptions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        watchOptions.addMouseListener(this);
        watchOptions.setLayoutOrientation(JList.VERTICAL);
        watchOptions.setVisibleRowCount(-1);
        JScrollPane watchScroll = new JScrollPane(watchOptions); 
        watchScroll.setPreferredSize(new Dimension(250*Container.width/1366, 210*Container.height/768));

        options = new JList(refineSearch("").toArray(new String[refineSearch("").size()]));
        options.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        options.addMouseListener(this);
        options.setLayoutOrientation(JList.VERTICAL);
        options.setVisibleRowCount(-1);
        JScrollPane listScroll = new JScrollPane(options); 
        listScroll.setPreferredSize(new Dimension(250*Container.width/1366, 400*Container.height/768));

        search = new JTextField();
        search.setPreferredSize(new 
            Dimension(250*Container.width/1366,15*Container.height/768));
        search.addKeyListener(this);

        // Look how non-invasive this was
        new GhostText(search, ghostText);

        setLayout(new BorderLayout());
        add(watchScroll, BorderLayout.NORTH);
        add(search, BorderLayout.CENTER);
        add(listScroll, BorderLayout.SOUTH);
        for(Variable v : f.getVars())
        {
            String name = v.getName().toString().toLowerCase().trim();
            if(watchList.indexOf(name) >= 0)
            {
                watchList.add(name);
                watchList.add("        =" + f.getValue(name) + " " + figure.getVar(name).getUnits());
            }
        }
    }

    public String getTitle()
    {
        return title;
    }
    
    public Figure getFigure()
    {
        return figure;
    }

    public void updateOptions()
    {
        for(Variable v : figure.getVars())
        {
            boolean flag = false;
            for(String s : optionsList)
            {
                if(!v.isConstant() || v.isEnvironmental() || v.getName().toString().toLowerCase().split("-")[0].trim().equals(s.toLowerCase().split("-")[0].trim()))
                    flag = true;
            }
            if(!flag) optionsList.add(v.getName().toString());
        }
        optionsList = sorted(optionsList);
    }

    /**
     * Returns list of all possible attributes that can be applied
     * 
     * @return tmp An arraylist of attributes
     */
    public ArrayList<String> getAllAttributes()
    {
        ReadTextFile read_file = new ReadTextFile("variableNames.txt");
        ArrayList<String> tmp = new ArrayList<String>();
        String currVar = read_file.readLine();
  
        while(!read_file.EOF())
        {
            tmp.add(currVar);
            currVar = read_file.readLine();
        }
        read_file.close();
        return sorted(tmp);
    }

    public ArrayList<String> sorted(ArrayList<String> array)
    {
        String[] arr = array.toArray(new String[array.size()]);
        Arrays.sort(arr);
        return new ArrayList(Arrays.asList(arr));
    }

    /**
     * Refreshes the Watch List whenever a new variable is appended to a Figure
     */
    public void refreshWatch()
    {
        for(int i = 0; i < watchList.size()-1; i++)
        {
            for(Variable v : figure.getVars())
            {
                try{
                    if(v.getName().toString().trim().equals(watchList.get(i))) //If variable is watched
                    {
                        if(watchList.get(i+1).substring(0,1).equals(" ")) //If said variable has value
                            watchList.remove(watchList.indexOf(watchList.get(i))+1);
                        DecimalFormat df = new DecimalFormat("#.#####");
                        String value = "";
                        try{
                            value = df.format(Double.parseDouble(v.getValue()+""))+"";
                        }catch(Exception e){value = v.getValue().toString();}
                        watchList.add(watchList.indexOf(watchList.get(i))+1, indent+c.getFinalVarValue(value,"")+" "+v.getUnits());
                    }
                }catch(Exception e){e.printStackTrace();watchList.add(indent+v.getValue().toString());}
            }
        }
        watchOptions.setListData(watchList.toArray());
    }

    /**
     * Search Method to narrow attribute list based on query
     * 
     * @param query The term to be searched for
     * @return tmp The new arraylist of attributes
     */
    public ArrayList<String> refineSearch(String query)
    {
        updateOptions();
        if(query.equals(ghostText)) query = "";
        ArrayList<String> tmp = new ArrayList<String>();
        for(String str : optionsList) // Runs through all names
        {
            if(str.split(" -")[0].toLowerCase().contains(query.toLowerCase())) //If any are equal to query
            {   
                tmp.add(str.split(" -")[0]); //Add them
                // If variable has value and the value is not (the name or null)
                if(figure.getValue(str.split(" -")[0]) != null && !(figure.getValue(str.split(" -")[0]).equals(str.split(" -")[0]) || str.split(" -").length==1))
                {
                    try
                    {tmp.add(indent + figure.getValue(str.split(" -")[0]) + " " + str.split(" -")[1]);}
                    catch(Exception e){tmp.add(indent + figure.getValue(str.split(" -")[0]));e.printStackTrace();}
                }
            }
        }
        return tmp;
    }

    /**
     * @return options the attributes in the form of a JList
     */
    public JList getOptions()
    {
        return options;
    }

    /**
     * @param obj A non-InfoPanel object
     * @return obj The parameter type casted to an InfoPanel
     */
    public static InfoPanel toInfoPanel(Object obj)
    {
        return (InfoPanel)obj;
    }

    /**
     * Called to refresh the JList of attributes
     */
    public void refresh()
    {
        options.setListData(refineSearch(search.getText()).toArray());
    }

    public void keyReleased(KeyEvent e){refresh();}

    public void keyPressed(KeyEvent e){refresh();}

    public void keyTyped(KeyEvent e){}

    public void mouseExited(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseReleased(MouseEvent e){}

    public void mouseClicked(MouseEvent e){}

    public void mousePressed(MouseEvent e)
    {
        try{
            // If clicked on options and it's not a value and not already on watch list
            if(e.getSource().equals(options) && !options.getSelectedValue().toString().substring(0,1).equals(" ") && !contains(watchList, options.getSelectedValue().toString()))
            {
                String str = options.getSelectedValue().toString();
                watchList.add(str);
                if(figure.getValue(str) != null)
                    watchList.add(indent+ figure.getValue(str) + " " + figure.getVar(str).getUnits());
            }
            // If you're removing something from the watch list
            else if(e.getSource().equals(watchOptions))
            {
                String str = watchOptions.getSelectedValue().toString();
                if(!str.substring(0,1).equals(" "))
                {
                    int index  = watchList.indexOf(str);
                    watchList.remove(str);
                    watchList.remove(index);
                }
                /*if(figure.getValue(str)!=null)
                {
                    watchList.remove(indent + c.getFinalVarValue(figure.getValue(str).toString(),"") + " " + figure.getVar(str).getUnits());
                }*/
            }
            watchOptions.setListData(watchList.toArray());
        }catch(Exception ex){}//ex.printStackTrace();}
    }

    public boolean contains(ArrayList<String> arr, String str)
    {
        for(String options : arr)
        {
            if(options.equals(str)) return true;
        }
        return false;
    }
}
