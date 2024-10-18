package frontend;

import error.Error;
import error.ErrorType;
import error.ErrorHandler;
import frontend.token.Token;
import frontend.token.TokenType;
import utils.InOut;
import utils.Tools;

import java.util.ArrayList;

public class Lexer {
    private int pos;
    private int line;
    private final String sourceCode;
    private final ArrayList<Token> tokenList;


    public Lexer(String sourceCode) {
        this.pos = 0;
        this.line = 1;
        this.sourceCode = sourceCode;
        this.tokenList = new ArrayList<>();
    }

    public void doLexerAnalysis() {
        Tools.printStartMessage("词法分析");
        while (pos < sourceCode.length()) {
            Token token = getNextToken();
            if (token != null) {
                if (token.getType() == TokenType.INVALID) {
                    handleInvalidTokenError(token);
                }
                tokenList.add(token);
            }
        }
        Tools.printEndMessage("词法分析");
    }

    private Token getNextToken() {
        skipMeaningless();
        if (pos >= sourceCode.length()) {
            return null;
        }
        char ch = sourceCode.charAt(pos);
        if (Character.isDigit(ch)) {
            return getIntConst();
        } else if (ch == '\"') {
            return getStringConst();
        } else if (ch == '\'') {
            return getCharConst();
        } else if (Character.isLetter(ch) || ch == '_') {
            return getWord();
        } else {
            return getSymbol();
        }
    }

    private void skipMeaningless() {
        skipWhiteSpaces();
        while (skipComments()) {
            skipWhiteSpaces();
        }
    }

    private boolean skipComments() {
        if (pos < sourceCode.length() && sourceCode.charAt(pos) == '/') {
            if (pos + 1 < sourceCode.length() && sourceCode.charAt(pos + 1) == '/') {
                while (pos < sourceCode.length() && sourceCode.charAt(pos) != '\n') {
                    pos++;
                }
                return true;
            } else if (pos + 1 < sourceCode.length() && sourceCode.charAt(pos + 1) == '*') {
                while (++pos < sourceCode.length() && !(sourceCode.charAt(pos) == '/' && sourceCode.charAt(pos - 1) == '*')) {
                    if (sourceCode.charAt(pos) == '\n')
                        line++;
                }
                pos++;
                return true;
            }
        }
        return false;
    }

    private void skipWhiteSpaces() {
        while (pos < sourceCode.length() && Character.isWhitespace(sourceCode.charAt(pos))) {
            if (sourceCode.charAt(pos) == '\n') {
                line++;
            }
            pos++;
        }
    }

    private Token getIntConst() {
        StringBuilder sb = new StringBuilder();
        while (pos < sourceCode.length() && Character.isDigit(sourceCode.charAt(pos))) {
            sb.append(sourceCode.charAt(pos));
            pos++;
        }
        return new Token(sb.toString(), TokenType.INTCON, line);
    }

    private Token getStringConst() {
        StringBuilder sb = new StringBuilder().append("\"");
        while (++pos < sourceCode.length()) {
            sb.append(sourceCode.charAt(pos));
            if (sourceCode.charAt(pos) == '\"') {
                pos++;
                break;
            } else if (sourceCode.charAt(pos) == '\\' && pos + 1 < sourceCode.length()) {
                pos++;
                sb.append(sourceCode.charAt(pos));
            }
        }
        return new Token(sb.toString(), TokenType.STRCON, line);
    }

    private Token getCharConst() {
        StringBuilder sb = new StringBuilder().append('\'');
        while (++pos < sourceCode.length()) {
            sb.append(sourceCode.charAt(pos));
            if (sourceCode.charAt(pos) == '\'') {
                pos++;
                break;
            } else if (sourceCode.charAt(pos) == '\\' && pos + 1 < sourceCode.length()) {
                pos++;
                sb.append(sourceCode.charAt(pos));
            }
        }
        return new Token(sb.toString(), TokenType.CHRCON, line);
    }

    private Token getWord() {
        StringBuilder sb = new StringBuilder();
        char ch;
        do {
            sb.append(sourceCode.charAt(pos++));
            ch = sourceCode.charAt(pos);
        } while ((pos < sourceCode.length() && (ch == '_' || Character.isLetter(ch) || Character.isDigit(ch))));
        String word = sb.toString();
        return new Token(word, Token.wordToTokenType(word), line);
    }

    private Token getSymbol() {
        char ch = sourceCode.charAt(pos);
        StringBuilder sb = new StringBuilder().append(ch);
        switch (ch) {
            case '&', '|' -> {
                if (pos + 1 < sourceCode.length() && sourceCode.charAt(pos + 1) == ch) {
                    sb.append(ch);
                    pos++;
                }
            }
            case '<', '>', '=', '!' -> {
                if (pos + 1 < sourceCode.length() && sourceCode.charAt(pos + 1) == '=') {
                    sb.append('=');
                    pos++;
                }
            }
            default -> {
            }
        }
        pos++;
        String symbol = sb.toString();
        return new Token(symbol, Token.symbolToTokenType(symbol), line);
    }

    // Error: a
    public void handleInvalidTokenError(Token token) {
        ErrorHandler.ERROR_HANDLER.addError(new Error(line, ErrorType.INVALID_TOKEN_ERROR));
        if (token.getContent().equals("&")) {
            token.updateType(TokenType.AND);
        } else if (token.getContent().equals("|")) {
            token.updateType(TokenType.OR);
        }
    }

    public void writeTokens() {
        InOut.writeLexerResult(Tools.arrayListToString(tokenList));
    }
}
