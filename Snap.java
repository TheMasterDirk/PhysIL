import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Snap extends JButton implements MouseListener, MouseMotionListener
{
    int x = 200, y = 200, originalX = 300, originalY = 300;
    Snap joint = null;
    public boolean selected = false, jointed = false, dragging = false, pressed = false;
    public Color color = null;
    Polygon shape;
    Figure fig;

    public Snap(int x, int y, Color c, Figure f)
    {
        this.x = x;
        this.y = y;
        fig = f;
        setVisible(true);
        color = c;
        setBackground(color);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public Snap(int x, int y, Polygon p)
    {
        shape = p;
        this.x = x;
        this.y = y;
        setBackground(new Color(100,100,100));
        setVisible(true);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setPosition(int x, int y)
    {
        setX(x);
        setY(y);
    }

    public int getOriginalX()
    {
        return originalX;
    }

    public int getOriginalY()
    {
        return originalY;
    }

    public void setOriginalX(int x)
    {
        originalX = x;
    }

    public void setOriginalY(int y)
    {
        originalY = y;
    }

    public void setColor(Color c)
    {
        setBackground(color = c);
    }

    public void shift(int dx, int dy)
    {
        x += dx;
        y += dy;
    }

    public boolean isDragging()
    {
        return dragging;
    }

    public void mouseMoved(MouseEvent e){}

    public void mouseDragged(MouseEvent e)
    {
        dragging = true;
        try{
            if(MouseInfo.getPointerInfo().getLocation().x < (Container.width*7)/8 - fig.getWidth() && MouseInfo.getPointerInfo().getLocation().y  > Container.height/8)
            {
                setX(MouseInfo.getPointerInfo().getLocation().x);
                setY(MouseInfo.getPointerInfo().getLocation().y-Container.height/8);
            }
        }catch(Exception e2)
        {
            if(color == null && MouseInfo.getPointerInfo().getLocation().x < (Container.width*7)/8 - shape.getWidth() && MouseInfo.getPointerInfo().getLocation().y  > Container.height/8)
            {
                setX(MouseInfo.getPointerInfo().getLocation().x);
                setY(MouseInfo.getPointerInfo().getLocation().y-Container.height/8);
            }
        }
        
        if(joint != null)
        {
            joint.setX(getX());
            joint.setY(getY());
        }
    }

    public void run(String method)
    {
        switch(method)
        {
            case "Delete Object":
            if(fig == null)
                Container.delete(shape);
            else if(shape ==null)
                Container.delete(fig);
            break;
            case "Create Joint w/ Snap":
            joint = shape.c.selectJoint(new ActionEvent(this, ActionEvent.ACTION_FIRST, "")); // Find the joint somehow
            System.out.println(joint);
            setX(joint.getX());
            setY(joint.getY());
            break;
        }
    }

    public boolean isPressed()
    {
        return pressed;
    }

    public static Snap parseSnap(Object obj)
    {
        return (Snap)obj;
    }

    public void mouseClicked(MouseEvent e){}

    public void mouseReleased(MouseEvent e)
    {
        pressed = false;
        if(shape != null){
            for(Snap s : shape.getSnaps())
            {
                s.setVisible(true);
            }
        }

        if(SwingUtilities.isRightMouseButton(e))
        {
            String[] options = {"Delete Object"};//, "Set Angle"};//, "Create Joint w/ Snap", "Create Joint at Point", "Unjoint"};
           // if(!jointed)
             //   options[4] = "";
            //if(getBackground().equals(Color.BLUE))
              //  options[1] = "Delete Object";

            // CHECK FOR CENTER SNAP
            new Popup(this, null, options, new Point(e.getX()+getX(), e.getY()+getY()+Container.height/8));
        }
        dragging = false;
    }

    public void mousePressed(MouseEvent e){pressed = true;}

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }
}