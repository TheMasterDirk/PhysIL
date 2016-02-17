import java.awt.Font;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Insets;

import javax.swing.*;
import java.io.File;

import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;

import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.border.Border;

public class TimePanel extends JPanel implements KeyListener, MouseListener, Runnable
{
    JTextField minutes = new JTextField("00");
    JLabel colon = new JLabel(":");
    JTextField seconds = new JTextField("00");
    Font font1 = new Font("SansSerif", Font.BOLD, 20);
    Button play = new Button(Color.GREEN);
    Button pause = new Button(Color.RED);
    boolean running = false;
    double elapsedSecs;
    long startTime, currentTime, oldTime;
    Border defaultBorder = minutes.getBorder();
    public transient GroupLayout gl;
    public Container c;

    public TimePanel(Container c)
    {
        JPanel buttons = new JPanel();
        play.addMouseListener(this);
        pause.addMouseListener(this);
        buttons.add(play); buttons.add(pause);

        this.c = c;

        JPanel time = new JPanel();
        time.add(minutes); time.add(colon); time.add(seconds);

        minutes.setFont(font1);
        minutes.addKeyListener(this);
        minutes.setHorizontalAlignment(JTextField.CENTER);
        seconds.setFont(font1);
        seconds.addKeyListener(this);
        seconds.setHorizontalAlignment(JTextField.CENTER);

        gl = new GroupLayout(this);
        setLayout(gl);
        gl.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = gl.createSequentialGroup();
        hGroup.addGroup(gl.createParallelGroup().addComponent(time).addComponent(buttons));
        gl.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = gl.createSequentialGroup();
        vGroup.addGroup(gl.createParallelGroup().addComponent(time));
        vGroup.addGroup(gl.createParallelGroup().addComponent(buttons));
        gl.setVerticalGroup(vGroup);

        setImageIcon(play, "start");
        setImageIcon(pause, "stop");

        setVisible(true);
    }
    
    /** 
     * Manually stops the running boolean and pauses the timer.
     */
    public void manualPause()
    {
        running = false;   
    }
    
    /**
     * Manually restarts the running boolean and unpauses the timer.
     */
    public void manualStart()
    {
        running = true;
    }

    /**
     * @return whether the timer is running or not.
     */
    public boolean isRunning()
    {
        return running;
    }

    /**
     * Convert Image to BufferedImage.
     *
     * @param image Image to be converted to BufferedImage.
     * @return BufferedImage corresponding to provided Image.
     */
    private static BufferedImage toBufferedImage(final Image image)
    {
        final BufferedImage bufferedImage =
            new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return bufferedImage;
    }

    /**
     * Make provided image transparent wherever color matches the provided color.
     *
     * @param im BufferedImage whose color will be made transparent.
     * @param color Color in provided image which will be made transparent.
     * @return Image with transparency applied.
     */
    public static Image makeColorTransparent(final BufferedImage im, final Color color)
    {
        final ImageFilter filter = new RGBImageFilter()
            {
                // the color we are looking for (white)... Alpha bits are set to opaque
                public int markerRGB = color.getRGB() | 0xFFFFFFFF;

                public final int filterRGB(final int x, final int y, final int rgb)
                {
                    if ((rgb | 0xFF000000) == markerRGB)
                    {
                        // Mark the alpha bits as zero - transparent
                        return 0x00FFFFFF & rgb;
                    }
                    else
                    {
                        // nothing to do
                        return rgb;
                    }
                }
            };

        final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    /**
     * Sets the image of a Button or JButton to a specified image
     * 
     * @param play The Button or JButton
     * @param filePath the name of the PNG file
     */
    public void setImageIcon(JButton play, String filePath)
    {

        try{
            play.setMargin(new Insets(0, 0, 0, 0));
            play.setBorder(null);
            play.setOpaque(false);
            play.setContentAreaFilled(false);
            play.setBorderPainted(false);
            play.setIcon(new ImageIcon(makeColorTransparent(toBufferedImage(ImageIO.read(new File(System.getProperty("user.dir") + "//Media//"+filePath+".png")).getScaledInstance(Container.width/36, Container.height/24, Image.SCALE_SMOOTH)), Color.WHITE)));
        }catch(Exception e){e.printStackTrace();try{play.setIcon(new ImageIcon(makeColorTransparent(toBufferedImage(ImageIO.read(new File(System.getProperty("user.dir") + "//Media//fileNotFound.png")).getScaledInstance(Container.width/36, Container.height/24, Image.SCALE_SMOOTH)), Color.WHITE)));}catch(Exception e2){e2.printStackTrace();System.out.println("YOU GOT RID OF THE 'FILE NOT FOUND' PICTURE???");}}
    }

    public void run()
    {
        // I had a problem with moving while time is running. [javax.swing.timer] may be necessary.
        while(running)
        {
            elapsedSecs = ((currentTime - startTime)/1000.0);
            int tmp = (int)elapsedSecs;
            seconds.setText(String.format("%02d", tmp%60));
            minutes.setText(String.format("%02d", (int)(tmp/60)));
            currentTime = System.currentTimeMillis() + oldTime;
            for(GraphPanel g : Container.graphArray)
            {
                g.repaint();
            }
        }
    }

    public double getTime()
    {
        return elapsedSecs;
    }

    public JTextField toJTextField(Object obj){return (JTextField)obj;}

    public void keyReleased(KeyEvent e){ toJTextField(e.getSource()).setText(toJTextField(e.getSource()).getText().replaceAll("[^\\d.]", ""));}

    public void keyPressed(KeyEvent e){}

    public void keyTyped(KeyEvent e){}

    public void mouseExited(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseReleased(MouseEvent e){}

    public void mouseClicked(MouseEvent e)
    {
        Thread thread = new Thread(this, "Timer Thread");
        if(e.getSource().equals(play) && !running)
        {
            // I switched the if and for loop, because I thought it was redundant otherwise.
            if(seconds.getText().equals("00") && minutes.getText().equals("00"))
            {
                for(Figure f : Container.figuresOnField)
                {
                    f.getSnap().setOriginalX(f.getX());
                    f.getSnap().setOriginalY(f.getY());
                }
            }

            try{
                pause.setIcon(new ImageIcon(makeColorTransparent(toBufferedImage(ImageIO.read(new File(System.getProperty("user.dir") + "//Media//stop.png")).getScaledInstance(Container.width/36, Container.height/24, Image.SCALE_SMOOTH)), Color.WHITE)));
            }catch(Exception e3){e3.printStackTrace();try{play.setIcon(new ImageIcon(makeColorTransparent(toBufferedImage(ImageIO.read(new File(System.getProperty("user.dir") + "//Media//fileNotFound.png")).getScaledInstance(Container.width/36, Container.height/24, Image.SCALE_SMOOTH)), Color.WHITE)));}catch(Exception e4){e4.printStackTrace();System.out.println("YOU GOT RID OF THE 'FILE NOT FOUND' PICTURE???");}}

            if(minutes.getText().equals("")) minutes.setText("00");
            if(seconds.getText().equals("")) seconds.setText("00");
            seconds.setEditable(false);
            minutes.setEditable(false);
            seconds.setBorder(BorderFactory.createEmptyBorder());
            minutes.setBorder(BorderFactory.createEmptyBorder());
            minutes.setBackground(new Color(214, 217, 223));
            seconds.setBackground(new Color(214, 217, 223));
            running = true;
            startTime = System.currentTimeMillis();
            oldTime = Integer.parseInt(seconds.getText())*1000 + Integer.parseInt(minutes.getText())*60000;
            currentTime = startTime + oldTime;
            thread.start();
        }
        else if(e.getSource().equals(pause) && !running)
        {
            for(Figure f : Container.figuresOnField)
            {
                f.getSnap().setX(f.getSnap().getOriginalX());
                f.getSnap().setY(f.getSnap().getOriginalY());
                //f.resetVars();
            }
            
            seconds.setText("00");
            minutes.setText("00");
        }
        else if(e.getSource().equals(pause))
        {
            running = false;
            seconds.setBorder(defaultBorder);
            minutes.setBorder(defaultBorder);
            seconds.setBackground(Color.WHITE);
            minutes.setBackground(Color.WHITE);
            seconds.setEditable(true);
            minutes.setEditable(true);
            try{
                pause.setIcon(new ImageIcon(makeColorTransparent(toBufferedImage(ImageIO.read(new File(System.getProperty("user.dir") + "//Media//reset.png")).getScaledInstance(Container.width/36, Container.height/24, Image.SCALE_SMOOTH)), Color.WHITE)));
            }catch(Exception e3){try{play.setIcon(new ImageIcon(makeColorTransparent(toBufferedImage(ImageIO.read(new File(System.getProperty("user.dir") + "//Media//fileNotFound.png")).getScaledInstance(Container.width/36, Container.height/24, Image.SCALE_SMOOTH)), Color.WHITE)));}catch(Exception e4){System.out.println("YOU GOT RID OF THE 'FILE NOT FOUND' PICTURE???");}}

            try{thread.join();}catch(Exception e2){e2.printStackTrace();}
        }
    }

    public void mousePressed(MouseEvent e){}
}