package frontend.ir.llvm.value.instruction.memory;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.Assembly;
import backend.assembly.instruction.Li;
import backend.assembly.instruction.MemoryInstruction;
import frontend.ir.llvm.value.Constant;
import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.type.ScalarValueType;

//store <ty> <value>, ptr <pointer>
public class Store extends MemoryOperation {
    public Store(String name, Value value, Value pointer) {
        super(ScalarValueType.VOID, name);
        addUsed(value);
        addUsed(pointer);
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        //rt: the register to sw from
        Register rt = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this.getUsedValue(0));
        if (rt == null) {
            rt = Register.K0;
        }

        if (getUsedValue(0) instanceof Constant) {
            //li
            Li li = new Li(rt, Integer.parseInt(getUsedValue(0).getName()));
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(li);
        } else if (rt == Register.K0) {
            //lw
            int offset = AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValueIfNotMapped(getUsedValue(0));
            MemoryInstruction lw = new MemoryInstruction("lw", rt, null, Register.SP, offset);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(lw);
        }

        //base: the register containing the address to sw to
        Register base = Assembly.movePointerValueToRegisterIfNotMapped(this.getUsedValue(1), Register.K1);

        //sw
        MemoryInstruction sw = new MemoryInstruction("sw", rt, null, base, 0);
        AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sw);
    }

    @Override
    public String toString() {
        return super.toString() + "store "
                + getUsedValue(0).getValueType() + " " + getUsedValue(0).getName() + ", "
                + getUsedValue(1).getValueType() + " " + getUsedValue(1).getName();
    }
}
