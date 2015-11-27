package solkris.ru.aste.desc;

/**
 * Convenience class for describing two-dimensional arrays positions
 * within.
 *
 */
public class Pos {
    /** Line */
    public int line;
    /** Offset in line */
    public int offset;
    /** Shift in the token*/
    public int interoffset;

    /**
     * Constructor 1. Without parameters.
     *
     */
    public Pos() {}

    /**
     * Constructor 2.
     *
     * @param line Line
     * @param offset Offset in line
     * @param interoffset Offset in the token
     */
    public Pos(int line, int offset, int interoffset) {
        this.line = line;
        this.offset = offset;
        this.interoffset = interoffset;
    }
}
