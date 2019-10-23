package com.gmail.thewarzealot.mainloop.api;

public class RawBlock {

    private RawNugget[] rawNuggets;

    public RawBlock(RawNugget[] rawNuggets) {
        this.rawNuggets = rawNuggets;
    }

    @Override
    public String toString() {
        StringBuilder s;
        s = new StringBuilder("[\"\"");
        for (RawNugget rawNugget : rawNuggets) {
            s.append(",{").append(rawNugget.toString()).append("}");
        }
        s.append("]");
        return s.toString();
    }
}
