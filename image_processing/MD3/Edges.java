/**
 * Edge detection using Sobel, Prewit and Laplace operators
 *
 * @author Gusts Kaksis, gk17025
 */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Edges
{

    enum EdgeMethod
    {
        SOBEL,
        PREWIT,
        LAPLACE
    };

    static class RGBColor
    {
        double r;
        double g;
        double b;

        RGBColor(int color)
        {
            r = ((color >> 16) & 0xFF) / 255.0;
            g = ((color >> 8) & 0xFF) / 255.0;
            b = (color & 0xFF) / 255.0;
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
     * Convert buffered image into a double array of RGBColor objects
     * with separated colors in range [0; 1]
     * @param image
     * @return array
     */
    private static RGBColor[][] Img2Arr(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        RGBColor[][] arr = new RGBColor[w][h];

        for (int x = 0; x < w; x ++)
        {
            for (int y = 0; y < h; y++)
            {
                arr[x][y] = new RGBColor(image.getRGB(x, y));
            }
        }

        return arr;
    }

    /**
     * Convert the array back into a buffered image
     * with separated colors in range [0; 1]
     * @param image
     * @return array
     */
    private static void Arr2Img(BufferedImage image, RGBColor[][] arr) throws Exception
    {
        int w = image.getWidth();
        int h = image.getHeight();

        if (arr.length != w || arr[0].length != h)
        {
            throw new Exception("Wrong dimensions");
        }

        for (int x = 0; x < w; x ++)
        {
            for (int y = 0; y < h; y++)
            {
                image.setRGB(x, y, arr[x][y].getRGB());
            }
        }
    }

    /**
     * Normalize (compress) color values in range [0; 1.0] by determining the minimum and maximum values
     * and lineary distributing them
     * @param input
     * @return
     */
    private static RGBColor[][] normalizeColors(RGBColor[][] input)
    {
        // Get min/max bounds
        double min = 255.0;
        double max = 0.0;
        for (int x = 0; x < input.length; x ++)
        {
            for (int y = 0; y < input[x].length; y ++)
            {
                min = Math.min(min, input[x][y].r);
                min = Math.min(min, input[x][y].g);
                min = Math.min(min, input[x][y].b);
                max = Math.max(max, input[x][y].r);
                max = Math.max(max, input[x][y].g);
                max = Math.max(max, input[x][y].b);
            }
        }

        // Calculate multiplier
        double mult = 1.0 / (max - min);

        // Normalize color range
        for (int x = 0; x < input.length; x ++)
        {
            for (int y = 0; y < input[x].length; y ++)
            {
                input[x][y].r = (input[x][y].r - min) * mult;
                input[x][y].g = (input[x][y].g - min) * mult;
                input[x][y].b = (input[x][y].b - min) * mult;
            }
        }

        return input;
    }

    private static RGBColor[][] maskFilter(RGBColor[][] input, double[][] mask)
    {
        int w =  input.length;
        int h;
        RGBColor[][] output = new RGBColor[w][];

        for (int x = 0; x < w; x++)
        {
            h = input[x].length;
            output[x] = new RGBColor[h];

            for (int y = 0; y < h; y ++)
            {
                // Start with an empty pixel
                RGBColor c = new RGBColor(0);

                // Calculate mask center offsets
                int xoff = (int)Math.floor(mask.length / 2);
                int yoff = (int)Math.floor(mask[0].length / 2);

                // Apply mask on the pixel
                for (int mx = 0; mx < mask.length; mx ++)
                {
                    for (int my = 0; my < mask[mx].length; my ++)
                    {
                        // Get the image pixel corrdinates by mirroring the image for the mask overflows
                        int mxx = Math.abs(x + mx - xoff);
                        mxx = (mxx >= w ? (w * 2) - mxx - 1 : mxx);
                        int myy = Math.abs(y + my - yoff);
                        myy = (myy >= h ? (h * 2) - myy - 1 : myy);

                        // Read the corresponding image pixel
                        RGBColor mpx = input[mxx][myy];

                        // Calculate the image pixel from adjecent pixels
                        c.r += mpx.r * mask[mx][my];
                        c.g += mpx.g * mask[mx][my];
                        c.b += mpx.b * mask[mx][my];
                    }
                }

                // Normalize the colors
                c.r = Math.abs(c.r);
                c.g = Math.abs(c.g);
                c.b = Math.abs(c.b);

                // Write the pixel value to image
                output[x][y] = c;
            }
        }

        return output;
    }

    private static double[][] operatorRotateCCW(double[][] operator)
    {
        int opLen = operator.length;
        double[][] operator2 = new  double[opLen][opLen];
        for (int x = 0; x < opLen; x++)
        {
            for (int y = 0; y < operator[x].length; y++)
            {
                operator2[opLen - x - 1][y] = operator[y][x];
            }
        }
        return operator2;
    }

    private static RGBColor[][] sumImages(RGBColor[][] image1, RGBColor[][] image2)
    {
        int w = image1.length;
        int h;
        RGBColor[][] image3 = new RGBColor[w][];
        for (int x = 0; x < w; x++)
        {
            h = image1[x].length;
            image3[x] = new RGBColor[h];
            for (int y = 0; y < h; y++)
            {
                // Perform g = sqrt(gx ^ 2 + gy ^ 2)
                RGBColor c = new RGBColor(0);
                RGBColor c1 = image1[x][y];
                RGBColor c2 = image2[x][y];
                c.r = Math.sqrt(Math.pow(c1.r, 2.0) + Math.pow(c2.r, 2.0));
                c.g = Math.sqrt(Math.pow(c1.g, 2.0) + Math.pow(c2.g, 2.0));
                c.b = Math.sqrt(Math.pow(c1.b, 2.0) + Math.pow(c2.b, 2.0));
                image3[x][y] = c;
            }
        }
        return image3;
    }

    /**
     * Find edges using Prewit operaor
     *
     * @param image
     * @return
     */
    private static BufferedImage findEdgesPrewit(BufferedImage image)
    {
        RGBColor[][] imageArr = Img2Arr(image);
        double[][] operator = new double[3][];
        operator[0] = new double[]{ -1.0, 0, 1.0 };
        operator[1] = new double[]{ -1.0, 0, 1.0 };
        operator[2] = new double[]{ -1.0, 0, 1.0 };
        RGBColor[][] gx = maskFilter(imageArr, operator);
        double[][] operator2 = operatorRotateCCW(operator);
        RGBColor[][] gy = maskFilter(imageArr, operator2);
        RGBColor[][] g = sumImages(gx, gy);
        g = normalizeColors(g);
        BufferedImage imageOut = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        try
        {
            Arr2Img(imageOut, g);
        }
        catch (Exception e)
        {

        }
        return imageOut;
    }
    /**
     * Find edges using Sobel operaor
     *
     * @param image
     * @return
     */
    private static BufferedImage findEdgesSobel(BufferedImage image)
    {
        RGBColor[][] imageArr = Img2Arr(image);
        double[][] operator = new double[3][];
        operator[0] = new double[]{ -1.0, 0, 1.0 };
        operator[1] = new double[]{ -2.0, 0, 2.0 };
        operator[2] = new double[]{ -1.0, 0, 1.0 };
        RGBColor[][] gx = maskFilter(imageArr, operator);
        double[][] operator2 = operatorRotateCCW(operator);
        RGBColor[][] gy = maskFilter(imageArr, operator2);
        RGBColor[][] g = sumImages(gx, gy);
        g = normalizeColors(g);
        BufferedImage imageOut = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        try
        {
            Arr2Img(imageOut, g);
        }
        catch (Exception e)
        {

        }
        return imageOut;
    }
    /**
     * Find edges using Laplace operaor
     *
     * @param image
     * @return
     */
    private static BufferedImage findEdgesLaplace(BufferedImage image)
    {
        RGBColor[][] imageArr = Img2Arr(image);
        double[][] operator = new double[3][];
        operator[0] = new double[]{ -1.0, -1.0, -1.0 };
        operator[1] = new double[]{ -1.0, 8.0, -1.0 };
        operator[2] = new double[]{ -1.0, -1.0, -1.0 };
        RGBColor[][] g = maskFilter(imageArr, operator);
        g = normalizeColors(g);
        BufferedImage imageOut = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        try
        {
            Arr2Img(imageOut, g);
        }
        catch (Exception e)
        {

        }
        return imageOut;
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

            EdgeMethod method = EdgeMethod.LAPLACE;
            if (args.length > 1)
            {
                if (args[1].equals("sobel"))
                {
                    method = EdgeMethod.SOBEL;
                }
                else if (args[1].equals("prewit"))
                {
                    method = EdgeMethod.PREWIT;
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
                switch (method)
                {
                    case SOBEL:
                        // Find edges using Sobel operator
                        img = findEdgesSobel(img);
                        break;
                    case PREWIT:
                        // Find edges using Prewit operator
                        img = findEdgesPrewit(img);
                        break;
                    case LAPLACE:
                        // Find edges using Laplace operator
                        img = findEdgesLaplace(img);
                        break;
                }
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
