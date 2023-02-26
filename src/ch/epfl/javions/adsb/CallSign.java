package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

public record CallSign(String string) {

    public CallSign{
        Pattern pattern = Pattern.compile("[A-Z0-9 ]{0,8}");
        if(!pattern.matcher(string).matches()){
            throw new IllegalArgumentException();
        }
    }
}
