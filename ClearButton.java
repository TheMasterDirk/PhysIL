import javax.swing.JFrame;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class ClearButton extends Button
{
    public ClearButton(JFrame f, ImageIcon i, String filePath)
    {
        super(f,i,filePath);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        Container.deleteAllFigures();
        Container.deleteAllPolygons();
    }
}