package frontend.node;

import frontend.IRBuilder;
import frontend.ir.value.BasicBlock;
import frontend.token.Token;

import java.util.ArrayList;
import java.util.Objects;

//<LOrExp> ::= <LAndExp> | <LOrExp> '||' <LAndExp>
public class LOrExpNode extends Node {
    private final LOrExpNode lorExpNode;
    private final Token orToken;
    private final LAndExpNode landExpNode;

    public LOrExpNode(LOrExpNode lorExpNode, Token orToken, LAndExpNode landExpNode) {
        super(NodeType.L_OR_EXP);
        this.lorExpNode = lorExpNode;
        this.orToken = orToken;
        this.landExpNode = landExpNode;
    }

    private void getAllLAndExpNodes(ArrayList<LAndExpNode> landExpNodes){
        if (lorExpNode == null) {
            landExpNodes.add(landExpNode);
        }
        else {
            lorExpNode.getAllLAndExpNodes(landExpNodes);
            landExpNodes.add(landExpNode);
        }
    }

    @Override
    public void checkSemantic() {
        //<LAndExp>
        if (lorExpNode == null) {
            landExpNode.checkSemantic();
        }
        //<LOrExp> '||' <LAndExp>
        else {
            lorExpNode.checkSemantic();
            landExpNode.checkSemantic();
        }
    }

    public void buildIRForBranch(BasicBlock ifBasicBlock, BasicBlock elseBasicBlock){
        /* this function only deals with the topmost LOrExp node */

        //all LAndExp nodes
        ArrayList<LAndExpNode> landExpNodes = new ArrayList<>();
        getAllLAndExpNodes(landExpNodes);

        for (int i = 0; i < landExpNodes.size(); i++) {
            //<LAndExp>
            if(i == landExpNodes.size() - 1){
                //build ir for this LAndExp node
                landExpNodes.get(i).buildIRForBranch(ifBasicBlock, elseBasicBlock);
            }
            //<LAndExp> {'||' <LAndExp>}
            else{
                //basic block for next LAndExp node
                BasicBlock basicBlock = new BasicBlock(IRBuilder.IR_BUILDER.getBasicBlockName());
                IRBuilder.IR_BUILDER.addBasicBlock(basicBlock);

                //build ir for this LAndExp node
                landExpNodes.get(i).buildIRForBranch(ifBasicBlock, basicBlock);

                //set basic block for next LAndExp node
                IRBuilder.IR_BUILDER.setCurrentBasicBlock(basicBlock);
            }
        }
    }

    @Override
    public String toString() {
        return Objects.toString(lorExpNode, "") +
                Objects.toString(orToken, "") + landExpNode + nodeType;
    }
}
