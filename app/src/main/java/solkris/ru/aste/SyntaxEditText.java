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
    private ArrayList<List<Token>> tokla = new ArrayList<>();;
    /** Current cursor position */
    private int curpos = 0;
    /** Current selected token */
    private Token currenttok = null;
    /** Position the current token in the array tokla */
    private Pos currentpos = null;
    /** Next token position*/
    private Pos np = new Pos(0, 0, 0);

    //private boolean nline;

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
        currentpos = new Pos(0, 0, 0);
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

                        if (tokla.size() == 0) { //Если это первый символ ввода
                            tokla.add(new ArrayList<Token>());
                            Token tok = new Token(sub, 0, 1, false);
                            createToken(new Pos(0, 0, 0), tok);
                            return;
                        } else {
                           np = getNextTokenPos(currentpos, 1);
                        }
                        if (sub.equals("\n")) {
                            ifCharNewLine();
                            return;
                        }
                        if (currenttok.tag == Tag.SPACE) { //если это последний симов токена и если впереди не пробельный токен
                            ifCurtokSpace(sub);
                        } else {
                            ifCurtokNotSpace(sub);

                        }
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
     * Branching analysis of character entered - if the current token is a
     * whitespace. Checks is the position of the last character in the input
     * stream. If so, it creates a new token. Next comes the test of whether
     * there is a token in front, other than whitespace. In this case the
     * addition of the following method is called a token. If the input character
     * is a space, it is complemented by the current token. After all the tests
     * concluded that the insertion of a character occurs in the body of the token
     * white space, and the method is called inserting a new token.
     *
     * @param sub Processed symbols
     */
    private void ifCurtokSpace(String sub) {
        try {
            if (np == null) {
                Token tok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                tok = lexer.overrideToken(tok);
                createToken(currentpos, tok);
            } else if (currentpos.interoffset == currenttok.length && //то дополняем впереди идущий
                    tokla.get(np.line).get(np.interoffset).tag != Tag.SPACE &&
                    !sub.equals(" ")) {
                appendTokenLeftSide(sub.charAt(0));
            } else if (sub.equals(" ")) { //иначе если введеный символ пробел
                addOneChar(sub.charAt(0)); //то дополняем пробельный токен
            } else {
                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                insertToken(currentpos, ntok);
            }
        } catch (Exception ignored) {}
    }

    /**
     * Branching analysis of character entered - if the current token is
     * non-whitespace. Checks whether the character typed space or not, and
     * calls the handlers of these types of characters.
     *
     * @param sub Processed symbols
     */
    private void ifCurtokNotSpace(String sub) {
        try {
            char ch;
            if (sub.length() == 1) {
                ch = sub.charAt(0);
            } else {
                ch = sub.charAt(sub.length() - 1);
            }
            if (ch != ' ') { //если введенный символ не пробел то то что ниже
                ifCharNotSpace(ch, sub);
            } else { //иначе провеодим сегментацию участ
                ifCharSpace(sub);
            }
        } catch (Exception ignored) {}
    }

    public void ifCharNewLine() {
        try {
            Token ntok = new Token("\n", currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
            ntok = lexer.overrideToken(ntok);
            createToken(currentpos, ntok);
            if (tokla.size() > currentpos.line) {
                tokla.add(new ArrayList<Token>());
            } else {
                tokla.add(currentpos.line, new ArrayList<Token>());
            }
        } catch (Exception ignored) {}
    }

    /**
     * Branching analysis of character entered - if the current character
     * space. Checks whether the current position of the input between the
     * two complementary symbols. If so, it creates a new token. If the
     * previous file symbol refers to complementary class, and ahead is not
     * some complimentary token, it complements it with this symbol. If the
     * input is happening on the far left position, it does not check whether
     * the character entered complimentary. If this is true, then it creates
     * from it a new token. If the character is a newline, it creates from it
     * a new token. If all the above is not true, it adds character to the
     * current token.
     *
     * @param ch Processed character
     * @param sub Processed symbols
     */
    public void ifCharNotSpace(char ch, String sub) {
        try {
            if (np != null) {
                if ((currenttok.lexeme.equals("(") && tokla.get(np.line).get(np.offset).lexeme.equals(")")) ||
                        (currenttok.lexeme.equals("[") && tokla.get(np.line).get(np.offset).lexeme.equals("]")) ||
                        (currenttok.lexeme.equals("{") && tokla.get(np.line).get(np.offset).lexeme.equals("}")) ||
                        (currenttok.lexeme.equals("\"") && tokla.get(np.line).get(np.offset).lexeme.equals("\"")) ||
                        (currenttok.lexeme.equals("'") && tokla.get(np.line).get(np.offset).lexeme.equals("'"))) {
                    Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                    ntok = lexer.overrideToken(ntok);
                    createToken(currentpos, ntok);
                } else if ((currenttok.lexeme.equals("(") && !tokla.get(np.line).get(np.offset).lexeme.equals(")")) ||
                        (currenttok.lexeme.equals("[") && !tokla.get(np.line).get(np.offset).lexeme.equals("]")) ||
                        (currenttok.lexeme.equals("{") && !tokla.get(np.line).get(np.offset).lexeme.equals("}")) ||
                        (currenttok.lexeme.equals("\"") && !tokla.get(np.line).get(np.offset).lexeme.equals("\"")) ||
                        (currenttok.lexeme.equals("'") && !tokla.get(np.line).get(np.offset).lexeme.equals("'"))) {
                    appendTokenLeftSide(ch);
                }
            }
            if (ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '{'||
                    ch == '}' || ch == '"'){
                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                createToken(new Pos(currentpos.line, currentpos.offset, 0), ntok);
            } else if (currenttok.lexeme.equals("\n")) {
                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                createToken(new Pos(currentpos.line, currentpos.offset, 0), ntok);
            } else {
                addOneChar(ch);
            }
        } catch (Exception ignored) {}
    }

    public void ifCharSpace(String sub) {
        try {
            if (np == null) {
                Token tok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                tok = lexer.overrideToken(tok);
                createToken(new Pos(currentpos.line, currentpos.offset, 0), tok);
            } else if (tokla.get(np.line - 1).get(np.offset).tag != Tag.SPACE) {
                Token tok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                tok = lexer.overrideToken(tok);
                createToken(currentpos, tok);
            } else {
                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                insertToken(currentpos, ntok);
            }
        } catch (Exception ignored) {}
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
        int line = 1;

        List<Token> tokens = lexer.scanAll(text);
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
        ;
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

    private void removeTokenSpans(Token tok) {
        ForegroundColorSpan[] fcs = getEditableText().getSpans(tok.offset - 1, tok.offset + tok.length - 1, ForegroundColorSpan.class);


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
            //if (!nline) {
                currentpos = getSelectedTokenPos(selStart);
            //} else {
            //   nline = false;
            //}
            //Неочевидная инкрементация позиции по строке в методе getSelectedTokenPos. Так же
            //сделать переном после пробела и проверить удаление переносов!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (currentpos != null) {
                currenttok = tokla.get(currentpos.line).get(currentpos.offset);
                Log.d("TOKEN", String.valueOf(currenttok.lexeme + " POS=" + currentpos.line + ":" + currentpos.offset + ":" + currentpos.interoffset));
            } else {
                Log.d("MSG", "currentpos NULL");
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
                replaceTokenInText(tok, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //resizeTokensOffset(tokpos, -1);
            tokla.get(tokpos.line).remove(tokpos.offset);
            if (!currenttok.lexeme.equals("\n")) {
                if (tokla.get(0).size() != 0) {
                    joinTokens(new Pos(currentpos.line, currentpos.offset - 1, 0), new Pos(currentpos.line, currentpos.offset, 0));
                } else {
                    tokla.remove(0);
                }
            }
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
            tok.lexeme += ch;
            resizeTokensOffset(tokpos, +1);
            if (tokpos.interoffset + 1 == tok.length) {
                tok.lexeme = tok.lexeme.substring(0, tokpos.interoffset) + ch;
            } else {
                tok.lexeme = tok.lexeme.substring(0, tokpos.interoffset) + ch + tok.lexeme.substring(tokpos.interoffset, tok.lexeme.length());
            }
            tok = lexer.overrideToken(tok);
            tokla.get(currentpos.line).set(currentpos.offset, tok);
            replaceTokenInText(tok, 0);
            if (currenttok.lexeme.equals("\n")) {
                //currentpos.line++;
                currentpos.offset = 0;
                //nline = true;
            }
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
            replaceTokenInText(tok, 0);
        } catch (Exception e) { e.printStackTrace(); }
    }

    //Метод режет рокен на две части и в середину вставляет новый
    private void insertToken(Pos pos, Token token) {
        try {
            Token ntok = new Token(currenttok.lexeme.substring(currentpos.interoffset, currenttok.length), currentpos.line, token.offset, currenttok.constflag);
            currenttok.lexeme = currenttok.lexeme.substring(0, currentpos.interoffset);
            currenttok.length = currentpos.interoffset;
            ntok = lexer.overrideToken(ntok);
            tokla.get(pos.line).set(pos.offset, lexer.overrideToken(currenttok));
            tokla.get(pos.line).add(pos.offset + 1, token);
            tokla.get(pos.line).add(pos.offset + 2, ntok);
            resizeTokensOffset(getNextTokenPos(pos, 1), token.length);
            replaceTokenInText(tokla.get(pos.line).get(pos.offset), 0);
            replaceTokenInText(token, 0);
            replaceTokenInText(ntok, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * Завершили мы работу тут, вроде как инъекция работает нормально и без глюков.
         * Полагалю что стоит начать с проверки метода getNextToken. Потом сделать слипание
         * токенов при удалении единицы. А псоле этого сплитирование непробельных токенов.
         *
         */
    }

    private void joinTokens(Pos p1, Pos p2) {
        try {
            Token t2 = tokla.get(p2.line).get(p2.offset);
            tokla.get(p1.line).get(p1.offset).lexeme += t2.lexeme;
            tokla.get(p1.line).get(p1.offset).length += t2.length;
            tokla.get(p1.line).get(p1.offset).constflag = t2.constflag;
            tokla.get(p2.line).remove(p2.offset);
            Token tokn = lexer.overrideToken(tokla.get(p1.line).get(p1.offset));
            tokla.get(p1.line).set(p1.offset, tokn);
            replaceTokenInText(tokn , 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createToken(Pos pos, Token token) {
        try {
            if (np == null) {
                tokla.get(pos.line).add(token);
            } else {
                tokla.get(pos.line).add(pos.offset, token);
            }
            Token tokn = lexer.overrideToken(token);
            resizeTokensOffset(getNextTokenPos(pos, 1), tokn.length);
            replaceTokenInText(tokn, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pos getNextTokenPos(Pos pos, int step) {
            int ls = tokla.get(pos.line).size();
            if (pos.offset + step > ls) {
                int min = pos.offset + step - ls;
                return new Pos(pos.line + 1, min, 0);
            } else {
                if (ls < pos.offset + step + 1) {
                    return null;
                } else {
                    return new Pos(pos.line, pos.offset + step, 0);
                }
            }

    }

    private Token getPrevToken(Pos pos, int step) {
        int ls = tokla.get(pos.line).size();
        if (pos.offset - step <= ls) {
            int min = pos.offset + step - ls;
            return tokla.get(pos.line - 1).get(min);
        } else {
            return tokla.get(pos.line).get(pos.offset - step);
        }
    }


    /**
     * Convenience class for describing two-dimensional arrays positions
     * within.
     *
     */
    private static class Pos {
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
