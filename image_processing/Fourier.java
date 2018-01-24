/**
 * Fourier transform filter homework
 *
 * @author Gusts Kaksis, gk17025
 */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Fourier
{

    /**
     * Convert buffered image into an array consisting of real and imaginary image
     * with separated colors in range [0; 1]
     * @param image
     * @return array
     */
    private static double[][][][] Img2Arr(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        double[][][][] arr = new double[2][w][h][3]; // Real and imaginary parts, width, heigth, 3 colors

        for (int y = 0; y < h; y ++)
        {
            for (int x = 0; x < w; x++)
            {
                for (int c = 0; c < 3; c++) // Color loop as we're working with 3 colors separately
                {
                    int px = image.getRGB(x, y);
                    px = (px >> (8 * c)) & 0xFF;
                    arr[0][x][y][c] = px / 255.0;
                    arr[1][x][y][c] = 0.0;
                }
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
    private static void Arr2Img(BufferedImage image, double[][][][] arr) throws Exception
    {
        int w = image.getWidth();
        int h = image.getHeight();

        if (arr[0].length != w || arr[0][0].length != h)
        {
            throw new Exception("Wrong dimensions");
        }

        for (int y = 0; y < h; y ++)
        {
            for (int x = 0; x < w; x++)
            {
                int px = 0;
                for (int c = 0; c < 3; c++) // Color loop as we're working with 3 colors separately
                {
                    int pxval = (int)(arr[0][x][y][c] * 255.0);
                    px |= ((pxval & 0xFF) << (8 * c));
                }
                image.setRGB(x, y, px);
            }
        }
    }

    /**
     * Perform a discrete fourier transform on an array (real/imaginary parts, width, height, colors)
     *
     * @param img
     * @param w
     * @param h
     * @return
     */
    private static double[][][][] DFT(double[][][][] img, int w, int h)
    {
        double[][][][] dft = new double[2][w][h][3]; // Real and imaginary parts, width, heigth, 3 colors

        // Calculate horizontal lines
        for (int y = 0; y < h; y ++)
        {
            for (int ox = 0; ox < w; ox++)
            {
                for (int c = 0; c < 3; c ++)
                {
                    dft[0][ox][y][c] = 0.0;
                    dft[1][ox][y][c] = 0.0;
                }
                for (int ix = 0; ix < w; ix++)
                {
                    double a = 2 * Math.PI * ox * ix / w;
                    double i = Math.pow(-1, ix);
                    for (int c = 0; c < 3; c++) // Color loop as we're working with 3 colors separately
                    {
                        double pxr = img[0][ix][y][c];
                        dft[0][ox][y][c] +=  i * pxr * Math.cos(a);
                        dft[1][ox][y][c] += -i * pxr * Math.sin(a);
                    }
                }
                for (int c = 0; c < 3; c ++)
                {
                    dft[0][ox][y][c] /= w;
                    dft[1][ox][y][c] /= w;
                }
            }
        }

        double[][][][] dft2 = new double[2][w][h][3]; // Real and imaginary parts, width, heigth, 3 colors

        // Calculate vertical lines
        for (int x = 0; x < w; x ++)
        {
            for (int oy = 0; oy < h; oy++)
            {
                for (int c = 0; c < 3; c ++)
                {
                    dft2[0][x][oy][c] = 0.0;
                    dft2[1][x][oy][c] = 0.0;
                }
                for (int iy = 0; iy < h; iy++)
                {
                    double a = 2 * Math.PI * iy * oy / h;
                    double i = Math.pow(-1, iy);
                    for (int c = 0; c < 3; c++) // Color loop as we're working with 3 colors separately
                    {
                        double pxr = dft[0][x][iy][c];
                        double pxi = dft[1][x][iy][c];
                        dft2[0][x][oy][c] +=  pxr * Math.cos(a) + pxi * Math.sin(a);
                        dft2[1][x][oy][c] += -pxr * Math.sin(a) + pxi * Math.cos(a);
                    }
                }
                for (int c = 0; c < 3; c ++)
                {
                    dft2[0][x][oy][c] /= h;
                    dft2[1][x][oy][c] /= h;
                }
            }
        }

        return dft2;
    }

    /**
     * Perform an inverse discrete fourier transform on an array (real/imaginary parts, width, height, colors)
     *
     * @param dft
     * @param w
     * @param h
     * @return
     */
    private static double[][][][] iDFT(double[][][][] dft, int w, int h)
    {
        double[][][][] arr = new double[2][w][h][3];

        // Calculate vertical lines
        for (int x = 0; x < w; x ++)
        {
            for (int oy = 0; oy < h; oy++)
            {
                double i = Math.pow(-1, oy);
                for (int c = 0; c < 3; c ++)
                {
                    arr[0][x][oy][c] = 0.0;
                    arr[1][x][oy][c] = 0.0;
                }
                for (int iy = 0; iy < h; iy++)
                {
                    double a = 2 * Math.PI * iy * oy / h;
                    for (int c = 0; c < 3; c++) // Color loop as we're working with 3 colors separately
                    {
                        double pxr = dft[0][x][iy][c];
                        double pxi = dft[1][x][iy][c];
                        arr[0][x][oy][c] += pxr * Math.cos(a) - pxi * Math.sin(a);
                        arr[1][x][oy][c] += pxr * Math.sin(a) + pxi * Math.cos(a);
                    }
                }
            }
        }

        double[][][][] arr2 = new double[2][w][h][3];

        // Calculate horizontal lines
        for (int y = 0; y < h; y ++)
        {
            for (int ox = 0; ox < w; ox++)
            {
                double i = Math.pow(-1, ox);
                for (int c = 0; c < 3; c ++)
                {
                    arr2[0][ox][y][c] = 0.0;
                    arr2[1][ox][y][c] = 0.0;
                }
                for (int ix = 0; ix < w; ix++)
                {
                    double a = 2 * Math.PI * ix * ox / w;
                    for (int c = 0; c < 3; c++) // Color loop as we're working with 3 colors separately
                    {
                        double pxr = arr[0][ix][y][c];
                        double pxi = arr[1][ix][y][c];
                        arr2[0][ox][y][c] += pxr * Math.cos(a) - pxi * Math.sin(a);
                        arr2[1][ox][y][c] += pxr * Math.sin(a) + pxi * Math.cos(a);
                    }
                }
                for (int c = 0; c < 3; c ++)
                {
                    arr2[0][ox][y][c] *= i;
                    arr2[1][ox][y][c] *= i;
                }
            }
        }

        return arr2;
    }

    /**
     * Some dummy fourier filter that just performs DFT on an image, filters out some data and then
     * performs iDFT on the data generating an image back
     * @param image
     * @return
     */
    private static BufferedImage fourierFilter(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        // Calculate the fourier transform
        double[][][][] arr = Img2Arr(image);
        double[][][][] dft = DFT(arr, w, h);

        // Do some data filtering
        // Comment out this block to see a correct DFT-iDFT calculations
        for (int x = 0; x < w; x ++)
        {
            for (int y = 0; y < h; y++)
            {
                for (int c = 0; c < 3; c++)
                {
                    if (x > w / 2 || y > h / 2)
                    {
                        // This should filter out all the higher frequency content
                        dft[0][x][y][c] = 0;
                        dft[1][x][y][c] = 0;
                    }
                }
            }
        }

        // Create a filtered image and return
        BufferedImage image2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        try
        {
            arr = iDFT(dft, w, h);
            Arr2Img(image2, arr);
        }
        catch (Exception e)
        {

        }

        return image2;
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
                img = fourierFilter(img);
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
