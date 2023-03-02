package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftTypeDesignator(String string) {

    public AircraftTypeDesignator {
        if(!string.isEmpty()) {
            Pattern pattern = Pattern.compile("[A-Z0-9]{2,4}");
            if (!pattern.matcher(string).matches()) {
                throw new IllegalArgumentException();
            }
        }
    }
}
