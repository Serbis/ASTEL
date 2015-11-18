package solkris.ru.aste;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.EditText;

import java.io.IOException;
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
    private List<Spannable> htmlStrings = new ArrayList<Spannable>();

    private ArrayList<List<Token>> tokla = null;

    private int curpos = 0;


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

    /**
     * Set some raw text. In the beginning there is a check whether the object
     * is a lexical analyzer. If not, then it creates. Then it sends text to
     * the lexical analyzer and receives a stream of tokens, which is based on
     * data about the number of rows in each token, breaks it into an array of
     * strings. Then invokes the method for forming an array of the token
     * string array, which then merges into a single row and set to
     * view.
     *
     * @param text Raw text
     */
    public void setText(String text) {
        int line = 1;
        if (lexer == null) {
            lexer = new Lexer();
        }

        List<Token> tokens = lexer.scanAll(text);
        tokla = new ArrayList<>();
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
        SpannableStringBuilder sp = new SpannableStringBuilder();
        for (int i = 0; i < htmlStrings.size(); i++) {
            sp.append(htmlStrings.get(i));
        }
        setText(sp, BufferType.SPANNABLE);
    }

    /**
     * It creates an array of rows of tokens array of strings in html format
     * suitable for placing in the edittext.
     *
     * @param ta Array of tokens
     * @return Html array of strings
     */
    private List<Spannable> formateHtmlString(ArrayList<List<Token>> ta) {
        List<Spannable> list = new ArrayList<>();
        for (int i = 0; i < ta.size(); i++) {
            //StringBuilder sb = new StringBuilder();
            SpannableStringBuilder spannable = new SpannableStringBuilder();
            int offset = 0;
            for (int j = 0; j < ta.get(i).size(); j++) {
                Token tok = ta.get(i).get(j);
                spannable.append(tok.lexeme);
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#" + tok.fontStyle.color)), offset, offset + tok.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannable.setSpan(new RelativeSizeSpan(tok.fontStyle.size), offset, offset + tok.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannable.setSpan(new TypefaceSpan(tok.fontStyle.font), offset, offset + tok.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                if (tok.fontStyle.bold)
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), offset, offset + tok.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                if (tok.fontStyle.italic)
                    spannable.setSpan(new StyleSpan(Typeface.ITALIC), offset, offset + tok.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                if (tok.fontStyle.underline)
                    spannable.setSpan(new UnderlineSpan(), offset, offset + tok.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                offset += tok.length;


                /*sb.append("<font color=\"#").append(tok.fontStyle.color).append("\" ");
                sb.append("size=\"").append(tok.fontStyle.size).append("\" ");
                sb.append("face=\"").append(tok.fontStyle.font).append("\">");
                if (tok.fontStyle.bold)
                    sb.append("<b>");
                if (tok.fontStyle.italic)
                    sb.append("<i>");
                if (tok.fontStyle.underline)
                    sb.append("<u>");
                if (tok.tag == Tag.SPACE) {
                    for (int m = 0; m < tok.length; m++) {
                        sb.append("&nbsp;");
                    }
                } else {
                    sb.append(tok.lexeme);
                }
                sb.append("</font>");
                if (tok.lexeme.equals("\n"))
                    sb.append("<br>");*/

            }
            list.add(spannable);
        }

        return list;
    }

    private Pos getSelectedTokenPos(int pos) {
        for (int i = 0; i < tokla.size(); i++) {
            for (int j = 0; j < tokla.get(i).size(); j++) {
                if (pos - i + 1 > tokla.get(i).get(j).offset && pos - i + 1 <= tokla.get(i).get(j).offset + tokla.get(i).get(j).length) {
                    //if (j == 0 && pos - i + 1 == tokla.get(i).get(j).offset - 1)
                    //    return new Pos(i - 1, tokla.get(i - 1).size() - 1);
                    return new Pos(i, j, pos - i + 1 - tokla.get(i).get(j).offset);
                }
            }
        }

        return null;
    }

    private void replaceTokenInText(Token tok) {

    }

    private void resizeTokensOffset(Pos pos, int inde) {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DEL:
                Pos tokpos = getSelectedTokenPos(curpos);
                Token tok = tokla.get(tokpos.line).get(tokpos.offset);
                tok.length--;
                resizeTokensOffset(tokpos, -1);
                tok.lexeme = tok.lexeme.substring(0, tokpos.interoffset) + tok.lexeme.substring(tokpos.interoffset + 1, tok.lexeme.length());
                try {
                    tok = lexer.overrideToken(tok);
                    replaceTokenInText(tok);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case KeyEvent.KEYCODE_SPACE:

                break;
            case KeyEvent.KEYCODE_ENTER:

                break;
            default:
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        curpos = selStart;

        if (tokla != null) {
            Pos tokpos = getSelectedTokenPos(selStart);
            if (tokpos != null) {
                Token tok = tokla.get(tokpos.line).get(tokpos.offset);

            }
        }
    }



    private class Pos {
        public int line;
        public int offset;
        public int interoffset;

        public Pos() {}

        public Pos(int line, int offset, int interoffset) {
            this.line = line;
            this.offset = offset;
            this.interoffset = interoffset;
        }
    }
}
