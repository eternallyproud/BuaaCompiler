package frontend.ir.value.instruction.memory;

import frontend.ir.value.Value;
import frontend.ir.value.type.PointerValueType;

//<result> = load <ty>, ptr <pointer>
public class Load extends MemoryOperation {
    public Load(String name, Value pointer) {
        super(((PointerValueType) pointer.getValueType()).getReferenceType(), name);
        addUsed(pointer);
    }

    @Override
    public String toString() {
        return super.toString() + name + " = load " + valueType + ", "
                + getUsed(0).getValueType() + " " + getUsed(0).getName();
    }
}
