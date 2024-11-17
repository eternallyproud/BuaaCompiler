package frontend.node;

import frontend.IRBuilder;
import frontend.ir.ValueTable;
import frontend.ir.value.BasicBlock;
import frontend.ir.value.global.Function;
import frontend.ir.value.Value;
import frontend.symbol.SymbolTable;
import frontend.token.Token;
import frontend.token.TokenType;

//<MainFuncDef> ::= 'int' 'main' '(' ')' <Block>
public class MainFuncDefNode extends Node {
    private final Token intToken;
    private final Token mainToken;
    private final Token lparentToken;
    private final Token rparentToken;
    private final BlockNode blockNode;

    public MainFuncDefNode(Token intToken, Token mainToken, Token lparentToken, Token rparentToken, BlockNode blockNode) {
        super(NodeType.MAIN_FUNC_DEF);
        this.intToken = intToken;
        this.mainToken = mainToken;
        this.lparentToken = lparentToken;
        this.rparentToken = rparentToken;
        this.blockNode = blockNode;
    }

    @Override
    public void checkSemantic() {
        blockNode.isFuncBlock();
        SymbolTable.SYMBOL_TABLE.addScope();
        blockNode.checkSemantic();
        if (blockNode.getReturnToken().getType() == TokenType.RBRACE) {
            SymbolTable.SYMBOL_TABLE.tackle(blockNode.getReturnToken());
        }
        SymbolTable.SYMBOL_TABLE.removeScope();
    }

    @Override
    public Value buildIR() {
        //push scope
        ValueTable.VALUE_TABLE.push();

        //add function
        IRBuilder.IR_BUILDER.addFunction(new Function(IRBuilder.IR_BUILDER.getFunctionName(), intToken.getContent()));

        //add basic block
        BasicBlock basicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
        IRBuilder.IR_BUILDER.addBasicBlock(basicBlock);
        IRBuilder.IR_BUILDER.setCurrentBasicBlock(basicBlock);

        //build IR
        blockNode.buildIR();

        //pop scope
        ValueTable.VALUE_TABLE.pop();

        return super.buildIR();
    }

    @Override
    public String toString() {
        return "" + intToken + mainToken + lparentToken + rparentToken + blockNode + nodeType;
    }
}
