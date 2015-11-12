package solkris.ru.aste;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import solkris.ru.aste.desc.FontStyle;
import solkris.ru.aste.desc.Keyword;
import solkris.ru.aste.lexer.Lexer;
import solkris.ru.aste.lexer.Token;

/**
 * Created by serbis on 11.11.15.
 */
public class SyntaxEditText extends EditText {
    /** Object of the lexical analyzer */
    private Lexer lexer = null;
    /** List of strings in html representation */
    private List<String> htmlStrings = new ArrayList<String>();

    /**
     * Constructor 1.
     *
     * @param context Application context
     */
    public SyntaxEditText(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor 2.
     *
     * @param context Application context
     * @param attrs Attributes
     */
    public SyntaxEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor 3.
     *
     * @param context Application context
     * @param attrs Attributes
     * @param defStyleAttr Style attributes
     */
    public SyntaxEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Overall initialization procedure for all constructors
     */
    public void init() {
        setSingleLine(false);
        setGravity(Gravity.TOP);
    }


    /**
     * Sets the global list of keywords
     *
     * @param list List of keywords
     */
    public void setKeywordList(List<Keyword> list) {
        Token.keywords = list;
    }

    /**
     * Sets a global style numerical tokens
     *
     * @param style Styles of numerical tokens
     */
    public void setNumbersStyle(FontStyle style) {
        Token.nubersStyle = style;
    }

    /**
     * Sets a global style constants tokens (Such as "...")
     *
     * @param style Styles of constants tokens
     */
    public void setConstantsStyle(FontStyle style) {
        Token.constantStyle = style;
    }

    /**
     * Sets a global style undefined text tokens
     *
     * @param style style of undefined text tokens
     */
    public void setTextStyle(FontStyle style) {
        Token.textStyle = style;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

        if (lexer == null) {
            lexer = new Lexer();
        }

        List<Token> lexstream = lexer.scanAll(text.toString());

        //Вызываем лексический анализатор - метод ScanAll
        //Он возращает нам массив лексемм и мы передаем его в метод
        //формовки html строки, откуда она и ставится как текст компонента
    }
}
