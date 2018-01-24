import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Vector;

/**
 * Main window class
 *
 * @author Gusts Kaksis, gk17025
 */
public class UIMainWindow extends JFrame
{
    static final String NO_FIGURES = "Nav figūras";

    private Vector<Figure> figures;
    private UIFigureRenderer rndFigures;
    private JLabel[] lblFigures;

    public UIMainWindow()
    {
        figures = new Vector<Figure>();

        // Create window
        setTitle("PD1: Figūras");
        setSize(640, 480);
        setMinimumSize(new Dimension(640,480));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create buttons
        JLabel lblAdd = new JLabel("Pievienot: ");
        JButton btnDot = createButton("Punktu", "images/dot.png", (ActionEvent e) -> {
            addFigure(Figure.Type.Dot);
        });

        JButton btnLine = createButton("Nogriezni", "images/line.png", (ActionEvent e) -> {
            addFigure(Figure.Type.Line);
        });

        JButton btnSquare = createButton("Taisnstūri", "images/square.png", (ActionEvent e) -> {
            addFigure(Figure.Type.Square);
        });

        JButton btnCube = createButton("Paralēlskaldni", "images/cube.png", (ActionEvent e) -> {
            addFigure(Figure.Type.Cube);
        });

        JPanel labelPanel = new JPanel(new GridLayout(1,2));
        labelPanel.add(new JLabel("Figūra A:"));
        labelPanel.add(new JLabel("Figūra B:"));
        labelPanel.setMaximumSize(new Dimension(2048, 20));

        lblFigures = new JLabel[2];
        lblFigures[0] = new JLabel(NO_FIGURES);
        lblFigures[1] = new JLabel(NO_FIGURES);
        JPanel infoPanel = new JPanel(new GridLayout(1,2));
        infoPanel.add(lblFigures[0]);
        infoPanel.add(lblFigures[1]);
        infoPanel.setMaximumSize(new Dimension(2048, 80));

        // Figure renderer:
        rndFigures = new UIFigureRenderer();

        JButton btnClear = new JButton("Notīrīt");
        btnClear.addActionListener((ActionEvent e) -> {
           clearFigures();
        });

        // Add buttons to the layout

        Container pane = getContentPane();
        GroupLayout layout = new GroupLayout(pane);
        pane.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(lblAdd)
                .addComponent(btnDot)
                .addComponent(btnLine)
                .addComponent(btnSquare)
                .addComponent(btnCube)
                .addComponent(btnClear)
            )
            .addGroup(layout.createParallelGroup()
                .addComponent(labelPanel)
                .addComponent(rndFigures)
                .addComponent(infoPanel)
            )
        );
        layout.setVerticalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblAdd)
                .addComponent(btnDot)
                .addComponent(btnLine)
                .addComponent(btnSquare)
                .addComponent(btnCube)
                .addComponent(btnClear)
            )
            .addGroup(layout.createSequentialGroup()
                .addComponent(labelPanel)
                .addComponent(rndFigures)
                .addComponent(infoPanel)
            )
        );
    }

    /**
     * Add a requested figure to scene
     * @param type
     */
    protected void addFigure(Figure.Type type)
    {
        if (figures.size() == 2)
        {
            JOptionPane.showMessageDialog(this,
                    "Nav iespējams pievienot vairāk kā 2 figūras.\n"
                    + "Notīriet laukumu, lai pievienotu jaunas figūras.");
            return;
        }
        try
        {
            if (type != Figure.Type.Dot)
            {
                String titleAdd = "";
                switch (type)
                {
                    case Line:
                        titleAdd = "nogriezni";
                        break;
                    case Square:
                        titleAdd = "taisnstūri";
                        break;
                    case Cube:
                        titleAdd = "taisnstūra paralēlskaldni";
                        break;
                }

                int[] dims = requestDims("Izveidot " + titleAdd);
                if (dims != null && dims.length == 3)
                {
                    Figure f = null;
                    switch (type)
                    {
                        case Line:
                            f = new FigureLine(dims[0], dims[1], dims[2]);
                            break;
                        case Square:
                            f = new FigureSquare(dims[0], dims[1], dims[2]);
                            break;
                        case Cube:
                            f = new FigureCube(dims[0], dims[1], dims[2]);
                            break;
                    }
                    if (f != null)
                    {
                        figures.add(f);
                        rndFigures.AddFigure(f);
                    }
                }
            }
            else
            {
                Figure f = new FigureDot();
                figures.add(f);
                rndFigures.AddFigure(f);
            }

            int i = 0;
            for (Figure f : figures)
            {
                String info = "<html><body>"
                            + "<strong>Figūra:</strong> " + f.getName() + "<br/>"
                            + "<strong>Izmēri</strong> P:" + f.width() + ", A:" + f.height() + ", Dz:" + f.depth() + "<br/>"
                            + "<strong>Laukums:</strong> " + f.area() + "<br/>";
                if (f instanceof FigureCube)
                {
                    info += "<strong>Tilpums:</strong> " + ((FigureCube)f).volume();
                }
                if (figures.size() == 2)
                {
                    info += "<br/><strong>Mazāka par pretējo figūru:</strong> " + (figures.elementAt((i + 1) % 2).canContain(f) ? "Jā" : "Nē");
                }
                info += "</body></html>";
                lblFigures[i].setText(info);
                i ++;
            }
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(this, "Kļuda datu ievadē, lūdzu mēģini vēlreiz");
        }
        catch (FigureException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        catch (Exception e)
        {

        }
        rndFigures.repaint();
        rndFigures.revalidate();
    }

    protected int[] requestDims(String title)
    {
        String s = (String)JOptionPane.showInputDialog(this,
                "Ievadiet figūras dimensijas\n"
                        + "Formātā \'platums,augstums,dziļums\':",
                title,
                JOptionPane.PLAIN_MESSAGE);
        if ((s != null) && (s.length() > 0))
        {
            String[] dims = s.split(",");
            if (dims.length == 3)
            {
                int[] idims = new int[3];
                idims[0] = Integer.parseInt(dims[0]);
                idims[1] = Integer.parseInt(dims[1]);
                idims[2] = Integer.parseInt(dims[2]);
                return idims;
            }
        }

        return null;
    }

    /**
     * Remove all the figures
     */
    protected void clearFigures()
    {
        figures.clear();
        rndFigures.Clear();
        lblFigures[0].setText(NO_FIGURES);
        lblFigures[1].setText(NO_FIGURES);
        rndFigures.repaint();
        rndFigures.revalidate();
    }

    /**
     * Helper method to create a button with icon
     * @param label
     * @param iconPath
     * @param callback
     * @return
     */
    protected JButton createButton(String label, String iconPath, ActionListener callback)
    {
        ImageIcon icon = null;
        if (iconPath != null)
        {
            URL imgURL = getClass().getResource(iconPath);
            if (imgURL != null)
            {
                icon = new ImageIcon(imgURL, label);
            }
        }

        JButton button = new JButton(label, icon);
        button.addActionListener(callback);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setMinimumSize(new Dimension(100, 90));

        return button;
    }
}
