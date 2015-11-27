package solkris.ru.aste.desc;

/**
 *
 */
public class Line {
    /** Start position */
    public int start;
    /** End position */
    public int end;

    /**
     * Constructor 1. Without parameters.
     *
     */
    public Line() {}

    /**
     * Constructor 2.
     *
     * @param start Start position
     * @param end End position
     */
    public Line(int start, int end) {
        this.start = start;
        this.end = end;
    }
}
