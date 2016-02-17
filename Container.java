import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.Robot;
import javax.imageio.*;
import javax.swing.UIManager.*;
import javax.swing.filechooser.*;
import java.io.*;

/**
 * The Container Class designed to regulate all subcomponents
 * 
 * @author Chandler Sears
 * @author Derek Schafer
 * @date December 16th, 2014
 */
public class Container extends JPanel implements MouseListener, MouseMotionListener
{
    public JPanel field;
    static Snap snap;
    public static JFrame frame = new JFrame();
    public static ArrayList<Popup> garbageBin = new ArrayList<Popup>();
    public static Information info;
    public static int width, height, mouseX = 0, mouseY = 0;
    public int polyInitialX = 0, polyInitialY = 0, polyFinalX = 0, polyFinalY = 0;
    public static Toolbar t;
    public static boolean panMode = false, polyGraphing = false, isTriangle = false, jointSearching = false;
    public static ArrayList<Figure> figuresOnField = new ArrayList<Figure>();
    public static ArrayList<Polygon> polygonsOnField = new ArrayList<Polygon>();
    public static final Color THEME = new Color(120,120,120); // Current theme is Charcoal Gray
    public static ArrayList<GraphPanel> graphArray = new ArrayList<GraphPanel>();
    public static ArrayList<Vector> vectors = new ArrayList<Vector>();
    public static boolean onBoard = false, haveAddedFields = false;

    public Container()
    {
        UIManager.put("nimbusBlueGrey", THEME);
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Since it's an application, we need this so the other thread will stop.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int)screenSize.getWidth();
        height = (int)screenSize.getHeight();
        t = new Toolbar(this);
        t.setBounds(0,0,width,(int)height/8);
        add(t); 
        info = new Information();
        info.setBounds((int)(width*0.875), (int)height/8, width/8, height*7/8);
        add(info);
        // Variables

        // JFRAME setup
        //setSize(width,height);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.add(this);
        setLayout(null);
        setBounds(0,0,width, height);
        // JPANEL setup
        field = new JPanel();
        field.setBackground(Color.WHITE);
        field.setVisible(true);
        field.addMouseListener(this);
        field.addMouseMotionListener(this);
        field.setLayout(null);
        field.setBounds(0,height/8,(int)(width*0.875),height*7/8);

        add(field);
        addMouseListener(this);
    }

    /**
     * @return The height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @return The width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @return The field panel
     */
    public JPanel getField()
    {
        return field;
    }

    /**
     * @return the vectors on field
     */
    public ArrayList<Vector> getVectors()
    {
        return vectors;
    }

    /**
     * @param vector 
     */
    public static void addVector(Vector v)
    {
        vectors.add(v);
    }

    /**
     * An alternative to painting that takes a screenshot of the field, then 
     * repaints the bufferedimage onto said field.
     * 
     * @param g The Graphics object
     * @param field The field panel
     */
    public void redraw(JComponent panel)
    {
        try{
            int ytmp = (int)field.getLocation().getY();
            int xtmp = (int)field.getLocation().getX();
            Rectangle screenRect = new Rectangle(xtmp, ytmp, (int)field.getWidth(), (int)field.getHeight());
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            panel.repaint();
            panel.getGraphics().drawImage(capture, xtmp, ytmp, this);
        }catch(Exception e){e.printStackTrace();}
    }

    /**
     * This is constantly called when something changes on screen in order to show
     * a smooth graphics effect.
     * 
     * @param g Graphics Object
     */
    public void update() 
    {  
        info.repaint();
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    field.repaint();
                }});
        reconfigure();
        reshade();
        t.repaint();
    }

    /**
     * Brings a figure to the front by updating it last in
     * the figure queue
     * 
     * @param f The figure
     */
    public static void bringToFront(Figure f)
    {
        if(figuresOnField.indexOf(f) > -1)
            figuresOnField.remove(f);
        figuresOnField.add(figuresOnField.size(),f);
    }

    public void reconfigure()
    {
        for(final Figure f : figuresOnField)
        {
            f.setLocation(f.getX(), f.getY());
            f.getSnap().setLocation(f.getSnap().getX(), f.getSnap().getY());
            f.getSnap().setSize(10,10);
            if(!t.timePanel.isRunning())
            {
                field.add(f.getSnap());
                f.getSnap().repaint();
            }
            field.add(f);
            //Not sure if this is the best way, but it moves drawing the figures into the component drawing thread to line up with them
            javax.swing.SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        getGraphics().drawImage(f.getImage(), f.getX(), f.getY()+height/8, f.getWidth(), f.getHeight(), null);
                    }});
        }
    }

    public Snap selectJoint(ActionEvent evt)
    {
        jointSearching = true;
        for(Polygon p : polygonsOnField)
        {
            for(Snap s : p.getSnaps())
            {
                s.setColor(Color.ORANGE);
            }
        } 
        addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    if(evt.getSource() instanceof Snap)
                    {
                        removeMouseListener(this);
                    }
                }
            });

        for(Polygon p : polygonsOnField)
        {
            for(Snap s : p.getSnaps())
            {
                s.setColor(new Color(100,100,100));
            }
        } 
        return (Snap)evt.getSource();
    }

    /**
     * Redraws all polygon "snaps" and calls shade
     */
    public void reshade()
    {
        for(final Polygon p: polygonsOnField)
        {
            if(p.hasChanged())
            {

                p.getCenter().setLocation(p.getCenter().getX(), p.getCenter().getY());
                p.getCenter().setSize(10,10);
                field.add(p.getCenter());

                p.getCenter().repaint();
                //Added this and made p final
            }

            for(final Snap s : p.getSnaps())
            {
                field.add(s);
                s.setLocation(s.getX(),s.getY());
                s.setSize(10,10);
                s.repaint();
            }
            java.awt.EventQueue.invokeLater(new Runnable(){
                    public void run(){
                        p.shade(getGraphics());}
                });
        }

    }

    public static void addGraph(GraphPanel gp)
    {
        graphArray.add(gp);
    }

    public void disableAll()
    {
        for(Figure f : figuresOnField)
        {
            ImageFilter filter = new GrayFilter(true, 50);  
            ImageProducer producer = new FilteredImageSource(f.getImage().getSource(), filter);  
            f.setImage(toBufferedImage(Toolkit.getDefaultToolkit().createImage(producer)));  
            f.removeMouseListener(this);
            f.getSnap().setVisible(false);
        }

        for(Polygon p : polygonsOnField)
        {
            for(Snap s : p.getSnaps())
            {
                s.setVisible(false);
            }
            p.getCenter().setVisible(false);
        }
    }    

    public void enableAll()
    {
        for(Figure f : figuresOnField)
        {
            f.setImage(toBufferedImage(f.getImage()));
            f.getSnap().setVisible(true);
            f.addMouseListener(this);
        }

        for(Polygon p : polygonsOnField)
        {
            for(Snap s : p.getSnaps())
            {
                s.setVisible(true);
            }
            p.getCenter().setVisible(true);
        }
    }

    /**
     * Runs a diagnostic to check if the update method needs to be called
     * This is necessary so that update method doesn't constantly make the
     * screen flicker.
     */
    public void checkForUpdates()
    {
        //check all components for thier updates
        if(t.timePanel.isRunning())
        {
            double currentTime = t.getTime();

            ArrayList<Double> fieldVals = t.getFieldValues(); 
            ArrayList<Double> fEVO = t.getOldFieldValues();

            for(Figure f : figuresOnField)
            {
                //Field Management
                Double eF = 0.0, gF = 0.0, mF = 0.0;
                Double eA = 0.0, gA = 0.0, mA = 0.0;
                /*for(Figure f2 : figuresOnField)
                {
                    if(!f.equals(f2))
                    {
                        double distance = Math.sqrt((f.getX()-f2.getX())*(f.getX()-f2.getX())+(f.getY()-f2.getY())*(f.getY()-f2.getY()));
                        double tEF = 9000000000.0*Double.parseDouble(f2.getVar("electric charge").getValue().toString())/distance/distance;
                        double tEA = Math.atan2(f.getY()-f2.getY(),f2.getX()-f.getX());
                        eA = Math.toDegrees(Math.atan2(tEF*Math.sin(tEA) + eF*Math.sin(eA),tEF*Math.cos(tEA)+eF*Math.cos(eA)));
                        eF = Math.sqrt(Math.pow(tEF*Math.sin(tEA) + eF*Math.sin(Math.toRadians(eA)),2) + Math.pow(tEF*Math.cos(tEA)+eF*Math.cos(Math.toRadians(eA)),2));
                    }
                }
                double elecFieldMag = fieldVals.get(2), elecFieldAng = fieldVals.get(3);
                eA = Math.toDegrees(Math.atan2(elecFieldMag*Math.sin(elecFieldAng) + eF*Math.sin(eA),elecFieldMag*Math.cos(elecFieldAng)+eF*Math.cos(eA)));
                eF = Math.sqrt(Math.pow(elecFieldMag*Math.sin(elecFieldAng) + eF*Math.sin(Math.toRadians(eA)),2) + Math.pow(elecFieldMag*Math.cos(elecFieldAng)+eF*Math.cos(Math.toRadians(eA)),2));
                //fieldVals.set(2,eF); fieldVals.set(3,eA);*/
                //System.out.println("Mag: " + eF + " Angle: " + eA);

                f.setFV(fieldVals);
                if(!haveAddedFields)
                {
                    messWithFields(f, fieldVals, fEVO);
                }

                f.setOFV(fieldVals);

                //Velocity management
                if(f.getVar("velocityInitial") == null)
                    f.addValue("velocityInitial","1","0.0",0.0);
                //Movement Management
                f.getVar("time").setValue(currentTime);
                info.updateTabs(f);
                Variable disp = f.getVar("displacement");
                try{
                    f.getSnap().setX((int)(Double.parseDouble(disp.getValue().toString())*Math.cos(Math.toRadians(disp.getAngle())))+f.getSnap().getOriginalX()); 
                    f.getSnap().setY((int)(f.getSnap().getOriginalY() - Double.parseDouble(disp.getValue().toString())*Math.sin(Math.toRadians(disp.getAngle()))));
                }catch(Exception e){}
            }
            haveAddedFields = true;
        }
        else
        {
            haveAddedFields = false;
            for(Polygon p: polygonsOnField)
            {
                if(p.hasChanged())
                {
                    for(Snap s : p.getSnaps())
                    {
                        field.remove(s);
                    }
                    update();
                    break;
                }
            }
        }

        for(Figure f : figuresOnField)
        {
            if(f.hasChanged())
            {
                field.remove(f);
                f.setX(f.getSnap().getX());
                f.setY(f.getSnap().getY());
                field.remove(f.getSnap());
                update();
                if(!t.timePanel.isRunning())break;
            }
        }
    }

    /**
     * Returns all polygons instantiated on the field panel
     * 
     * @return A list of polygons
     */
    public static ArrayList<Polygon> getPolygonList()
    {
        return polygonsOnField;
    }

    /**
     * Adds a figure to the list of instantiated figures, as 
     * well as creates and updates the information panels for
     * them.
     * 
     * @param f The figure
     */
    public static void addFigure(Figure f)
    {
        info.createTab(f);
        figuresOnField.add(f);
    }

    public static void delete(Figure f)
    {
        info.deleteTab(f);
        f.setVisible(false);
        f.getSnap().setVisible(false);
        int pos = f.getTabValue();
        figuresOnField.remove(f);
        for(Figure fig: figuresOnField)
        {
            if(fig.getTabValue() > pos)
            {
                fig.setTabValue(fig.getTabValue()-1);
            }
        }
        f = null;
    }

    public static void delete(Polygon p)
    {
        polygonsOnField.remove(p);
        p.getCenter().setVisible(false);
        for(Snap s: p.getSnaps())
        {
            s.setVisible(false);
        }
        p=null;
    }

    public static Figure getFigure(ImageIcon ii)
    {
        for(Figure f : figuresOnField)
        {
            if(f.getImage().equals(ii))
                return f;
        }
        return null;
    }

    /**
     * Adds a polygon to the list of instantiated polygons.
     * 
     * @param p The polygon
     */
    public static void addPolygon(Polygon p)
    {
        polygonsOnField.add(p);
    }

    /**
     * Returns the size of the list of all instantiated polygons on the 
     * field panel.
     * 
     * @returns the size of polygonsOnField
     */
    public static int getPolygons()
    {
        return polygonsOnField.size();
    }

    /**
     * Returns the size of the list of all instantiated figures on the 
     * field panel.
     * 
     * @returns the size of figuresOnField
     */
    public int getFigures()
    {
        return figuresOnField.size();
    }

    public static void run(String str){panMode = str.equals("Pan");}

    public static void appendPopup(Popup obj)
    {
        garbageBin.add(obj);
    }

    public void mouseExited(MouseEvent e){}

    public void mouseEntered(MouseEvent e)
    {
        for(Polygon p : polygonsOnField)
            p.shade(getGraphics());
    }

    public void mouseReleased(MouseEvent e)
    {
        polyGraphing = false;
        for(Popup p : garbageBin)
            p.setVisible(false);
        if(SwingUtilities.isRightMouseButton(e))
        {
            String[] mouseSettings = {"Pointer", "Pan"};
            new Popup(field, null, mouseSettings, new Point(e.getX(), e.getY()+height/8));
        }
        else
        {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        checkForUpdates();
    }

    public void mouseMoved(MouseEvent e)
    {
        if(panMode)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        else if(polyGraphing)
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        else
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void mouseDragged(MouseEvent e)
    {
        if(polyGraphing)
        {
            polyFinalX = e.getXOnScreen();
            polyFinalY = e.getYOnScreen();
            polygonsOnField.get(polygonsOnField.size()-1).setWidth(polyFinalX-polyInitialX);
            polygonsOnField.get(polygonsOnField.size()-1).setHeight(polyFinalY-polyInitialY);
        }

        if(panMode)
        {
            for(Figure f : figuresOnField)
            {
                f.getSnap().setX(f.getX()+mouseX-e.getXOnScreen());
                f.getSnap().setY(f.getY()+mouseY-e.getYOnScreen());
            }
            for(Polygon p : polygonsOnField)
            {
                p.getCenter().shift(mouseX-e.getXOnScreen(), mouseY-e.getYOnScreen());
            }
            mouseX = e.getXOnScreen();
            mouseY = e.getYOnScreen();
        }
        checkForUpdates();
    }

    public void mouseClicked(MouseEvent e){}

    public void mousePressed(MouseEvent e)
    {
        mouseX = e.getXOnScreen(); 
        mouseY = e.getYOnScreen();

        if(polyGraphing)
        {
            Polygon p = new Polygon(this, isTriangle, mouseX, mouseY-height/8, 1, 1, Color.BLUE);
            polygonsOnField.add(p);
        }
        polyInitialX = mouseX;
        polyInitialY = mouseY;
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

    /**
     * Removes all empty literals from an array and converts to an arrayList
     * 
     * @param the arraylist
     */
    public ArrayList<String> removeWhiteSpaceElements(String[] al)
    {
        ArrayList<String> al2 = new ArrayList<String>();
        String tmp = "";
        for(int i = 0 ; i < al.length; i++)
        {
            if(!al[i].equals("") && !al[i].equals("|"))
            {
                tmp += al[i];
            }
            else if(tmp.length() > 0)
            {
                al2.add(tmp);
                tmp = "";
            }
        }
        al2.add(tmp);
        return al2;
    }

    /**
     * Saves the current session
     */
    public static void save()
    {
        // Establish Saves Folder if does not exist
        File parentDirectory = new File(System.getProperty("user.dir")+"//Saves//");
        parentDirectory.mkdir();

        // Create a new save folder inside of /Saves 
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(parentDirectory);
        int retrival = chooser.showSaveDialog(null);
        int saveN = 1;
        File newSave = null;
        PrintWriter pw = null;
        File data = null;
        if(retrival == JFileChooser.APPROVE_OPTION) 
        {
            try{
                String name = chooser.getSelectedFile().getName(); // Temporary Value
                newSave = new File(chooser.getCurrentDirectory().getAbsolutePath()+"//"+ name +"//");
                while(!newSave.mkdir())
                {
                    name = name + saveN;
                    saveN++;
                    newSave = new File(chooser.getCurrentDirectory().getAbsolutePath()+"//"+ name +"//");
                }
                data = new File(newSave.getAbsolutePath()+"//objects.txt");
                pw = new PrintWriter(data.getAbsolutePath(), "UTF-8");
            }catch(Exception ex){}

            // Figures
            int count = 0;
            pw.println();
            for(Figure fig : figuresOnField)
            {
                count++;
                pw.println(count + "|" + fig.getX() + "|" + fig.getY());

                for(Variable v : fig.getVars())
                {
                    if(v.getAngle() != null)
                        pw.println("||" + v.getName() + "|" + v.getValue() + "|" + v.isConstant() + "|" + v.isEnvironmental() + "|" + v.isSolved() + "|" + v.getAngle());
                    else
                        pw.println("||" + v.getName() + "|" + v.getValue() + "|" + v.isConstant() + "|" + v.isEnvironmental() + "|" + v.isSolved());
                }

                File img = new File(newSave.getAbsolutePath()+"//"+count+".png");
                try{
                    img.createNewFile();
                    ImageIO.write(toBufferedImage(fig.getOriginalImage()), "png", img);
                }catch(Exception e){}
            }
            pw.println("~");

            // Polygons
            count = 0;
            for(Polygon poly : polygonsOnField)
            {
                count++;
                pw.print(count+"|");
                for(Snap snap : poly.getSnaps())
                {
                    pw.print(snap.getX()+"|"+snap.getY()+"|");
                }
                pw.println();
            }

            pw.flush();
            pw.close();
        }
    }

    public void load()
    {
        deleteAllFigures();
        deleteAllPolygons();

        // DELETE POLYGONS

        // Establish Saves Folder if does not exist
        File parentDirectory = new File(System.getProperty("user.dir")+"//Saves//");
        parentDirectory.mkdir();

        // Create a new save folder inside of /Saves 
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(parentDirectory);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File", "txt");
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);

        int retrival = chooser.showDialog(null, "Load");
        if(retrival == JFileChooser.APPROVE_OPTION) 
        {
            try(BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) 
            {
                String line = br.readLine(); // First Line is empty
                boolean readingFigs = true;
                ArrayList<Variable> vars = new ArrayList<Variable>();
                line = br.readLine();
                String fileName = "1";
                int x = 300, y = 300, w = 100, h = 100;
                while (line != null)
                {
                    if(readingFigs)
                    {
                        // Adds a value
                        if(line.length() > 2 && line.substring(0,2).equals("||"))
                        {
                            ArrayList<String> tmp = removeWhiteSpaceElements(line.trim().split("|"));
                            Variable v = new Variable(tmp.get(0), "~", tmp.get(1), tmp.get(2).equals("true"), tmp.get(3).equals("true"));
                            //Checking if var had been solved for before
                            v.setSolved(tmp.get(4).equals("true"));
                            //Adding it's angle, if it had one
                            if(tmp.size() > 5)v.setAngle(Double.parseDouble(tmp.get(5)));
                            vars.add(v);
                        }
                        // Inserts figure
                        else if(vars.size() > 0 && line.length() > 1)
                        {
                            ArrayList<String> tmp = removeWhiteSpaceElements(line.trim().split("|"));
                            Figure f = new Figure(vars, ImageIO.read(new File(chooser.getSelectedFile().getAbsolutePath().substring(0,chooser.getSelectedFile().getAbsolutePath().lastIndexOf("\\"))+"\\"+fileName+".png")),x,y);
                            figuresOnField.add(0,f);
                            info.createTab(f);
                            // Vars
                            vars = new ArrayList<Variable>();
                            x = Integer.parseInt(tmp.get(1));
                            y = Integer.parseInt(tmp.get(2));
                            fileName = tmp.get(0);
                        }
                        // Defines a figure
                        else if(line.length() > 2)
                        {
                            ArrayList<String> tmp = removeWhiteSpaceElements(line.trim().split("|"));
                            fileName = tmp.get(0);
                            x = Integer.parseInt(tmp.get(1));
                            y = Integer.parseInt(tmp.get(2));
                        }
                    }
                    else
                    {
                        ArrayList<String> tmp = removeWhiteSpaceElements(line.trim().split("|"));
                        fileName = tmp.get(0);
                        x = Integer.parseInt(tmp.get(1));
                        y = Integer.parseInt(tmp.get(2));
                    }
                    if(line.equals("~"))
                    {
                        readingFigs = false;
                        ArrayList<String> tmp = removeWhiteSpaceElements(line.trim().split("|"));
                        Figure f = new Figure(vars, ImageIO.read(new File(chooser.getSelectedFile().getAbsolutePath().substring(0,chooser.getSelectedFile().getAbsolutePath().lastIndexOf("\\"))+"\\"+fileName+".png")),x,y);
                        figuresOnField.add(0,f);
                        info.createTab(f);
                    }
                    line = br.readLine();
                }
            }catch(Exception e){/*(e.printStackTrace();*/}
        }
    }

    public static void deleteAllFigures()
    {
        for(int i = 0; i < figuresOnField.size();)
        {
            delete(figuresOnField.get(i));
        }
    }

    public static void deleteAllPolygons()
    {
        for(int i = 0; i < polygonsOnField.size();)
        {
            delete(polygonsOnField.get(i));
        }
    }

    private void messWithFields(Figure f, ArrayList<Double> fieldVals, ArrayList<Double> fieldValsOld)
    {

        if(f.getVar("electric field").isKnown())
        {
            if(!f.getVar("electric field").dynamicallyChange(fieldValsOld.get(2), fieldValsOld.get(3),fieldVals.get(2)+"",fieldVals.get(3)))
                f.addValue("electric field", "e", fieldVals.get(2),fieldVals.get(3));
            f.getVar("electric field").setEnvironmental(true);
        }
        else
        {
            f.getVar("electric field").setVandA(fieldVals.get(2),fieldVals.get(3));
            f.getVar("electric field").setEnvironmental(true);
        }
        //{
        if(f.getVar("acceleration").isKnown())
        {
            if(!f.getVar("acceleration").dynamicallyChange(fieldValsOld.get(0), fieldValsOld.get(1),fieldVals.get(0)+"",fieldVals.get(1)))
                f.addValue("acceleration", "g", fieldVals.get(0),fieldVals.get(1));

            f.getVar("acceleration").setEnvironmental(true);
        }
        else if(fieldVals.get(0) != 0.0)
        {
            f.getVar("acceleration").setVandA(fieldVals.get(0),fieldVals.get(1));
            f.getVar("acceleration").setEnvironmental(true);
        }
        //}

        if(f.getVar("magnetic field").isKnown())
        {
            if(!f.getVar("magnetic field").dynamicallyChange(fieldValsOld.get(4), fieldValsOld.get(5),fieldVals.get(4)+"",fieldVals.get(5)))
                f.addValue("magnetic field", "m", fieldVals.get(4),fieldVals.get(5));

            f.getVar("magnetic field").setEnvironmental(true);
        }
        else
        {
            f.getVar("magnetic field").setVandA(fieldVals.get(4),fieldVals.get(5));
            f.getVar("magnetic field").setEnvironmental(true);
        }

    }
}