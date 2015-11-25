package solkris.ru.aste;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import solkris.ru.aste.desc.FontStyle;
import solkris.ru.aste.desc.Keyword;
import solkris.ru.aste.desc.Line;
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
    /** A string representation of the array of tokens */
    private ArrayList<List<Token>> tokla = new ArrayList<>();

    private List<Line> linespos = new ArrayList<Line>();

    private boolean intputf = false;

    private boolean notfirts = false;

    private boolean replacef = false;

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
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > after) {//deleted one char
                    if (notfirts) {
                        int l = getSelectedLine(start);
                        resizeLinesOffset(l, -1);
                        linespos.get(l).end--;
                        intputf = true;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > before) {//add one char
                    try {
                        String sub = s.subSequence(start, start + count).toString();
                        //if (!replacef) {
                            if (notfirts) {
                                int l = getSelectedLine(start);
                                resizeLinesOffset(l, 1);
                                linespos.get(l).end++;
                                intputf = true;
                            }
                       // } else {
                        //    replacef = false;
                        //}

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * Sets the global list of keywords
     *
     * @param list List of keywords
     */
    public void setKeywordList(List<Keyword> list) {
        Token.keywords = list;
        lexer = new Lexer();
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
        int line = 0;

        List<Token> tokens = lexer.scanAll(text);
        List<Token> ts = new ArrayList<>();

        boolean f = true;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).line == line) {
                if (linespos.size() == tokens.get(i).line) {
                    linespos.add(new Line());
                    if (f) {
                        linespos.get(tokens.get(i).line).start = tokens.get(i).offset - 1;
                        f = false;
                    } else {
                        linespos.get(tokens.get(i).line).start = tokens.get(i - 1).offset;
                    }
                }
                ts.add(tokens.get(i));
            } else {
                linespos.get(tokens.get(i - 1).line).end = tokens.get(i - 2).offset + tokens.get(i - 2).length;
                line = tokens.get(i).line;
                tokla.add(ts);
                ts = new ArrayList<>();
                ts.add(tokens.get(i));
            }
        }
        linespos.add(new Line(ts.get(0).offset, ts.get(ts.size() - 1).offset + ts.get(ts.size() - 1).length));
        tokla.add(ts);
        htmlStrings = formateHtmlString(tokla);
        SpannableStringBuilder sp = new SpannableStringBuilder();
        for (int i = 0; i < htmlStrings.size(); i++) {
            sp.append(htmlStrings.get(i));
        }

        setText(sp, BufferType.EDITABLE);
        notfirts = true;
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
            SpannableStringBuilder spannable = new SpannableStringBuilder();
            int offset = 0;
            for (int j = 0; j < ta.get(i).size(); j++) {
                Token tok = ta.get(i).get(j);
                spannable.append(tok.lexeme);
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#" + tok.fontStyle.color)), offset, offset + tok.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new RelativeSizeSpan(tok.fontStyle.size), offset, offset + tok.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new TypefaceSpan(tok.fontStyle.font), offset, offset + tok.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (tok.fontStyle.bold)
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), offset, offset + tok.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (tok.fontStyle.italic)
                    spannable.setSpan(new StyleSpan(Typeface.ITALIC), offset, offset + tok.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (tok.fontStyle.underline)
                    spannable.setSpan(new UnderlineSpan(), offset, offset + tok.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                offset += tok.length;
            }
            list.add(spannable);
        }

        return list;
    }

    /**
     * It replaces a token one. According to the offset of the token is his
     * style, removes it and installs the new one.
     *
     * @param tok New token
     * @param rcor Rigth side offset correction
     */
    private void replaceTokenInText(Token tok, int rcor) {
        ForegroundColorSpan[] fcs = getEditableText().getSpans(tok.offset - 1, tok.offset + tok.length - 1, ForegroundColorSpan.class);
        RelativeSizeSpan[] rcs = getEditableText().getSpans(tok.offset - 1, tok.offset + tok.length - 1, RelativeSizeSpan.class);
        TypefaceSpan[] tcs = getEditableText().getSpans(tok.offset - 1, tok.offset + tok.length - 1, TypefaceSpan.class);
        StyleSpan[] scs = getEditableText().getSpans(tok.offset - 1, tok.offset + tok.length - 1, StyleSpan.class);
        UnderlineSpan[] ucs = getEditableText().getSpans(tok.offset - 1, tok.offset + tok.length - 1, UnderlineSpan.class);

        try {
            for (ForegroundColorSpan fc : fcs) {
                getEditableText().removeSpan(fc);
            }
            for (RelativeSizeSpan rc : rcs) {
                getEditableText().removeSpan(rc);
            }
            for (TypefaceSpan tc : tcs) {
                getEditableText().removeSpan(tc);
            }
            for (StyleSpan sc : scs) {
                getEditableText().removeSpan(sc);
            }
            for (UnderlineSpan uc : ucs) {
                getEditableText().removeSpan(uc);
            }
        } catch (Exception ignored) {}
        getEditableText().setSpan(new ForegroundColorSpan(Color.parseColor("#" + tok.fontStyle.color)), tok.offset - 1, tok.offset + tok.length - 1 + rcor, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getEditableText().setSpan(new RelativeSizeSpan(tok.fontStyle.size), tok.offset - 1, tok.offset + tok.length - 1 + rcor, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getEditableText().setSpan(new TypefaceSpan(tok.fontStyle.font), tok.offset - 1, tok.offset + tok.length - 1 + rcor, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (tok.fontStyle.bold)
            getEditableText().setSpan(new StyleSpan(Typeface.BOLD), tok.offset - 1, tok.offset + tok.length - 1 + rcor, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (tok.fontStyle.italic)
            getEditableText().setSpan(new StyleSpan(Typeface.ITALIC), tok.offset - 1, tok.offset + tok.length - 1 + rcor, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (tok.fontStyle.underline)
            getEditableText().setSpan(new UnderlineSpan(), tok.offset - 1, tok.offset + tok.length - 1 + rcor, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    private int getSelectedLine(int pos) {
        for (int i = 0; i < linespos.size(); i++) {
            if (pos >= linespos.get(i).start && pos <= linespos.get(i).end) {
                return i;
            }
        }

        return 0;
    }

    private void resizeLinesOffset(int fromline, int offset) {

    }


    /**
     * Processes change the cursor position.It causes some token on which the
     * course and sets the global variables and the current cursor position
     * of the token.
     *
     * @param selStart -
     * @param selEnd -
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        if (linespos != null) {
            String str = getText().toString();
            int line = getSelectedLine(selStart + 1);
            if (linespos.get(line).start == 0) {
                str = str.substring(linespos.get(line).start, linespos.get(line).end - 1);
            } else {
                str = str.substring(linespos.get(line).start - 1, linespos.get(line).end - 1);
            }
            Log.d("SUBSTRING " + String.valueOf(line), str);

            if (intputf) {

                ArrayList<List<Token>> ta = new ArrayList<List<Token>>();
                ta.add(lexer.scanAll(str));
                List<Spannable> lsp = formateHtmlString(ta);
                SpannableStringBuilder sp = new SpannableStringBuilder();
                for (int i = 0; i < lsp.size(); i++) {
                    sp.append(lsp.get(i));
                }
                intputf = false;
                //replacef = true;
                if (line == 0) {
                    getText().replace(linespos.get(line).start, linespos.get(line).end - 1, sp);
                } else {
                    getText().replace(linespos.get(line).start - 1, linespos.get(line).end - 1, sp);
                }


            }
        }

    }

}
