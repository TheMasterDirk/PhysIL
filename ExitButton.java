import javax.swing.JFrame;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class ExitButton extends Button
{
    public ExitButton(JFrame f, ImageIcon i, String filePath)
    {
        super(f,i,filePath);
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        System.exit(0);
    }
}
