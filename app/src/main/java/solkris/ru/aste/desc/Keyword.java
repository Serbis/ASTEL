package solkris.ru.aste.desc;

import solkris.ru.aste.lexer.FontStyle;

/**
 * Describes the keyword and its characteristics
 */
public class Keyword {
    /** Text representation of keyword */
    public String lexeme;
    /** Font style of the keyword */
    public FontStyle fontStyle;

    /**
     * Constructor
     *
     * @param lexeme Text representation of keyword
     * @param fontStyle Font style of the keyword
     */
    public Keyword(String lexeme, FontStyle fontStyle) {
        this.lexeme = lexeme;
        this.fontStyle = fontStyle;
    }

    /**
     * Empty constructor
     */
    public Keyword() {

    }
}
