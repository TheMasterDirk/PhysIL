import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
/**
 * The blue snap
 * 
 * @author Derek Schafer
 */
public class CenterSnap extends Snap implements MouseMotionListener
{
    private Polygon shape;

    /**
     * Constructor for objects of class CenterSnap
     */
    public CenterSnap(int x, int y, Polygon p)
    {
        super(x,y,p);
        shape = p;
        setColor(Color.BLUE);
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(color.equals(Color.BLUE) && MouseInfo.getPointerInfo().getLocation().x < (Container.width*7)/8 - shape.getWidth() && MouseInfo.getPointerInfo().getLocation().y  > Container.height/8 + shape.getWidth())
        {

            for(Snap s: shape.getSnaps())
            {
                s.setVisible(false);
                setX(MouseInfo.getPointerInfo().getLocation().x);
                setY(MouseInfo.getPointerInfo().getLocation().y-Container.height/8);
                //s.repaint(); I don't think this is necessary?
            }
        }

    }
}
    