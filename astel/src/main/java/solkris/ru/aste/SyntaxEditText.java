package solkris.ru.aste;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private boolean notfirts = true;

    private boolean replacef = false;

    private boolean replacedf = false;

    private LexAn prevLexAn = null;

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
        linespos.add(new Line(0, 1));
        setImeOptions(EditorInfo.IME_ACTION_NONE);
        setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!replacedf) {
                    if (notfirts && count > after) {
                        if (s.subSequence(start, start + 1).toString().equals("\n")) {
                            int curlinepos = getSelectedLine(start + 2);
                            int prevlinepos = getSelectedLine(start + 1);
                            Line curline = linespos.get(curlinepos);
                            Line prevline = linespos.get(prevlinepos);
                            if (curline.end - curline.start <= 1) {
                                linespos.get(curlinepos - 1).end += 2;
                                linespos.remove(curlinepos);
                                replacedf = true;
                                //getText().delete(start, start + 1);
                            } else { //Если в ней есть символы
                                linespos.get(curlinepos - 1).end += 1;
                                linespos.get(curlinepos - 1).end += curline.end - curline.start;
                                linespos.remove(curlinepos);
                            }
                        }
                    }
                    replacedf = false;
                } else {
                    replacedf = false;
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!replacef) {
                    String sub;
                    int curlinepos = getSelectedLine(start);
                    if (linespos.get(curlinepos).end - linespos.get(curlinepos).start == 0) {
                        sub = "";
                    } else {
                        sub = s.subSequence(start, start + count).toString();
                    }
                    if (sub.equals("\n")) {
                        curlinepos = getSelectedLine(start);
                        if (s.charAt(start - 1) == '\n') {
                            curlinepos++;
                        }
                        if (start + 1 == linespos.get(curlinepos).start) { //Новая строка с начала
                            int nlinestart = start + 1;
                            int nlineend = start + 1;
                            linespos.add(curlinepos, new Line(nlinestart, nlineend));
                            resizeLinesOffset(curlinepos + 1, 1);
                            Log.d("d", "d");
                        } else if (start + 1 == linespos.get(curlinepos).end) { //Новая строка с конца
                            int nlinestart = start + 2;
                            int nlineend = start + 2;
                            linespos.add(curlinepos + 1, new Line(nlinestart, nlineend));
                            resizeLinesOffset(curlinepos + 2, 1);
                            Log.d("d", "d");
                            return;
                        } else {// Новая строка с середины
                            int nlinestart = start + 2;
                            int nlineend = linespos.get(curlinepos).end + 1;
                            linespos.add(curlinepos + 1, new Line(nlinestart, nlineend));
                            linespos.get(curlinepos).end = start + 1;
                            resizeLinesOffset(curlinepos + 2, 1);
                            intputf = true;
                            reSetLine(getStringInLine(curlinepos), curlinepos);
                            intputf = true;
                            reSetLine(getStringInLine(curlinepos + 1), curlinepos + 1);
                            return;
                        }
                    }
                    if (notfirts) {
                        if (count > before) {//add one char
                            try {
                                int l = getSelectedLine(start + 1);
                                resizeLinesOffset(l + 1, 1);
                                linespos.get(l).end++;
                                intputf = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (count < before) { //del one char
                            int l = getSelectedLine(start);
                            resizeLinesOffset(l + 1, -1);
                            if (start != 0)
                                linespos.get(l).end--;
                            intputf = true;
                        }
                    }

                    replacef = false;

                } else {
                    replacef = false;
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
        boolean nlf = false;
        linespos.clear();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).line == line) {
                if (linespos.size() == tokens.get(i).line) {
                    nlf = false;
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
                if (nlf) {
                    linespos.add(new Line());
                    linespos.get(tokens.get(i - 1).line).start = tokens.get(i - 2).offset + tokens.get(i - 2).length;
                    linespos.get(tokens.get(i - 1).line).end = tokens.get(i - 2).offset + tokens.get(i - 2).length;
                    line = tokens.get(i).line;
                    tokla.add(ts);
                    ts = new ArrayList<>();
                    ts.add(tokens.get(i));
                } else {
                    linespos.get(tokens.get(i - 1).line).end = tokens.get(i - 2).offset + tokens.get(i - 2).length;
                    line = tokens.get(i).line;
                    tokla.add(ts);
                    ts = new ArrayList<>();
                    ts.add(tokens.get(i));
                    nlf = true;
                }
            }
        }
        linespos.add(new Line(ts.get(0).offset, ts.get(ts.size() - 1).offset + ts.get(ts.size() - 1).length));
        tokla.add(ts);
        LexAn la = new LexAn();
        htmlStrings = la.formateHtmlString(tokla);
        SpannableStringBuilder sp = new SpannableStringBuilder();
        for (int i = 0; i < htmlStrings.size(); i++) {
            sp.append(htmlStrings.get(i));
        }
        notfirts = false;
        setText(sp, BufferType.EDITABLE);
        notfirts = true;
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
        for (int i = fromline; i < linespos.size(); i++) {
            linespos.get(i).start += offset;
            linespos.get(i).end += offset;
        }
    }

    private String getStringInLine(int line) {
        try {
            if (linespos.get(line).start == 0) {
                return getText().toString().substring(linespos.get(line).start, linespos.get(line).end - 1);
            } else {
                return getText().toString().substring(linespos.get(line).start - 1, linespos.get(line).end - 1);
            }
        } catch (Exception e) {
            return "";
        }
    }

    private void reSetLine(String str, int line) {
        if (intputf) {
            intputf = false;
            replacef = true;
            replacedf = true;

            LexAn lexAn = new LexAn();
            lexAn.init(str, line);
            if (prevLexAn != null) {
                prevLexAn.stop = true;
                prevLexAn.cancel(true);
            }

            ExecutorService service = Executors.newCachedThreadPool();
            //service.submit(lexAn.execute());
            prevLexAn = lexAn;

            lexAn.execute();
        }
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
            int line = getSelectedLine(selStart + 1);
            String str = getStringInLine(line);
            Log.d("SUBSTRING " + String.valueOf(line), str);

            reSetLine(str, line);
        }

    }

    public class LexAn extends AsyncTask<Void, Void, Void> {
        private String str = "";
        private int line = 0;
        private SpannableStringBuilder sp;
        public boolean stop = false;

        public void init(String str, int line) {
            this.str = str;
            this.line = line;

        }



        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<List<Token>> ta = new ArrayList<List<Token>>();
            ta.add(lexer.scanAll(str));
            List<Spannable> lsp = formateHtmlString(ta);
            sp = new SpannableStringBuilder();
            for (int i = 0; i < lsp.size(); i++) {
                sp.append(lsp.get(i));
            }
            publishProgress();

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (!stop) {
                try {
                    if (line == 0) {
                        getText().replace(linespos.get(line).start, linespos.get(line).end - 1, sp);
                    } else {
                        getText().replace(linespos.get(line).start - 1, linespos.get(line).end - 1, sp);
                    }
                } catch (Exception ignored) {}
            } else {
                Log.d("D", "STOP");
            }
        }

        /**
         * It creates an array of rows of tokens array of strings in html format
         * suitable for placing in the edittext.
         *
         * @param ta Array of tokens
         * @return Html array of strings
         */
        public List<Spannable> formateHtmlString(ArrayList<List<Token>> ta) {
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
    }

}
