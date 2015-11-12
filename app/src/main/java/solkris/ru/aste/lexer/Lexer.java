package solkris.ru.aste.lexer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by serbis on 26.10.15.
 */
public class Lexer {
    public static int line = 1;
    private int charcounter = 0;
    private char peek = ' ';
    private Hashtable<String, Key> keys = new Hashtable<String, Key>();

    public Lexer() {
        for (int i = 0; i < Token.keywords.size(); i++) {
            reserve(new Key(Token.keywords.get(i).lexeme));
        }
    }

    private void reserve(Key t) {
        keys.put(t.lexeme, t);
    }

    private void readch() throws IOException {
        peek = (char) System.in.read();
        charcounter++;
    }

    private boolean readch(char c) throws IOException{
        readch();
        if (peek != c) return false;
        peek = ' ';
        return true;
    }

    public List<Token> scanAll(String input) {
        List<Token> tokens = new ArrayList<Token>();
        Token st;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        try {
            while(true) {
                tokens.add(scan());
            }
        } catch (IOException ignored) { }

        return tokens;
    }

    public Token scan() throws IOException {
        readch();
        /*for ( ; ; readch()) {
            if (peek == ' ' || peek == '\t') continue;
            else if (peek == '\n') line++;
            else break;
        }*/
        /*switch (peek) {
            case '&':
                if (readch('&')) return Word.and;
                else return new Token('&');
            case '|':
                if (readch('|')) return Word.or;
                else return new Token('|');
            case '=':
                if (readch('=')) return Word.eq;
                else return new Token('=');
            case '!':
                if (readch('=')) return Word.ne;
                else return new Token('!');
            case '<':
                if (readch('=')) return Word.le;
                else return new Token('<');
            case '>':
                if (readch('=')) return Word.ge;
                else return new Token('>');
        }*/
        /*if (Character.isDigit(peek)) {
            int v = 0;
            do {
                v = 10 * v + Character.digit(peek, 10);
                readch();
            } while (Character.isDigit(peek));
            if (peek != '.') return new Num(v);
            float x = v;
            float d = 10;
            for (;;) {
                readch();
                if (!Character.isDigit(peek)) break;
                x = x + Character.digit(peek, 10) / d;
                d = d * 10;
            }
            return new Real(x);
        }*/

        if (Character.isLetter(peek)) {
            StringBuffer b = new StringBuffer();
            do {
                b.append(peek);
                readch();
            } while (Character.isLetterOrDigit(peek));
            String s = b.toString();
            Key k = (Key) keys.get(s);
            if (k != null) {
                k.line = line;
                k.offset = charcounter;
                return k;
            }
            Text t = new Text(s, line, charcounter);
            return t;
        }
        //Token t = new Token("", 0, 0);
        peek = ' ';
        return null;
    }


}
