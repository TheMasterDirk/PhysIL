import java.awt.Image;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.io.File;

import java.awt.Toolkit;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;

import util.*;
import java.util.ArrayList;

public class Figure extends JLabel implements MouseListener, MouseMotionListener
{
    private ArrayList<String> fileVars = new ArrayList<String>(0);
    private ArrayList<Variable> variables = new ArrayList<Variable>(0);
    private ArrayList<Variable> changedVariables = new ArrayList<Variable>(0);
    private ArrayList<Double> FV = new ArrayList<Double>();
    private ArrayList<Double> OFV = new ArrayList<Double>();
    BufferedImage img, current;
    boolean imageChanged = false;
    Snap center;
    private int x = 0, y = 0, width, height, tabValue = 0;

    public Figure()
    {
        width = 0;
        height = 0;
        setSize(width, height);
        this.img = img;
        current = img;
        variables.add(new Variable("time", "t", 0.0002, true, true));
        variables.add(new Variable("Pi", "Pi", Math.PI, false, false));
        addMouseListener(this);
        //be careful when moving this statement
        getVarsFromFile();
        addAllVariables();
    }

    public Figure(BufferedImage img)
    {
        center = new Snap(300,300, Color.BLUE, this);
        width = img.getWidth();
        height = img.getHeight();
        setSize(width, height);
        this.img = img;
        current = img;
        resize(img.getHeight(this));
        variables.add(new Variable("time", "t", 0.00002, true, true));
        variables.add(new Variable("Pi", "Pi", Math.PI, false, false));
        addMouseListener(this);
        //be careful when moving this statement
        getVarsFromFile();
        addAllVariables();
    }

    //THIS ONE IS ONLY FOR READING IN FROM A FILE
    //DOES NOT ADD ANY CONSTANTS IN
    public Figure(ArrayList<Variable> vars, BufferedImage img, int x, int y)
    {
        center = new Snap(x,y, Color.BLUE, this);
        width = img.getWidth();
        height = img.getHeight();
        setSize(width, height);
        this.img = img;
        current = img;
        resize(img.getHeight(this));
        variables = vars;
        //Doesn't override vars, it simply just reads the default in from the file so that units can be applied to the correct variables
        getVarsFromFile();
        //Updating units
        updateUnits();
        //Doesnt add any more variables since this one if for reading from file
        addMouseListener(this);
    }

    public void getVarsFromFile()
    {
        ArrayList<String> zzz = new ArrayList<String>();
        ReadTextFile read_file = new ReadTextFile("variableNames.txt");
        
        String tmp = read_file.readLine();
        while(!read_file.EOF())
        {
            zzz.add(tmp);
            tmp = read_file.readLine();
        }
        read_file.close();
        fileVars = zzz;
    }

    public void addAllVariables()
    {
        for(String s : fileVars)
        {
            addValue(s, "~", s.split("-")[0].trim(), false, false);
        }
    }

    private void updateUnits()
    {
        for(Variable unitless : variables)
        {
            for(String unit : fileVars)
            {
                if(unit.split("-")[0].trim().equals(unitless.getName()))
                {
                    if(unit.indexOf("-") > 0)
                        unitless.setUnits(unit.split("-")[1]);
                    break;
                }
            }
        }
    }

    /** 
     * Returns all variables of a figure
     * 
     * @return The arraylist of variables
     */
    public ArrayList<Variable> getVars()
    {
        return variables;
    }

    public void setTabValue(int i)
    {
        tabValue = i;
    }

    public int getTabValue()
    {
        return tabValue;
    }

    /**
     * Returns the image of a figure
     * 
     * @return An Image object
     */
    public Image getImage()
    {
        return current;
    }

    public Image getOriginalImage()
    {
        return img;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setWidth(int w)
    {
        width = w;
    }

    public void setHeight(int h)
    {
        height = h;
    }

    public void setImage(BufferedImage img)
    {
        setIcon(new ImageIcon(current = img));
    }

    /**
     * Adds a variable to the variables arraylist
     * 
     * @param name The full name of the variable
     * @param shortname The abbreviation of the variable
     * @param val The value it holds (which could be a String or int)
     */
    public void addValue(String name, String shortname, Object val)
    {
        variables.add(new Variable(name, shortname, val, true, false));
    }

    /**
     * Adds a variable to the variables arraylist
     * 
     * @param name The full name of the variable
     * @param shortname The abbreviation of the variable
     * @param val The value it holds (which could be a String or int)
     * @param angle The angle of the varaible
     */
    public void addValue(String name, String shortname, Object val, Double angle)
    {
        Variable v = new Variable(name, shortname, val, true, false);
        v.setAngle(angle);
        variables.add(v);
    }

    /**
     * Adds a variable to the variables arraylist
     * 
     * @param name The full name of the variable
     * @param shortname The abbreviation of the variable
     * @param val The value it holds (which could be a String or int)
     */
    public void addValue(String name, String shortname, Object val, boolean isConstant, boolean isEnvironmental)
    {
        variables.add(new Variable(name, shortname, val, isConstant, isEnvironmental));
    }

    /**
     * Sets the x-position of a Figure
     * 
     * @param x The new x-position
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * Sets the y-position of a Figure
     * 
     * @param y The new y-position
     */
    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * @return The x-position of the figure
     */
    public int getX()
    {
        return x;
    }

    /**
     * @return The y-position of the figure
     */
    public int getY()
    {
        return y;
    }

    /**
     * @return whether or not the Figure's position has changed since last update
     */
    public boolean hasChanged()
    {
        if(center.getX() != x || center.getY() != y || imageChanged)
        {
            imageChanged = false;
            return true;
        }
        return false;
    }

    /**
     * @return the Center Snap of a Figure
     */
    public Snap getSnap()
    {
        return center;
    }

    /**
     * @param name the specified name of a value
     * @return the value of the first variable with specified name
     */
    public Object getValue(String name)
    {
        for(Variable v: variables)
        {
            if(v.getName().toString().toLowerCase().trim().equals(name.toLowerCase().trim()))
            {
                return v.getValue();
            }
        }
        return null;
    }

    /**
     * @param name the specified name of a Variable
     * @return the first Variable of the Figure with specified name
     */
    public Variable getVar(String name)
    {
        for(Variable v : variables)
        {
            if(v.getName().toString().toLowerCase().trim().equals(name.toLowerCase().trim()))
                return v;
        }
        return null;
    }

    public ArrayList<Variable> getAllSameVars(String name)
    {
        ArrayList<Variable> sameName = new ArrayList<Variable>();
        for(Variable v : variables)
        {
            if(v.getName().toString().toLowerCase().trim().equals(name.toLowerCase().trim()))
                sameName.add(v);
        }
        return sameName;
    }

    /**
     * Evaluates a variable at a specific time
     * 
     * @param variable The variable to be evaluated.
     * @param time The time to evaluate at
     * @return the value of the variable at a specific time
     */
    public String evaluateAt(Variable v, int time)
    {
        //can't return steing due to case of Vi = mass, and mass would change
        double tmp = Container.t.timePanel.getTime();
        getVar("time").setValue(time);
        Object valueAtTime = null;
        Calculate c = new Calculate(this);
        try{
            valueAtTime = Double.parseDouble(c.getValue(v.getValue().toString()));
        }catch(Exception e)
        {
            String tmpStr = "";
            for(String str : Equation.splitEquation(v.getValue().toString()))
            {
                if(getVar(str) != null && !str.contains("Initial") && !str.contains("Final")) 
                {
                    tmpStr += str + "Initial";
                }
                else
                {
                    tmpStr += str;
                }
            }

            valueAtTime = tmpStr;
        }
        getVar("time").setValue(tmp);
        return valueAtTime.toString();
    }

    /** 
     * Parses an object to a Figure
     * 
     * @param obj The non-Figure object
     * @return the new Figure
     */
    public static Figure parseFigure(Object obj)
    {
        return (Figure)obj;
    }

    public void resize(int value)
    { 
        double proportion = img.getWidth(this)*1.0/img.getHeight(this);
        BufferedImage resized = toBufferedImage(img.getScaledInstance((int)(value*proportion), value, Image.SCALE_SMOOTH));
        setImage(resized);
        img = resized;
        current = img;
        setWidth((int)(value*proportion));
        setHeight(value);
        setSize((int)(value*proportion), value);
    }

    /**
     * Returns all attributes currently in use by this Figure
     * But, does not reference the source file so it can save time.
     * 
     * @return The arraylist of attributes
     */
    public ArrayList<String> getCurrentAttributes()
    {
        ArrayList<String> tmp = new ArrayList<String>();
        for(Variable v : variables)
            tmp.add((String)v.getName());
        return tmp;
    }
    
    public void resetVars()
    {
        ArrayList<Variable> tmp = new ArrayList<Variable>();
        for(Variable v : variables)
        {
            tmp.add(new Variable(v.getName(), "~", v.getName(), true, false));
        }
        variables = tmp;
    }

    /**
     * @return arraylist of ALL changed attributes
     */
    public ArrayList<String> getAllChangedAttributes()
    {
        ArrayList<String> tmp = new ArrayList<String>();
        for(String s : getCurrentAttributes())
        {
            if(getVar(s.split("-")[0].trim()).isConstant() || getVar(s.split("-")[0].trim()).isSolved())
            {
                tmp.add(s);
            }
        }
        return tmp;
    }

    public ArrayList<String> getAllVectorAttributes()
    {
        ArrayList<String> tmp = new ArrayList<String>();
        for(String s : getAllChangedAttributes())
        {
            if(getVar(s.split("-")[0].trim()).getAngle() != null) tmp.add(s);
        }
        return tmp;
    }

    /**
     * @return arraylist of ALL attributes
     */
    public ArrayList<String> getAllAttributes()
    {
        ArrayList<String> tmp = new ArrayList<String>();
        ReadTextFile read_file = new ReadTextFile("variableNames.txt");
        
        String curr_line = read_file.readLine();
        while(!read_file.EOF())
        {
            tmp.add(curr_line);
            curr_line = read_file.readLine();
        }
        read_file.close();
        return tmp;
    }

    public ArrayList<String> getNumericalAttributes()
    {
        ArrayList<String> tmp = new ArrayList<String>();
        Calculate calc = new Calculate(this);
        for(String s : getCurrentAttributes())
        {
            try{
                if(!(s.equals("Pi") || s.equals("time") || s.equals("gravity")))
                {
                    String butts = calc.getFinalVarValue(s,"");
                    double irrelevantVariable = Double.parseDouble(butts);
                    tmp.add(s);
                }
            }catch(Exception e){}
        }
        return tmp;
    }

    public void run(String method)
    {
        switch(method)
        {
            case "Add Attribute":
            new VariableFrame(this, getAllAttributes(), "Add");
            break;

            case "Change Attribute":
            new VariableFrame(this, getAllChangedAttributes(), "Change");
            break;

            case "Delete Attribute":
            new VariableFrame(this, getCurrentAttributes(), "Delete");
            break;

            case "Delete Figure":
            Container.delete(this);
            break;

            case "Bring to Front":
            Container.bringToFront(this);
            break;

            case "Resize Figure":
            new ResizeFigurePanel(this);
            break;

            case "Show Vector Diagram":
            new VectorPanel(this);
            break;

            case "Hide Vector Diagrams":
            resetImage();
            break;

            default:
            break;
        }
    }

    /**
     * Draws vectors onto copy of the figure's Image
     * 
     * @param
     */
    public void drawVectors(String vector) throws Exception
    {
        //Reset the image
        //resetImage();
        //Get all variables of the type passed in
        ArrayList<Variable> vars = getVar(vector).getAllComponents();
        //A large canvas to paint on
        BufferedImage currentImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)currentImage.getGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0,0,1000,1000);
        //Scale the arrow to the base image
        int avg = (current.getWidth()+current.getHeight())/3;
        if(avg < 15) avg = 15;
        BufferedImage vectorImage = ImageIO.read(new File(System.getProperty("user.dir")+"//Media//vectorRed.png"));
        vectorImage = toBufferedImage(vectorImage.getScaledInstance(avg, avg*vectorImage.getHeight()/vectorImage.getWidth(), Image.SCALE_SMOOTH));
        vectorImage = toBufferedImage(makeColorTransparent(vectorImage, Color.WHITE));
        //Draw the base image
        g.drawImage(current, 500-current.getWidth()/2, 500-current.getHeight()/2, this);
        //Actually drawing the arrows
        for(Variable v : vars)
        {
            g.translate(500, 500);
            g.rotate(-1*Math.toRadians(v.getAngle()));
            g.drawImage(vectorImage, current.getWidth()/2, vectorImage.getHeight()/-2, this);
            g.rotate(Math.toRadians(v.getAngle()));
            g.translate(-500, -500);
        }
        //Finishing touches
        currentImage = shortenPic(currentImage);
        //Internal Figure Size
        setWidth(currentImage.getWidth());
        setHeight(currentImage.getHeight());
        //Extrnal JLabel Size
        setSize(currentImage.getWidth(), currentImage.getHeight());
        setImage(toBufferedImage(makeColorTransparent(currentImage, Color.WHITE)));
        imageChanged = true;
        revalidate();
    }

    /**
     * Crops icon out of jpanel
     */
    public static BufferedImage shortenPic(BufferedImage image)
    {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        ArrayList<Integer> arry = new ArrayList<Integer>();
        try{
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
                return image.getSubimage(min(arr), min(arry), max(arr)-min(arr), max(arry)-min(arry));
            }
        }catch(Exception e){e.printStackTrace();}
        return image;
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
     * Type casts an Image to a BufferedImage
     * 
     * @param img the Image object
     * @return img the BufferedImage object
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if(img instanceof BufferedImage)
            return (BufferedImage)img;
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    public void resetImage()
    {
        setWidth(img.getWidth());
        setHeight(img.getHeight());
        setImage(img);
        current = img;
        imageChanged = true;
    }

    public void mouseExited(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseReleased(MouseEvent e)
    {
        for(Popup p : Container.garbageBin)
            p.setVisible(false);
        if(SwingUtilities.isRightMouseButton(e))
        {
            String[] options = {"Add Attribute", "Change Attribute", "Delete Attribute", "Show Vector Diagram", "Hide Vector Diagrams", "Delete Figure", "Bring to Front", "Resize Figure"};
            new Popup(this, variables, options, new Point(e.getX()+getX(), e.getY()+Container.height/8+getY()));
        }
    }

    public void mouseClicked(MouseEvent e){}

    public void mousePressed(MouseEvent e){}

    public void mouseMoved(MouseEvent e){}

    public void mouseDragged(MouseEvent e){}
    
    public void setFV(ArrayList<Double> d)
    {
        FV = d;
    }
    
    public void setOFV(ArrayList<Double> d)
    {
        OFV = d;
    }
    
    public ArrayList<Double> getFV()
    {
        return FV;
    }
    
    public ArrayList<Double> getOFV()
    {
        return OFV;
    }
}
