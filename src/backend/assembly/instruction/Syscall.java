package backend.assembly.instruction;

public class Syscall extends Instruction{
    @Override
    public String toString() {
        return super.toString() + "syscall";
    }
}
