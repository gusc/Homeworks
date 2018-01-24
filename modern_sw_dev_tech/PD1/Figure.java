/**
 * Base (abstract) figure class
 *
 * @author Gusts Kaksis, gk17025
 */
public abstract class Figure
{
    enum Type
    {
        Unknown,
        Dot,
        Line,
        Square,
        Cube
    };

    private Type type;
    private int w;
    private int h;
    private int d;
    private int dims;

    /**
     * Create a figure
     * @param type - figure type
     * @param w - width
     * @param h - height
     * @param d - depth
     * @throws Exception
     */
    Figure(Type type, int w, int h, int d) throws FigureException
    {
        this.type = type;
        if (this.type == Type.Unknown)
        {
            throw new FigureException("Neatpazīts figūras tips");
        }

        this.dims = 0;
        this.dims += (this.w = w) > 0 ? 1 : 0;
        this.dims += (this.h = h) > 0 ? 1 : 0;
        this.dims += (this.d = d) > 0 ? 1 : 0;

        if (this.w > 200 || this.h > 200 || this.d > 200)
        {
            throw new FigureException("Figūras izmērs pārsniedz 200");
        }

        validate();
    }

    /**
     * Name of the figure type
     * @param name - figure name
     * @param w - width
     * @param h - height
     * @param d - depth
     * @throws Exception
     */
    Figure(String name, int w, int h, int d) throws Exception
    {
        this(Figure.nameToType(name), w, h, d);
    }

    /**
     * Convert figure name to type
     * @param s - figure name
     * @return figure type
     */
    protected static Type nameToType(String s)
    {
        switch (s)
        {
            case "punkts":
                return Type.Dot;
            case "nogrieznis":
                return Type.Line;
            case "taisnstūris":
            case "kvadrāts":
                return Type.Square;
            case "taisnstūra paralēlskaldnis":
            case "kubs":
                return Type.Cube;
            default:
                return Type.Unknown;
        }
    }

    /**
     * Convert figure type to name
     * @param t - figure type
     * @return figure name
     */
    private static String  typeToName(Type t)
    {
        switch (t)
        {
            case Dot:
                return "punkts";
            case Line:
                return "nogrieznis";
            case Square:
                return "taisnstūris";
            case Cube:
                return "taisnstūra paralēlskaldnis";
            default:
                return "nezināms";
        }
    }

    /**
     * Get width
     * @return
     */
    int width()
    {
        return w;
    }

    /**
     * Get height
     * @return
     */
    int height()
    {
        return h;
    }

    /**
     * Get depth
     * @return
     */
    int depth()
    {
        return d;
    }

    /**
     * Get dimensions - 0-3D
     * @return
     */
    int dimensions()
    {
        return dims;
    }

    /**
     * Rotate the figure on a specific axis
     * @param axis
     */
    void rotate(int axis)
    {
        int t;
        switch (axis)
        {
            case 0:
                t = w;
                w = h;
                h = t;
                break;
            case 1:
                t = w;
                w = d;
                d = t;
                break;
            case 2:
                t = h;
                h = d;
                d = t;
                break;
        }
    }

    /**
     * Rotate the object so that it faces the viewer
     * 3D objects and dots are not affected
     */
    void normalize()
    {
        if (type == Type.Dot || type == Type.Cube)
        {
            return;
        }
        if (w == 0)
        {
            rotate(1);
        }
        if (h == 0)
        {
            rotate(2);
        }
    }

    /**
     * Perform figure type and dimension validation - certain types must have enough dimensions
     *
     * @throws FigureException
     */
    void validate() throws FigureException
    {
        if (this.type == Type.Dot && this.dims != 0)
        {
            throw new FigureException("Punktam nevar būt dimensijas");
        }
        else if (this.type == Type.Line && this.dims != 1)
        {
            throw new FigureException("Nogrieznim jābūt norādītai tikai vienai dimensijai");
        }
        else if (this.type == Type.Square && this.dims != 2)
        {
            throw new FigureException("Taisnstūrim jābūt norādītām tikai divām dimensijām");
        }
        else if (this.type == Type.Cube && this.dims != 3)
        {
            throw new FigureException("Taisnstūra paralēlskaldnim jābūt norādītām visām trīs dimensijām");
        }
    }

    /**
     * Get surface area of the figure
     * @return
     */
    int area()
    {
        return 0;
    }

    /**
     * Get the figure type
     * @return
     */
    Type getType()
    {
        return type;
    }

    /**
     * Get the figure type name
     * @return
     */
    String getName()
    {
        return typeToName(type);
    }

    /**
     * Check if this figure can contain the one passed in as an argmuent
     * @param f
     * @return
     */
    boolean canContain(Figure f)
    {
        if (dimensions() < f.dimensions())
        {
            // 2D will never be bigger than 3D
            return false;
        }
        int axis = 0;
        while (axis < 3)
        {
            if (width() >= f.width() && height() >= f.height() && depth() >= f.depth())
            {
                return true;
            }
            rotate(axis % 2); // rotate z, then x (which is now y) and then z (which is now y)
            axis++;
        }
        return false;
    }

}