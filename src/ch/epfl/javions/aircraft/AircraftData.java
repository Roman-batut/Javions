package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 *  Record representing an aircraft data
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public record AircraftData(AircraftRegistration registration,
                           AircraftTypeDesignator typeDesignator,
                           String model,
                           AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {

    //* Constructor

    /**
     *  Constructor of an aircraft data
     *  @param registration the aircraft registration
     *  @param typeDesignator the aircraft type designator
     *  @param model the aircraft model
     *  @param description the aircraft description
     *  @param wakeTurbulenceCategory the aircraft wake turbulence category
     *  @throws NullPointerException if any of the arguments is null
     */
    public AircraftData{
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
