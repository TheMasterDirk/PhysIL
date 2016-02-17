import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.File;
import javax.imageio.ImageIO;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.ImageIcon;

import java.util.ArrayList;
import java.text.DecimalFormat;

public class GraphPanel extends JPanel implements MouseMotionListener, MouseListener
{
    Variable v;
    TimePanel t;
    ArrayList<Point> points = new ArrayList<Point>();
    double widthInterval = 5, heightInterval = 1;
    JFrame frame = new JFrame("Graph Frame");
    JLabel picLabel = new JLabel();
    Figure f;
    JLabel yMax = new JLabel("0.0");
    JLabel tMax = new JLabel("0.0");
    int w=288, h=213, t1 = 0, y1 = 0;
    Button exit;

    public GraphPanel(TimePanel t, Figure f, Variable v)
    {
        this.v = v;
        this.f = f;
        this.t = t;
        frame = new JFrame(v.getName() + " / time");
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);
        frame.setAlwaysOnTop(true);
        frame.add(this);
        frame.setShape(new java.awt.geom.RoundRectangle2D.Double(0,0,getWidth(),getHeight(),5,5));
        frame.setResizable(false);
        frame.setSize(w,h);
        frame.setLocationRelativeTo(null);

        addMouseMotionListener(this);
        setLayout(null);
        try{
            exit = new Button(frame, new ImageIcon(ImageIO.read(new File(System.getProperty("user.dir") + "//Media//exit.png"))), "exit");
        }catch(Exception e){System.out.println("Exit button not found.");}
        add(exit);
        exit.setBounds(244,0,39,19);
        try{
            BufferedImage myPicture = ImageIO.read(new File(System.getProperty("user.dir") + "//Media//graphBackground.png"));
            picLabel = new JLabel(new ImageIcon(myPicture));
        }catch(Exception e){System.out.println("No.");}
        picLabel.setBounds(0,0,getWidth(), getHeight());
        add(yMax);add(tMax);
        yMax.setBounds(1,23,300,20);
        tMax.setBounds(263,188,300,20);
        add(picLabel);
    }

    public int getHeight()
    {
        return h;
    }

    public int getWidth()
    {
        return w;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Multiply by intervals
        int displace = 0;
        try{
            try{
                Calculate c2 = new Calculate(f);
                displace = (int)Math.round(Double.parseDouble(c2.getFinalVarValue(v.getValue().toString(),"")));
            }catch(Exception e2)
            {
                displace = 0;
            }

            for(Point p : points)
            {
                p.setLocation((Math.round(p.getX()-29)*widthInterval), Math.round(p.getY()-7)*heightInterval);
            }
            // Expand Bounds if necessary
            if(46+Math.round(t.getTime()*getWidth()/widthInterval) > (int)getWidth())widthInterval *= 2;
            if(getHeight()/2-displace/heightInterval < 29)
            {
                heightInterval *= 2;
                for(int i = 0; i < points.size(); i++)
                {
                    Point p2 = points.get(i);
                    p2.setLocation(Math.round(p2.getX()), Math.round(p2.getY()) + getHeight()*heightInterval/4);
                }
            }
            // Adjust points to expansion
            for(Point p : points)
            {
                p.setLocation(29+Math.round(p.getX()/widthInterval), Math.round(7+p.getY()/heightInterval));
            }
            // Add new points
            points.add(new Point(29+(int)Math.round((t.getTime()*getWidth()/widthInterval)), (int)Math.round(7+getHeight()/2-displace/heightInterval) ));
            for(int i = 0; i < points.size()-1; i++)
                g.drawLine((int)Math.round(points.get(i).getX()), (int)Math.round(points.get(i).getY()), (int)Math.round(points.get(i+1).getX()), (int)Math.round(points.get(i+1).getY()));

            // Set Bounds 
            DecimalFormat format = new DecimalFormat("0.0");
            yMax.setText(format.format(heightInterval*(getHeight()/2-29))+" "+v.getUnits());
            tMax.setText(format.format(widthInterval*(getWidth()-46)/getWidth())+" s");
            tMax.setBounds(263-5*tMax.getText().length()+20,188,300,20);

            // Titles
            Graphics2D g2 = (Graphics2D)g;
            g2.translate(15,180);
            g2.rotate(-1.5708);
            g2.drawString(v.getName().toString(), 0, 0);
            g2.rotate(1.5708);
            g2.translate(-15,-180);
            g2.drawString("time", 50, 210);
        }catch(Exception e){e.printStackTrace();}

    }

    public void mouseMoved(MouseEvent e){}

    public void mouseDragged(MouseEvent e)
    {
        int t = frame.getLocation().x;
        int y = frame.getLocation().y;
        frame.setLocation(t+e.getX(), y+e.getY());
    }
    
    public void mouseEntered(MouseEvent e){}
    public void mousePressed(MouseEvent e)
    {
        t1 = e.getX();
        y1 = e.getY();
    }
    
    public void mouseReleased(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
}