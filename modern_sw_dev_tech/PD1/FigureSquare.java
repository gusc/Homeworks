/**
 * Square figure
 *
 * @author Gusts Kaksis, gk17025
 */
public class FigureSquare extends Figure
{
    FigureSquare(int w, int h) throws FigureException
    {
        super(Type.Square, w, h, 0);
    }

    FigureSquare(int w, int h, int d) throws FigureException
    {
        super(Type.Square, w, h, d);
        // Make the figure face front
        normalize();
    }

    @Override
    public int area()
    {
        return width() * height();
    }
}
