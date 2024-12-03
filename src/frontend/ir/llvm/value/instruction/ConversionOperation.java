package frontend.ir.llvm.value.instruction;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Assembly;
import backend.assembly.instruction.ComputationalInstruction;
import backend.assembly.instruction.MemoryInstruction;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.type.ScalarValueType;
import frontend.ir.llvm.value.type.ValueType;

//<result> = <operator> <ty> <value> to <ty2>
public class ConversionOperation extends Instruction {
    private enum ConversionOperator {
        TRUNC, ZEXT, WTF;

        public static ConversionOperator getByValueType(ValueType from, ValueType to) {
            if (from == ScalarValueType.INT32 && to == ScalarValueType.INT8) {
                return TRUNC;
            } else if ((from == ScalarValueType.INT8 || from == ScalarValueType.INT1) && to == ScalarValueType.INT32) {
                return ZEXT;
            } else {
                return WTF;
            }
        }
    }

    private final ConversionOperator operator;

    public ConversionOperation(ValueType valueType, String name, Value operand) {
        super(valueType, name);
        addUsed(operand);
        operator = ConversionOperator.getByValueType(operand.getValueType(), valueType);
    }

    public boolean isZext(){
        return operator == ConversionOperator.ZEXT;
    }

    @Override
    public String hash() {
        return operator.toString().toLowerCase() + " " + valueType + " " + getUsedValue(0).getName();
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();
        switch (operator) {
            case TRUNC -> {
                Register rs = Assembly.moveScalarValueToRegisterIfNotMapped(getUsedValue(0), Register.K0);
                Register rt = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this);
                if (rt == null) {
                    rt = Register.K0;
                }

                //andi
                ComputationalInstruction andi = new ComputationalInstruction("andi", rt, rs, 0xff);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(andi);

                Assembly.saveValueOnStackFromRegisterIfNotMapped(this, rt);
            }
            case ZEXT -> {
                if (AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(getUsedValue(0)) == null) {
                    AssemblyBuilder.ASSEMBLY_BUILDER.mapValueToExistedValueOnStack(this, getUsedValue(0));
                } else {
                    //sw
                    Register register = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(getUsedValue(0));
                    int offset = AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValue(this);
                    MemoryInstruction sw = new MemoryInstruction("sw", register, null, Register.SP, offset);
                    AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sw);
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + name + " = " + operator.toString().toLowerCase() + " "
                + getUsedValue(0).getValueType() + " " + getUsedValue(0).getName() + " to " + valueType;
    }
}
