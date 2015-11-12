package solkris.ru.aste.lexer;

/**
 * Created by serbis on 12.11.15.
 */
public class Space extends Token {
    int tag = Tag.SPACE;

    public Space(String lexeme, int line, int offset) {
        super(lexeme, line, offset);

        identSpace();
    }
}
