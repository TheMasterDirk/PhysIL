import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.File;

import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Graphics2D;

public class DrawImportFrame extends JFrame
{
    final JFileChooser fc;
    String path;
    public DrawImportFrame()
    {
        fc = new JFileChooser(System.getProperty("user.dir")+"//Sample Pictures");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG, PNG, BMP", "jpg", "png", "bmp");
        fc.setFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            path = fc.getSelectedFile().getAbsolutePath();
            try{
                BufferedImage img = ImageIO.read(new File(path));
                int w = img.getWidth();
                int h = img.getHeight();
                while(w > Container.width/3 || h > Container.height/3)
                {
                    w -= 1;
                    h -= h/w;
                }            
                img = toBufferedImage(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
                Container.addFigure(new Figure(toBufferedImage(makeColorTransparent(img, Color.WHITE))));
            }catch(Exception e){e.printStackTrace();}
        }
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
}