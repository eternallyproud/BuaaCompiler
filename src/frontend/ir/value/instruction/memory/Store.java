package frontend.ir.value.instruction.memory;

import frontend.ir.value.Value;
import frontend.ir.value.type.ScalarValueType;

//store <ty> <value>, ptr <pointer>
public class Store extends MemoryOperation {
    public Store(String name, Value source, Value destination) {
        super(ScalarValueType.VOID, name);
        addUsed(source);
        addUsed(destination);
    }

    @Override
    public String toString() {
        return super.toString() + "store "
                + getUsed(0).getValueType() + " " + getUsed(0).getName() + ", "
                + getUsed(1).getValueType() + " " + getUsed(1).getName();
    }
}
