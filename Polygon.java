import java.awt.Color;
import java.awt.Graphics;

public class Polygon
{
    int width = 1, height = 1, num = 4, xPos = 200, yPos = 200, xi = 0, yi = 0;
    public Snap[] arr;
    public Snap center;
    public Color color;
    public boolean isTriangle;
    public int[] xPoints, yPoints, xPointsShift, yPointsShift;
    Container c;

    public Polygon(Container c, boolean isTriangle, int xi, int yi, int width, int height, Color color)
    {
        this.color = color;
        this.width = Math.abs(width);
        this.c = c;
        this.height = Math.abs(height);
        this.isTriangle = isTriangle;
        this.xi = xi;
        this.yi = yi;
        center = new CenterSnap(xi, yi, this);
        num = isTriangle ? 3 : 4;
        xPoints = new int[num];
        yPoints = new int[num];
        xPointsShift = new int[num];
        yPointsShift = new int[num];
        create();
    }

    /**
     * @return an array of Snaps
     */
    public Snap[] getSnaps()
    {
        return arr;
    }

    /**
     * Updates all snap positions by changing them to revolve around the center Snap
     */
    public void updateSnapPositions()
    {
        for(Snap s : arr)
        {
            s.shift(center.getX() - xPos, center.getY() - yPos);
        }
        xPos = center.getX();
        yPos = center.getY();
    }

    /**
     * Calls updateSnapPositions() and returns the Center Snap
     * 
     * @return the center Snap
     */
    public Snap getCenter()
    {
        updateSnapPositions();
        return center;
    }

    /**
     * Tests if the center has moved at all, then tests for each of the Snaps
     * 
     * @return if any Snap has moved
     */
    public boolean hasChanged()
    {
        if(!(center.getX() == xPos && center.getY() == yPos))
        {
            return true;
        }

        for(int index = 0; index < arr.length; index++)
        {
            if(arr[index].isPressed()) return true;
        }
        return false;
    }

    /**
     * Sets length of each side
     * 
     * @param len the Length
     */
    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public double getWidth()
    {
        return width;
    }

    public double getHeight()
    {
        return height;
    }

    /**
     * Defines an array of Snap Objects
     */
    public void create()
    {
        arr = new Snap[num];
        // Create Points          
        arr[0] = new Snap(xi,yi, this);
        arr[1] = new Snap(xi, yi+height, this);
        if(isTriangle)
            arr[2] = new Snap(xi+width, yi+height, this);
        else
        {
            arr[2] = new Snap(xi+width, yi+height, this);
            arr[3] = new Snap(xi+width, yi, this);
        }
    }
    
    public void shade(Graphics g)
    {
        if(Container.polyGraphing)
        {
            arr[0].setPosition(xi,yi);
            arr[1].setPosition(xi, yi+height);
            if(isTriangle)
                arr[2].setPosition(xi+width, yi+height);
            else
            {
                arr[2].setPosition(xi+width, yi+height);
                arr[3].setPosition(xi+width, yi);
            }
            center.setX(xi+width/2);
            center.setY(yi+height/2);
        }
        for(int i = 0; i < arr.length; i++)
        {
            xPoints[i] = arr[i].getX();
            yPoints[i] = arr[i].getY();
            xPointsShift[i] = xPoints[i] + 5;
            yPointsShift[i] = yPoints[i] + Container.height/8 + 5;
        }
        g.drawPolygon(xPointsShift, yPointsShift, num);
    }
}