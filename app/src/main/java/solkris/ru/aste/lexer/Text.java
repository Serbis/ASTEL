package solkris.ru.aste.lexer;

/**
 * Describes text tokens
 */
public class Text extends Token{
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
    public Text(String lexeme, int line, int offset, boolean constflag) {
        super(lexeme, line, offset, constflag);

        tag = Tag.TEXT;
        identText();
    }
}
