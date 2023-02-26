package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftRegistration(String string) {

    public AircraftRegistration {
        Pattern pattern = Pattern.compile("[A-Z0-9 .?/_+-]+");
        if(!pattern.matcher(string).matches() || string.isEmpty()){
            throw new IllegalArgumentException();
        }
    }
}
