package frontend.ir.llvm.value.instruction.memory;

import backend.AssemblyBuilder;
import backend.Register;
import backend.assembly.instruction.ComputationalInstruction;
import backend.assembly.instruction.MemoryInstruction;
import frontend.ir.llvm.value.initializer.Initializer;
import frontend.ir.llvm.value.type.ArrayValueType;
import frontend.ir.llvm.value.type.PointerValueType;
import frontend.ir.llvm.value.type.ValueType;

//<result> = alloca <type>
public class Alloca extends MemoryOperation {
    private Initializer initializer = null;

    public Alloca(String name, ValueType referencedType) {
        super(new PointerValueType(referencedType), name);
    }

    public Alloca(String name, ValueType referencedType, Initializer initializer) {
        super(new PointerValueType(referencedType), name);
        this.initializer = initializer;
    }

    public boolean isConstant(){
        return initializer!=null;
    }

    public int getInitialValue(int index) {
        if (index > initializer.getValues().size() - 1) {
            return 0;
        }
        return initializer.getValues().get(index);
    }

    @Override
    public void buildAssembly() {
        super.buildAssembly();

        //alloc for alloca object
        int offset;
        if (valueType.getPointerReferenceValueType() instanceof ArrayValueType arrayValueType) {
            offset = AssemblyBuilder.ASSEMBLY_BUILDER.allocSpaceOnStackTop(arrayValueType.getElementNumber());
        } else {
            offset = AssemblyBuilder.ASSEMBLY_BUILDER.allocSpaceOnStackTop(1);
        }

        //has corresponding register
        if (AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this) != null) {
            Register register = AssemblyBuilder.ASSEMBLY_BUILDER.getRegisterOfValue(this);
            ComputationalInstruction addi = new ComputationalInstruction("addi", register, Register.SP, offset);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(addi);
        }
        //no corresponding register
        else {
            //addi
            ComputationalInstruction addi = new ComputationalInstruction("addi", Register.K0, Register.SP, offset);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(addi);

            //sw
            offset = AssemblyBuilder.ASSEMBLY_BUILDER.assignWordOnStackTopForValue(this);
            MemoryInstruction sw = new MemoryInstruction("sw", Register.K0, null, Register.SP, offset);
            AssemblyBuilder.ASSEMBLY_BUILDER.addToText(sw);
        }
    }

    @Override
    public String toString() {
        return super.toString() + name + " = alloca " + ((PointerValueType) valueType).getReferenceType();
    }
}
