import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;

public class DrawButton extends Button
{
    boolean in = false;
    public DrawButton(boolean in, JFrame j, ImageIcon ii, String filePath) throws Exception
    {
        super(j, ii, filePath);
        this.in = in;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if(in)
            new DrawFrame();
        else
            new DrawImportFrame();
    }

}