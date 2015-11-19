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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import solkris.ru.aste.desc.FontStyle;
import solkris.ru.aste.desc.Keyword;
import solkris.ru.aste.lexer.Lexer;
import solkris.ru.aste.lexer.Tag;
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
    private ArrayList<List<Token>> tokla = null;
    /** Current cursor position */
    private int curpos = 0;
    /** Current selected token */
    private Token currenttok = null;
    /** Position the current token in the array tokla */
    private Pos currentpos = null;


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
                    deleteOneChar();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > before) {//add one char
                    try {
                        String sub = s.subSequence(start, start + count).toString();
                        if (currenttok.tag == Tag.SPACE) { //если это последний симов токена и если впереди не пробельный токен
                            if (currentpos.interoffset == currenttok.length && //то дополняем впереди идущий
                                    tokla.get(currentpos.line).get(currentpos.interoffset + 1).tag != Tag.SPACE &&
                                    !sub.equals(" ")) {
                                appendTokenLeftSide(sub.charAt(0));
                            } else if (sub.equals(" ")) { //иначе если введеный символ пробел
                                addOneChar(sub.charAt(0)); //то дополняем пробельный токен
                            } else {
                                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                                ntok = lexer.overrideToken(ntok);
                                insertToken(currentpos, ntok);
                            }


                             //       иначе если введеный символ пробел то дополняем пробельный токен
                            //        иначе создаем новы токен на позиции ввода
                        } else {
                            //если введенный символ не пробел то то что ниже
                            if (sub.length() == 1) {
                                addOneChar(sub.charAt(0));
                            } else {
                                addOneChar(sub.charAt(sub.length() - 1));
                            }
                            //иначе провеодим сегментацию участа
                        }
                    } catch (Exception ignored) {}

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
        setText(sp, BufferType.EDITABLE);
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
     * It returns the position of the token which is currently under the
     * cursor. To search for loops through the whole array To search for
     * a token goes through the entire array tokla until the fall at the
     * right Range of displacement.
     *
     * @param pos Absolute position in the text
     * @return Pos object
     */
    private Pos getSelectedTokenPos(int pos) {
        for (int i = 0; i < tokla.size(); i++) {
            for (int j = 0; j < tokla.get(i).size(); j++) {
                if (pos + 1 > tokla.get(i).get(j).offset && pos + 1 <= tokla.get(i).get(j).offset + tokla.get(i).get(j).length) {
                    return new Pos(i, j, pos + 1 - tokla.get(i).get(j).offset);
                }
            }
        }

        return null;
    }

    /**
     * It replaces a token one. According to the offset of the token is his
     * style, removes it and installs the new one.
     *
     * @param tok New token
     */
    private void replaceTokenInText(Token tok) {
        ForegroundColorSpan[] fcs = getEditableText().getSpans(tok.offset - 1, tok.offset + tok.length - 1, ForegroundColorSpan.class);
        try {
            getEditableText().removeSpan(fcs[0]);
        } catch (Exception ignored) {}
        getEditableText().setSpan(new ForegroundColorSpan(Color.parseColor("#" + tok.fontStyle.color)), tok.offset - 1, tok.offset + tok.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * Shifts displacement of tokens going for token specified in the argument
     * by some amount.
     *
     * @param pos Position of the starting token
     * @param inde Incremented or decremented value
     */
    private void resizeTokensOffset(Pos pos, int inde) {
        boolean first = true;
        for (int i = pos.line; i < tokla.size(); i++) {
            for (int j = 0; j < tokla.get(i).size(); j++) {
                if (first) {
                    if (j >= pos.offset) {
                        first = false;
                    }
                } else {
                    tokla.get(i).get(j).offset += inde;
                }
            }
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

        curpos = selStart;

        if (tokla != null) {
            currentpos = getSelectedTokenPos(selStart);
            if (currentpos != null) {
                currenttok = tokla.get(currentpos.line).get(currentpos.offset);
                Log.d("TOKEN", currenttok.lexeme);
            }
        }
    }

    /**
     * It removes from the current token one character. It produces the
     * following steps. By reducing the size of the current token unit.It
     * calls the displacement of going ahead tokens. Change the text of the
     * token, and in the lexical analyzer redefines its type. Then it calls
     * the replacement of identity token in the text. It has branching
     * situations when the token is a single symbol. In this case, the token
     * is removed and all the front running tokens are shifted by one.
     *
     */
    private void deleteOneChar() {
        Pos tokpos = getSelectedTokenPos(curpos);
        Token tok = tokla.get(tokpos.line).get(tokpos.offset);
        tok.length--;
        resizeTokensOffset(tokpos, -1);
        if (tok.lexeme.length() > 1) {
            if (tokpos.interoffset == 1) {
                tok.lexeme = tok.lexeme.substring(tokpos.interoffset, tok.lexeme.length());
            } else {
                tok.lexeme = tok.lexeme.substring(0, tokpos.interoffset - 1) + tok.lexeme.substring(tokpos.interoffset, tok.lexeme.length());
            }
            try {
                tok = lexer.overrideToken(tok);
                replaceTokenInText(tok);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //resizeTokensOffset(tokpos, -1);
            tokla.get(tokpos.line).remove(tokpos.offset);
        }

    }

    /**
     * Adds the current token is a single character. It produces the following
     * steps. It increases the size of the current token unit.It calls the
     * displacement of going ahead tokens. Change the text of the token, and in
     * the lexical analyzer redefines its type. Then it calls the replacement
     * of identity token in the text.
     *
     * @param ch Adding character
     */
    private void addOneChar(char ch) {
        try {
            Pos tokpos = getSelectedTokenPos(curpos);
            Token tok = tokla.get(tokpos.line).get(tokpos.offset);
            tok.length++;
            resizeTokensOffset(tokpos, +1);
            if (tokpos.interoffset + 1 == tok.length) {
                tok.lexeme = tok.lexeme.substring(0, tokpos.interoffset) + ch;
            } else {
                tok.lexeme = tok.lexeme.substring(0, tokpos.interoffset) + ch + tok.lexeme.substring(tokpos.interoffset, tok.lexeme.length());
            }
            tok = lexer.overrideToken(tok);
            replaceTokenInText(tok);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Adds a symbol to the beginning of the token. This method is called
     * in a situation where there is an attempt to enter the character at
     * the beginning of the token to token positions whitespace. Since the
     * logic of the program does not allow to do it directly, this method is
     * called.
     *
     * @param ch Appending character
     */
    private void appendTokenLeftSide(char ch) {
        try {
            Pos tokpos = new Pos(currentpos.line, currentpos.offset + 1, 0);
            Token tok = tokla.get(tokpos.line).get(tokpos.offset);
            tok.length++;
            resizeTokensOffset(tokpos, +1);
            tok.lexeme = ch + tok.lexeme;
            tok = lexer.overrideToken(tok);
            replaceTokenInText(tok);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void insertToken(Pos pos, Token token) {
        try {
            currenttok.length = currentpos.interoffset;
            Token ntok = new Token(currenttok.lexeme.substring(currentpos.interoffset - 1, currenttok.length), currentpos.line, currenttok.offset + currenttok.length, currenttok.constflag);
            ntok = lexer.overrideToken(ntok);
            tokla.get(pos.line).add(pos.offset + 1, token);
            tokla.get(pos.line).add(pos.offset + 2, ntok);
            resizeTokensOffset(getNextTokenPos(pos, 1), token.length);
            replaceTokenInText(token);
            replaceTokenInText(ntok);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * Завершили мы работу тут, вроде как инъекция работает нормально и без глюков.
         * Полагалю что стоит начать с проверки метода getNextToken. Потом сделать слипание
         * токенов при удалении единицы. А псоле этого сплитирование непробельных токенов.
         *
         */
    }

    private Pos getNextTokenPos(Pos pos, int step) {
        int ls = tokla.get(pos.line).size();
        if (pos.offset + step > ls) {
            int min = pos.offset + step - ls;
            return new Pos(pos.line + 1, min, 0);
        } else {
            return new Pos(pos.line, pos.offset + step, 0);
        }
    }


    /**
     * Convenience class for describing two-dimensional arrays positions
     * within.
     *
     */
    private class Pos {
        /** Line */
        public int line;
        /** Offset in line */
        public int offset;
        /** Shift in the token*/
        public int interoffset;

        /**
         * Constructor 1. Without parameters.
         *
         */
        public Pos() {}

        /**
         * Constructor 2.
         *
         * @param line Line
         * @param offset Offset in line
         * @param interoffset Offset in the token
         */
        public Pos(int line, int offset, int interoffset) {
            this.line = line;
            this.offset = offset;
            this.interoffset = interoffset;
        }
    }
}
