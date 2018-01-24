/**
 * Loseless image compresion using prediction method
 *
 * Compression workflow:
 *   1. Separate each color channel in it's own buffer
 *   2. Compare channels - if they match - merge them into one to save space
 *   3. Run Paeth predicion algorithm on each channel
 *   4. Run Huffman coding compression on each channel
 *   5. Serialize all the image data and write to file
 *
 * Decompression workflow:
 *   1. Unserialize image data from file
 *   2. Run Huffman coding decompression on each channel
 *   3. Run reverse Paeth predicion algorithm on each channel
 *   4. Expand merged channels
 *   5. Generate an image object from this data
 *
 * @author Gusts Kaksis, gk17025
 */

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Compress
{
    /**
     * Convert buffered image into three contigous arrays consisting of color data in range [0; 255]
     * @param image
     * @return array
     */
    private static byte[][] img2Arr(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();
        int a = w * h;

        byte[][] arr = new byte[3][a]; // 3 colors, area

        for (int y = 0; y < h; y ++)
        {
            for (int x = 0; x < w; x++)
            {
                for (int c = 0; c < 3; c++) // Color loop as we're working with 3 colors separately
                {
                    int px = image.getRGB(x, y);
                    px = (px >> (8 * c)) & 0xFF;
                    arr[c][x + y * w] = (byte)px;
                }
            }
        }

        return arr;
    }

    /**
     * Convert the array back into a buffered image
     * with separated colors in range [0; 255]
     * @param image
     * @return array
     */
    private static void arr2Img(BufferedImage image, byte[][] arr) throws Exception
    {
        int w = image.getWidth();
        int h = image.getHeight();

        if (arr[0].length < w * h)
        {
            throw new Exception("Array is not the size of width and height multiplied");
        }

        for (int y = 0; y < h; y ++)
        {
            for (int x = 0; x < w; x++)
            {
                int px = 0;
                for (int c = 0; c < 3; c++) // Color loop as we're working with 3 colors separately
                {
                    int pxval = (int)arr[c][x + y * w];
                    px |= ((pxval & 0xFF) << (8 * c));
                }
                image.setRGB(x, y, px);
            }
        }
    }

    /**
     * Compress image
     *
     * @param image
     * @return
     */
    private static CompressedImage compressImage(BufferedImage image)
    {
        CompressedImage cd = new CompressedImage();
        cd.width = image.getWidth();
        cd.height = image.getHeight();
        cd.numChannels = 3;
        cd.data = img2Arr(image);
        cd.compress();
        return cd;
    }

    /**
     * Decompress the image
     *
     * @param cd
     * @return
     */
    private static BufferedImage decompressImage(CompressedImage cd)
    {
        BufferedImage image = new BufferedImage(cd.width, cd.height, BufferedImage.TYPE_INT_RGB);
        try
        {
            cd.decompress();
            arr2Img(image, cd.data);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return image;
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

            String task = "compress";
            if (args.length > 1)
            {
                task = args[1];
            }

            if (task.equals("compress"))
            {
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

                CompressedImage ci = null;
                if (img != null)
                {
                    ci = compressImage(img);
                }
                else
                {
                    System.out.format("Can't open image %s, null returned\n", imgPath);
                    return;
                }

                String imgPathOut = getBaseName(imgPath) + ".cim";
                try
                {
                    ci.writeToFile(imgPathOut);
                }
                catch (IOException e)
                {
                    System.out.format("Can't write image %s, exception thrown\n", imgPathOut);
                    return;
                }
            }
            else
            {
                CompressedImage ci = new CompressedImage();
                try
                {
                    ci.readFromFile(imgPath);
                }
                catch (IOException e)
                {
                    System.out.format("Can't read compressed image %s, exception thrown\n", imgPath);
                    return;
                }

                BufferedImage img = decompressImage(ci);

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
}
