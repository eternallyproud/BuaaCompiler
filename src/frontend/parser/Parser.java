package frontend.parser;

import error.*;
import error.Error;
import frontend.parser.node.*;
import frontend.parser.node.stmt.*;
import frontend.lexer.token.*;
import utils.InOut;
import utils.Tools;

import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    private final ArrayList<Token> tokenList;
    private CompUnitNode compUnitNode;
    private Token currentToken;
    private int pos;


    public Parser(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        pos = -1;
        nextToken();
    }

    public CompUnitNode getCompUnitNode() {
        return compUnitNode;
    }

    public void writeCompUnitNode() {
        InOut.writeParserResult(compUnitNode.toString());
    }

    private void nextToken() {
        currentToken = ++pos >= tokenList.size() ? null : tokenList.get(pos);
    }

    private Token expect(TokenType type) {
        if (currentToken == null || currentToken.getType() != type) {
            if (type == TokenType.SEMICN) {
                return handleMissingSemicnError();
            } else if (type == TokenType.RPARENT) {
                return handleMissingRparentError();
            } else if (type == TokenType.RBRACK) {
                return handleMissingRbrackError();
            }
            handleUnexpectedError();
            return null;
        }
        Token result = currentToken;
        nextToken();
        return result;
    }

    private TokenType peek(int delta) {
        if (pos + delta < tokenList.size()) {
            return tokenList.get(pos + delta).getType();
        } else {
            return null;
        }
    }

    private boolean existAssignTokenInLine() {
        int curLine = tokenList.get(pos).getLine();
        for (int i = pos; i < tokenList.size(); i++) {
            Token t = tokenList.get(i);
            if (t.getLine() != curLine) {
                return false;
            }
            if (t.getType() == TokenType.ASSIGN) {
                return true;
            }
        }
        return false;
    }

    public void doParserAnalysis() {
        Tools.printStartMessage("语法分析");
        compUnitNode = parseCompUnit();
        Tools.printEndMessage("语法分析");
    }

    //<CompUnit> ::= {<Decl>} {<FuncDef>} <MainFuncDef>
    //<Decl>:           [const] int/char <Ident>
    //<FuncDef>:        void/int/char func()
    //<MainFuncDef>:    int main()
    private CompUnitNode parseCompUnit() {
        //Decl
        ArrayList<DeclNode> declNodes = new ArrayList<>();
        while (peek(2) != TokenType.LPARENT) {
            declNodes.add(parseDecl());
        }

        //FuncDef
        ArrayList<FuncDefNode> funcDefNodes = new ArrayList<>();
        while (peek(1) != TokenType.MAINTK) {
            funcDefNodes.add(parseFuncDef());
        }

        //MainFuncDef
        MainFuncDefNode mainFuncDefNode = parseMainFuncDef();

        return new CompUnitNode(declNodes, funcDefNodes, mainFuncDefNode);
    }

    //<Decl> ::= <ConstDecl> | <VarDecl>
    //<ConstDecl>:      const int/char <Ident>
    //<VarDecl>:        int/char <Ident>
    private DeclNode parseDecl() {
        ConstDeclNode constDeclNode = null;
        VarDeclNode varDeclNode = null;

        //ConstDecl
        if (peek(0) == TokenType.CONSTTK) {
            constDeclNode = parseConstDecl();
        }

        //VarDecl
        else if (peek(0) == TokenType.INTTK || peek(0) == TokenType.CHARTK) {
            varDeclNode = parseVarDecl();
        }

        return new DeclNode(constDeclNode, varDeclNode);
    }

    //<ConstDecl> ::= 'const' <BType> <ConstDef> { ',' <ConstDef> } ';'
    //<BType>           int/char
    //<ConstDef>        <Ident>
    private ConstDeclNode parseConstDecl() {
        //'const'
        Token constToken = expect(TokenType.CONSTTK);

        //<BType>
        BTypeNode bTypeNode = parseBType();

        //<ConstDef>
        ArrayList<ConstDefNode> constDefNodes = new ArrayList<>();
        constDefNodes.add(parseConstDef());

        //{ ',' <ConstDef> }
        ArrayList<Token> commaTokens = new ArrayList<>();
        while (peek(0) == TokenType.COMMA) {
            commaTokens.add(expect(TokenType.COMMA));
            constDefNodes.add(parseConstDef());
        }

        //';'
        Token semicnToken = expect(TokenType.SEMICN);

        return new ConstDeclNode(constToken, bTypeNode, constDefNodes, commaTokens, semicnToken);

    }

    //<BType> ::= 'int' | 'char'
    private BTypeNode parseBType() {
        Token intToken = null;
        Token charToken = null;

        //'int'
        if (peek(0) == TokenType.INTTK) {
            intToken = expect(TokenType.INTTK);
        }

        //'char'
        else if (peek(0) == TokenType.CHARTK) {
            charToken = expect(TokenType.CHARTK);
        }

        return new BTypeNode(intToken, charToken);
    }

    //<ConstDef> ::= <Ident> [ '[' <ConstExp> ']' ] '=' <ConstInitVal>
    private ConstDefNode parseConstDef() {
        //<Ident>
        Token identToken = expect(TokenType.IDENFR);

        //[ '[' <ConstExp> ']' ]
        Token lbrackToken = null;
        ConstExpNode constExpNode = null;
        Token rbrackToken = null;
        if (peek(0) == TokenType.LBRACK) {
            lbrackToken = expect(TokenType.LBRACK);
            constExpNode = parseConstExp();
            rbrackToken = expect(TokenType.RBRACK);
        }

        //'='
        Token assignToken = expect(TokenType.ASSIGN);

        //<ConstInitVal>
        ConstInitValNode constInitValNode = parseConstInitVal();

        return new ConstDefNode(identToken, lbrackToken, constExpNode, rbrackToken, assignToken, constInitValNode);
    }

    //<ConstInitVal> ::= <ConstExp> | '{' [ <ConstExp> { ',' <ConstExp> } ] '}' | <StringConst>
    private ConstInitValNode parseConstInitVal() {
        ConstExpNode constExpNode = null;
        Token lbraceToken = null;
        ArrayList<ConstExpNode> constExpNodes = new ArrayList<>();
        ArrayList<Token> commaTokens = new ArrayList<>();
        Token rbraceToken = null;
        Token strconToken = null;

        //'{' [ <ConstExp> { ',' <ConstExp> } ] '}'
        if (peek(0) == TokenType.LBRACE) {
            lbraceToken = expect(TokenType.LBRACE);
            if (peek(0) != TokenType.RBRACE) {
                constExpNodes.add(parseConstExp());
                while (peek(0) == TokenType.COMMA) {
                    commaTokens.add(expect(TokenType.COMMA));
                    constExpNodes.add(parseConstExp());
                }
            }
            rbraceToken = expect(TokenType.RBRACE);
        }

        //<StringConst>
        else if (peek(0) == TokenType.STRCON) {
            strconToken = expect(TokenType.STRCON);
        }

        //<ConstExp>
        else {
            constExpNode = parseConstExp();
        }

        return new ConstInitValNode(constExpNode, lbraceToken, constExpNodes, commaTokens, rbraceToken, strconToken);
    }

    //<VarDecl> ::= <BType> <VarDef> { ',' <VarDef> } ';'
    private VarDeclNode parseVarDecl() {
        //<BType>
        BTypeNode bTypeNode = parseBType();

        //<VarDef>
        ArrayList<VarDefNode> varDefNodes = new ArrayList<>();
        varDefNodes.add(parseVarDef());

        //{ ',' <VarDef> }
        ArrayList<Token> commaTokens = new ArrayList<>();
        while (peek(0) == TokenType.COMMA) {
            commaTokens.add(expect(TokenType.COMMA));
            varDefNodes.add(parseVarDef());
        }

        //';'
        Token semicnToken = expect(TokenType.SEMICN);

        return new VarDeclNode(bTypeNode, varDefNodes, commaTokens, semicnToken);
    }

    //<VarDef> ::= <Ident> [ '[' <ConstExp> ']' ] [ '=' <InitVal> ]
    private VarDefNode parseVarDef() {
        //<Ident>
        Token identToken = expect(TokenType.IDENFR);

        //[ '[' <ConstExp> ']' ]
        Token lbrackToken = null;
        ConstExpNode constExpNode = null;
        Token rbrackToken = null;
        if (peek(0) == TokenType.LBRACK) {
            lbrackToken = expect(TokenType.LBRACK);
            constExpNode = parseConstExp();
            rbrackToken = expect(TokenType.RBRACK);
        }

        //[ '=' <InitVal> ]
        Token assignToken = null;
        InitValNode initValNode = null;
        if (peek(0) == TokenType.ASSIGN) {
            assignToken = expect(TokenType.ASSIGN);
            initValNode = parseInitVal();
        }

        return new VarDefNode(identToken, lbrackToken, constExpNode, rbrackToken, assignToken, initValNode);
    }

    //<InitVal> ::= <Exp> | '{' [ <Exp> { ',' <Exp> } ] '}' | <StringConst>
    private InitValNode parseInitVal() {
        ExpNode expNode = null;
        Token lbraceToken = null;
        ArrayList<ExpNode> expNodes = new ArrayList<>();
        ArrayList<Token> commaTokens = new ArrayList<>();
        Token rbraceToken = null;
        Token strconToken = null;

        //'{' [ <Exp> { ',' <Exp> } ] '}'
        if (peek(0) == TokenType.LBRACE) {
            lbraceToken = expect(TokenType.LBRACE);
            expNodes.add(parseExp());
            while (peek(0) == TokenType.COMMA) {
                commaTokens.add(expect(TokenType.COMMA));
                expNodes.add(parseExp());
            }
            rbraceToken = expect(TokenType.RBRACE);
        }

        //<StringConst>
        else if (peek(0) == TokenType.STRCON) {
            strconToken = expect(TokenType.STRCON);
        }

        //<Exp>
        else {
            expNode = parseExp();
        }

        return new InitValNode(expNode, lbraceToken, expNodes, commaTokens, rbraceToken, strconToken);
    }

    //<FuncDef> ::= <FuncType> <Ident> '(' [ <FuncFParams> ] ')' <Block>
    private FuncDefNode parseFuncDef() {
        //<FuncType>
        FuncTypeNode funcTypeNode = parseFuncType();

        //<Ident>
        Token identToken = expect(TokenType.IDENFR);

        //'('
        Token lparentToken = expect(TokenType.LPARENT);

        //[ <FuncFParams> ]
        FuncFParamsNode funcFParamsNode = null;
        if (peek(0) == TokenType.INTTK || peek(0) == TokenType.CHARTK) {
            funcFParamsNode = parseFuncFParams();
        }

        //')'
        Token rparentToken = expect(TokenType.RPARENT);

        //<Block>
        BlockNode blockNode = parseBlock();

        return new FuncDefNode(funcTypeNode, identToken, lparentToken, funcFParamsNode, rparentToken, blockNode);
    }

    //<MainFuncDef> ::= 'int' 'main' '(' ')' <Block>
    private MainFuncDefNode parseMainFuncDef() {
        //'int'
        Token intToken = expect(TokenType.INTTK);

        //'main'
        Token mainToken = expect(TokenType.MAINTK);

        //'('
        Token lparentToken = expect(TokenType.LPARENT);

        //')'
        Token rparentToken = expect(TokenType.RPARENT);

        //<Block>
        BlockNode blockNode = parseBlock();

        return new MainFuncDefNode(intToken, mainToken, lparentToken, rparentToken, blockNode);
    }

    //<FuncType> ::= 'void' | 'int' | 'char'
    private FuncTypeNode parseFuncType() {
        Token voidToken = null;
        Token intToken = null;
        Token charToken = null;

        //'void'
        if (peek(0) == TokenType.VOIDTK) {
            voidToken = expect(TokenType.VOIDTK);
        }

        //'int'
        else if (peek(0) == TokenType.INTTK) {
            intToken = expect(TokenType.INTTK);
        }

        //'char'
        else {
            charToken = expect(TokenType.CHARTK);
        }

        return new FuncTypeNode(voidToken, intToken, charToken);
    }

    //<FuncFParams> ::= <FuncFParam> { ',' <FuncFParam> }
    private FuncFParamsNode parseFuncFParams() {
        //<FuncFParam>
        ArrayList<FuncFParamNode> funcFParamNodes = new ArrayList<>();
        funcFParamNodes.add(parseFuncFParam());

        //{ ',' <FuncFParam> }
        ArrayList<Token> commaTokens = new ArrayList<>();
        while (peek(0) == TokenType.COMMA) {
            commaTokens.add(expect(TokenType.COMMA));
            funcFParamNodes.add(parseFuncFParam());
        }

        return new FuncFParamsNode(funcFParamNodes, commaTokens);
    }

    //<FuncFParam> ::= <BType> <Ident> [ '[' ']' ]
    private FuncFParamNode parseFuncFParam() {
        //<BType>
        BTypeNode bTypeNode = parseBType();

        //<Ident>
        Token identToken = expect(TokenType.IDENFR);

        //[ '[' ']' ]
        Token lbrackToken = null;
        Token rbrackToken = null;
        if (peek(0) == TokenType.LBRACK) {
            lbrackToken = expect(TokenType.LBRACK);
            rbrackToken = expect(TokenType.RBRACK);
        }

        return new FuncFParamNode(bTypeNode, identToken, lbrackToken, rbrackToken);
    }

    //<Block> ::= '{' { <BlockItem> } '}'
    private BlockNode parseBlock() {
        //'{'
        Token lbraceToken = expect(TokenType.LBRACE);

        // { <BlockItem> }
        ArrayList<BlockItemNode> blockItemNodes = new ArrayList<>();
        while (peek(0) != TokenType.RBRACE) {
            blockItemNodes.add(parseBlockItem());
        }

        //'{'
        Token rbraceToken = expect(TokenType.RBRACE);

        return new BlockNode(lbraceToken, blockItemNodes, rbraceToken);
    }

    //<BlockItem> ::= <Decl> | <Stmt>
    //<Decl>        [const] int/char
    private BlockItemNode parseBlockItem() {
        DeclNode declNode = null;
        StmtNode stmtNode = null;

        //<Decl>
        if (peek(0) == TokenType.CONSTTK || peek(0) == TokenType.INTTK || peek(0) == TokenType.CHARTK) {
            declNode = parseDecl();
        }

        //<Stmt>
        else {
            stmtNode = parseStmt();
        }

        return new BlockItemNode(declNode, stmtNode);
    }

    //<Stmt> ::= <LValStmt> | <ExpStmt> | <BlockStmt> | <IfStmt> | <ForStmt> | <BreakStmt> | <ContinueStmt> | <ReturnStmt> | <PrintfStmt>
    //<LValStmt>        Ident '='
    //<BlockStmt>       '{'
    //<IfStmt>          'if'
    //<ForStmt>         'for'
    //<BreakStmt>       'break'
    //<ContinueStmt>    'continue'
    //<ReturnStmt>      'return'
    //<PrintfStmt>      'printf'
    private StmtNode parseStmt() {
        StmtNode stmtNode;

        //<LValStmt>
        if (existAssignTokenInLine() && peek(0) == TokenType.IDENFR) {
            stmtNode = parseLValStmt();
        }

        //<BlockStmt>
        else if (peek(0) == TokenType.LBRACE) {
            stmtNode = parseBlockStmt();
        }

        //<IfStmt>
        else if (peek(0) == TokenType.IFTK) {
            stmtNode = parseIfStmt();
        }

        //<ForStmt>
        else if (peek(0) == TokenType.FORTK) {
            stmtNode = parseForStmt();
        }

        //<BreakStmt>
        else if (peek(0) == TokenType.BREAKTK) {
            stmtNode = parseBreakStmt();
        }

        //<ContinueStmt>
        else if (peek(0) == TokenType.CONTINUETK) {
            stmtNode = parseContinueStmt();
        }

        //<ReturnStmt>
        else if (peek(0) == TokenType.RETURNTK) {
            stmtNode = parseReturnStmt();
        }

        //<PrintfStmt>
        else if (peek(0) == TokenType.PRINTFTK) {
            stmtNode = parsePrintfStmt();
        }

        //<ExpStmt>
        else {
            stmtNode = parseExpStmt();
        }

        return stmtNode;
    }

    //<Stmt> ::= <LVal> '=' <LVal> '=' 'getint' '(' ')' ';' | <LVal> '=' 'getchar' '(' ')' | <Exp> ';'
    private LValStmtNode parseLValStmt() {
        Token getintToken = null;
        Token getcharToken = null;
        Token lparenToken = null;
        Token rparenToken = null;
        ExpNode expNode = null;
        //<LVal>
        LValNode lValNode = parseLVal();

        // '='
        Token assignToken = expect(TokenType.ASSIGN);

        //'getint' '(' ')'
        if (peek(0) == TokenType.GETINTTK) {
            getintToken = expect(TokenType.GETINTTK);
            lparenToken = expect(TokenType.LPARENT);
            rparenToken = expect(TokenType.RPARENT);
        }

        //'getchar' '(' ')'
        else if (peek(0) == TokenType.GETCHARTK) {
            getcharToken = expect(TokenType.GETCHARTK);
            lparenToken = expect(TokenType.LPARENT);
            rparenToken = expect(TokenType.RPARENT);
        }

        //<Exp>
        else {
            expNode = parseExp();
        }

        //';'
        Token semicnToken = expect(TokenType.SEMICN);

        return new LValStmtNode(lValNode, assignToken, getintToken, getcharToken, lparenToken, rparenToken, expNode, semicnToken);
    }

    //<ExpStmt> ::= [ <Exp> ] ';'
    private ExpStmtNode parseExpStmt() {
        //[ <Exp> ]
        ExpNode expNode = switch (Objects.requireNonNull(peek(0))) {
            case LPARENT, IDENFR, INTCON, CHRCON, PLUS, MINU, NOT -> parseExp();
            default -> null;
        };

        //';'
        Token semicnToken = expect(TokenType.SEMICN);

        return new ExpStmtNode(expNode, semicnToken);
    }

    //<BlockItem> ::= <Block>
    private BlockStmtNode parseBlockStmt() {
        //<Block>
        BlockNode blockNode = parseBlock();

        return new BlockStmtNode(blockNode);
    }

    //<IfStmt> ::= 'if' '(' <Cond> ')' <Stmt> [ 'else' <Stmt> ]
    private IfStmtNode parseIfStmt() {
        //'if'
        Token ifToken = expect(TokenType.IFTK);

        //'('
        Token lparenToken = expect(TokenType.LPARENT);

        //<Cond>
        CondNode condNode = parseCond();

        //')'
        Token rparenToken = expect(TokenType.RPARENT);

        //<Stmt>
        StmtNode ifstmtNode = parseStmt();

        //[ 'else' <Stmt> ]
        Token elseToken = null;
        StmtNode elseStmtNode = null;
        if (peek(0) == TokenType.ELSETK) {
            elseToken = expect(TokenType.ELSETK);
            elseStmtNode = parseStmt();
        }

        return new IfStmtNode(ifToken, lparenToken, condNode, rparenToken, ifstmtNode, elseToken, elseStmtNode);
    }

    //<ForStmt> ::= 'for' '(' [<ForAssign>] ';' [<Cond>] ';' [<ForAssign>] ')' <Stmt>
    private ForStmtNode parseForStmt() {
        //'for'
        Token forToken = expect(TokenType.FORTK);

        //'('
        Token lparenToken = expect(TokenType.LPARENT);

        //<ForAssign>
        ForAssignNode forAssignNode1 = null;
        if (peek(0) != TokenType.SEMICN) {
            forAssignNode1 = parseForAssign();
        }

        //';'
        Token semicnToken1 = expect(TokenType.SEMICN);

        //<Cond>
        CondNode condNode = null;
        if (peek(0) != TokenType.SEMICN) {
            condNode = parseCond();
        }

        //';'
        Token semicnToken2 = expect(TokenType.SEMICN);

        //<ForAssign>
        ForAssignNode forAssignNode2 = null;
        if (peek(0) != TokenType.RPARENT) {
            forAssignNode2 = parseForAssign();
        }

        //')'
        Token rparenToken = expect(TokenType.RPARENT);

        //<Stmt>
        StmtNode stmtNode = parseStmt();

        return new ForStmtNode(forToken, lparenToken, forAssignNode1, semicnToken1, condNode, semicnToken2, forAssignNode2, rparenToken, stmtNode);
    }

    //<BreakStmt> ::= 'break' ';'
    private BreakStmtNode parseBreakStmt() {
        //'break'
        Token breakToken = expect(TokenType.BREAKTK);
        //';'
        Token semicnToken = expect(TokenType.SEMICN);

        return new BreakStmtNode(breakToken, semicnToken);
    }

    //<ContinueStmt> ::= 'continue' ';'
    private ContinueStmtNode parseContinueStmt() {
        //'continue'
        Token continueToken = expect(TokenType.CONTINUETK);

        //';'
        Token semicnToken = expect(TokenType.SEMICN);

        return new ContinueStmtNode(continueToken, semicnToken);
    }

    //<ReturnStmt> ::= 'return' [ <Exp> ] ';'
    //<Exp>             (/<Ident>/<Number>/<Character>/+/-/！
    private ReturnStmtNode parseReturnStmt() {
        //'return'
        Token returnToken = expect(TokenType.RETURNTK);

        //[ <Exp> ]
        ExpNode expNode = switch (Objects.requireNonNull(peek(0))) {
            case LPARENT, IDENFR, INTCON, CHRCON, PLUS, MINU, NOT -> parseExp();
            default -> null;
        };

        //';'
        Token semicnToken = expect(TokenType.SEMICN);

        return new ReturnStmtNode(returnToken, expNode, semicnToken);
    }

    //<PrintfStmt> ::= 'printf' '(' <StringConst> { ',' <Exp> } ')' ';'
    private PrintfStmtNode parsePrintfStmt() {
        //'printf'
        Token printfToken = expect(TokenType.PRINTFTK);

        //'('
        Token lparentToken = expect(TokenType.LPARENT);

        //<StringConst>
        Token stringConstToken = expect(TokenType.STRCON);

        //{ ',' <Exp> }
        ArrayList<Token> commaTokens = new ArrayList<>();
        ArrayList<ExpNode> expNodes = new ArrayList<>();
        while (peek(0) == TokenType.COMMA) {
            commaTokens.add(expect(TokenType.COMMA));
            expNodes.add(parseExp());
        }

        //')'
        Token rparentToken = expect(TokenType.RPARENT);

        //';'
        Token semicnToken = expect(TokenType.SEMICN);

        return new PrintfStmtNode(printfToken, lparentToken, stringConstToken, commaTokens, expNodes, rparentToken, semicnToken);

    }

    //<AssignStmt> ::= <LVal> '=' <Exp>
    private ForAssignNode parseForAssign() {
        //<LVal>
        LValNode lvalNode = parseLVal();

        //'='
        Token assignToken = expect(TokenType.ASSIGN);

        //<Exp>
        ExpNode expNode = parseExp();

        return new ForAssignNode(lvalNode, assignToken, expNode);
    }

    //<Exp> ::= <AddExp>
    private ExpNode parseExp() {
        //<AddExp>
        AddExpNode addExpNode = parseAddExp();

        return new ExpNode(addExpNode);
    }

    //<Cond> ::= <LOrExp>
    private CondNode parseCond() {
        //<LOrExp>
        LOrExpNode lOrExpNode = parseLOrExp();

        return new CondNode(lOrExpNode);
    }

    //<LVal> ::= <Ident> [ '[' <Exp> ']' ]
    private LValNode parseLVal() {
        //<Ident>
        Token identToken = expect(TokenType.IDENFR);

        //[ '[' <Exp> ']' ]
        Token lbrackToken = null;
        ExpNode expNode = null;
        Token rbrackToken = null;
        if (peek(0) == TokenType.LBRACK) {
            lbrackToken = expect(TokenType.LBRACK);
            expNode = parseExp();
            rbrackToken = expect(TokenType.RBRACK);
        }

        return new LValNode(identToken, lbrackToken, expNode, rbrackToken);
    }

    //<PrimaryExp> ::= '(' <Exp> ')' | <LVal> | <Number> | <Character>
    private PrimaryExpNode parsePrimaryExp() {
        Token lparenToken = null;
        ExpNode expNode = null;
        Token rparenToken = null;
        LValNode lvalNode = null;
        NumberNode numberNode = null;
        CharacterNode characterNode = null;

        //'(' <Exp> ')'
        if (peek(0) == TokenType.LPARENT) {
            lparenToken = expect(TokenType.LPARENT);
            expNode = parseExp();
            rparenToken = expect(TokenType.RPARENT);
        }

        //<LVal>
        else if (peek(0) == TokenType.IDENFR) {
            lvalNode = parseLVal();
        }

        //<Number>
        else if (peek(0) == TokenType.INTCON) {
            numberNode = parseNumber();
        }

        //<Character>
        else {
            characterNode = parseCharacter();
        }

        return new PrimaryExpNode(lparenToken, expNode, rparenToken, lvalNode, numberNode, characterNode);
    }

    //<Number> ::= <IntConst>
    private NumberNode parseNumber() {
        //<IntConst>
        Token intConstToken = expect(TokenType.INTCON);

        return new NumberNode(intConstToken);
    }

    //<Character> ::= <CharConst>
    private CharacterNode parseCharacter() {
        //<CharConst>
        Token charConstToken = expect(TokenType.CHRCON);

        return new CharacterNode(charConstToken);

    }

    //<UnaryExp> ::= <PrimaryExp> | <Ident> '(' [ <FuncRParams> ] ')' | <UnaryOp> <UnaryExp>
    private UnaryExpNode parseUnaryExp() {
        PrimaryExpNode primaryExpNode = null;
        Token identToken = null;
        Token lparenToken = null;
        FuncRParamsNode funcRParamsNode = null;
        Token rparenToken = null;
        UnaryOpNode unaryOpNode = null;
        UnaryExpNode unaryExpNode = null;

        //<Ident> '(' [ <FuncRParams> ] ')'
        if (peek(0) == TokenType.IDENFR && peek(1) == TokenType.LPARENT) {
            identToken = expect(TokenType.IDENFR);
            lparenToken = expect(TokenType.LPARENT);
            funcRParamsNode = switch (Objects.requireNonNull(peek(0))) {
                case LPARENT, IDENFR, INTCON, CHRCON, PLUS, MINU, NOT -> parseFuncRParams();
                default -> null;
            };
            rparenToken = expect(TokenType.RPARENT);
        }

        //<UnaryOp> <UnaryExp>
        else if (peek(0) == TokenType.PLUS || peek(0) == TokenType.MINU || peek(0) == TokenType.NOT) {
            unaryOpNode = parseUnaryOp();
            unaryExpNode = parseUnaryExp();
        }

        //<PrimaryExp>
        else {
            primaryExpNode = parsePrimaryExp();
        }

        return new UnaryExpNode(primaryExpNode, identToken, lparenToken, funcRParamsNode, rparenToken, unaryOpNode, unaryExpNode);
    }

    //<UnaryOp> ::= '+' | '-' | '!'
    private UnaryOpNode parseUnaryOp() {
        Token opToken = switch (Objects.requireNonNull(peek(0))){
            case PLUS -> expect(TokenType.PLUS);
            case MINU -> expect(TokenType.MINU);
            default -> expect(TokenType.NOT);
        };

        return new UnaryOpNode(opToken);
    }

    //<FuncRParams> ::= <Exp> { ',' <Exp> }
    private FuncRParamsNode parseFuncRParams() {
        ArrayList<ExpNode> expNodes = new ArrayList<>();
        ArrayList<Token> commaTokens = new ArrayList<>();

        //<Exp>
        expNodes.add(parseExp());

        //{ ',' <Exp> }
        while (peek(0) == TokenType.COMMA) {
            commaTokens.add(expect(TokenType.COMMA));
            expNodes.add(parseExp());
        }

        return new FuncRParamsNode(expNodes, commaTokens);
    }

    //<MulExp> ::=  <UnaryExp> | <MulExp> ('*' | '/' | '%') <UnaryExp>
    private MulExpNode parseMulExp() {
        // <UnaryExp>
        UnaryExpNode unaryExpNode = parseUnaryExp();

        // <MulExp> ('*' | '/' | '%') <UnaryExp>
        MulExpNode mulExpNode = null;
        Token mulToken = null;
        while (peek(0) == TokenType.MULT || peek(0) == TokenType.DIV || peek(0) == TokenType.MOD) {
            mulExpNode = new MulExpNode(mulExpNode, mulToken, unaryExpNode);
            mulToken = expect(peek(0));
            unaryExpNode = parseUnaryExp();
        }

        return new MulExpNode(mulExpNode, mulToken, unaryExpNode);
    }

    //<AddExp> ::=  <MulExp> | <AddExp> ('+' | '−') <MulExp>
    private AddExpNode parseAddExp() {
        // <MulExp>
        MulExpNode mulExpNode = parseMulExp();

        // <AddExp> ('+' | '−') <MulExp>
        AddExpNode addExpNode = null;
        Token addToken = null;
        while (peek(0) == TokenType.PLUS || peek(0) == TokenType.MINU) {
            addExpNode = new AddExpNode(addExpNode, addToken, mulExpNode);
            addToken = expect(peek(0));
            mulExpNode = parseMulExp();
        }

        return new AddExpNode(addExpNode, addToken, mulExpNode);
    }

    //<RelExp> ::= <AddExp> | <RelExp> ('<' | '>' | '<=' | '>=') <AddExp>
    private RelExpNode parseRelExp() {
        //<AddExp>
        AddExpNode addExpNode = parseAddExp();

        //<RelExp> ('<' | '>' | '<=' | '>=') <AddExp>
        RelExpNode relExpNode = null;
        Token relToken = null;
        while (peek(0) == TokenType.LSS || peek(0) == TokenType.GRE || peek(0) == TokenType.LEQ || peek(0) == TokenType.GEQ) {
            relExpNode = new RelExpNode(relExpNode, relToken, addExpNode);
            relToken = expect(peek(0));
            addExpNode = parseAddExp();
        }

        return new RelExpNode(relExpNode, relToken, addExpNode);
    }


    //<EqExp> ::= <RelExp> | <EqExp> ('==' | '!=') <RelExp>
    private EqExpNode parseEqExp() {
        //<RelExp>
        RelExpNode relExpNode = parseRelExp();

        //<EqExp> ('==' | '!=') <RelExp>
        EqExpNode eqExpNode = null;
        Token eqToken = null;
        while (peek(0) == TokenType.EQL || peek(0) == TokenType.NEQ) {
            eqExpNode = new EqExpNode(eqExpNode, eqToken, relExpNode);
            eqToken = expect(peek(0));
            relExpNode = parseRelExp();
        }

        return new EqExpNode(eqExpNode, eqToken, relExpNode);
    }

    //<LAndExp> ::= <EqExp> | <LAndExp> '&&' <EqExp>
    private LAndExpNode parseLAndExp() {
        //<EqExp>
        EqExpNode eqExpNode = parseEqExp();

        //<LAndExp> '&&' <EqExp>
        LAndExpNode landExpNode = null;
        Token andToken = null;
        while (peek(0) == TokenType.AND) {
            landExpNode = new LAndExpNode(landExpNode, andToken, eqExpNode);
            andToken = expect(TokenType.AND);
            eqExpNode = parseEqExp();
        }

        return new LAndExpNode(landExpNode, andToken, eqExpNode);
    }

    //<LOrExp> ::= <LAndExp> | <LOrExp> '||' <LAndExp>
    private LOrExpNode parseLOrExp() {
        //<LAndExp>
        LAndExpNode landExpNode = parseLAndExp();

        //<LOrExp> '||' <LAndExp>
        LOrExpNode lorExpNode = null;
        Token orToken = null;
        while (peek(0) == TokenType.OR) {
            lorExpNode = new LOrExpNode(lorExpNode, orToken, landExpNode);
            orToken = expect(TokenType.OR);
            landExpNode = parseLAndExp();
        }

        return new LOrExpNode(lorExpNode, orToken, landExpNode);
    }

    //<ConstExp> ::= <AddExp>
    private ConstExpNode parseConstExp() {
        //<AddExp>
        AddExpNode addExpNode = parseAddExp();

        return new ConstExpNode(addExpNode);
    }


    //Error: i
    public Token handleMissingSemicnError() {
        int line = tokenList.get(pos - 1).getLine();
        ErrorHandler.ERROR_HANDLER.addError(new Error(line, ErrorType.MISSING_SEMICN_ERROR));
        return new Token("", TokenType.SEMICN, line);
    }

    //Error: j
    public Token handleMissingRparentError() {
        int line = tokenList.get(pos - 1).getLine();
        ErrorHandler.ERROR_HANDLER.addError(new Error(line, ErrorType.MISSING_RPARENT_ERROR));
        return new Token("", TokenType.RPARENT, line);
    }

    //Error: k
    public Token handleMissingRbrackError() {
        int line = tokenList.get(pos - 1).getLine();
        ErrorHandler.ERROR_HANDLER.addError(new Error(line, ErrorType.MISSING_RBRACK_ERROR));
        return new Token("", TokenType.RBRACK, line);
    }

    //Error: u
    public void handleUnexpectedError() {
        ErrorHandler.ERROR_HANDLER.addError(new Error(currentToken.getLine(), ErrorType.UNEXPECTED_ERROR));
    }
}
