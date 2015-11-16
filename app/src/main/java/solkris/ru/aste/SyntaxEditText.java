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
        Token.numbersStyle = style;
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

    public void setText(String text) {
        int line = 1;
        if (lexer == null) {
            lexer = new Lexer();
        }

        List<Token> tokens = lexer.scanAll(text);
        ArrayList<List<Token>> tokla = new ArrayList<>();
        List<Token> ts = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).line == line) {
                ts.add(tokens.get(i));
            } else {
                line = tokens.get(i).line;
                tokla.add(ts);
                ts = new ArrayList<>();
                ts.add(tokens.get(i));
            }
        }
        tokla.add(ts);
        htmlStrings = formateHtmlString(tokla);
        int a;
        a = 1 + 2;
        //setText(Html.fromHtml(formateHtml(tokens)));
    }

    private List<String> formateHtmlString(ArrayList<List<Token>> ta) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < ta.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < ta.get(i).size(); j++) {
                String tmp = "<font color=\"#FF8C00\" size=\"4\" face=\"Arial, Helvetica, sans-serif\"><u><i><b>class</font><br>";
                Token tok = ta.get(i).get(j);
                sb.append("<font color=\"#").append(parceHexFromInt(tok.fontStyle.color)).append("\" ");
                sb.append("size=\"").append(tok.fontStyle.size).append("\" ");
                sb.append("face=\"").append(tok.fontStyle.font).append("\">");
                if (tok.fontStyle.bold)
                    sb.append("<b>");
                if (tok.fontStyle.italic)
                    sb.append("<i>");
                if (tok.fontStyle.underline)
                    sb.append("<u>");
                //if (tok.ta)
                sb.append(tok.lexeme);
                sb.append("</font>");
                if (tok.lexeme.equals("\n"))
                    sb.append("<br>");
            }
            list.add(sb.toString());
        }

        return list;
    }

    private String parceHexFromInt(int n) {
        String hex = Integer.toHexString(n);
        if (hex.length() < 6) { //добавляем отсутствуюшие нули
            String h = hex;
            for (int i = 0; i < 6 - h.length(); i++) {
                hex = "0" + hex;
            }
        }
        return hex;
    }
}
