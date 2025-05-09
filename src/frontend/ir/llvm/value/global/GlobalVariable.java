package frontend.ir.llvm.value.global;

import backend.AssemblyBuilder;
import backend.assembly.directive.GlobalDeclaration;
import frontend.ir.llvm.value.initializer.Initializer;
import frontend.ir.llvm.value.type.PointerValueType;
import frontend.ir.llvm.value.type.ValueType;

public class GlobalVariable extends GlobalValue {

    private final Initializer initializer;
    private final boolean isConstant;

    //LLVM 内的 GlobalVariable 其实是指针类型，指向一块内存区域
    //详见 https://evian-zhang.github.io/llvm-ir-tutorial/ 3.3
    public GlobalVariable(String name, ValueType referencedType, Initializer initializer, boolean isConstant) {
        super(new PointerValueType(referencedType), name);
        this.initializer = initializer;
        this.isConstant = isConstant;
    }

    public int getInitialValue(int index) {
        if (index > initializer.getValues().size() - 1) {
            return 0;
        }
        return initializer.getValues().get(index);
    }

    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public String toString() {
        return name + " = dso_local global " + initializer;
    }

    @Override
    public void buildAssembly() {
        //no need to process different types separately
        GlobalDeclaration.Word word = new GlobalDeclaration.Word(name.substring(1), initializer.getElementNumber(), initializer.getValues());
        AssemblyBuilder.ASSEMBLY_BUILDER.addToData(word);
    }
}
