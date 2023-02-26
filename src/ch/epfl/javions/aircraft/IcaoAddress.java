package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record IcaoAddress(String string) {

    public IcaoAddress{
        Pattern pattern = Pattern.compile("[0-9A-F]{6}");
        if(!pattern.matcher(string).matches() || string.isEmpty()){
            throw new IllegalArgumentException();
        }
    }
}
