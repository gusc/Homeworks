import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Huffman coding - compress and decompress an arbitrary byte buffer
 * Derived from: https://gist.github.com/ahmedengu/aa8d85b12fccf0d08e895807edee7603
 */
public class HuffmanCoding
{
    /**
     * Huffman code tree node
     */
    private static class HuffmanNode
    {
        HuffmanNode left;
        HuffmanNode right;
        double frequency;
        byte value;

        public HuffmanNode(double frequency, byte value)
        {
            this.frequency = frequency;
            this.value = value;
            left = null;
            right = null;
        }

        public HuffmanNode(HuffmanNode left, HuffmanNode right)
        {
            this.frequency = left.frequency + right.frequency;
            if (left.frequency < right.frequency)
            {
                this.right = right;
                this.left = left;
            }
            else
                {
                this.right = left;
                this.left = right;
            }
        }

        public HuffmanNode(ByteArrayInputStream bi)
        {
            int flags = bi.read();
            if (flags != -1)
            {
                this.value = (byte)bi.read();

                if ((flags & 0x80) == 0x80)
                {
                    this.left = new HuffmanNode(bi);
                }
                if ((flags & 0x40) == 0x40)
                {
                    this.right = new HuffmanNode(bi);
                }
            }
        }

        private void getBytes(ByteArrayOutputStream bo, int depth)
        {
            byte flags = (byte)depth;
            if (this.left != null)
            {
                flags |= 0x80; // 8th bit set - left branch exists
            }
            if (this.right != null)
            {
                flags |= 0x40; // 7th bit set - right branch exists
            }
            bo.write(flags);
            bo.write(value);

            if (this.left != null)
            {
                this.left.getBytes(bo, depth + 1);
            }
            if (this.right != null)
            {
                this.right.getBytes(bo, depth + 1);
            }
        }

        public byte[] toByteArray()
        {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            getBytes(bo,0);
            return bo.toByteArray();
        }
    }

    /**
     * Huffman variable width byte
     */
    private static class HuffmanByte
    {
        int bits;
        int size;

        HuffmanByte (int bits, int size)
        {
            this.bits = bits;
            this.size = size;
        }
    }

    private HuffmanNode rootNode;
    private byte[] data;
    private BitSet encodedData;

    /**
     * Construct a Huffman coding class with un-compressed data. Used for compressing arbitrary byte buffer.
     * @param data
     */
    HuffmanCoding(byte[] data)
    {
        this.data = data;
    }

    /**
     * Construct a Huffman coding class with compressed data and a coding node tree
     * @param encodedData
     * @param rootNode
     */
    HuffmanCoding(BitSet encodedData, HuffmanNode rootNode)
    {
        this.encodedData = encodedData;
        this.rootNode = rootNode;
    }

//    //
//    // Debug methods
//    //
//
//    private void dumpIndent(int depth)
//    {
//        for (int i = 0; i < depth; i ++)
//        {
//            System.out.print("  ");
//        }
//    }
//    private int dumpNode(HuffmanNode node, int depth)
//    {
//        int count = 1;
//        dumpIndent(depth);
//        System.out.print("Node @");
//        System.out.print(depth);
//        System.out.print(" value:");
//        System.out.print(node.value);
//        System.out.print("\n");
//        if (node.left != null)
//        {
//            dumpIndent(depth);
//            System.out.print("Left:\n");
//            int c = dumpNode(node.left, depth + 1);
//            dumpIndent(depth);
//            System.out.print("Node count: ");
//            System.out.print(c);
//            System.out.print("\n");
//            count += c;
//        }
//        if (node.right != null)
//        {
//            dumpIndent(depth);
//            System.out.print("Right:\n");
//            int c = dumpNode(node.right, depth + 1);
//            dumpIndent(depth);
//            System.out.print("Node count: ");
//            System.out.print(c);
//            System.out.print("\n");
//            count += c;
//        }
//        return count;
//    }
//
//    public void dumpNodes()
//    {
//        int count = dumpNode(rootNode, 1);
//        System.out.print("Node count: ");
//        System.out.print(count);
//        System.out.print("\n");
//    }

    /**
     * Generate code table
     * @param codes
     * @param node
     * @param i
     * @param depth
     */
    private void generateCodes(TreeMap<Integer, HuffmanByte> codes, HuffmanNode node, int i, int depth)
    {
        if (node != null)
        {
            if (node.right != null)
            {
                generateCodes(codes, node.right, i | (1 << depth), depth + 1);
            }

            if (node.left != null)
            {
                generateCodes(codes, node.left, i, depth + 1);
            }

            if (node.left == null && node.right == null)
            {
                codes.put((int)node.value, new HuffmanByte(i, depth));
            }
        }
    }

    /**
     * Perform data compression
     */
    public void compress()
    {
        PriorityQueue<HuffmanNode> nodes = new PriorityQueue<>((o1, o2) -> (o1.frequency < o2.frequency) ? -1 : 1);
        TreeMap<Integer, HuffmanByte> codes = new TreeMap<>();

        // Calculate frequency intervals
        int[] repeats = new int[256];
        for (int i = 0; i < data.length; i++)
        {
            repeats[(data[i] & 0xFF)]++;
        }
        for (int value = 0; value < repeats.length; value++)
        {
            if (repeats[(value & 0xFF)] > 0)
            {
                nodes.add(new HuffmanNode((double) repeats[(value & 0xFF)] / (double) data.length, (byte)value));
            }
        }

        // Build the tree
        while (nodes.size() > 1)
        {
            nodes.add(new HuffmanNode(nodes.poll(), nodes.poll()));
        }
        rootNode = nodes.peek();

        // Generate code tree
        generateCodes(codes, rootNode, 0, 0);

        // Encode data
        encodedData = new BitSet();
        for (int i = 0, j = 0; i < data.length; i++)
        {
            HuffmanByte hbyte = codes.get((int)data[i]);
            for (int k = 0; k < hbyte.size; k++, j++)
            {
                int bit = ((hbyte.bits >> k) & 1);
                encodedData.set(j, bit == 1);
            }
        }
    }

    /**
     * Perform data decompression
     */
    public void decompress()
    {
        // Decode data
        Stack<Integer> decoded = new Stack<>();
        for (int i = 0; i < encodedData.size(); )
        {
            HuffmanNode tmpNode = rootNode;
            boolean end = false;
            while (tmpNode.left != null && tmpNode.right != null && i < encodedData.size())
            {
                if (encodedData.get(i))
                {
                    // 1 = right
                    tmpNode = tmpNode.right;
                }
                else
                {
                    // 0 = left
                    tmpNode = tmpNode.left;
                }
                i++;
            }
            decoded.push((int)tmpNode.value);
        }

        // Re-create byte buffer
        data = new byte[decoded.size()];
        for (int i = 0; i < decoded.size(); i ++)
        {
            byte b = decoded.get(i).byteValue();
            data[i] = b;
        }
    }

    /**
     * Get compressed byte buffer which includes serialized node tree and compressed data
     * @return
     */
    public byte[] getEncodedBytes()
    {
        // Prepare byte array
        ByteArrayOutputStream bo = new ByteArrayOutputStream();

        try
        {
            // Write node tree
            byte[] nodeArray = rootNode.toByteArray();
            bo.write(ByteBuffer.allocate(4).putInt(nodeArray.length).array());
            bo.write(nodeArray);

            // Write encoded byte blob
            byte[] encodedBytes = encodedData.toByteArray();
            bo.write(ByteBuffer.allocate(4).putInt(encodedBytes.length).array());
            bo.write(encodedBytes);
        }
        catch (IOException e)
        {
            System.out.println("IOException 2");
        }

        // Store this data
        return bo.toByteArray();
    }

    /**
     * Get decompressed data buffer
     * @return
     */
    public byte[] getDecodedBytes()
    {
        return data;
    }

    /**
     * Create Huffman code class from serialized byte buffer (from HuffmanCoding.getEncodedBytes())
     * @param encodedBytes
     * @return
     */
    static HuffmanCoding createFromBytes(byte[] encodedBytes)
    {
        ByteArrayInputStream bi = new ByteArrayInputStream(encodedBytes);

        // Read node tree
        ByteBuffer nodeLengthBuff = ByteBuffer.allocate(4);
        bi.read(nodeLengthBuff.array(), 0, 4);
        int nodeLength = nodeLengthBuff.getInt();
        byte[] nodeArray = new byte[nodeLength];
        bi.read(nodeArray, 0, nodeLength);
        HuffmanNode rootNode = new HuffmanNode(new ByteArrayInputStream(nodeArray));

        // Read encoded byte blob
        ByteBuffer buffLengthBuff = ByteBuffer.allocate(4);
        bi.read(buffLengthBuff.array(), 0, 4);
        int buffLength = buffLengthBuff.getInt();
        byte[] buff = new byte[buffLength];
        int readLen = bi.read(buff, 0, buffLength);
        BitSet encoded = BitSet.valueOf(buff);

        return new HuffmanCoding(encoded, rootNode);
    }
}