/**
 * PD1
 *
 * @author Gusts Kaksis, gk17025
 */

import java.awt.EventQueue;

public class PD1
{
    /**
     * Main entry point
     *
     * @param args
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(() -> {
            UIMainWindow win = new UIMainWindow();
            win.setVisible(true);
        });
    }
}
