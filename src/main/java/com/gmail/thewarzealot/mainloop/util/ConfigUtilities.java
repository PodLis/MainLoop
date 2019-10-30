package com.gmail.thewarzealot.mainloop.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

public class ConfigUtilities {

    public static String[][] getStringsAndKeys(ConfigurationSection section) {

        if (section == null) {
            return new String[][] {{""},{""}};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        String[][] result = new String[2][keys.length];

        for (int i = 0; i < keys.length; i++) {
            result[0][i] = keys[i];
            result[1][i] = (String) section.getValues(false).get(keys[i]);
        }

        return result;
    }

    public static String[] getStrings(ConfigurationSection section, String parameter) {

        if (section == null) {
            return new String[] {""};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        MemorySection[] memorySections = new MemorySection[keys.length];
        String[] texts = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {
            memorySections[i] = (MemorySection) section.getValues(false).get(keys[i]);
            texts[i] = memorySections[i].getString(parameter);
        }

        return texts;
    }

    public static String[][] getArrayOfStrings(ConfigurationSection section, String parameter) {

        if (section == null) {
            return new String[][] {{""}};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        MemorySection[] memorySections = new MemorySection[keys.length];
        String[][] texts = new String[keys.length][];

        for (int i = 0; i < keys.length; i++) {
            memorySections[i] = (MemorySection) section.getValues(false).get(keys[i]);
            texts[i] = memorySections[i].getStringList(parameter).toArray(new String[0]);
        }

        return texts;
    }

    public static double[] getDoubles(ConfigurationSection section, String parameter) {

        if (section == null) {
            return new double[] {0};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        MemorySection[] memorySections = new MemorySection[keys.length];
        double[] doubles = new double[keys.length];

        for (int i = 0; i < keys.length; i++) {
            memorySections[i] = (MemorySection) section.getValues(false).get(keys[i]);
            doubles[i] = memorySections[i].getDouble(parameter);
        }

        return doubles;
    }

    public static int[] getIntegers(ConfigurationSection section, String parameter) {

        if (section == null) {
            return new int[] {0};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        MemorySection[] memorySections = new MemorySection[keys.length];
        int[] integers = new int[keys.length];

        for (int i = 0; i < keys.length; i++) {
            memorySections[i] = (MemorySection) section.getValues(false).get(keys[i]);
            integers[i] = memorySections[i].getInt(parameter);
        }

        return integers;
    }

    public static boolean[] getBooleans(ConfigurationSection section, String parameter) {

        if (section == null) {
            return new boolean[] {false};
        }

        String[] keys = section.getKeys(false).toArray(new String[0]);
        MemorySection[] memorySections = new MemorySection[keys.length];
        boolean[] booleans = new boolean[keys.length];

        for (int i = 0; i < keys.length; i++) {
            memorySections[i] = (MemorySection) section.getValues(false).get(keys[i]);
            booleans[i] = memorySections[i].getBoolean(parameter);
        }

        return booleans;
    }

}
