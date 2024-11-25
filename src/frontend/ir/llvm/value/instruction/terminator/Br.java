package frontend.ir.llvm.value.instruction.terminator;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.instruction.BranchInstruction;
import backend.assembly.instruction.JumpInstruction;
import backend.assembly.instruction.MemoryInstruction;
import frontend.ir.llvm.value.BasicBlock;
import frontend.ir.llvm.value.Value;

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

        public void setIfBasicBlock(BasicBlock ifBasicBlock) {
            setUsed(1, ifBasicBlock);
        }

        public BasicBlock getIfBasicBlock() {
            return (BasicBlock) getUsedValue(1);
        }

        public void setElseBasicBlock(BasicBlock elseBasicBlock) {
            setUsed(2, elseBasicBlock);
        }

        public BasicBlock getElseBasicBlock() {
            return (BasicBlock) getUsedValue(2);
        }

        @Override
        public void buildAssembly() {
            super.buildAssembly();

            Register condRegister = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(getUsedValue(0));

            //no corresponding register for cond value
            if (condRegister == null) {
                //k0 is unused
                condRegister = Register.K0;

                //lw
                MemoryInstruction lw = new MemoryInstruction("lw", condRegister, null, Register.SP, AssemblyBuilder.ASSEMBLY_BUILDER.getValueStackOffset(getUsedValue(0)));
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);
            }
            //bne
            BranchInstruction bne = new BranchInstruction("bne", condRegister, Register.ZERO, getUsedValue(1).getName());
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(bne);

            //j
            JumpInstruction j = new JumpInstruction("j", getUsedValue(2).getName(), null);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(j);
        }

        @Override
        public String toString() {
            return super.toString() + "i1 " + getUsedValue(0).getName() + ", label %" + getUsedValue(1).getName() + ", label %" + getUsedValue(2).getName();
        }
    }

    //br label <dest>
    public static class UnconditionalBr extends Br {
        public UnconditionalBr(String name, BasicBlock destBasicBlock) {
            super(name);
            addUsed(destBasicBlock);
        }

        public BasicBlock getDestBasicBlock() {
            return (BasicBlock) getUsedValue(0);
        }

        @Override
        public void buildAssembly() {
            super.buildAssembly();

            //j
            JumpInstruction j = new JumpInstruction("j", getUsedValue(0).getName(), null);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(j);
        }

        @Override
        public String toString() {
            return super.toString() + "label %" + getUsedValue(0).getName();
        }
    }

    @Override
    public String toString() {
        return super.toString() + "br ";
    }
}
