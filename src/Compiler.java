import error.ErrorHandler;
import frontend.Lexer;
import utils.InOut;

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer(InOut.readTestfile());
        lexer.doLexerAnalysis();
        lexer.writeTokens();
        ErrorHandler.ERROR_HANDLER.writeErrors();
    }
}
