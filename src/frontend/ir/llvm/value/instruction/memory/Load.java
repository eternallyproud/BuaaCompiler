package frontend.ir.llvm.value.instruction.memory;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Assembly;
import backend.assembly.instruction.MemoryInstruction;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.type.PointerValueType;

//<result> = load <ty>, ptr <pointer>
public class Load extends MemoryOperation {
    public Load(String name, Value pointer) {
        super(((PointerValueType) pointer.getValueType()).getReferenceType(), name);
        addUsed(pointer);
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        //rs: the register containing the address to lw from
        Register rs = Assembly.movePointerValueToRegisterIfNotMapped(getUsedValue(0), Register.K0);

        //rt: the register to lw to
        Register rt = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this);
        if (rt == null) {
            rt = Register.K0;
        }

        //lw
        MemoryInstruction lw = new MemoryInstruction("lw", rt, null, rs, 0);
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);

        Assembly.saveValueOnStackFromRegisterIfNotMapped(this, rt);
    }

    @Override
    public String toString() {
        return super.toString() + name + " = load " + valueType + ", "
                + getUsedValue(0).getValueType() + " " + getUsedValue(0).getName();
    }
}
