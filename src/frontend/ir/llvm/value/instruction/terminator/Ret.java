package frontend.ir.llvm.value.instruction.terminator;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Assembly;
import backend.assembly.instruction.JumpInstruction;
import frontend.ir.llvm.value.Value;

public class Ret extends TerminatorInstruction {
    private boolean isMain = false;

    public Ret(String name, Value returnValue) {
        super(name);
        if (returnValue != null) {
            addUsed(returnValue);
        }
    }

    public void markAsMain() {
        isMain = true;
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        if (isMain) {
            //j exit
            JumpInstruction j = new JumpInstruction("j", "exit", null);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(j);
        } else {
            //char or int
            if (getUsed(0) != null) {
                //move value to v0
                Assembly.moveScalarValueToRegister(getUsed(0), Register.V0);
            }

            //jr
            JumpInstruction jr = new JumpInstruction("jr", null, Register.RA);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(jr);
        }
    }

    @Override
    public String toString() {
        String retString = getUsed(0) == null ? "void" : getUsed(0).getValueType() + " " + getUsed(0).getName();
        return super.toString() + "ret " + retString;
    }
}
