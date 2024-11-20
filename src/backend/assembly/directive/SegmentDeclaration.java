package backend.assembly.directive;

public class SegmentDeclaration extends Directive{
    public final static SegmentDeclaration DATA = new SegmentDeclaration(".data");
    public final static SegmentDeclaration TEXT = new SegmentDeclaration(".text");
    private final String directive;

    private SegmentDeclaration(String directive) {
        this.directive = directive;
    }

    @Override
    public String toString() {
        return directive;
    }
}
