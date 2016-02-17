/**
 * The Main runner of the package
 */
public class Runner extends Thread implements Runnable
{
    public static Container c;
    public int frameRate = 30; // In Hz

    public static void main(String[] args) throws Exception
    {
        c = new Container();
        new Runner().start();
    }

    public void run() 
    {
        try{
            c.checkForUpdates();
            while(c.isVisible())
            {
                sleep(1000/frameRate);
                c.checkForUpdates();
            }
        }catch(Exception e){e.printStackTrace();}
    }
}