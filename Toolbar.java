import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.event.MouseEvent;

/** 
 * The overhead toolbar
 */
public class Toolbar extends JToolBar
{
    public PolyButton poly, poly2;
    TimePanel timePanel;
    GraphButton gb;
    EnvironmentalPanel ep;
    int w;
    int h;

    public Toolbar(Container c)
    {
        setFloatable(false);
        setRollover(true);
        setVisible(true);

        // Poly Panel
        JPanel pp = new JPanel();
        pp.setLayout(new BorderLayout());
        // Buttons     
        timePanel = new TimePanel(c);
        ArrayList<JComponent> objs = new ArrayList<JComponent>();
        Button draw = new Button(Color.BLUE), imprt = new Button(Color.BLUE), load = new Button(Color.BLUE), save = new Button(Color.BLUE), clr = new Button(Color.BLUE), ext = new Button(Color.BLUE);
        ep = new EnvironmentalPanel();
        int numberOfObjects = 4;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        w = (int)(screenSize.getWidth()/(numberOfObjects*3));
        h = (int)screenSize.getHeight()/8;
        try{
            load = new LoadButton(c, makeImageIcon("//Media//btnLoad.png",1));
            save = new SaveButton(c, makeImageIcon("//Media//btnSave.png",1));
            gb = new GraphButton(c.frame, makeImageIcon("//Media//btnGraph.png",1),"btnGraph.png");
            draw = new DrawButton(true, c.frame, makeImageIcon("//Media//btnDraw.png",1),"btnDraw.png");
            imprt = new DrawButton(false, c.frame, makeImageIcon("//Media//btnImport.png",1),"btnImport.png");
            poly = new PolyButton(c.frame, makeImageIcon("//Media//btnRectangle.png",3), "btnRectangle.png");
            poly2 = new PolyButton(c.frame, makeImageIcon("//Media//btnTriangle.png",3), "btnTriangle.png");
            clr = new ClearButton(c.frame, makeImageIcon("//Media//btnClear.png",1),"//Media//btnClear.png");
            ext = new ExitButton(c.frame,makeImageIcon("//Media//btnExt.png",1),"btnExt.png");
        }catch(Exception e){e.printStackTrace();}
        pp.add(poly, BorderLayout.NORTH);
        pp.add(poly2, BorderLayout.SOUTH);
        objs.add(load);
        objs.add(save);
        objs.add(draw);
        objs.add(imprt);
        objs.add(timePanel);
        objs.add(pp);
        objs.add(ep);
        objs.add(gb);
        objs.add(gb);
        objs.add(clr);
        objs.add(ext);
        objs.add(ext);
        //objs.add(snaps);

        // Layout
        setLayout(null);
        for(int i = 0; i < objs.size(); i++)
        {
            if(objs.get(i).equals(ep)) {objs.get(i).setBounds(6+w*i,2,2*w-2,h-4);}
            else objs.get(i).setBounds(6+w*i,2,w-2,h-4);
            add(objs.get(i));
        }
    }

    public ImageIcon makeImageIcon(String filepath, int ratio)
    {
        try{
            return new ImageIcon(DrawFrame.makeColorTransparent(Container.toBufferedImage(ImageIO.read(new File(System.getProperty("user.dir") + filepath)).getScaledInstance(w-5, h/ratio, Image.SCALE_SMOOTH))));
        }
        catch(Exception e){}
        return null;
    }

    public ArrayList<Double> getFieldValues()
    {
        return ep.getValues();
    }
    
    public ArrayList<Double> getOldFieldValues()
    {
        return ep.getOldValues();
    }

    public double getTime()
    {
        return timePanel.getTime();
    }

}