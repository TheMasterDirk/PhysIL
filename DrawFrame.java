import java.awt.*;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;

import java.util.ArrayList;

public class DrawFrame extends JPanel implements MouseListener, MouseMotionListener 
{
    /**
     * Some constants to represent the color selected by the user.
     */
    private final static int BLACK=0,RED=1,GREEN=2,BLUE=3,CYAN=4,BROWN=5,YELLOW=6;
    private int currentColor = BLACK;  // The currently selected drawing color
    private int prevX, prevY;     // The previous location of the mouse.
    private boolean dragging;      // This is set to true while the user is drawing.
    private Graphics2D graphicsForDrawing;  // A graphics context for the panel
    JFrame f; // Container Frame

    /**
     * Constructor for DrawFrame class sets the background color to be
     * white and sets it to listen for mouse events on itself.
     */
    DrawFrame() 
    {
        f = new JFrame("Draw Figure");
        f.setVisible(true);
        f.setSize(600,480);
        f.add(this);
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Draw the contents of the panel.  Since no information is
     * saved about what the user has drawn, the user's drawing
     * is erased whenever this routine is called.
     */
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);  // Fill with background color (white).
        int width = getWidth();    // Width of the panel.
        int height = getHeight();  // Height of the panel.
        int colorSpacing = (height - 56) / 7;
        // Distance between the top of one colored rectangle in the palette
        // and the top of the rectangle below it.  The height of the
        // rectangle will be colorSpacing - 3.  There are 7 colored rectangles,
        // so the available space is divided by 7.  The available space allows
        // for the gray border and the 50-by-50 Post button.

        /* Draw a 3-pixel border around the applet in gray.  This has to be
        done by drawing three rectangles of different sizes. */

        g.setColor(Color.GRAY);
        g.drawRect(0, 0, width-1, height-1);
        g.drawRect(1, 1, width-3, height-3);
        g.drawRect(2, 2, width-5, height-5);

        /* Draw a 56-pixel wide gray rectangle along the right edge of the applet.
        The color palette and Post button will be drawn on top of this.
        (This covers some of the same area as the border I just drew. */

        g.fillRect(width - 56, 0, 56, height);

        /* Draw the "Post button" as a 50-by-50 white rectangle in the lower right
        corner of the applet, allowing for a 3-pixel border. */

        g.setColor(Color.WHITE);
        g.fillRect(width-53,  height-53, 50, 50);
        g.setColor(Color.BLACK);
        g.drawRect(width-53, height-53, 49, 49);
        g.drawString("POST", width-48, height-23); 

        /* Draw the seven color rectangles. */

        g.setColor(Color.BLACK);
        g.fillRect(width-53, 3 + 0*colorSpacing, 50, colorSpacing-3);
        g.setColor(Color.RED);
        g.fillRect(width-53, 3 + 1*colorSpacing, 50, colorSpacing-3);
        g.setColor(Color.GREEN);
        g.fillRect(width-53, 3 + 2*colorSpacing, 50, colorSpacing-3);
        g.setColor(Color.BLUE);
        g.fillRect(width-53, 3 + 3*colorSpacing, 50, colorSpacing-3);
        g.setColor(Color.CYAN);
        g.fillRect(width-53, 3 + 4*colorSpacing, 50, colorSpacing-3);
        g.setColor(new Color(181, 131 ,11));
        g.fillRect(width-53, 3 + 5*colorSpacing, 50, colorSpacing-3);
        g.setColor(Color.YELLOW);
        g.fillRect(width-53, 3 + 6*colorSpacing, 50, colorSpacing-3);

        /* Draw a 2-pixel white border around the color rectangle
        of the current drawing color. */

        g.setColor(Color.WHITE);
        g.drawRect(width-55, 1 + currentColor*colorSpacing, 53, colorSpacing);
        g.drawRect(width-54, 2 + currentColor*colorSpacing, 51, colorSpacing-2);

    } // end paintComponent()

    /**
     * Change the drawing color after the user has clicked the
     * mouse on the color palette at a point with y-coordinate y.
     * (Note that I can't just call repaint and redraw the whole
     * panel, since that would erase the user's drawing!)
     */
    private void changeColor(int y) {

        int width = getWidth();           // Width of applet.
        int height = getHeight();         // Height of applet.
        int colorSpacing = (height - 56) / 7;  // Space for one color rectangle.
        int newColor = y / colorSpacing;       // Which color number was clicked?

        if (newColor < 0 || newColor > 6)      // Make sure the color number is valid.
            return;

        /* Remove the highlight from the current color, by drawing over it in gray.
        Then change the current drawing color and draw a hilite around the
        new drawing color.  */

        Graphics g = getGraphics();
        g.setColor(Color.GRAY);
        g.drawRect(width-55, 1 + currentColor*colorSpacing, 53, colorSpacing);
        g.drawRect(width-54, 2 + currentColor*colorSpacing, 51, colorSpacing-2);
        currentColor = newColor;
        g.setColor(Color.WHITE);
        g.drawRect(width-55, 1 + currentColor*colorSpacing, 53, colorSpacing);
        g.drawRect(width-54, 2 + currentColor*colorSpacing, 51, colorSpacing-2);
        g.dispose();

    } // end changeColor()

    /**
     * This routine is called in mousePressed when the user clicks on the drawing area.
     * It sets up the graphics context, graphicsForDrawing, to be used to draw the user's 
     * sketch in the current color.
     */
    private void setUpDrawingGraphics() {
        graphicsForDrawing = (Graphics2D)getGraphics();
        switch (currentColor) {
            case BLACK:
            graphicsForDrawing.setColor(Color.BLACK);
            break;
            case RED:
            graphicsForDrawing.setColor(Color.RED);
            break;
            case GREEN:
            graphicsForDrawing.setColor(Color.GREEN);
            break;
            case BLUE:
            graphicsForDrawing.setColor(Color.BLUE);
            break;
            case CYAN:
            graphicsForDrawing.setColor(Color.CYAN);
            break;
            case BROWN:
            graphicsForDrawing.setColor(new Color(181, 131, 11));
            break;
            case YELLOW:
            graphicsForDrawing.setColor(Color.YELLOW);
            break;
        }
    } // end setUpDrawingGraphics()

    /**
     * This is called when the user presses the mouse anywhere in the applet.  
     * There are three possible responses, depending on where the user clicked:  
     * Change the current color, Post the drawing, or start drawing a curve.  
     * (Or do nothing if user clicks on the border.)
     */
    public void mousePressed(MouseEvent evt) {

        int x = evt.getX();   // x-coordinate where the user clicked.
        int y = evt.getY();   // y-coordinate where the user clicked.

        int width = getWidth();    // Width of the panel.
        int height = getHeight();  // Height of the panel.

        if (dragging)  // Ignore mouse presses that occur
            return;            //    when user is already drawing a curve.
        //    (This can happen if the user presses
        //    two mouse buttons at the same time.)

        if (x > width - 53) {
            // User clicked to the right of the drawing area.
            // This click is either on the Post button or
            // on the color palette.
            if (y > height - 53)
            {
                submit(this);       //  Clicked on "Post button".
                f.dispose();
            }
            else
                changeColor(y);  // Clicked on the color palette.
        }
        else if (x > 3 && x < width - 56 && y > 3 && y < height - 3) {
            // The user has clicked on the white drawing area.
            // Start drawing a curve from the point (x,y).
            prevX = x;
            prevY = y;
            dragging = true;
            setUpDrawingGraphics();
        }

    } // end mousePressed()

    /**
     * Called whenever the user releases the mouse button. If the user was drawing 
     * a curve, the curve is done, so we should set drawing to false and get rid of
     * the graphics context that we created to use during the drawing.
     */
    public void mouseReleased(MouseEvent evt) {
        if (!dragging)
            return;  // Nothing to do because the user isn't drawing.
        dragging = false;
        graphicsForDrawing.dispose();
        graphicsForDrawing = null;
    }

    /**
     * Returns picture submitted
     */
    public static void submit(JPanel panel) 
    {
        try{
            int ytmp = (int)panel.getLocationOnScreen().getY();
            int xtmp = (int)panel.getLocationOnScreen().getX();
            Rectangle screenRect = new Rectangle(xtmp+3, ytmp+3, (int)panel.getWidth()-60, (int)panel.getHeight()-7);
            File docfile = new File("tmp.png");
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            ImageIO.write(capture, "png", docfile);
            if(shortenPic("tmp.png"))
                Container.addFigure(new Figure(toBufferedImage(makeColorTransparent(ImageIO.read(new File("tmp.png")), Color.WHITE))));
        }catch(Exception e){e.printStackTrace();}
    }

    /**
     * Crops icon out of jpanel
     */
    public static boolean shortenPic(String file)
    {
        BufferedImage image = null;
        ArrayList<Integer> arr = new ArrayList<Integer>();
        ArrayList<Integer> arry = new ArrayList<Integer>();
        try{
            image = ImageIO.read(new File(file));
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int  clr   = image.getRGB(x, y); 
                    int  red   = (clr & 0x00ff0000) >> 16;
                    int  green = (clr & 0x0000ff00) >> 8;
                    int  blue  =  clr & 0x000000ff;
                    if(red+green+blue<765)
                    {
                        arr.add(x);
                        arry.add(y);
                    }
                }
            }
            if(arr.size()>0)
            {
                BufferedImage dest = image.getSubimage(min(arr), min(arry), max(arr)-min(arr), max(arry)-min(arry));
                ImageIO.write(dest, "png", new File("tmp.png"));
                return true;
            }
        }catch(Exception e){e.printStackTrace();}
        return false;
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
                public final int filterRGB(final int x, final int y, final int rgb)
                {
                    int r = rgb&0xFF, g = (rgb>>8)&0xFF, b = (rgb>>16)&0xFF;

                    if (r+g+b > 650)
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

    public static Image makeColorTransparent(BufferedImage im)
    {
        for(int i = 201; i < 256; i++)
            im = Container.toBufferedImage(makeColorTransparent(im, new Color(i,i,i)));
        return im;
    }

    /**
     * Returns max value of array list
     */
    public static int max(ArrayList<Integer> arr)
    {
        int m = 0;
        for(int i = 0; i < arr.size(); i++)
        {
            if(m < arr.get(i))
                m = arr.get(i);
        }
        return m;
    }

    /**
     * Returns min value of array list
     */
    public static int min(ArrayList<Integer> arr)
    {
        int m = 100000;
        for(int i = 0; i < arr.size(); i++)
        {
            if(m > arr.get(i))
                m = arr.get(i);
        }
        return m;
    }

    /**
     * Called whenever the user moves the mouse while a mouse button is held down.  
     * If the user is drawing, draw a line segment from the previous mouse location 
     * to the current mouse location, and set up prevX and prevY for the next call.  
     * Note that in case the user drags outside of the drawing area, the values of
     * x and y are "clamped" to lie within this area.  This avoids drawing on the color 
     * palette or Post button.
     */
    public void mouseDragged(MouseEvent evt) {
        int stroke = 5;
        if(SwingUtilities.isRightMouseButton(evt))
        {
            stroke = 20;
        }
        if (!dragging)
            return;  // Nothing to do because the user isn't drawing.

        int x = evt.getX();   // x-coordinate of mouse.
        int y = evt.getY();   // y-coordinate of mouse.

        if (x < 3)                          // Adjust the value of x,
            x = 3;                           //   to make sure it's in
        if (x > getWidth() - 57)       //   the drawing area.
            x = getWidth() - 57;

        if (y < 3)                          // Adjust the value of y,
            y = 3;                           //   to make sure it's in
        if (y > getHeight() - 4)       //   the drawing area.
            y = getHeight() - 4;
        graphicsForDrawing.setStroke(new BasicStroke(stroke));
        graphicsForDrawing.drawLine(prevX, prevY, x, y);  // Draw the line.
        //graphicsForDrawing.fillOval(prevX, prevY, Math.abs(x-prevX), Math.abs(y-prevY));
        prevX = x;  // Get ready for the next line segment in the curve.
        prevY = y;

    } 

    public void mouseEntered(MouseEvent evt) { }   // Some empty routines.
    public void mouseExited(MouseEvent evt) { }    //    (Required by the MouseListener
    public void mouseClicked(MouseEvent evt) { }   //    and MouseMotionListener
    public void mouseMoved(MouseEvent evt) { }     //    interfaces).

}