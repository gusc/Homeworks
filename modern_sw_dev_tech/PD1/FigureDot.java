/**
 * Dot figure
 *
 * @author Gusts Kaksis, gk17025
 */
public class FigureDot extends Figure
{
    FigureDot() throws FigureException
    {
        super(Type.Dot, 0, 0, 0);
    }

    FigureDot(int w, int h, int d) throws FigureException
    {
        super(Type.Dot, w, h, d);
    }
}
