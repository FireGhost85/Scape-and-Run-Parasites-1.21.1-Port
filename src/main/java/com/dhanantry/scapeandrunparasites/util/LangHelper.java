package com.dhanantry.scapeandrunparasites.util;


/** Colored roman numerals of the strength levels 1 to 5 (node lamp, field guide). */
public final class LangHelper {
    private LangHelper() {
    }

    public static String toRoman(int n) {
        switch (n) {
            case 1:
                return "\u00a7aI";
            case 2:
                return "\u00a7eII";
            case 3:
                return "\u00a76III";
            case 4:
                return "\u00a7cIV";
            case 5:
                return "\u00a7dV";
            default:
                return Integer.toString(n);
        }
    }
}
