package frontend.ir.value.instruction.terminator;

import frontend.ir.value.BasicBlock;
import frontend.ir.value.Value;

public class Br extends TerminatorInstruction {
    public Br(String name) {
        super(name);
    }

    //br i1 <cond>, label <if>, label <else>
    public static class ConditionalBr extends Br {
        public ConditionalBr(String name, Value condValue, BasicBlock ifBasicBlock, BasicBlock elseBasicBlock) {
            super(name);
            addUsed(condValue);
            addUsed(ifBasicBlock);
            addUsed(elseBasicBlock);
        }

        @Override
        public String toString() {
            return super.toString() + "i1 " + getUsed(0).getName() + ", label %" + getUsed(1).getName() + ", label %" + getUsed(2).getName();
        }
    }

    //br label <dest>
    public static class UnconditionalBr extends Br {
        public UnconditionalBr(String name, BasicBlock destBlock) {
            super(name);
            addUsed(destBlock);
        }

        @Override
        public String toString() {
            return super.toString() + "label %" + getUsed(0).getName();
        }
    }

    @Override
    public String toString() {
        return super.toString() + "br ";
    }
}
