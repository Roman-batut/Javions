package ch.epfl.javions.aircraft;

import java.lang.reflect.Field;
import java.util.Objects;

public record AirCraftData(AircraftRegistration registration,
                           AircraftTypeDesignator typeDesignator,
                           String model,
                           AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {

    public AirCraftData{
        for(Field field : AirCraftData.class.getClass().getFields()){
            Objects.requireNonNull(field);
        }

    }
}
