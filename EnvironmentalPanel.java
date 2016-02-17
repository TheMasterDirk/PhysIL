import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Dimension;

import java.awt.BorderLayout;

import java.util.ArrayList;

public class EnvironmentalPanel extends JPanel
{
    JFrame frame = new JFrame();
    JTextField gVal = new JTextField(), gAng = new JTextField(), eVal = new JTextField(), eAng = new JTextField(), mVal = new JTextField(), mAng = new JTextField();

    ArrayList<Double> curVal = new ArrayList<Double>();
    ArrayList<Double> oldVal = new ArrayList<Double>();
    public EnvironmentalPanel()
    {
        for(int i = 0; i < 6; i++)
            curVal.add(0.0);
        setLayout(new BorderLayout());
        addField("Gravitational Field", BorderLayout.NORTH, "0", gVal, gAng);
        addField("Electric Field         ", BorderLayout.CENTER, "0", eVal, eAng);
        addField("Magnetic Field      ", BorderLayout.SOUTH, "0", mVal, mAng);

        frame.add(this);
    }

    /**
     * @return {gVal, gAng, eVal, eAng, mVal, mAng}
     */
    public ArrayList<Double> getValues()
    {
        oldVal = curVal;
        ArrayList<Double> vars = new ArrayList<Double>();
        for(int i = 0; i < 6; i++) vars.add(0.0);
        if(Variable.canBeDouble(gVal.getText()) && Variable.canBeDouble(gAng.getText()))
        {
            vars.set(0,Double.parseDouble(gVal.getText()));
            vars.set(1,Double.parseDouble(gAng.getText()));
        }
        if(Variable.canBeDouble(eVal.getText()) && Variable.canBeDouble(eAng.getText()))
        {
            vars.set(2,Double.parseDouble(eVal.getText()));
            vars.set(3,Double.parseDouble(eAng.getText()));
        }
        if(Variable.canBeDouble(mVal.getText()) && Variable.canBeDouble(mAng.getText()))
        {
            vars.set(4,Double.parseDouble(mVal.getText()));
            vars.set(5,Double.parseDouble(mAng.getText()));
        }
        curVal = vars;
        return vars;
    }
    
    public ArrayList<Double> getOldValues()
    {
        return oldVal;
    }

    public void addField(String name, String position, String defaultValue, JTextField v, JTextField a)
    {
        JPanel p = new JPanel();
        JLabel l = new JLabel(name);
        v.setPreferredSize(new Dimension(100,Container.height/24));
        p.setPreferredSize(new Dimension(Container.width,Container.height/24));
        p.setLayout(new BorderLayout());
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        new GhostText(v, "Value");
        p.add(a, BorderLayout.EAST);
        new GhostText(a, "Angle");
        add(p, position);
    }
}