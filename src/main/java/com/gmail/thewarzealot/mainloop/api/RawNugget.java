package com.gmail.thewarzealot.mainloop.api;

public class RawNugget {

    private String text;

    private boolean bold;
    private boolean italic;
    private boolean strikethrough;
    private boolean underlined;
    private boolean obfuscated;

    private String color;

    public RawNugget(String text, String color, boolean bold, boolean italic, boolean strikethrough, boolean underlined, boolean obfuscated) {
        this.text = text;
        this.bold = bold;
        this.italic = italic;
        this.strikethrough = strikethrough;
        this.underlined = underlined;
        this.obfuscated = obfuscated;
        this.color = color;
    }

    @Override
    public String toString() {
        return
                "\"text\":\"" + text + "\"" +
                ",\"bold\":" + bold +
                ",\"italic\":" + italic +
                ",\"strikethrough\":" + strikethrough +
                ",\"underlined\":" + underlined +
                ",\"obfuscated\":" + obfuscated +
                ",\"color\":\"" + color + "\"";
    }
}
