package solkris.ru.aste.lexer;

/**
 * Created by serbis on 12.11.15.
 */
public class Text extends Token{
    int tag = Tag.UNDEF;

    public Text(String lexeme, int line, int offset) {
        super(lexeme, line, offset);

        identText();
    }
}
