import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class VariableFrame extends JFrame implements KeyListener, MouseListener
{
    Figure f;
    Calculate c;
    transient GroupLayout gl;
    JTextField search, value, angle;
    JList options;
    ArrayList<String> optionsList = new ArrayList<String>();
    ArrayList<String> changeValues = new ArrayList<String>();
    ArrayList<Double> changeAngles = new ArrayList<Double>();

    ArrayList<String> tmpVals = new ArrayList<String>();
    ArrayList<Double> tmpAngles = new ArrayList<Double>();
    JRadioButton init, fin, none;
    String btnName;
    JRadioButton selected;
    String modifier = "";

    public VariableFrame(Figure f, ArrayList<String> varOptions, String btnName)
    {
        super("Variable");
        c = new Calculate(f);
        this.btnName = btnName;
        this.f = f;
        addKeyListener(this);
        if(btnName.equals("Change"))
        {
            for(String var: varOptions)
            {
                for(Variable oldVar: f.getVar(var).getAllComponents())
                {
                    optionsList.add(oldVar.getName()+"");
                    changeValues.add(oldVar.getValue()+"");
                    changeAngles.add(oldVar.getAngle());
                }
            }
        }
        else
            optionsList = sorted(varOptions);
        // Components
        // JRadioButtons
        init = new JRadioButton("Initial", false);
        init.addMouseListener(this);
        fin = new JRadioButton("Final", false);
        fin.addMouseListener(this);
        none = new JRadioButton("None", true);
        none.addMouseListener(this);

        // Text Fields
        search = new JTextField();
        search.setPreferredSize(new Dimension(250,30));
        search.addKeyListener(this);
        //new GhostText(search, "Search");
        value = new JTextField();
        value.setPreferredSize(new Dimension(70,30));
        //new GhostText(value, "Value");
        angle = new JTextField();
        angle.setPreferredSize(new Dimension(70,30));
        //new GhostText(angle, "Angle(Optional)");
        // Labels
        JLabel sLabel = new JLabel("Search :");
        JLabel vLabel = new JLabel("Value :");
        JLabel aLabel = new JLabel("(Optional) Angle :");
        // Panels
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(5,1));
        JPanel sPanel = new JPanel();
        JPanel vPanel = new JPanel();
        JPanel aPanel = new JPanel();
        sPanel.add(sLabel);
        sPanel.add(search);
        vPanel.add(vLabel);
        vPanel.add(value);
        aPanel.add(aLabel);
        aPanel.add(angle);

        //Disables these buttons when users are changing variables
        if(!btnName.equals("Change"))
        {
            radioPanel.add(init);
            radioPanel.add(fin);
            radioPanel.add(none);
        }

        options = new JList(varOptions.toArray(new String[varOptions.size()]));
        options.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        options.addMouseListener(this);
        options.setLayoutOrientation(JList.VERTICAL);
        options.setVisibleRowCount(-1);
        JScrollPane listScroll = new JScrollPane(options); 
        listScroll.setPreferredSize(new Dimension(250, 300));
        // Button Return
        JButton ok = new JButton(btnName);
        ok.addMouseListener(this);
        radioPanel.add(ok);
        // Layout
        gl = new GroupLayout(this.getContentPane());
        gl.setAutoCreateGaps(true);
        GroupLayout.SequentialGroup hGroup = gl.createSequentialGroup();
        hGroup.addGroup(gl.createParallelGroup().addComponent(sPanel).addComponent(listScroll));
        hGroup.addGroup(gl.createParallelGroup().addComponent(vPanel).addComponent(radioPanel).addComponent(aPanel));
        gl.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = gl.createSequentialGroup();
        vGroup.addGroup(gl.createParallelGroup().addComponent(sPanel).addComponent(vPanel));
        vGroup.addGroup(gl.createParallelGroup().addComponent(aPanel));
        vGroup.addGroup(gl.createParallelGroup().addComponent(listScroll).addComponent(radioPanel));
        gl.setVerticalGroup(vGroup);

        getContentPane().setLayout(gl);
        setSize(550,300);
        options.setListData(refineSearch(search.getText()).toArray());
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
        if(query.equals("Search")) query = "";
        ArrayList<String> tmp = new ArrayList<String>();
        ArrayList<String> vals = new ArrayList<String>();
        ArrayList<Double> angs = new ArrayList<Double>();
        for(int index = 0; index < optionsList.size(); index++)
        {
            if(optionsList.get(index).split(" -")[0].toLowerCase().contains(query.toLowerCase()))
            {
                tmp.add(optionsList.get(index).split(" -")[0]);
                if(btnName.equals("Change"))
                {
                    vals.add(changeValues.get(index));
                    angs.add(changeAngles.get(index));
                }
            }
        }
        /*
        for(String str : optionsList)
        {
        if(str.split(" -")[0].toLowerCase().contains(query.toLowerCase()))
        {   
        tmp.add(str.split(" -")[0]);
        }
        }*/
        tmpVals = vals;
        tmpAngles = angs;
        return tmp;
    }

    /**
     * Parses an object to a JRadioButton
     * 
     * @param obj the non-JRB object
     * @return the JRadioButton
     */
    public JRadioButton parseJRB(Object obj){return (JRadioButton)obj;}

    public void keyReleased(KeyEvent e)
    {
        options.setListData(refineSearch(search.getText()).toArray());
        if(e.getKeyCode() == KeyEvent.VK_ENTER)
            handleEnter();
    }

    public void handleEnter()
    {
        String nm = "";
        boolean dispose = true;
        if(options.getSelectedValue() == null)
        {
            if(options.getModel().getSize() == 1)
                nm = options.getModel().getElementAt(0).toString();
            else
                nm = search.getText();
        }
        else
        {
            nm = options.getSelectedValue().toString().trim();
        }
        if(btnName.equals("Change"))
        {
            if(options.getSelectedValue() != null)
            {
                String oldValueText = tmpVals.get(options.getSelectedIndex());
                String newValueText = autoCorrect(value.getText());

                Double oldAngle = tmpAngles.get(options.getSelectedIndex());
                Double newAngle = oldAngle;
                try{
                    newAngle = Double.parseDouble(angle.getText());
                }catch(Exception e3){}
                //Cannot change variables from numbers to things like "time"
                f.getVar(nm).dynamicallyChange(oldValueText, oldAngle, newValueText,newAngle);
                f.getVar(nm).setConstant(true); //???
                Container.info.updateTabs(f);
                c.combineVariables();
            }
            else
            {
                dispose = false;
                JOptionPane.showMessageDialog(this, "Please select a variable to change."); 
            }
        }
        else if(btnName.equals("Add") && value.getText().length()>0)
        {
            //When we add help boxes, explain to user that they cannot make variables like "mass3" or "q1"
            String newValueText = autoCorrect(value.getText());
            String abrev = nm.substring(0,1);

            Double angleV = null;
            try{
                angleV = Double.parseDouble(angle.getText()); 
            }catch(Exception e3){}

            // Tests if modifier selected
            if(selected != null)
            {
                abrev += selected.getText().substring(0,1).toLowerCase();
                if(modifier.equals("Final") && f.getVar(nm+"Initial") == null)
                {
                    f.addValue(nm+"Initial -" + findUnits(nm), abrev, nm+"Initial", angleV);
                    f.getVar(nm+"Initial").setConstant(true);
                }
                else if(modifier.equals("Initial") && f.getVar(nm+"Final") == null)
                {
                    f.addValue(nm+"Final -" + findUnits(nm), abrev, nm+"Final", angleV);
                    f.getVar(nm+"Final").setConstant(false);
                }
                nm += modifier;
            }

            if(f.getVar(nm) == null)
                f.addValue(nm+" -" + findUnits(nm), "1", newValueText, angleV);
            else if(f.getVar(nm).isConstant() || f.getVar(nm).isSolved())
                f.addValue(nm+" -" + findUnits(nm), f.getVar(nm).getAllComponents().size()+1+"", newValueText, angleV);
            else if(!f.getVar(nm).isConstant())
            {
                f.getVar(nm).setVariableName("1");
                f.getVar(nm).setVandA(newValueText,angleV);
                f.getVar(nm).setUnits(findUnits(nm));
            }
            f.getVar(nm).setConstant(true);
            c.combineVariables();
            Container.info.updateTabs(f);

            // Adding initial variable
            if(selected == null && f.getVar(nm+"Initial") == null)
            {
                f.addValue(nm+"Initial -" + findUnits(nm), abrev, f.evaluateAt(f.getVar(nm), 0) ,angleV);
                f.getVar(nm+"Initial").setConstant(true);
            }
        }
        else if(btnName.equals("Add"))
        {
            JOptionPane.showMessageDialog(this, "Please give your variable a value."); 
            dispose = false;
        }
        else if(btnName.equals("Delete"))
        {
            f.getVars().remove(f.getVar(nm));
        }
        if(dispose)
            dispose();
    }

    public void keyPressed(KeyEvent e){options.setListData(refineSearch(search.getText()).toArray());}

    public void keyTyped(KeyEvent e){}

    public void mouseExited(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseReleased(MouseEvent e)
    {
        String nm = "";
        if(e.getSource() instanceof JRadioButton)
        {
            init.setSelected(false);
            fin.setSelected(false);
            none.setSelected(false);
            selected = parseJRB(e.getSource());
            selected.setSelected(true);
            modifier = selected.getText();
            if(modifier.equals("None")) selected = null;
        }
        else if(e.getSource() instanceof JButton)
        {
            handleEnter();
        }
        else if(e.getSource() instanceof JList)
        {
            try{
                search.setText(options.getSelectedValue().toString());
                if(btnName.equals("Change"))
                {
                    if(!f.getValue(options.getSelectedValue().toString()).toString().trim().equals(options.getSelectedValue().toString().trim()))
                    {
                        value.setText(tmpVals.get(options.getSelectedIndex()));
                        if(f.getVar(options.getSelectedValue().toString()).getAngle()!=null)
                            angle.setText(tmpAngles.get(options.getSelectedIndex())+"");
                        else
                            angle.setText("");
                    }
                    else
                        value.setText("");
                }
                else
                    value.setText("");
            }catch(Exception e2){}
        }
    }

    public ArrayList<String> sorted(ArrayList<String> array)
    {
        String[] arr = array.toArray(new String[array.size()]);
        Arrays.sort(arr);
        return new ArrayList(Arrays.asList(arr));
    }

    /**
     * Autocorrects missing *s, i.e. autoCorrect("3m+22") returns "3*m+22"
     * 
     * @param obj the String equation
     * @return the fixed string equation
     */
    public String autoCorrect(String obj)
    {
        for(int i = 0; i < obj.length()-1; i++)
        {
            char c = obj.toCharArray()[i];
            char c2 = obj.toCharArray()[i+1];
            if((Character.isLetter(c) && Character.isDigit(c2)) || (Character.isLetter(c2) && Character.isDigit(c)))
            {
                obj = obj.substring(0,i+1) + "*" + obj.substring(i+1);
            }
        }
        return obj;
    }

    /**
     * @param name the Name of a variable
     * @return the units of said variable
     */
    public String findUnits(String name)
    {
        try{
            File f = new File("variableNames.txt");
            Scanner s = new Scanner(f);
            while(s.hasNextLine())
            {
                String[] n = s.nextLine().split(" -");
                try{
                    if(n[0].equals(name))
                        return n[1];
                }catch(Exception e){e.printStackTrace();return "";}
            }
        }catch(Exception e){e.printStackTrace();}
        return "";
    }

    public void mouseClicked(MouseEvent e){}

    public void mousePressed(MouseEvent e){}
}