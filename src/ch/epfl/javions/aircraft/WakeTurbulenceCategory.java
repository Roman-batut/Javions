package ch.epfl.javions.aircraft;

public enum WakeTurbulenceCategory {

    LIGHT("L"),
    MEDIUM("M"),
    HEAVY("H"),
    UNKNOWN("");

    private final String letter;

    private WakeTurbulenceCategory(String s) {
        letter = s;
    }

    public static WakeTurbulenceCategory of(String s){
        for(WakeTurbulenceCategory category : WakeTurbulenceCategory.values()) {
            if (category.letter.equals(s)) {
                return category;
            }
        }

        return UNKNOWN;
    }
}

