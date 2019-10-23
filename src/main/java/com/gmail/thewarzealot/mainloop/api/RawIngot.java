package com.gmail.thewarzealot.mainloop.api;


public class RawIngot extends RawNugget{

    private String clickEventAction;
    private String clickEventValue;
    private String hoverEventAction;
    private RawNugget hoverEventValue;

    public RawIngot(String text, String color, boolean bold, boolean italic, boolean strikethrough, boolean underlined, boolean obfuscated, String clickEventAction, String clickEventValue, String hoverEventAction, RawNugget hoverEventValue) {
        super(text, color, bold, italic, strikethrough, underlined, obfuscated);
        this.clickEventAction = clickEventAction;
        this.clickEventValue = clickEventValue;
        this.hoverEventAction = hoverEventAction;
        this.hoverEventValue = hoverEventValue;
    }

    @Override
    public String toString() {
        return
                super.toString() +

                ",\"clickEvent\":{" +
                "\"action\":\"" + clickEventAction + "\"," +
                "\"value\":\"" + clickEventValue + "\"" +
                "}" +

                ",\"hoverEvent\":{" +
                "\"action\":\"" + hoverEventAction + "\"," +
                "\"value\":{" + hoverEventValue.toString() + "}" +
                "}";

    }
}
