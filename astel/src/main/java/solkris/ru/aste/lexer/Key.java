package solkris.ru.aste.lexer;

/**
 * Describe the token keyword. The keyword must be understood in touch
 * backlight requiring multiple words.
 */
public class Key extends Token{
    /**
     * Constructor 1.
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
    public Key(String lexeme, int line, int offset, boolean constflag) {
        super(lexeme, line, offset, constflag);

        tag = Tag.KEY;
        identKey();
    }

    /**
     * Constructor 2. Simplified constructor for keyword reservation
     * procedure.
     *
     * @param lexeme Text representation of token
     */
    public Key(String lexeme) {
        super(lexeme, 0, 0, false);
        this.lexeme = lexeme;
        tag = Tag.KEY;
    }
}
