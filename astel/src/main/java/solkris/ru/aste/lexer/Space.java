package solkris.ru.aste.lexer;

/**
 * Describes whitespace tokens
 */
public class Space extends Token {

    /**
     * Constructor 1.
     *
     * @param lexeme Text representation of token
     * @param line Line number on witch the token
     * @param offset Offset in characters from the beginning of the file
     */
    public Space(String lexeme, int line, int offset) {
        super(lexeme, line, offset, false);

        tag = Tag.SPACE;
        identSpace();
    }
}
