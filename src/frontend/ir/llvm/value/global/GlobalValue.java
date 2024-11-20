package frontend.ir.llvm.value.global;

import frontend.ir.llvm.value.Value;
import frontend.ir.llvm.value.type.ValueType;

public class GlobalValue extends Value {

    public GlobalValue(ValueType type, String name) {
        super(type, name);
    }
}
