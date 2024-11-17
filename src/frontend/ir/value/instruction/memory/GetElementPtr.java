package frontend.ir.value.instruction.memory;

import frontend.ir.value.Value;
import frontend.ir.value.type.ArrayValueType;
import frontend.ir.value.type.PointerValueType;
import frontend.ir.value.type.ValueType;

//<result> = getelementptr inbounds <ty>, ptr <ptrval>{, <ty> <idx>}*
public class GetElementPtr extends MemoryOperation {
    public GetElementPtr(ValueType valueType, String name, Value pointer, Value offset) {
        super(new PointerValueType(valueType), name);
        addUsed(pointer);
        addUsed(offset);
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
