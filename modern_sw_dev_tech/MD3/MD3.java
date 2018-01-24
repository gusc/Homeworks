/**
 * Majas darbs Nr. 3
 * @author Gusts Kaksis, gk17025
 */

import java.math.BigInteger;


// ================ Klasi MD3 modificet aizliegts!
public class MD3 {

    public static void main(String[] args)
    {
        LielsSkaitlis lielsSkaitlis1 = new LielsSkaitlis(args[0]);
        LielsSkaitlis lielsSkaitlis2 = new LielsSkaitlis(args[1]);

        lielsSkaitlis1.add(lielsSkaitlis2);
        lielsSkaitlis1.display();
        lielsSkaitlis1.reverse();
        lielsSkaitlis1.display();

        lielsSkaitlis2.sub(lielsSkaitlis1);
        lielsSkaitlis2.display();
        lielsSkaitlis2.reverse();
        lielsSkaitlis2.display();
    }
}
// ================ Klasi MD3 modificet aizliegts!


//Japapildina klase "LielsSkaitlis" ar nepieciesamo funcionalitati
class LielsSkaitlis {
    private String skaitlis;
    private boolean valid = true;

    LielsSkaitlis(String str)
    {
        skaitlis = str;
        // Validate on input
        validate();
    }

    public void add(LielsSkaitlis other)
    {
        if (valid)
        {
            // Only perform addition if the number is valid
            if (!other.isValid())
            {
                // If the incoming number is not valid, we take it's "invalid" value
                this.skaitlis = other.toString();
                this.valid = false;
                return;
            }
            BigInteger current = new BigInteger(this.skaitlis);
            current = current.add(other.toBigInt());
            this.skaitlis = current.toString();
            this.validate();
        }
    }
    public void sub(LielsSkaitlis other)
    {
        if (valid)
        {
            // Only perform subtraction if the number is valid
            if (!other.isValid())
            {
                // If the incoming number is not valid, we take it's "invalid" value
                this.skaitlis = other.toString();
                this.valid = false;
                return;
            }
            BigInteger current = new BigInteger(this.skaitlis);
            current = current.subtract(other.toBigInt());
            this.skaitlis = current.toString();
            this.validate();
        }
    }
    public void reverse()
    {
        if (valid)
        {
            StringBuffer sb = new StringBuffer(skaitlis);
            boolean negative = false;
            if (sb.charAt(0) == '-')
            {
                // Remove negative sign and store it for later use
                sb.deleteCharAt(0);
                negative = true;
            }
            // Reverse the byte buffer
            sb.reverse();
            // Trim leading zeroes
            while (sb.charAt(0) == '0' && sb.length() > 1)
            {
                sb.deleteCharAt(0);
            }
            // Restore negative sign if necessary
            if (negative)
            {
                sb.insert(0, '-');
            }
            this.skaitlis = sb.toString();
        }
    }

    /**
     * Convert to BigInteger
     * @return
     */
    public BigInteger toBigInt()
    {
        return new BigInteger(this.skaitlis);
    }

    /**
     * Convert to String
     * @return
     */
    public String toString()
    {
        return this.skaitlis;
    }

    /**
     * Is the number valid (false if too big or too small)
     * @return
     */
    public boolean isValid()
    {
        return valid;
    }

    /**
     * Perform number validation
     */
    protected void validate()
    {
        if (this.skaitlis.length() > 20)
        {
            this.valid = false;
            if (this.skaitlis.charAt(0) == '-')
            {
                this.skaitlis = "SKAITLIS PAR MAZU";
            }
            else
            {
                this.skaitlis = "SKAITLIS PAR LIELU";
            }
        }
    }

    // ================= Metodi display() modificet aizliegts!
    public void display() {System.out.println(skaitlis);}
}
