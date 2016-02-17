import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JFrame;

import java.awt.image.BufferedImage;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class ResizeFigurePanel extends JPanel implements ChangeListener
{
    int init, min = 20, max = Container.height*7/8;
    JSlider size;
    Figure figure;
    BufferedImage img;

    public ResizeFigurePanel(Figure figure)
    {
        this.figure = figure;
        JFrame f = new JFrame("Resize Figure");
        f.setVisible(true);
        f.setLocation(figure.getX(), figure.getY()+Container.height/8);
        f.add(this);
        setSize(Container.width/6, Container.height/6);
        img = (BufferedImage)figure.getImage();
        init = figure.getHeight();
        size = new JSlider(JSlider.HORIZONTAL, min, max, init); 
        size.addChangeListener(this);
        add(size);

        f.pack();
    }

    public void stateChanged(ChangeEvent e)
    {
        JSlider source = (JSlider)e.getSource();
        int value = source.getValue();

        if(init > 0)
        {
            figure.resize(value);
        }
    }
}