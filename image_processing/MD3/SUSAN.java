/**
 * SUSAN non-linear filter
 * Based on: http://www-2.dc.uba.ar/materias/ipdi/smith95susan.pdf
 *
 * @author Gusts Kaksis, gk17025
 */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class SUSAN
{
    //
    // The magic happens in this function
    //

    private static double[] getRGB(BufferedImage image, int x, int y)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        // Get the image pixel corrdinates by mirroring the image for the mask overflows
        x = Math.abs(x);
        x = (x >= w ? (w * 2) - x - 1 : x);
        y = Math.abs(y);
        y = (y >= h ? (h * 2) - y - 1 : y);

        // Get the pixel color and translate it to range of [0;1]
        int px = image.getRGB(x, y);
        double px_r = (double)((px >> 16) & 0xFF) / 255.0;
        double px_g = (double)((px >> 8) & 0xFF) / 255.0;
        double px_b = (double)(px & 0xFF) / 255.0;

        return new double[] { px_r, px_g, px_b };
    }

    private static int getPx(double[] color)
    {
        int px_r = (int)(color[0] * 255.0);
        int px_g = (int)(color[1] * 255.0);
        int px_b = (int)(color[2] * 255.0);
        return (0xFF000000 | (px_r << 16) | (px_g << 8) | px_b);
    }

    private static BufferedImage susanFilter(BufferedImage image, double sigma, double tau)
    {
        int w = image.getWidth();
        int h = image.getHeight();
        int mr = 3;

        BufferedImage image2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y ++)
        {
            for (int x = 0; x < w; x++)
            {
                // Start with the default pixel value
                double[] color = getRGB(image, x, y);
                int msize = mr * 2 + 1;
                double[] dividend = {0.0, 0.0, 0.0};
                double[] divisor = {0.0, 0.0, 0.0};

                // Filter values
                for (int my = 0; my < msize; my++)
                {
                    for (int mx = 0; mx < msize; mx++)
                    {
                        if (!(mx - mr == 0 && my - mr == 0))
                        {
                            double r2 = Math.pow(mx - mr, 2.0) + Math.pow(my - mr, 2.0);
                            double[] mcolor = getRGB(image, x + mx - mr, y + my - mr);
                            for (int c = 0; c < 3; c++)
                            {
                                // Gausian blur part
                                double gauss = r2 / (2.0 * Math.pow(sigma, 2.0));
                                // Edge detection part
                                double edge = Math.pow(mcolor[c] - color[c], 2.0) / Math.pow(tau, 2.0);
                                // SUSAN
                                double susan = Math.exp(0.0 - gauss - edge);

                                dividend[c] += mcolor[c] * susan;
                                divisor[c] += susan;
                            }
                        }
                    }
                }

                // Caluclate final pixel values
                for (int c = 0; c < 3; c++)
                {
                    color[c] = dividend[c] / divisor[c];
                }

                // Pack the values together and save to image
                image2.setRGB(x, y, getPx(color));
            }
        }
        return image2;
    }

    //
    // Path processing functions
    //

    private static String getBaseName(String fileName)
    {
        int index = fileName.lastIndexOf('.');
        if (index == -1)
        {
            return fileName;
        }
        else
        {
            return fileName.substring(0, index);
        }
    }

    private static String getExt(String fileName)
    {
        int index = fileName.lastIndexOf('.');
        if (index == -1)
        {
            return "";
        }
        else
        {
            return fileName.substring(index + 1);
        }
    }

    // Entry point
    public static void main(String[] args)
    {
        if (args.length > 0)
        {
            String imgPath = args[0];
            File imgFile = new File(imgPath);
            if (!imgFile.exists())
            {
                System.out.format("Image %s does not exist\n", imgPath);
                return;
            }

            double sigma = 2.f;
            if (args.length > 1)
            {
                sigma = Double.parseDouble(args[1]);
            }
            double tau = 0.2f;
            if (args.length > 2)
            {
                tau = Double.parseDouble(args[2]);
            }

            BufferedImage img = null;
            try
            {
                // Read image from file
                img = ImageIO.read(imgFile);
            }
            catch (IOException e)
            {
                System.out.format("Can't open image %s, exception thrown\n", imgPath);
                return;
            }

            if (img != null)
            {
                // Perform SUSAN filter
                img = susanFilter(img, sigma, tau);
            }
            else
            {
                System.out.format("Can't open image %s, null returned\n", imgPath);
                return;
            }

            String imgPathOut = getBaseName(imgPath) + "_out.png";
            File imgFileOut = new File(imgPathOut);
            try
            {
                // Write image to file
                ImageIO.write(img, "png", imgFileOut);
            }
            catch (IOException e)
            {
                System.out.format("Can't write image %s, exception thrown\n", imgPathOut);
                return;
            }

        }
    }
}
