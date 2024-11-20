package backend.assembly;

public class Label extends Assembly{
    private final String label;
    public Label(String label){
        this.label = label;
    }
    @Override
    public String toString(){
        return "\n" + label + ":";
    }
}
