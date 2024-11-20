package frontend.ir.llvm.value.instruction.io;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.instruction.La;
import backend.assembly.instruction.Li;
import backend.assembly.instruction.Syscall;
import frontend.ir.llvm.value.global.StringLiteral;
import frontend.ir.llvm.value.type.ScalarValueType;

public class PutStr extends IOInstruction {
    private final StringLiteral stringLiteral;

    public PutStr(String name, StringLiteral stringLiteral) {
        super(ScalarValueType.VOID, name);
        this.stringLiteral = stringLiteral;
    }

    public static String getDeclaration() {
        return "declare void @putstr(i8*)";
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        //la
        La la = new La(Register.A0, stringLiteral.getName().substring(1));
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(la);

        //li
        Li li = new Li(Register.V0, 4);
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(li);

        //syscall
        Syscall syscall = new Syscall();
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(syscall);
    }

    @Override
    public String toString() {
        return super.toString() + "call void @putstr(i8* getelementptr inbounds (" +
                stringLiteral.getValueType() + ", " +
                stringLiteral.evaluate() + " " +
                stringLiteral.getName() + ", i64 0, i64 0))";
    }
}
