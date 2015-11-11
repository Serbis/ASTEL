package solkris.ru.aste.lexer;

import android.graphics.Color;

/**
 * Describes text style of token
 */
public class FontStyle {
    /** Font size */
    private int size;
    /** Font color */
    private int color;
    /** Font name */
    private String font;
    /** Flag of bold text style */
    private boolean bold = false;
    /** Flag of italic text style */
    private boolean italic = false;
    /** Flag of underline text style */
    private boolean underline = false;

    /** Constructor 1. Init default values */
    public FontStyle() {
        size = 10;
        color = Color.BLACK;
        font = "";
    }

    /**
     * Constructor 2. It identifies all the style settings
     *
     * @param size Font size
     * @param color Font color
     * @param font Font name
     * @param bold Flag of bold text style
     * @param italic Flag of italic text style
     * @param underline Flag of underline text style
     */
    public FontStyle(int size, int color, String font, boolean bold, boolean italic, boolean underline) {
        this.size = size;
        this.color = color;
        this.font = font;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
    }

    /**
     * Set the font size
     *
     * @param size font size int pt
     */
    public void setFontSyze(int size) {
        this.size = size;
    }

    /**
     * Set the font color
     *
     * @param color int value of color
     */
    public void setFontColor(int color) {
        this.color = color;
    }

    /**
     * Set used font
     *
     * @param font string name of font
     */
    public void setFont(String font) {
        this.font = font;
    }

    /**
     * Set bold text style
     *
     * @param enabled -
     */
    public void setBold(boolean enabled) {
        bold = enabled;
    }

    /**
     * Set italic text style
     *
     * @param enabled -
     */
    public void setItalic(boolean enabled) {
        italic = enabled;
    }

    /**
     * Set underline text style
     *
     * @param enabled -
     */
    public void setUnderlie(boolean enabled) {
        underline = enabled;
    }



}
