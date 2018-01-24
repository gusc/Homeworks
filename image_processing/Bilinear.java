/**
 * Bilinear resize
 *
 * @author Gusts Kaksis, gk17025
 */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Bilinear
{
    /**
     * 1D linear resize
     *
     * @param row
     * @param size
     * @param scale
     * @return
     */
    private static int[] bilinearResize1D(int[] row, int size, int scale)
    {
        // Create scaled array
        int[] row_out = new int[size * scale];

        for (int x = 0; x < size; x ++)
        {
            for (int sx = 0; sx < scale; sx ++)
            {
                int px = 0;
                int px0 = row[x];
                int px1 = row[x + 1 >= size ? size - 1 : x + 1];

                // Color loop
                for (int c = 0; c < 3; c ++)
                {
                    double npx0 = ((px0 >> (c * 8)) & 0xFF);
                    double npx1 = ((px1 >> (c * 8)) & 0xFF);
                    double dx = (double)sx / (double)scale;
                    double ipx = npx0 * (1.0 - dx) + npx1 * dx;

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
     * 2D bilinear resize
     *
     * @param image
     * @param scale
     * @return
     */
    private static BufferedImage bilinearResize(BufferedImage image, int scale)
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
            tmp[y] = bilinearResize1D(row, w, scale);
        }

        for (int x = 0; x < w * scale; x ++)
        {
            // Get Column
            int[] col = new int[h];
            for (int y = 0; y < h; y++)
            {
                col[y] = tmp[y][x];
            }

            int[] col_out = bilinearResize1D(col, h, scale);

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
                // Perform bilinear resize to the scale of 4
                img = bilinearResize(img, scale);
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
