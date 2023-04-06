package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;

import java.util.Objects;

/**
 * Class representing an accumulator of aircraft states
 * @author Roman Batut (356158) 
 * @author Guillaume Chevallier (360709)
 */
public class AircraftStateAccumulator<T extends AircraftStateSetter> {

    private T aircraftstatesetter;
    private AirbornePositionMessage[] oldmessages;

    //* Constructor

    /**
     * Constructor of an aircraft state accumulator
     * @param stateSetter the aircraft state setter
     * @throws NullPointerException if the aircraft state setter is null
     */
    public AircraftStateAccumulator(T stateSetter){
        Objects.requireNonNull(stateSetter);

        aircraftstatesetter = stateSetter;
        oldmessages = new AirbornePositionMessage[]{null, null};
    }

    //* Getters

    /**
     * Returns the aircraft state setter
     */
    public T stateSetter(){
        return aircraftstatesetter;
    }


    //* Methods
    
    
    /**
     * Updates the aircraft state setter with the given message
     * @param message the message
     * @throws NullPointerException if the message is null
     */
    public void update(Message message){
        aircraftstatesetter.setLastMessageTimeStampNs(message.timeStampNs());

        switch (message) {
            case AircraftIdentificationMessage idm -> {
                aircraftstatesetter.setCallSign(idm.callSign());
                aircraftstatesetter.setCategory(idm.category());
            }

            case AirborneVelocityMessage velm -> {
                aircraftstatesetter.setVelocity(velm.speed());
                aircraftstatesetter.setTrackOrHeading(velm.trackOrHeading());
            }

            case AirbornePositionMessage posm -> {
                aircraftstatesetter.setAltitude(posm.altitude());

                AirbornePositionMessage oldmessage = oldmessages[((posm.parity()+1)%2)];
                if(oldmessage != null && posm.timeStampNs()-oldmessage.timeStampNs() < Math.pow(10, 10)) {
                    GeoPos position;
                    if(posm.parity() == 0){
                        position = CprDecoder.decodePosition(posm.x(), posm.y(), oldmessage.x(), oldmessage.y(), 0);
                    }else {
                        position = CprDecoder.decodePosition(oldmessage.x(), oldmessage.y(), posm.x(), posm.y(), 1);
                    }

                    if(position != null){
                        aircraftstatesetter.setPosition(position);
                    }
                }
                oldmessages[posm.parity()] = posm;
            }

            default -> System.out.println();
        }
    }
}
