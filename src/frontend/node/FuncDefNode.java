package frontend.node;

import frontend.symbol.NumericalSymbol;
import frontend.symbol.SymbolTable;
import frontend.token.Token;
import frontend.token.TokenType;

import java.util.ArrayList;
import java.util.Objects;

//<FuncDef> ::= <FuncType> <Ident> '(' [ <FuncFParams> ] ')' <Block>
public class FuncDefNode extends Node {
    private final FuncTypeNode funcTypeNode;
    private final Token identToken;
    private final Token lparentToken;
    private final FuncFParamsNode funcFParamsNode;
    private final Token rparentToken;
    private final BlockNode blockNode;

    public FuncDefNode(FuncTypeNode funcTypeNode, Token identToken, Token lparentToken, FuncFParamsNode funcFParamsNode, Token rparentToken, BlockNode blockNode) {
        super(NodeType.FUNC_DEF);
        this.funcTypeNode = funcTypeNode;
        this.identToken = identToken;
        this.lparentToken = lparentToken;
        this.funcFParamsNode = funcFParamsNode;
        this.rparentToken = rparentToken;
        this.blockNode = blockNode;
    }

    @Override
    public void checkSemantic() {
        ArrayList<NumericalSymbol> parameterSymbols;
        ArrayList<Token> parameterTokens;
        if (funcFParamsNode != null) {
            funcFParamsNode.checkSemantic();
            parameterSymbols = funcFParamsNode.getParameterSymbols();
            parameterTokens = funcFParamsNode.getParameterTokens();
        } else {
            parameterSymbols = new ArrayList<>();
            parameterTokens = new ArrayList<>();
        }
        SymbolTable.SYMBOL_TABLE.tackle(identToken, funcTypeNode.getFuncType(), parameterSymbols);
        blockNode.isFuncBlock();
        SymbolTable.SYMBOL_TABLE.addScope();
        //add function to symbol table
        SymbolTable.SYMBOL_TABLE.tackle(parameterTokens, parameterSymbols);
        //check semantic
        blockNode.checkSemantic();
        //check return statement
        switch (funcTypeNode.getReturnType()) {
            case VOID -> blockNode.checkReturnVoid();
            case INT, CHAR -> {
                if (blockNode.getReturnToken().getType() == TokenType.RBRACE) {
                    SymbolTable.SYMBOL_TABLE.tackle(blockNode.getReturnToken());
                }
            }
        }
        SymbolTable.SYMBOL_TABLE.removeScope();
    }

    @Override
    public String toString() {
        return "" + funcTypeNode + identToken + lparentToken + Objects.toString(funcFParamsNode, "") + rparentToken + blockNode + nodeType;
    }
}
