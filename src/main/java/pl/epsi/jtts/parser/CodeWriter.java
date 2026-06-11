package pl.epsi.jtts.parser;

public final class CodeWriter {
    private final StringBuilder builder = new StringBuilder();
    private int indent;

    public void indent() {
        indent++;
    }

    public void dedent() {
        indent--;
    }

    public void line(String text) {
        builder.append("    ".repeat(indent));
        builder.append(text);
        builder.append('\n');
    }

    public void newline() {
        builder.append("\n").append("    ".repeat(indent));
    }

    public void append(String text) {
        builder.append(text);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
