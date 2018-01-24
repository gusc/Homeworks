/**
 * Hue correction
 *
 * @author Gusts Kaksis, gk17025
 */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Hue
{
    /**
     * Class to convert RGB into HSI color space and back
     */
    static class HSIColor
    {
        private double r;
        private double g;
        private double b;

        HSIColor(int color)
        {
            r = ((color >> 16) & 0xFF) / 255.0;
            g = ((color >> 8) & 0xFF) / 255.0;
            b = (color & 0xFF) / 255.0;
        }

        /**
         * Convert RGB to hue component of HSI color space
         * @return double in range [0; 2pi)
         */
        public double getHue()
        {
            return Math.atan2(Math.sqrt(3) * (g - b), (2.0 * r - g - b));
        }

        private double getMin()
        {
            double min = Math.min(r, g);
            return Math.min(min, b);
        }
        /**
         * Convert RGB to saturation component of HSI color space
         * @return double in range [0; 1]
         */
        public double getSaturation()
        {
            double i = getIntensity();
            if (i > 0.0)
            {
                return 1.0 - getMin() / i;
            }
            return 0.0;
        }
        /**
         * Convert RGB to intensity component of HSI color space
         * @return double in range [0; 1]
         */
        public double getIntensity()
        {
            return (r + g + b) / 3.0;
        }

        /**
         * Convert double [0; 1] values to int [0; 255] values with normalization
         * @param channel - color intensity in range [0; 1]
         * @return
         */
        private int normalize(double channel)
        {
            //
            int c = (int)(channel * 255.0);
            if (c > 255)
            {
                c = 255;
            }
            else if (c < 0)
            {
                c = 0;
            }
            return c;
        }
        /**
         * Set new hue angle
         * @param hue - angle in radians
         */
        public void setHue(double hue)
        {
            // Normalize hue in range [0; 2pi)
            while (hue < 0)
            {
                hue += 2.0 * Math.PI;
            }
            hue = hue % (2.0 * Math.PI);

            // Convert HSI to RGB
            double i = getIntensity();
            double s = getSaturation();
            double a = i * (1.0 - s);
            if (hue < 2.0 * Math.PI / 3.0) // 0 - 120 degrees
            {
                b = a;
                r = i * (1.0 + s * Math.cos(hue) / Math.cos(Math.PI / 3.0 - hue));
                g = 3.0 * i - (b + r);
            }
            else if (hue < 4.0 * Math.PI / 3.0) // 120 - 240 degrees
            {
                r = a;
                g = i * (1.0 + s * Math.cos(hue - 2.0 * Math.PI / 3.0) / Math.cos(Math.PI - hue));
                b = 3.0 * i - (r + g);
            }
            else // 240 - 360 degrees
            {
                g = a;
                b = i * (1.0 + s * Math.cos(hue - 4.0 * Math.PI / 3.0) / Math.cos(5.0 * Math.PI / 3.0 - hue));
                r = 3.0 * i - (g + b);
            }
        }
        /**
         * Get RGB pixel value compozite
         * @return
         */
        public int getRGB()
        {
            int color = 0;
            color |= ((normalize(r) & 0xFF) << 16);
            color |= ((normalize(g) & 0xFF) << 8);
            color |= (normalize(b) & 0xFF);
            return color;
        }
    }
    /**
     * Translate image colors using hue angle rotation
     *
     * @param image
     * @param hueAngle - angle in degrees (0-360)
     * @return
     */
    private static BufferedImage changeHue(BufferedImage image, int hueAngle)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        for (int y = 0; y < h; y ++)
        {
            for (int x = 0; x < w; x ++)
            {
                int px = image.getRGB(x, y);

                HSIColor c = new HSIColor(image.getRGB(x, y));

                // Do the hue rotation
                double hue = c.getHue();
                hue += hueAngle * Math.PI / 180.0;
                c.setHue(hue);

                image.setRGB(x, y, c.getRGB());
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

            int angle = 0;
            if (args.length > 1)
            {
                angle = Integer.parseInt(args[1]);
                if (angle < 2)
                {
                    angle = 2;
                }
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
                // Perform hue rotation
                img = changeHue(img, angle);
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
