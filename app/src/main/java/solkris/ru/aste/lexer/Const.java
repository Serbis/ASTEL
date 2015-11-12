package solkris.ru.aste.lexer;

/**
 * Created by serbis on 12.11.15.
 */
public class Const extends Token{
    int tag = Tag.CONST;

    public Const(String lexeme, int line, int offset) {
        super(lexeme, line, offset);

        identConst();
    }
}
