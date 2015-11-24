package solkris.ru.aste.lexer;

import java.util.ArrayList;
import java.util.List;

import solkris.ru.aste.desc.Pos;

/**
 * Created by serbis on 24.11.15.
 */
public class Dla {
    private ArrayList<List<Token>> tokla;
    private Token currenttok;
    private Pos currentpos;
    private Pos np;
    private String sub;
    private Lexer lexer;

    public Dla() {

    }

    public TokenOperation trace(ArrayList<List<Token>> tokla, Token ct, Pos cp, Pos np, String sub) {
        this.tokla = tokla;
        currenttok = ct;
        currentpos = cp;
        this.np = np;
        this.sub = sub;
        lexer = new Lexer();

        return firstStep();
    }

    private TokenOperation firstStep() {
        if (sub.equals("\n")) {
            return ifCharNewLine();
        }
        if (currenttok.tag == Tag.SPACE) { //если это последний симов токена и если впереди не пробельный токен
           return ifCurtokSpace(sub);
        } else if (currenttok.tag == Tag.NUM) {
            return ifCurtokNum(sub);
        } else {
            return ifCurtokNotSpace(sub);

        }
    }

    private TokenOperation ifCharNewLine() {
        try {
            Token ntok = new Token("\n", currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
            ntok = lexer.overrideToken(ntok);
            return new TokenOperation(TokenOperationType.TOKEN_ADD, ntok, currentpos);
        } catch (Exception ignored) { return null; }
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
    private TokenOperation ifCurtokSpace(String sub) {
        try {
            if (sub.equals(" ")) { //иначе если введеный символ пробел
                return addOneChar(sub.charAt(0)); //то дополняем пробельный токен
            } else if (np == null) {
                Token tok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                tok = lexer.overrideToken(tok);
                return new TokenOperation(TokenOperationType.TOKEN_ADD, tok, currentpos);
            } else if (currentpos.interoffset == currenttok.length && //то дополняем впереди идущий
                    tokla.get(np.line).get(np.offset).tag != Tag.SPACE &&
                    !sub.equals(" ")) {
                return appendTokenLeftSide(sub.charAt(0));
            } else  {
                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                return new TokenOperation(TokenOperationType.TOKEN_INSERT, ntok, currentpos);
            }
        } catch (Exception ignored) { return null; }
    }

    private TokenOperation ifCurtokNum(String sub) {
        try {
            char ch;
            if (sub.length() == 1) {
                ch = sub.charAt(0);
            } else {
                ch = sub.charAt(sub.length() - 1);
            }
            if (Character.isDigit(ch) || ch == '.') {
                return addOneChar(ch);
            }
            if (!Character.isDigit(ch) && ch != '.') {
                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                return new TokenOperation(TokenOperationType.TOKEN_ADD, ntok,new Pos(currentpos.line, currentpos.offset, 0));
            }
        } catch (Exception ignored) { return null; }

        return null;
    }

    /**
     * Branching analysis of character entered - if the current token is
     * non-whitespace. Checks whether the character typed space or not, and
     * calls the handlers of these types of characters.
     *
     * @param sub Processed symbols
     */
    private TokenOperation ifCurtokNotSpace(String sub) {
        try {
            char ch;
            if (sub.length() == 1) {
                ch = sub.charAt(0);
            } else {
                ch = sub.charAt(sub.length() - 1);
            }
            if (ch != ' ') { //если введенный символ не пробел то то что ниже
                return ifCharNotSpace(ch, sub);
            } else { //иначе провеодим сегментацию участ
                return ifCharSpace(sub);
            }
        } catch (Exception ignored) { return null; }
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
    public TokenOperation ifCharNotSpace(char ch, String sub) {
        try {
            if (np != null) {
                if ((currenttok.lexeme.equals("(") && tokla.get(np.line).get(np.offset).lexeme.equals(")")) ||
                        (currenttok.lexeme.equals("[") && tokla.get(np.line).get(np.offset).lexeme.equals("]")) ||
                        (currenttok.lexeme.equals("{") && tokla.get(np.line).get(np.offset).lexeme.equals("}")) ||
                        (currenttok.lexeme.equals("\"") && tokla.get(np.line).get(np.offset).lexeme.equals("\"")) ||
                        (currenttok.lexeme.equals("'") && tokla.get(np.line).get(np.offset).lexeme.equals("'"))) {
                    Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                    ntok = lexer.overrideToken(ntok);
                    return new TokenOperation(TokenOperationType.TOKEN_ADD, ntok, currentpos);
                } else if ((currenttok.lexeme.equals("(") && !tokla.get(np.line).get(np.offset).lexeme.equals(")")) ||
                        (currenttok.lexeme.equals("[") && !tokla.get(np.line).get(np.offset).lexeme.equals("]")) ||
                        (currenttok.lexeme.equals("{") && !tokla.get(np.line).get(np.offset).lexeme.equals("}")) ||
                        (currenttok.lexeme.equals("\"") && !tokla.get(np.line).get(np.offset).lexeme.equals("\"")) ||
                        (currenttok.lexeme.equals("'") && !tokla.get(np.line).get(np.offset).lexeme.equals("'"))) {
                    return appendTokenLeftSide(ch);
                }
            }
            if (ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '{'||
                    ch == '}' || ch == '"' || ch == '\''){
                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                return new TokenOperation(TokenOperationType.TOKEN_ADD, ntok, new Pos(currentpos.line, currentpos.offset, 0));

            } else if (currenttok.lexeme.equals("(") || currenttok.lexeme.equals("[") || currenttok.lexeme.equals("{")||
                    currenttok.lexeme.equals("\"") || currenttok.lexeme.equals("'")) {
                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                return new TokenOperation(TokenOperationType.TOKEN_ADD, ntok, new Pos(currentpos.line, currentpos.offset, 0));
            } else if (ch == '=' || ch == '!' || (currenttok.lexeme.charAt(0) == '=' || currenttok.lexeme.charAt(0) == '!')) {
                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                return new TokenOperation(TokenOperationType.TOKEN_ADD, ntok, new Pos(currentpos.line, currentpos.offset, 0));
            } else if (ch == '.' && currenttok.tag == Tag.NUM) {
                return addOneChar(ch);
            } else if (currenttok.lexeme.equals("\n")) {
                currentpos.line++;
                currentpos.offset = 1;
                Token ntok = new Token(sub, currenttok.line + 1, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                return new TokenOperation(TokenOperationType.TOKEN_ADD, ntok, new Pos(currentpos.line, currentpos.offset, 0));
            } else {
                return addOneChar(ch);
            }
        } catch (Exception ignored) { return null; }
    }

    public TokenOperation ifCharSpace(String sub) {
        try {
            if (currenttok.lexeme.equals("\n")) {
                currentpos.line++;
                currentpos.offset = 1;
                Token ntok = new Token(sub, currenttok.line + 1,  currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                return new TokenOperation(TokenOperationType.TOKEN_ADD, ntok, new Pos(currentpos.line, currentpos.offset, 0));
            } else if (np == null) {
                Token tok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                tok = lexer.overrideToken(tok);
                return new TokenOperation(TokenOperationType.TOKEN_ADD, tok, new Pos(currentpos.line, currentpos.offset, 0));
            } else if (tokla.get(np.line).get(np.offset).tag != Tag.SPACE) {
                Token tok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                tok = lexer.overrideToken(tok);
                return new TokenOperation(TokenOperationType.TOKEN_ADD, tok, currentpos);
            } else if (tokla.get(np.line).get(np.offset).tag == Tag.SPACE) {
                return appendTokenLeftSide(sub.charAt(0));
            } else {
                Token ntok = new Token(sub, currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                return new TokenOperation(TokenOperationType.TOKEN_INSERT, ntok, currentpos);
            }
        } catch (Exception ignored) { return null; }
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
    private TokenOperation addOneChar(char ch) {
        try {
            Pos tokpos = currentpos;
            Token tok = tokla.get(tokpos.line).get(tokpos.offset);
            tok.length++;
            if (tokpos.interoffset + 1 == tok.length) {
                tok.lexeme = tok.lexeme.substring(0, tokpos.interoffset) + ch;
            } else {
                tok.lexeme = tok.lexeme.substring(0, tokpos.interoffset) + ch + tok.lexeme.substring(tokpos.interoffset, tok.lexeme.length());
            }
            tok = lexer.overrideToken(tok);
            return new TokenOperation(TokenOperationType.TOKEN_CHANGE, tok, currentpos);
        } catch (Exception e) { return null; }
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
    private TokenOperation appendTokenLeftSide(char ch) {
        try {
            Pos tokpos = new Pos(currentpos.line, currentpos.offset + 1, 0);
            Token tok = tokla.get(tokpos.line).get(tokpos.offset);
            if (tok.lexeme.equals("(") || tok.lexeme.equals(")") || tok.lexeme.equals("[") || tok.lexeme.equals("]") ||
                    tok.lexeme.equals("{") || tok.lexeme.equals("}") || tok.lexeme.equals("\"") || tok.lexeme.equals("'") ||
                    tok.lexeme.equals("!") || tok.lexeme.equals("=") || tok.lexeme.equals("<") || tok.lexeme.equals(">")) {
                Token ntok = new Token(String.valueOf(ch), currenttok.line, currenttok.offset + currentpos.interoffset, currenttok.constflag);
                ntok = lexer.overrideToken(ntok);
                return new TokenOperation(TokenOperationType.TOKEN_ADD, ntok, new Pos(currentpos.line, currentpos.offset, 0));
            }
            tok.length++;
            tok.lexeme = ch + tok.lexeme;
            tok = lexer.overrideToken(tok);
            return new TokenOperation(TokenOperationType.TOKEN_CHANGE, tok, tokpos);
        } catch (Exception e) { return null; }
    }

    public class TokenOperation {
        public TokenOperationType tokenOperationType;
        public Token token;
        public Pos pos;

        public TokenOperation() {}

        public TokenOperation(TokenOperationType top, Token token, Pos pos) {
            this.tokenOperationType = top;
            this.token = token;
            this.pos = pos;
        }
    }
    public enum TokenOperationType {
        TOKEN_ADD, TOKEN_CHANGE, TOKEN_DELETE, TOKEN_INSERT
    }
}
