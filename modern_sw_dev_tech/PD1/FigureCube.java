/**
 * Cube figure
 *
 * @author Gusts Kaksis, gk17025
 */
public class FigureCube extends Figure
{
    FigureCube(int w, int h, int d) throws FigureException
    {
        super(Type.Cube, w, h, d);
    }

    /**
     * Get the volume of this figure
     * @return
     */
    public int volume()
    {
        return width() * height() * depth();
    }

    @Override
    public int area()
    {
        return (width() * height() + width() * depth() + height() * depth()) * 2;
    }
}
