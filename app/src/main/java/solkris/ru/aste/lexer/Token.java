package solkris.ru.aste.lexer;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import solkris.ru.aste.desc.FontStyle;
import solkris.ru.aste.desc.Keyword;

/**
 * Main view class token
 */
public class Token {
    /** The numeric code of the token */
    public int tag;
    /** Text representation of token */
    public String lexeme;
    /** Line number on witch the token */
    public int line;
    /** Offset in characters from the beginning of the file */
    public int offset;
    /** Length in characters of the lexeme */
    public int length;
    /** Flag constant values. See the definition in the constructor*/
    public boolean constflag = false;
    /** Font style of a particular token */
    public FontStyle fontStyle;
    /** List of keyword specified */
    public static List<Keyword> keywords = new ArrayList<Keyword>();
    /** Text style of numbers*/
    public static FontStyle numbersStyle = null;
    /** Text style of constants (Such as "...") */
    public static FontStyle constantStyle  = null;
    /** Text style of undefined words */
    public static FontStyle textStyle  = null;
    /** Define default style of number keyword */
    public static final FontStyle STYLE_DEF_NUMBER = new FontStyle(10, Color.BLUE, "-", false, false, false);
    /** Define default style of constant keyword */
    public static final FontStyle STYLE_DEF_CONSTANT = new FontStyle(10, Color.GREEN, "-", false, false, false);
    /** Define default style of undefined text keyword */
    public static final FontStyle STYLE_DEF_TEXT = new FontStyle(10, Color.BLACK, "-", false, false, false);

    /**
     * Constructor 1. Sets the token params. Specifies the length of the token.
     * Sets default tokens style.
     *
     * @param lexeme Text representation of token
     * @param line Line number on witch the token
     * @param offset Offset in characters from the beginning of the file
     * @param constflag Flag constant values. If it is set, the style of
     *                  illumination of the tokens will be replaced with
     *                  the style constants. This is to ensure that definitely
     *                  determine the constants within quotation marks, even
     *                  if they con- tains other keywords.
     */
    public Token(String lexeme, int line, int offset, boolean constflag) {
        this.lexeme = lexeme;
        this.line = line;
        this.offset = offset;
        this.constflag = constflag;
        length = lexeme.length();
    }

    /**
     *  Sets the style of the token as a key word. It goes through all the
     *  keywords you need to find the style assigns token style keyword.
     *  If the token is a constant attributable to a constant of his style.
     *  In the absence of any style, it initiates from the default.
     */
    public void identKey() {
        for (int i = 0; i < keywords.size(); i++) {
            if (keywords.get(i).lexeme.equals(lexeme)) {
                if (!constflag) {
                    fontStyle = keywords.get(i).fontStyle;
                } else {
                    if (constantStyle != null) {
                        fontStyle = constantStyle;
                    } else {
                        fontStyle = STYLE_DEF_CONSTANT;
                    }
                }
                break;
            }
        }
    }

    /**
     *  Sets the style of the token as a number. If the token is a constant
     *  attributable to a constant of his style. In the absence of any style,
     *  it initiates from the default.
     */
    public void identNum() {
        if (!constflag) {
            if (numbersStyle != null) {
                fontStyle = numbersStyle;
            } else {
                fontStyle = STYLE_DEF_NUMBER;
            }
        } else {
            if (constantStyle != null) {
                fontStyle = constantStyle;
            } else {
                fontStyle = STYLE_DEF_CONSTANT;
            }
        }
    }

    /**
     * Do nothing...
     */
    public void identSpace() {
        fontStyle = STYLE_DEF_TEXT;
    }

    /**
     *  Sets the style of the token as a text. If the token is a constant
     *  attributable to a constant of his style. In the absence of any style,
     *  it initiates from the default.
     *
     */
    public void identText() {
        if (!constflag) {
            if (numbersStyle != null) {
                fontStyle = textStyle;
            } else {
                fontStyle = STYLE_DEF_TEXT;
            }
        } else {
            if (constantStyle != null) {
                fontStyle = constantStyle;
            } else {
                fontStyle = STYLE_DEF_CONSTANT;
            }
        }
    }
}
