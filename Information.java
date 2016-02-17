import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.Point;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class Information extends JTabbedPane implements MouseListener
{
    public int tabWidth = 0;
    private int delCount = 0;
    Popup popup;

    /**
     * Creates a new Tab for a Figure
     * 
     * @param f The new Figure
     */
    public void createTab(Figure f)
    {
        addMouseListener(this);
        InfoPanel panel = new InfoPanel(this, f, "F"+(getTabCount()+1));
        f.setTabValue(getTabCount());
        panel.setPreferredSize(new Dimension(getWidth(), getHeight()));
        addTab("F"+(getTabCount()+1), panel); // Second Param is possible icon
        //setMnemonicAt(getTabCount(), KeyEvent.VK_1+getTabCount()); <-- This causes figures to disappear? I don't know.
        updateTabs(f);
    }

    /**
     * Deletes a specified tab for a Figure
     * 
     * @param f The old Figure
     */
    public void deleteTab(Figure f)
    {
        remove(f.getTabValue());
    }

    /**
     * Updates all Figure tabs by setting the title and refreshing both lists
     */
    public void updateTabs(Figure f)
    {
        updateFigure(f);
        for(int i = 0; i < getTabCount(); i++)
        {
            setTitleAt(i, "Fig"+(i+1));
            InfoPanel.toInfoPanel(getComponentAt(i)).refresh();
            InfoPanel.toInfoPanel(getComponentAt(i)).refreshWatch();
        }
    }

    public void updateFigure(Figure f)
    {
        Calculate calc = new Calculate(f);
        calc.combineVariables();
        for(Variable var : f.getVars())
        {
            if(!(var.isEnvironmental() || var.isConstant()))
            {
                String varName = var.getName().toString();
                calc.setUnknownVariable(varName);
                calc.solve();
            }
        }
    }

    /**
     * Parses an object to an Information data type
     * 
     * @param obj The non-Information object
     * @return The Information version of the parameter
     */
    public static Information parseInformation(Object obj)
    {
        return (Information)obj;
    }

    public void mouseExited(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseReleased(MouseEvent e){}

    public void mouseClicked(MouseEvent e)
    {}

    public void mousePressed(MouseEvent e){}
}