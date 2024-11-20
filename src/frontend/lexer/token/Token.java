package frontend.lexer.token;

import java.util.HashMap;

public class Token {
    private TokenType type;
    private final int line;
    private final String content;
    private static final HashMap<String, TokenType> stringToTokenType = new HashMap<>();

    static {
        Object[][] tokens = {
                {"main", TokenType.MAINTK},
                {"const", TokenType.CONSTTK},
                {"int", TokenType.INTTK},
                {"char", TokenType.CHARTK},
                {"break", TokenType.BREAKTK},
                {"continue", TokenType.CONTINUETK},
                {"if", TokenType.IFTK},
                {"else", TokenType.ELSETK},
                {"!", TokenType.NOT},
                {"&&", TokenType.AND},
                {"||", TokenType.OR},
                {"for", TokenType.FORTK},
                {"getint", TokenType.GETINTTK},
                {"getchar", TokenType.GETCHARTK},
                {"printf", TokenType.PRINTFTK},
                {"return", TokenType.RETURNTK},
                {"+", TokenType.PLUS},
                {"-", TokenType.MINU},
                {"void", TokenType.VOIDTK},
                {"*", TokenType.MULT},
                {"/", TokenType.DIV},
                {"%", TokenType.MOD},
                {"<", TokenType.LSS},
                {"<=", TokenType.LEQ},
                {">", TokenType.GRE},
                {">=", TokenType.GEQ},
                {"==", TokenType.EQL},
                {"!=", TokenType.NEQ},
                {"=", TokenType.ASSIGN},
                {";", TokenType.SEMICN},
                {",", TokenType.COMMA},
                {"(", TokenType.LPARENT},
                {")", TokenType.RPARENT},
                {"[", TokenType.LBRACK},
                {"]", TokenType.RBRACK},
                {"{", TokenType.LBRACE},
                {"}", TokenType.RBRACE}
        };

        for (Object[] token : tokens) {
            stringToTokenType.put((String) token[0], (TokenType) token[1]);
        }
    }

    public Token(String content, TokenType type, int line) {
        this.content = content;
        this.type = type;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public TokenType getType() {
        return type;
    }

    public void updateType(TokenType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    private static TokenType stringToTokenType(String s, TokenType defaultType) {
        return stringToTokenType.getOrDefault(s, defaultType);
    }

    public static TokenType wordToTokenType(String s) {
        return stringToTokenType(s, TokenType.IDENFR);
    }

    public static TokenType symbolToTokenType(String s) {
        return stringToTokenType(s, TokenType.INVALID);
    }

    @Override
    public String toString() {
        return type + " " + content + "\n";
    }
}