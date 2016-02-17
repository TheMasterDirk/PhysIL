import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class LoadButton extends Button
{
    Container c;
    public LoadButton(Container c, ImageIcon ii)
    {
        super(Color.BLUE);
        setIcon(ii);
        this.c = c;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {   
        c.load();
    }

}