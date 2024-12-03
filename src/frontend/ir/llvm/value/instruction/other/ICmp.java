package frontend.ir.llvm.value.instruction.other;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Assembly;
import backend.assembly.instruction.ComputationalInstruction;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.type.ScalarValueType;

//<result> = icmp <cmp op> <ty> <op1>, <op2>
public class ICmp extends Operation {
    private enum CompareOperator {
        EQ, NE, SGT, SGE, SLT, SLE, UNDEFINED;

        private static CompareOperator getBySymbol(String symbol) {
            return switch (symbol) {
                case "==" -> EQ;
                case "!=" -> NE;
                case ">" -> SGT;
                case ">=" -> SGE;
                case "<" -> SLT;
                case "<=" -> SLE;
                default -> UNDEFINED;
            };
        }
    }

    private final CompareOperator operator;

    public ICmp(String name, String symbol, Value operand1, Value operand2) {
        super(ScalarValueType.INT1, name);
        operator = CompareOperator.getBySymbol(symbol);
        addUsed(operand1);
        addUsed(operand2);
    }

    @Override
    public String hash() {
        return operator.toString().toLowerCase() + " " + getUsedValue(0).getName() + " " + getUsedValue(1).getName();
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        //rs rt rd
        Register rs = Assembly.moveScalarValueToRegisterIfNotMapped(getUsedValue(0), Register.K0);
        Register rt = Assembly.moveScalarValueToRegisterIfNotMapped(getUsedValue(1), Register.K1);
        Register rd = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this);
        if (rd == null) {
            rd = Register.K0;
        }

        switch (operator) {
            case EQ -> {
                ComputationalInstruction seq = new ComputationalInstruction("seq", rd, rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(seq);
            }
            case NE -> {
                ComputationalInstruction sne = new ComputationalInstruction("sne", rd, rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sne);
            }
            case SGT -> {
                ComputationalInstruction sgt = new ComputationalInstruction("sgt", rd, rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sgt);
            }
            case SGE -> {
                ComputationalInstruction sge = new ComputationalInstruction("sge", rd, rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sge);
            }
            case SLT -> {
                ComputationalInstruction slt = new ComputationalInstruction("slt", rd, rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(slt);
            }
            case SLE -> {
                ComputationalInstruction sle = new ComputationalInstruction("sle", rd, rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sle);
            }
        }

        Assembly.saveValueOnStackFromRegisterIfNotMapped(this, rd);
    }

    @Override
    public String toString() {
        return super.toString() + name + " = icmp " + operator.toString().toLowerCase() + " i32 "
                + getUsedValue(0).getName() + ", " + getUsedValue(1).getName();
    }
}
