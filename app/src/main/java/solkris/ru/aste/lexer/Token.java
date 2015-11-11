package solkris.ru.aste.lexer;

import android.graphics.Color;

import java.util.List;

import solkris.ru.aste.desc.Keyword;

/**
 * Created by serbis on 26.10.15.
 */
public class Token {
    /** Text representation of token */
    public String lexeme;
    /** Line number on witch the token */
    public int line;
    /** Offset in characters from the beginning of the file */
    public int offset;
    /** Length in characters of the lexeme */
    public int length;
    /** Font style of a particular token */
    public FontStyle fontStyle;
    /** List of keyword specified */
    public static List<Keyword> keywords;
    /** Text style of numbers*/
    public static FontStyle nubersStyle;
    /** Text style of constants (Such as "...") */
    public static FontStyle constantStyle;
    /** Text style of undefined words */
    public static FontStyle textStyle;
    /** Define default style of number keyword */
    public static final FontStyle STYLE_DEF_NUMBER = new FontStyle(10, Color.BLUE, "-", false, false, false);
    /** Define default style of constant keyword */
    public static final FontStyle STYLE_DEF_CONSTANT = new FontStyle(10, Color.GREEN, "-", false, false, false);
    /** Define default style of undefined text keyword */
    public static final FontStyle STYLE_DEF_TEXT = new FontStyle(10, Color.BLACK, "-", false, false, false);

    /**
     * Constructor. Sets the token params. Specifies the length of the token.
     * Sets default tokens style.
     *
     * @param lexeme Text representation of token
     * @param line Line number on witch the token
     * @param offset Offset in characters from the beginning of the file
     * @param fontStyle Font style of the token
     */
    public Token(String lexeme, int line, int offset, FontStyle fontStyle) {
        this.lexeme = lexeme;
        this.line = line;
        this.offset = offset;
        length = lexeme.length();
        this.fontStyle = fontStyle;
    }

}
