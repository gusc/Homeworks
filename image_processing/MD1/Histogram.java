/**
 * Histogram normalization homework
 *
 * @author Gusts Kaksis, gk17025
 */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Histogram
{
    //
    // The magic happens in this function
    //

    private static BufferedImage histogramNormalization(BufferedImage image)
    {
        int[][] histogram = new int[3][256]; // Allocate histogram array for 3 colors Red, Green, Blue (color values 0-255)

        // Calculate histogram
        for (int x = 0; x < image.getWidth(); x ++)
        {
            for (int y = 0; y < image.getHeight(); y ++)
            {
                // Read the pixel data
                int px = image.getRGB(x, y);

                // The pixel is packed into a 32-bit value, so we need to separate the colors into individual bands.
                int px_r = ((px >> 16) & 0xFF);
                int px_g = ((px >> 8) & 0xFF);
                int px_b = (px & 0xFF);

                // Increment each color intensity value of histogram array
                histogram[0][px_r]++; // i.e. increment the red histogram at the intensity of current red pixel
                histogram[1][px_g]++;
                histogram[2][px_b]++;
            }
        }

        // Make histogram lineary incremented
        for (int i = 1; i < 256; i ++)
        {
            histogram[0][i] += histogram[0][i - 1]; // Add previous intensity counter to current one
            histogram[1][i] += histogram[1][i - 1];
            histogram[2][i] += histogram[2][i - 1];
        }
        // Normalize values in the range 0-255
        int px_count = image.getWidth() * image.getHeight();
        for (int i = 0; i < 256; i ++)
        {
            histogram[0][i] = (histogram[0][i] * 255) / px_count;
            histogram[1][i] = (histogram[1][i] * 255) / px_count;
            histogram[2][i] = (histogram[2][i] * 255) / px_count;
        }

        // Normalize the image

        for (int x = 0; x < image.getWidth(); x ++)
        {
            for (int y = 0; y < image.getHeight(); y++)
            {
                // Read the pixel data (again)
                int px = image.getRGB(x, y);

                // The pixel is packed into a 32-bit value, so we need to separate the colors into individual bands.
                int px_r = ((px >> 16) & 0xFF);
                int px_g = ((px >> 8) & 0xFF);
                int px_b = (px & 0xFF);

                // Now read the normalized value from the histogram
                px_r = histogram[0][px_r];
                px_g = histogram[0][px_g];
                px_b = histogram[0][px_b];

                // Pack the values together
                px = 0xFF000000 | (px_r << 16) | (px_g << 8) | px_b;

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
                img = histogramNormalization(img);
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
