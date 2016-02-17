import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.ImageIcon;

public class PolyButton extends Button
{
    public PolyButton(JFrame j, ImageIcon ii, String filePath)
    {
        super(j, ii, filePath);
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        if(filePath.contains("Triangle"))Container.isTriangle = true;
        else Container.isTriangle = false;
        Container.polyGraphing = !Container.polyGraphing;
    }

}