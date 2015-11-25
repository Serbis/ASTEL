package solkris.ru.aste.lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * The main module of the lexical analyzer. The lexical analyzer has two
 * main objectives. The first lies in the fact that getting to the part
 * of some of the text, he breaks it down into a stream of tokens, each of
 * which contains full information about themselves necessary for the
 * construction of the highlighted text. This character offset, size, style
 * of the text, token type, whether it is a constant and Parameters of the
 * other. The second task of the lexical analyzer is a redefinition of the
 * token. This happens every time there is a change of any token in a text
 * editor.
 */
public class Lexer {
    /** Counter lines of source code */
    private int line = 0;
    /** Pointer to the number of the current unreadable characters */
    private int chpointer = 0;
    /** Source code */
    private String scanstr;
    /** Read from the stream of the source text symbol */
    private char peek = ' ';
    /** The number of spaces, which will be replaced by a tab if it is to meet
     *  in source code*/
    private int tabspace = 5;
    /** Arbitrary displacement, it will stack to shift the original token.
     * There is a consequence of the fact that the tab character is replaced
     * by a certain number of spaces, and offset token starts to lag behind
     * in the n characters to the left. This value is compensated.*/
    private int interoffset = 0;
    /** The flag constants. Set occurs when the quote character. Cleared when
     *  detecting the closing symbol.
     */
    private boolean constactive = false;
    /** An array of keywords */
    private Hashtable<String, Key> keys = new Hashtable<>();

    /**
     * Constructor. Produces reservations keywords
     */
    public Lexer() {
        for (int i = 0; i < Token.keywords.size(); i++) {
            reserve(new Key(Token.keywords.get(i).lexeme));
        }
    }

    /**
     * Add keywords to an array of keywords
     *
     * @param t Keyword token
     */
    private void reserve(Key t) {
        keys.put(t.lexeme, t);
    }

    /**
     * Reads a character from the stream and moves the pointer to the unit.
     * If you reach the end of the stream returns -1
     *
     * @throws IOException The general case of exception handling
     */
    private void readch() throws IOException {
        try {
            peek = scanstr.charAt(chpointer);
            chpointer++;
        } catch (IndexOutOfBoundsException e) {
            peek = (char) -1;
        }
    }

    /**
     * Reads a character from the stream, and compares with the arguments.
     * It returns the result of the comparison.
     *
     * @param c The symbol for comparison
     * @return Result of the comparison
     * @throws IOException The general case of exception handling
     */
    private boolean readch(char c) throws IOException{
        readch();
        if (peek != c) return false;
        peek = ' ';
        return true;
    }

    /**
     * It converts incoming text into an array of tokens. Calls the scan(),
     * each call is returned, a separate token. This token is added to the
     * array. Sampling techniques to occur until the scan method scan()
     * does not return to null.
     *
     * @param input Source text
     * @return Array of token
     *
     */
    public List<Token> scanAll(String input) {
        List<Token> tokens = new ArrayList<>();
        Token st;
        chpointer = 0;
        peek = ' ';
        interoffset = 0;
        line = 0;
        scanstr = input;
        try {
            while((st = scan()) != null) {
                tokens.add(st);
            }
        } catch (IOException ignored) { }

        return tokens;
    }

    /**
     * Gets the next token in the stream of characters. The method is divided
     * into several logical blocks. At the beginning of the design tested model
     * of dual language characters.. If the result is not given the results,
     * there is an attempt to determine the numeric token. If she does not try
     * to identify the given control character. Further checks for an
     * alphabetic token. If If all these checks fail, then this text token
     * indefinitely.
     *
     * @return Detected token
     * @throws IOException
     */
    public Token scan() throws IOException {
        readch();
        switch (peek) {
            case '&':
                if (readch('&')) {
                    return new Text("&&", line, chpointer - 2 + interoffset, constactive);
                } else {
                    chpointer--;
                    return new Text("&", line, chpointer - 1 + interoffset, constactive);
                }
            case '|':
                if (readch('|')) {
                    return new Text("||", line, chpointer - 2 + interoffset, constactive);
                } else {
                    chpointer--;
                    return new Text("|", line, chpointer - 1 + interoffset, constactive);
                }
            case '=':
                if (readch('=')) {
                    return new Text("==", line, chpointer - 2 + interoffset, constactive);
                } else {
                    chpointer--;
                    return new Text("=", line, chpointer + interoffset, constactive);
                }
            case '!':
                if (readch('=')) {
                    return new Text("!=", line, chpointer - 2 + interoffset, constactive);
                } else {
                    chpointer--;
                    return new Text("!", line, chpointer - 1 + interoffset, constactive);
                }
            case '<':
                if (readch('=')) {
                    return new Text("<=", line, chpointer - 2 + interoffset, constactive);
                } else {
                    chpointer--;
                    return new Text("<", line, chpointer - 1 + interoffset, constactive);
                }
            case '>':
                if (readch('=')) {
                    return new Text(">=", line, chpointer - 2 + interoffset, constactive);
                } else {
                    chpointer--;
                    return new Text(">", line, chpointer - 1 + interoffset, constactive);
                }
        }
        if (Character.isDigit(peek)) {
            int v = 0;
            do {
                v = 10 * v + Character.digit(peek, 10);
                readch();
            } while (Character.isDigit(peek));
            if (peek != '.') {
                chpointer--;
                return new Num(String.valueOf(v), line, chpointer - String.valueOf(v).length() + 1 + interoffset, constactive);
            } else {
                String t = "";
                for (;;) {
                    readch();
                    if (!Character.isDigit(peek)) break;
                    t += peek;
                }
                chpointer--;
                return new Num(String.valueOf(v) + "." + t, line, chpointer - String.valueOf(v).length() - t.length() + interoffset, constactive);
            }
            /*float x = v;
            float d = 10;
            for (;;) {
                readch();
                if (!Character.isDigit(peek)) break;
                x = x + Character.digit(peek, 10) / d;
                d = d * 10;
            }
            chpointer--;
            return new Num(String.valueOf(x), line, chpointer - String.valueOf(x).length() + 1 + interoffset, constactive);*/
        }

        Token tokk = getSpecSymbol(peek);
        if (tokk != null) {
            return tokk;
        }

        if (Character.isLetter(peek)) {

            StringBuilder b = new StringBuilder();
            do {
                b.append(peek);
                readch();
            } while (Character.isLetterOrDigit(peek));
            String s = b.toString();
            Key k = keys.get(s);
            if (k != null) {
                Key key = new Key(k.lexeme, line,chpointer - k.length + interoffset,constactive);
                chpointer--;
                return key;
            }
            Text t = new Text(s, line, chpointer - s.length() + interoffset, constactive);
            chpointer--;
            return t;
        }

        if (peek == (char) -1) {
            return null;
        }
        Text t = new Text(String.valueOf(peek), line, chpointer + interoffset, constactive);
        //peek = ' ';
        return t;
    }

    /**
     * Checks symbol of belonging to the control characters. Control characters
     * are to be understood in relation to the lexical analyzer (to quote an
     * example is such). If this space is a method getSpaceToken () for to read
     * a sequence of whitespace. If it's a newline, it increments the counter
     * lines. If it is a tab character, calls the getSpaceToken () to form a
     * tab sequence whitespaces. If this is what any of the quotes, sets a flag
     * constants. Otherwise it distorts null.
     *
     * @param c Incoming symbol
     * @return Detected token
     * @throws IOException
     */
    private Token getSpecSymbol(char c) throws IOException {
        Token t;
        switch (c) {
            case ' ':
                return getSpaceToken();
            case '\n':
                Text text =  new Text(String.valueOf(c), line, chpointer + interoffset, constactive);
                line++;
                return text;
            case '\t':
                return getTabSpaces();
            case '\'':
                if (!constactive) {
                    Key k = keys.get("\'");
                    if (k != null) {
                        t = new Key(String.valueOf(c), line, chpointer + interoffset, constactive);
                    } else {
                        t = new Text(String.valueOf(c), line, chpointer + interoffset, constactive);
                    }
                    constactive = true;
                } else {
                    constactive = false;
                    Key k = keys.get("\'");
                    if (k != null) {
                        t = new Key(String.valueOf(c), line, chpointer + interoffset, constactive);
                    } else {
                        t = new Text(String.valueOf(c), line, chpointer + interoffset, constactive);
                    }
                }
                return t;
            case '"':
                if (!constactive) {
                    Key k = keys.get("\"");
                    if (k != null) {
                        t = new Key(String.valueOf(c), line, chpointer + interoffset, constactive);
                    } else {
                        t = new Text(String.valueOf(c), line, chpointer + interoffset, constactive);
                    }
                    constactive = true;
                } else {
                    constactive = false;
                    Key k = keys.get("\"");
                    if (k != null) {
                        t = new Key(String.valueOf(c), line, chpointer + interoffset, constactive);
                    } else {
                        t = new Text(String.valueOf(c), line, chpointer + interoffset, constactive);
                    }
                }
                return t;

            default:
                return null;
        }
    }

    /**
     * Selects from the stream sequentially consecutive spaces and forms of
     * these tokens whitespace
     *
     * @return Whitespace token
     * @throws IOException
     */
    private Token getSpaceToken() throws IOException {
        StringBuilder sb = new StringBuilder();
            while (peek == ' ') {
                sb.append(' ');
                readch();
                if (peek == (char) -1)
                    return null;
            }
            Space space = new Space(sb.toString(), line, chpointer - sb.length() + interoffset);
            chpointer--;
            return space;
    }

    /**
     * Sets the number of spaces to be replaced by a tab
     *
     * @param count Number of spaces
     */
    public void setTabSpacesCount(int count) {
        tabspace = count;
    }

    /**
     * It creates a white space on the basis of token value tabulation number
     * of spaces. Equivalent increments relative displacement going ahead
     * tokens.
     *
     * @return Whitespace token
     */
    private Token getTabSpaces() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tabspace; i++) {
            sb.append(" ");
            interoffset++;
        }
        Space space = new Space(sb.toString(), line, chpointer - tabspace + interoffset);
        interoffset--;
        return space;
    }

    /**
     * It overrides the token based on the new token. Calls for her scan method
     * and then transfers the characteristics of the old into the new token.
     *
     * @param token Overridden token
     * @return New token
     * @throws IOException
     */
    public Token overrideToken(Token token) throws IOException {
        scanstr = token.lexeme;
        chpointer = 0;
        constactive = token.constflag;
        Token tok = scan();
        if (tok != null) {
            tok.line = token.line;
            tok.constflag = token.constflag;
            tok.offset = token.offset;
            return tok;
        }

        return null;
    }

    /*private boolean isSymbol(char c) {
        for (int i = 0; i < Tag.SINGLES.length; i++) {
            if (c == Tag.SINGLES[i]) {
                return true;
            }
        }

        return false;
    }*/


}
