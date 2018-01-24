/**
 * Paeth predictor - perform Paeth predicion on a byte buffer representing an image
 * Derived from: https://www.w3.org/TR/PNG-Filters.html#Filter-type-4-Paeth
 */
public class PaethPredictor
{
    byte[] buff;
    int stride;

    /**
     * Construct a Paeth predictor
     * @param buff
     * @param stride
     * @throws Exception
     */
    PaethPredictor(byte[] buff, int stride) throws Exception
    {
        this.buff = buff;
        this.stride = stride;
    }

    /**
     * Run a Paeth predictor algorithm on neighbouring pixels
     * @param a - left pixel
     * @param b - upper pixel
     * @param c - upper-left pixe;
     * @return predicted value
     */
    private static byte predict(byte a, byte b, byte c)
    {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);

        byte px;
        if (pa <= pb && pa <= pc)
        {
            px = a;
        }
        else if (pb <= pc)
        {
            px = b;
        }
        else
        {
            px = c;
        }
        return px;
    }

    /**
     * Get neighbouring pixel values
     * @param src
     * @param i
     * @return
     */
    private byte[] getNeighbours(byte[] src, int i)
    {
        byte[] out = new byte[3];
        int x = i % stride;
        int y = (int)Math.floor(i / stride);
        if (y == 0)
        {
            out[1] = 0;
            out[2] = 0;
        }
        else
        {
            out[1] = src[i - stride];
            if (x == 0)
            {
                out[2] = 0;
            }
            else
            {
                out[2] = src[i - stride - 1];
            }
        }
        if (x == 0)
        {
            out[0] = 0;
        }
        else
        {
            out[0] = src[i - 1];
        }
        return out;
    }

    public void encode()
    {
        byte[] dbuff = new byte[buff.length];
        for (int i = 0; i < buff.length; i++)
        {
            byte[] neighbours = getNeighbours(buff, i);
            dbuff[i] = (byte)(buff[i] - predict(neighbours[0], neighbours[1], neighbours[2]));
        }
        buff = dbuff;
    }

    public void decode()
    {
        byte[] dbuff = new byte[buff.length];
        for (int i = 0; i < buff.length; i++)
        {
            byte[] neighbours = getNeighbours(dbuff, i);
            dbuff[i] = (byte)(buff[i] + predict(neighbours[0], neighbours[1], neighbours[2]));
        }
        buff = dbuff;
    }

    public byte[] getData()
    {
        return buff;
    }
}