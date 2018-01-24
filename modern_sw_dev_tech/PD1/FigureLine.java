/**
 * Line figure
 *
 * @author Gusts Kaksis, gk17025
 */
public class FigureLine extends Figure
{
    FigureLine(int length) throws FigureException
    {
        super(Type.Line, length, 0, 0);
    }

    FigureLine(int w, int h, int d) throws FigureException
    {
        super(Type.Line, w, h, d);
        // Make the figure face front
        normalize();
    }
}
