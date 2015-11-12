package solkris.ru.aste.lexer;

/**
 * Created by serbis on 26.10.15.
 */
public class Num extends Token {
    int tag = Tag.NUM;

    public Num(String lexeme, int line, int offset) {
        super(lexeme, line, offset);

        identNum();
    }
}
