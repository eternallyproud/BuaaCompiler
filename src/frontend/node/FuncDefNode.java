package frontend.node;

import frontend.IRBuilder;
import frontend.ir.ValueTable;
import frontend.ir.value.BasicBlock;
import frontend.ir.value.Value;
import frontend.ir.value.global.Function;
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
    public Value buildIR() {
        //push scope
        ValueTable.VALUE_TABLE.push();

        //add function
        Function function = new Function(IRBuilder.IR_BUILDER.getFunctionName(identToken.getContent()), funcTypeNode.getReturnType().name().toLowerCase());
        IRBuilder.IR_BUILDER.addFunction(function);
        ValueTable.VALUE_TABLE.addToGlobalScope(identToken.getContent(), function);

        //add basic block
        BasicBlock basicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
        IRBuilder.IR_BUILDER.addBasicBlock(basicBlock);
        IRBuilder.IR_BUILDER.setCurrentBasicBlock(basicBlock);

        //build IR
        if(funcFParamsNode != null){
            funcFParamsNode.buildIR();
        }
        blockNode.buildIR();

        //ensure ret exist
        IRBuilder.IR_BUILDER.ensureRetExist();

        //pop scope
        ValueTable.VALUE_TABLE.pop();

        return null;
    }

    @Override
    public String toString() {
        return "" + funcTypeNode + identToken + lparentToken + Objects.toString(funcFParamsNode, "") + rparentToken + blockNode + nodeType;
    }
}
