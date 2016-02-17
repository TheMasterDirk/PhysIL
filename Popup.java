import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.Point;
import javax.swing.border.BevelBorder;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Popup extends JPopupMenu
{
    Object obj;
    Point p;
    public Popup(Object obj, ArrayList<Variable> variables, String[] options, Point p)
    {
        Container.appendPopup(this);
        this.obj = obj;
        this.p = p;
        ActionListener menuListener = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setVisible(false);
                }
            };
        if(variables != null)
        {
            // ADD VARIABLES
            for(Variable v : variables)
            {
                if((v.isConstant() && !v.isEnvironmental()))
                {
                    if(v.getAngle() != null)
                        add(new PopupOption(this, obj, v.getName().toString() + " : " + v.getValue().toString() + " " + v.getUnits() + ", " + v.getAngle() + "°", menuListener, p));
                        else
                        add(new PopupOption(this, obj, v.getName().toString() + " : " + v.getValue().toString() + " " + v.getUnits(), menuListener, p));
                }
            }

            addSeparator();
        }
        for(String str : options)
        {
            if(!str.equals(""))
                add(new PopupOption(this, obj, str, menuListener, p));
        }
        setLocation((int)p.getX(), (int)p.getY());
        setBorder(new BevelBorder(BevelBorder.RAISED));
        setVisible(true);
    }

    public class PopupOption extends JMenuItem implements MouseListener
    {
        String str = "";
        Object obj;
        Point p;
        Popup popup;
        
        public PopupOption(Popup popup, Object obj, String str, ActionListener al, Point p)
        {
            super(str);
            this.p = p;
            this.popup = popup;
            this.str = str;
            this.obj = obj;
            setHorizontalTextPosition(JMenuItem.RIGHT);
            addActionListener(al);
            addMouseListener(this);
        }

        public void mouseExited(MouseEvent e){setArmed(false);}

        public void mouseEntered(MouseEvent e){setArmed(true);}

        public void mouseReleased(MouseEvent e)
        {
            popup.setVisible(false);
            if(obj instanceof Snap)
                Snap.parseSnap(obj).run(str);
            else if(obj instanceof Figure)
                Figure.parseFigure(obj).run(str);
            else
                Container.run(str);                
        }

        public void mouseClicked(MouseEvent e){}

        public void mousePressed(MouseEvent e){}
    }
}