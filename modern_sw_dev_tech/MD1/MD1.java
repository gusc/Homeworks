/**
 * MD1
 *
 * @author Gusts Kaksis, gk17025
 */
public class MD1
{

    /**
     * Prints the ruler of an arbitrary length
     *
     * @param length
     */
    private static void printRuler(int length)
    {
        for (int i = 1; i <= length ; i ++)
        {
            System.out.print(i % 10);
        }
        System.out.print("\n");
    }

    /**
     * Prints the triangle figure
     *
     * @param offset - character count to pad the figure from the left side of the screen
     * @param height - number of rows in the triangle
     */
    private static void printTriangle(int offset, int height)
    {
        for (; height > 0; height --, offset ++)
        {
            for (int i = 0; i < offset + height; i ++)
            {
                if (i < offset)
                {
                    // Padding from the left side of the screen
                    System.out.print(" ");
                }
                else
                {
                    // Figure
                    System.out.print("+ ");
                }
            }
            System.out.print("\n");
        }
    }

    /**
     * Main entry point
     *
     * @param args
     */
    public static void main(String[] args)
    {
        int Z = 0;
        int N = 0;

        // Better safe than sorry :)
        try
        {
            Z = Integer.parseInt(args[0]);
            N = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            // Input is not a number
            System.out.println("DATI NAV KOREKTI!");
            return;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            // Not enough arguments
            System.out.println("DATI NAV KOREKTI!");
            return;
        }

        // Validate input data
        if (Z <= 0 || Z >= 20 || N <= 0 || N >= 30)
        {
            System.out.println("DATI NAV KOREKTI!");
            return;
        }

        // Print the assignment solution
        printRuler(70);
        printTriangle(N, Z);
    }
}
