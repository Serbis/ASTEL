package solkris.ru.aste.lexer;

/**
 * Created by serbis on 12.11.15.
 */
public class Key extends Token{
    int tag = Tag.KEY;

    public Key(String lexeme, int line, int offset) {
        super(lexeme, line, offset);

        identKey();
    }

    public Key(String lexeme) {
        super(lexeme, 0, 0);
        this.lexeme = lexeme;
    }
}
