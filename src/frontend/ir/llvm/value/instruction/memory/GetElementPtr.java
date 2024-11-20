package frontend.ir.llvm.value.instruction.memory;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Assembly;
import backend.assembly.instruction.ComputationalInstruction;
import backend.assembly.instruction.MemoryInstruction;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.type.ArrayValueType;
import frontend.ir.llvm.value.type.PointerValueType;
import frontend.ir.llvm.value.type.ValueType;

//<result> = getelementptr inbounds <ty>, ptr <ptrval>{, <ty> <idx>}*
public class GetElementPtr extends MemoryOperation {
    public GetElementPtr(ValueType valueType, String name, Value pointer, Value offset) {
        super(new PointerValueType(valueType), name);
        addUsed(pointer);
        addUsed(offset);
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        //rs: the register containing the address of the array
        Register rs = Assembly.movePointerValueToRegisterIfNotMapped(getUsed(0), Register.K0);

        //rs: the register containing the offset
        Register rt = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(getUsed(1));
        if (rt == null) {
            rt = Register.K1;
        }

        //rd: the register where result is saved to
        Register rd = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this);
        if (rd == null) {
            rd = Register.K0;
        }

        if (getUsed(1) instanceof Constant) {
            //addi
            ComputationalInstruction addi = new ComputationalInstruction("addi", rd, rs, Integer.parseInt(getUsed(1).getName()) * 4);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(addi);
        } else {
            if (rt == Register.K1) {
                //lw
                int offset = AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValueIfNotMapped(getUsed(1));
                MemoryInstruction lw = new MemoryInstruction("lw", rt, null, Register.SP, offset);
                AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);
            }
            //sll (mult 4)
            ComputationalInstruction sll = new ComputationalInstruction("sll", rt, rt, 2);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sll);

            //addu
            ComputationalInstruction addu = new ComputationalInstruction("addu", rd, rs, rt);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(addu);
        }
        Assembly.saveValueOnStackFromRegisterIfNotMapped(this, rd);
    }

    @Override
    public String toString() {
        Value pointer = getUsed(0);
        Value offset = getUsed(1);
        ValueType referenceType = pointer.getValueType().getPointerReferenceValueType();
        return super.toString() + name + " = getelementptr inbounds " +
                referenceType + ", " + pointer.getValueType() + " " + pointer.getName() + ", " +
                ((referenceType instanceof ArrayValueType) ? "i32 0, " : "") +
                offset.getValueType() + " " + offset.getName();
    }
}
