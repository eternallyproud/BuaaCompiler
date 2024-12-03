package frontend.ir.llvm.value.instruction;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Assembly;
import backend.assembly.instruction.ComputationalInstruction;
import backend.assembly.instruction.MoveInstruction;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.type.ScalarValueType;

public class BinaryOperation extends Instruction {
    private enum BinaryOperator {
        ADD, SUB, MUL, SDIV, SREM, UNDEFINED;

        private static BinaryOperator getBySymbol(String symbol) {
            return switch (symbol) {
                case "+" -> ADD;
                case "-" -> SUB;
                case "*" -> MUL;
                case "/" -> SDIV;
                case "%" -> SREM;
                default -> UNDEFINED;
            };
        }

        private String getString(){
            return switch (this) {
                case ADD -> "+";
                case SUB -> "-";
                case MUL -> "*";
                case SDIV -> "/";
                case SREM -> "%";
                default -> "";
            };
        }
    }

    private final BinaryOperator operator;

    public BinaryOperation(String name, String symbol, Value operand1, Value operand2) {
        super(ScalarValueType.INT32, name);
        this.operator = BinaryOperator.getBySymbol(symbol);
        addUsed(operand1);
        addUsed(operand2);
    }
    
    public String getOperator(){
        return operator.getString();
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
            case ADD -> {
                //addu
                ComputationalInstruction addu = new ComputationalInstruction("addu", rd, rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(addu);
            }
            case SUB -> {
                //subu
                ComputationalInstruction subu = new ComputationalInstruction("subu", rd, rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(subu);
            }
            case MUL -> {
                //mult
                ComputationalInstruction mult = new ComputationalInstruction("mul", rd, rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(mult);
            }
            case SDIV -> {
                //div
                ComputationalInstruction div = new ComputationalInstruction("div", rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(div);

                //mflo
                MoveInstruction mflo = new MoveInstruction("mflo", rd);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(mflo);
            }
            case SREM -> {
                //div
                ComputationalInstruction rem = new ComputationalInstruction("div", rs, rt);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(rem);

                //mfhi
                MoveInstruction mfhi = new MoveInstruction("mfhi", rd);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(mfhi);
            }
        }

        Assembly.saveValueOnStackFromRegisterIfNotMapped(this, rd);
    }

    @Override
    public String toString() {
        return super.toString() + name + " = " + operator.toString().toLowerCase() + " i32 " + getUsedValue(0).getName() + ", " + getUsedValue(1).getName();
    }
}
