import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.io.File;
import java.awt.Color;
import java.awt.Dimension;
import javax.imageio.ImageIO;

public class Button extends JButton implements MouseListener
{
    int w = getWidth();
    int h = getHeight();
    String filePath = "";
    JFrame c;

    public Button(JFrame c, ImageIcon img, String filePath)
    {
        addMouseListener(this);
        setVisible(true);
        this.filePath = filePath;
        this.c = c;
        setIcon(img);
        setMaximumSize(new Dimension(Container.width, Container.height));
    }

    public Button(String str)
    {
        setText(str);
        setVisible(true);
        addMouseListener(this);
        setMaximumSize(new Dimension(Container.width, Container.height));
    }

    public Button(Color c)
    {
        setBackground(c);
        setVisible(true);
        addMouseListener(this);
        setMaximumSize(new Dimension(Container.width, Container.height));
    }

    public void mouseExited(MouseEvent e)
    {
        try{
            setIcon(new ImageIcon(ImageIO.read(new File(System.getProperty("user.dir") + "//Media//" + filePath + ".png"))));   
        }catch(Exception e2){}
    }

    public void mouseEntered(MouseEvent e)
    {
        try{
            setIcon(new ImageIcon(ImageIO.read(new File(System.getProperty("user.dir") + "//Media//" + filePath + "Hovered.png"))));   
        }catch(Exception e2){}
    }

    public void mouseReleased(MouseEvent e)
    {
        if(filePath.equals("exit"))
            c.dispose();
    }

    public void mouseClicked(MouseEvent e){}

    public void mousePressed(MouseEvent e){}
}