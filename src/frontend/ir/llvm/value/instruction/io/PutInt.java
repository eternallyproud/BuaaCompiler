package frontend.ir.llvm.value.instruction.io;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Assembly;
import backend.assembly.instruction.Li;
import backend.assembly.instruction.Syscall;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.type.ScalarValueType;

public class PutInt extends IOInstruction {
    public PutInt(String name, Value used) {
        super(ScalarValueType.VOID, name);
        addUsed(used);
    }

    public static String getDeclaration() {
        return "declare void @putint(i32)";
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        //move value to a0
        Assembly.moveScalarValueToRegister(getUsedValue(0), Register.A0);

        //li
        Li li = new Li(Register.V0, 1);
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(li);

        //syscall
        Syscall syscall = new Syscall();
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(syscall);
    }

    @Override
    public String toString() {
        return super.toString() + "call void @putint(i32 " + getUsedValue(0).getName() + ")";
    }
}
