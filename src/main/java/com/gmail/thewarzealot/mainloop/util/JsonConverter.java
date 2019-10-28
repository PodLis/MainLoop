package com.gmail.thewarzealot.mainloop.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonConverter {

    private static Map<String, String> hovers = new HashMap<>();
    private static Map<String, String[]> executes = new HashMap<>();

    public static void setHoversExecutes(String[] hoverNames, String[] hoverValues, String[] executeNames, String[] executeValues) {
        if (hoverNames.length == hoverValues.length && executeNames.length == executeValues.length) {
            for (int i = 0; i < hoverNames.length; i++) {
                JsonConverter.hovers.put(hoverNames[i], hoverValues[i]);
            }
            for (int i = 0; i < executeNames.length; i++) {
                JsonConverter.executes.put(executeNames[i], StringUtilities.splitExecuteMods(executeValues[i]));
            }
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public static String getJsonString(String chatText) {
        StringBuilder builder = new StringBuilder();
        ArrayList<String[]> source = StringUtilities.splitTextParts(chatText);

        builder.append("\"\"");
        for (String[] item : source) {
            builder.append(",{");
            builder.append("\"text\":\"").append(item[0]).append("\"");
            if (!item[2].equals("") && executes.containsKey(item[2])) {
                builder.append(",\"clickEvent\":{");

                builder.append("\"action\":\"").append(StringUtilities.getExecuteMod(executes.get(item[2])[0])).append("\"");
                builder.append(",");
                builder.append("\"value\":\"").append(executes.get(item[2])[1]).append("\"");

                builder.append("}");
            }

            if (!item[1].equals("") && hovers.containsKey(item[1])) {
                builder.append(",\"hoverEvent\":{");

                builder.append("\"action\":\"").append("show_text").append("\"");
                builder.append(",");
                builder.append("\"value\":\"").append(hovers.get(item[1])).append("\"");

                builder.append("}");
            }
            builder.append("}");
        }

        String result = builder.toString();
        result = "[" + result + "]";
        return result;
    }

}
