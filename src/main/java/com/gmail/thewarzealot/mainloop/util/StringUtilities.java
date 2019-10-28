package com.gmail.thewarzealot.mainloop.util;

import java.util.ArrayList;

class StringUtilities {

    static String[] splitExecuteMods(String s) {
        String[] result = s.split("| ", 2);
        if (result.length < 1) return new String[]{"A", "Error"};
        if (result.length < 2) return new String[]{"A", result[0]};
        result[1] = result[1].replace("| ", "");
        if (!result[0].equals("A") && !result[0].equals("U") && !result[0].equals("E")) return new String[]{"A", result[1]};
        return result;
    }

    static String getExecuteMod(String s) {
        switch (s) {
            case "A": return "suggest_command";
            case "U": return "open_url";
            case "E": return "run_command";
        }
        return "suggest_command";
    }

    static ArrayList<String[]> splitTextParts(String s) {
        String[] bef_texts = s.split("<.*?>");
        ArrayList<String> aft_texts = new ArrayList<>();

        for (String text : bef_texts) {
            if (!text.equals("")) {
                aft_texts.add(text);
            }
        }

        String[] texts = aft_texts.toArray(new String[0]);

        String links = s;
        for (String s1 : texts) {
            links = links.replace(s1, ":");
        }
        String[] mods = links.split(":");

        for (int i = 0; i < mods.length; i++) {
            mods[i] = mods[i].replace("<", "");
            mods[i] = mods[i].replace(">", "");
        }

        ArrayList<String[]> result = new ArrayList<>();
        for (String mod : mods) {
            result.add(mod.split(","));
        }

        ArrayList<String[]> res = new ArrayList<>();
        String exec;
        for (int i = 0; i < texts.length; i++) {
            if (result.get(i).length > 1) {
                exec = result.get(i)[1];
            } else {
                exec = "";
            }
            res.add(new String[]{texts[i], result.get(i)[0], exec});
        }
        return res;
    }
}
