package frontend.ir.value.global;

import frontend.ir.value.initializer.Initializer;
import frontend.ir.value.type.PointerValueType;
import frontend.ir.value.type.ValueType;

public class GlobalVariable extends GlobalValue {

    private final Initializer initializer;

    //LLVM 内的 GlobalVariable 其实是指针类型，指向一块内存区域
    //详见 https://evian-zhang.github.io/llvm-ir-tutorial/ 3.3
    public GlobalVariable(String name, ValueType referencedType, Initializer initializer) {
        super(new PointerValueType(referencedType), name);
        this.initializer = initializer;
    }

    @Override
    public String toString() {
        return name + " = dso_local global " + initializer;
    }
}
