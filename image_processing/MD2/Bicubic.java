/**
 * Bicubic resize
 *
 * @author Gusts Kaksis, gk17025
 */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Bicubic
{
    /**
     * 1D cubic resize
     *
     * @param row
     * @param size
     * @param scale
     * @return
     */
    private static int[] bicubicResize1D(int[] row, int size, int scale)
    {
        // Create scaled array
        int[] row_out = new int[size * scale];

        for (int x = 0; x < size; x ++)
        {
            // Get neighbouring pixel array
            int[] npx = new int[4];
            for (int nx = x - 1, ox = 0; nx < x + 3; nx++, ox++)
            {
                int nnx = nx;
                if (nnx < 0)
                {
                    nnx = 0;
                }
                else if (nnx >= size)
                {
                    nnx = size - 1;
                }
                npx[ox] = row[nnx];
            }

            // Scale interpolation loop
            for (int sx = 0; sx < scale; sx ++)
            {
                int px = 0;

                // Color loop
                for (int c = 0; c < 3; c ++)
                {
                    double npx0 = ((npx[0] >> (c * 8)) & 0xFF);
                    double npx1 = ((npx[1] >> (c * 8)) & 0xFF);
                    double npx2 = ((npx[2] >> (c * 8)) & 0xFF);
                    double npx3 = ((npx[3] >> (c * 8)) & 0xFF);

                    double dx = (double)sx / (double)scale;

                    // https://en.wikipedia.org/wiki/Cubic_Hermite_spline#Interpolation_on_the_unit_interval_with_matched_derivatives_at_endpoints
                    // Errors at dx=0
//                    double ipx = (2.0 * Math.pow(dx, 1) - Math.pow(dx, 3) - dx) * npx0;
//                    ipx += (3.0 * Math.pow(dx, 3) - 5.0 * Math.pow(dx, 2) + 2.0) * npx1;
//                    ipx += (4.0 * Math.pow(dx, 2) - 3.0 * Math.pow(dx, 3) + dx) * npx2;
//                    ipx += (Math.pow(dx, 3) - Math.pow(dx, 2)) * npx3;
//                    ipx /= 2.0;

                    // https://en.wikipedia.org/wiki/Cubic_Hermite_spline#Unit_interval_(0,_1)
                    // Not really cubic, more like linear, because uses only nearest neighbour pixels for interpolation
//                    double ipx = (2 * Math.pow(dx, 3) - 3 * Math.pow(dx, 2) + 1) * npx1;
//                    ipx += (Math.pow(dx, 3) - 2 * Math.pow(dx, 2) + dx) * (npx1 - npx2) / 2;
//                    ipx += (-2 * Math.pow(dx, 3) + 3 * Math.pow(dx, 2)) * npx2;
//                    ipx += (Math.pow(dx, 3) - Math.pow(dx, 2)) * (npx1 - npx2) / 2;

                    // Found somewhere on the internet
                    // Errors in neighbouring areas - not as smooth as should be
                    dx /= 2.0; // the delta x is between 4 points
                    double ipx = dx * (3.0 * (npx1 - npx2) + npx3 - npx0);
                    ipx += dx * (2.0 * npx0 - 5.0 * npx1 + 4.0 * npx2 - npx3 + ipx);
                    ipx += npx1 + 0.5 * dx * (npx2 - npx0 + ipx);

                    if (ipx < 0.0)
                    {
                        ipx = 0;
                    }
                    else if (ipx > 255.0)
                    {
                        ipx = 255.0;
                    }

                    px |= ((((int)ipx) & 0xFF) << (c * 8));
                }

                row_out[(x * scale) + sx] = px;
            }
        }

        return row_out;
    }

    /**
     * 2D bicubic resize
     *
     * @param image
     * @param scale
     * @return
     */
    private static BufferedImage bicubicResize(BufferedImage image, int scale)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        int wn = w * scale;
        int hn = h * scale;

        BufferedImage image2 = new BufferedImage(wn, hn, BufferedImage.TYPE_INT_RGB);

        int[][]tmp = new int[h][w * scale];
        for (int y = 0; y < h; y ++)
        {
            // Get Row
            int[] row = new int[w];
            for (int x = 0; x < w; x++)
            {
                row[x] = image.getRGB(x, y);
            }
            tmp[y] = bicubicResize1D(row, w, scale);
        }

        for (int x = 0; x < w * scale; x ++)
        {
            // Get Column
            int[] col = new int[h];
            for (int y = 0; y < h; y++)
            {
                col[y] = tmp[y][x];
            }

            int[] col_out = bicubicResize1D(col, h, scale);

            for (int y = 0; y < h * scale; y ++)
            {
                image2.setRGB(x, y, col_out[y]);
            }
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

            int scale = 4;
            if (args.length > 1)
            {
                scale = Integer.parseInt(args[1]);
                if (scale < 2)
                {
                    scale = 2;
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
                // Perform bicubic resize to the scale of 4
                img = bicubicResize(img, scale);
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
