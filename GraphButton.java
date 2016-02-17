import javax.swing.JFrame;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class GraphButton extends Button
{
    public GraphButton(JFrame f, ImageIcon i, String filePath)
    {
        super(f,i,filePath);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        new GraphSelectionPanel();
    }
}