package frontend.ir.value.instruction.memory;

import frontend.ir.value.type.PointerValueType;
import frontend.ir.value.type.ValueType;

//<result> = alloca <type>
public class Alloca extends MemoryOperation {
    public Alloca(String name, ValueType referencedType) {
        super(new PointerValueType(referencedType), name);
    }

    @Override
    public String toString() {
        return super.toString() + name + " = alloca " + ((PointerValueType) valueType).getReferenceType();
    }
}
