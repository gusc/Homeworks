import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Class representing a compressed image data
 */
public class CompressedImage
{
    int width;
    int height;
    // 1-3 channels ussually, but there wouldn't be a problem to have alpha as well
    int numChannels;
    // If channel data is equal we can merge them into single channel
    // 0b00000111 - all channels merged into 1st (red)
    // Not implemented:
    // 0b00000110 - green and blue are merged into green, red is a separate channel
    // 0b00000101 - blue and red are merged into red, green is a separate channel
    // 0b00000011 - green and red are merged into red, blue is a separate channel
    int mergedChannels;
    byte[][] data;

    /**
     * Perform a buffer comparisson
     * @param a
     * @param b
     * @return true when buffers match completely
     */
    private static boolean compare(byte[] a, byte[] b)
    {
        if (a.length != b.length)
        {
            return false;
        }

        for (int i = 0; i < a.length; i ++)
        {
            if (a[i] != b[i])
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Perform image loseless image compression
     */
    public void compress()
    {
        int sizeBefore = 0;
        int sizeAfter = 0;
        try
        {
            // Merge matching channels
            if (compare(data[0], data[1]) && compare(data[0], data[2]))
            {
                // Currently only total grayscale support
                mergedChannels = 0b111;
                numChannels = 1;
                data[1] = null;
                data[2] = null;
            }

            for (int i = 0; i < numChannels; i++)
            {
                sizeBefore += data[i].length;

                // Perfrom prediction algorithm to decrease entropy
                PaethPredictor p = new PaethPredictor(data[i], width);
                p.encode();
                data[i] = p.getData();

                // Perform Huffman coding to save space
                HuffmanCoding c = new HuffmanCoding(data[i]);
                c.compress();
                data[i] = c.getEncodedBytes();

                sizeAfter += data[i].length;
            }
        }
        catch (Exception e)
        {
            System.out.print("Compression failed: ");
            System.out.println(e.getMessage());
        }

        System.out.printf("Compression ratio: %d : %d bytes = %d%%\n", sizeBefore, sizeAfter, (int)(((double)sizeAfter / (double)sizeBefore) * 100.0));
    }

    public void decompress()
    {
        int sizeBefore = 0;
        int sizeAfter = 0;
        try
        {
            for (int i = 0; i < numChannels; i++)
            {
                sizeAfter = data[i].length;

                // Perform Huffman decoding
                HuffmanCoding c = HuffmanCoding.createFromBytes(data[i]);
                c.decompress();
                data[i] = c.getDecodedBytes();

                // Perform reverse Paeth prediction algorithm
                PaethPredictor p = new PaethPredictor(data[i], width);
                p.decode();
                data[i] = p.getData();

                sizeBefore = data[i].length;
            }

            if (mergedChannels == 0b111)
            {
                // This was a single channel image - recreate separate channels
                numChannels = 3;
                data[1] = data[0];
                data[2] = data[0];
            }
        }
        catch (Exception e)
        {
            System.out.print("Decompression failed: ");
            System.out.println(e.getMessage());
        }

        System.out.printf("Compression ratio: %d : %d bytes = %d%%\n", sizeBefore, sizeAfter, (int)(((double)sizeAfter / (double)sizeBefore) * 100.0));
    }

    /**
     * Write compressed image to file
     * @param file
     * @throws IOException
     */
    void writeToFile(String file) throws IOException
    {
        FileOutputStream fw = new FileOutputStream(file);
        fw.write(ByteBuffer.allocate(4).putInt(width).array());
        fw.write(ByteBuffer.allocate(4).putInt(height).array());
        fw.write(ByteBuffer.allocate(4).putInt(numChannels).array());
        fw.write(ByteBuffer.allocate(4).putInt(mergedChannels).array());
        for (int i = 0; i < numChannels; i ++)
        {
            fw.write(ByteBuffer.allocate(4).putInt(data[i].length).array());
            fw.write(data[i]);
        }
    }

    /**
     * Read compressed image from file
     * @param file
     * @throws IOException
     */
    void readFromFile(String file) throws IOException
    {
        FileInputStream fr = new FileInputStream(file);
        ByteBuffer intBuff = ByteBuffer.allocate(4);
        fr.read(intBuff.array(), 0, 4);
        width = intBuff.getInt();
        intBuff.rewind();
        fr.read(intBuff.array(), 0, 4);
        height = intBuff.getInt();
        intBuff.rewind();
        fr.read(intBuff.array(), 0, 4);
        numChannels = intBuff.getInt();
        intBuff.rewind();
        fr.read(intBuff.array(), 0, 4);
        mergedChannels = intBuff.getInt();
        intBuff.rewind();
        data = new byte[3][]; // Always create 3 channels
        for (int i = 0; i < numChannels; i ++)
        {
            fr.read(intBuff.array(), 0, 4);
            int len = intBuff.getInt();
            intBuff.rewind();
            data[i] = new byte[len];
            fr.read(data[i]);
        }
    }
}