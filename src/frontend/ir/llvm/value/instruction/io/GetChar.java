package frontend.ir.llvm.value.instruction.io;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.instruction.Li;
import backend.assembly.instruction.MemoryInstruction;
import backend.assembly.instruction.MoveInstruction;
import backend.assembly.instruction.Syscall;
import frontend.ir.llvm.value.type.ScalarValueType;

public class GetChar extends IOInstruction {
    public GetChar(String name) {
        super(ScalarValueType.INT32, name);
    }

    public static String getDeclaration() {
        return "declare i32 @getchar()";
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        //li
        Li li = new Li(Register.V0, 12);
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(li);

        //syscall
        Syscall syscall = new Syscall();
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(syscall);

        //has corresponding register
        if (AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this) != null) {
            //move
            MoveInstruction move = new MoveInstruction(AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this), Register.V0);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(move);
        }
        //no corresponding register
        else {
            //lw
            int offset = AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValueIfNotMapped(this);
            MemoryInstruction sw = new MemoryInstruction("sw", Register.V0, null, Register.SP, offset);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sw);
        }
    }

    @Override
    public String toString() {
        return super.toString() + name + " = call i32 @getchar()";
    }
}
