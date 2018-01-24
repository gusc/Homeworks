/**
 * Gaussian blur homework
 *
 * @author Gusts Kaksis, gk17025
 */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Gauss
{
    //
    // The magic happens in this function
    //

    private static BufferedImage gaussianBlur(BufferedImage image, double sigma)
    {
        double[][] mask = new double[7][7]; // Filter mask array
        int r = 3;
        int w = image.getWidth();
        int h = image.getHeight();
        double i = 1 / Math.pow(2 * r + 1, 2); // Additional intensity multiplier

        // As the mask is symetrical in 4 directions, we only calculate one and then mirror it
        for (int x = 0; x < r + 1; x ++)
        {
            for (int y = 0; y < r + 1; y ++)
            {
                double gauss = (1 / (2 * Math.PI * Math.pow(sigma, 2)));
                gauss *= Math.exp(0 - ((Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(sigma, 2))));
                mask[r + x][r + y] = gauss;
                mask[r - x][r + y] = gauss;
                mask[r + x][r - y] = gauss;
                mask[r - x][r - y] = gauss;
            }
        }
        double gsum = 0;
        for (int x = 0; x < 7; x ++)
        {
            for (int y = 0; y < 7; y ++)
            {
                gsum += mask[x][y];
            }
        }
        double multi = 1 / gsum; // Additional multiplier as the sum of all mask elements should be 1.0

        // Blur the image

        for (int x = 0; x < w; x ++)
        {
            for (int y = 0; y < h; y++)
            {
                // Start with an empty pixel
                double px_r = 0;
                double px_g = 0;
                double px_b = 0;

                // Blur by iterating each matrix pixel on top of corresponding image pixel
                // with mirroring of edges
                for (int mx = 0; mx < r * 2 + 1; mx ++)
                {
                    for (int my = 0; my < r * 2 + 1; my ++)
                    {
                        // Get the image pixel corrdinates by mirroring the image for the mask overflows
                        int mxx = Math.abs(x + mx - r);
                        mxx = (mxx >= w ? (w * 2) - mxx - 1 : mxx);
                        int myy = Math.abs(y + my - r);
                        myy = (myy >= h ? (h * 2) - myy - 1 : myy);

                        // Read the corresponding image pixel
                        int mpx = image.getRGB(mxx, myy);
                        double mpx_r = (double)((mpx >> 16) & 0xFF);
                        double mpx_g = (double)((mpx >> 8) & 0xFF);
                        double mpx_b = (double)(mpx & 0xFF);

                        // Calculate the image pixel from adjecent pixels
                        px_r += mpx_r * mask[mx][my] * multi;
                        px_g += mpx_g * mask[mx][my] * multi;
                        px_b += mpx_b * mask[mx][my] * multi;
                    }
                }

                // Pack the values together and save to image
                int px = 0xFF000000 | ((int)px_r << 16) | ((int)px_g << 8) | (int)px_b;
                image.setRGB(x, y, px);
            }
        }
        return image;
    }

    //
    // Path processing functions
    //

    private static String getBaseName(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

    private static String getExt(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return "";
        } else {
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

            double sigma = 1.f;
            if (args.length > 1)
            {
                sigma = Double.parseDouble(args[1]);
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
                // Perform histogram normalization
                img = gaussianBlur(img, sigma);
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
