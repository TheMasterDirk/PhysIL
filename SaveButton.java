import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class SaveButton extends Button
{
    Container c;
    public SaveButton(Container c, ImageIcon ii)
    {
        super(Color.BLUE);
        setIcon(ii);
        this.c = c;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {   
        c.save();
    }

}