import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * Custom Swing component to render Figure elements.
 * 3D elements are rendered in isometric space.
 *
 * @author Gusts Kaksis, gk17025
 */
public class UIFigureRenderer extends JComponent
{
    private Vector<Figure> figures;

    UIFigureRenderer()
    {
        figures = new Vector<Figure>();
    }

    /**
     * Add a figure to render-set
     * @param f
     */
    void AddFigure(Figure f)
    {
        if (figures.size() < 2)
        {
            figures.add(f);
        }
    }

    /**
     * Clear figure set
     */
    void Clear()
    {
        figures.clear();
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        Graphics2D g = (Graphics2D)graphics;
        g.setColor(Color.white);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.black);
        g.drawRect(0, 0, this.getWidth(), this.getHeight());
        g.setStroke(new BasicStroke(4));

        int pad = 10;
        int figW = (this.getWidth() - (4 * pad)) / 2;
        int figH = (this.getHeight() - (2 * pad));
        int figX = pad;

        for (Figure f : figures)
        {
            f.normalize();

            double prop = (double)figW / 200.0;

            if (f.getType() == Figure.Type.Dot)
            {
                // Draw dot
                g.fillOval(
                        figX + (figW / 2) - 5,
                        pad + (figH / 2) - 5,
                        10, 10
                );
            }
            else if (f.getType() == Figure.Type.Line)
            {
                if (f.width() > 0)
                {
                    int w = (int) (f.width() * prop);
                    g.drawLine(
                            figX + (figW - w) / 2,
                            pad + figH / 2,
                            figX + (figW - w) / 2 + w,
                            pad + figH / 2
                    );
                }
                else if (f.height() > 0)
                {
                    int h = (int) (f.height() * prop);
                    graphics.drawLine(
                            figX + figW / 2,
                            pad + (figH - h) / 2,
                            figX + figW / 2,
                            pad + (figH - h) / 2 + h
                    );
                }
            }
            else if (f.getType() == Figure.Type.Square)
            {
                int w = (int) (f.width() * prop);
                int h = (int) (f.height() * prop);

                // Draw any other figure
                g.drawRect(
                        figX + (figW - w) / 2,
                        pad + (figH - h) / 2,
                        w, h
                );
            }
            else
            {
                double depth_iso = f.depth() * Math.pow(Math.sin(Math.PI / 4.0), 2) + f.width();
                if (depth_iso > 200.0)
                {
                    prop = (double)figW / depth_iso;
                }
                int w = (int) (f.width() * prop);
                int h = (int) (f.height() * prop);
                int d = (int) (f.depth() * prop * Math.pow(Math.sin(Math.PI / 4.0), 2));

                int x1 = figX + (figW - w - d) / 2;
                int y1 = pad + (figH - h - d) / 2;
                int x2 = x1 + d;
                int y2 = y1 + d;
                int x3 = x1 + w;
                int y3 = y1 + h;
                int x4 = x3 + d;
                int y4 = y3 + d;

                g.setColor(Color.gray);
                g.drawRect(x2, y1, w, h);
                g.drawLine(x1, y2, x2, y1);
                g.drawLine(x3, y2, x4, y1);
                g.drawLine(x1, y4, x2, y3);
                g.drawLine(x3, y4, x4, y3);
                g.setColor(Color.black);
                g.drawRect(x1, y2, w, h);
            }
            figX += figW + (2 * pad);
        }

    }
}
