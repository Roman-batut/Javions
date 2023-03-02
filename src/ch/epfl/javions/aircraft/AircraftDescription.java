package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftDescription(String string) {

    public AircraftDescription {
        if(!string.isEmpty()) {
            Pattern pattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
            if (!pattern.matcher(string).matches()) {
                throw new IllegalArgumentException();
            }
        }
    }
}
