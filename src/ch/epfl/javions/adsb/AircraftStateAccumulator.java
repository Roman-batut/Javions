package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {

    private T aircraftstatesetter;
    private AirbornePositionMessage[] oldmessages;

    public AircraftStateAccumulator(T stateSetter){
        Preconditions.checkNotNull(stateSetter);

        aircraftstatesetter = stateSetter;
        oldmessages = new AirbornePositionMessage[]{null, null};
    }
    public T stateSetter(){
        return aircraftstatesetter;
    }

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
                    aircraftstatesetter.setPosition(position);
                }
                oldmessages[posm.parity()] = posm;
            }
            default -> System.out.println("Autre type de message");
        }
    }
}
