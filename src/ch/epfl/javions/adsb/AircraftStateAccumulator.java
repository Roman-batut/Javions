package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {

    private T aircraftstatesetter;

    public AircraftStateAccumulator(T stateSetter){
        //if(stateSetter == null){
        //    throw new NullPointerException();
        //}
        Preconditions.checkNotNull(stateSetter);

        aircraftstatesetter = stateSetter;
    }
    public T stateSetter(){
        return aircraftstatesetter;
    }

    public void update(Message message){
        aircraftstatesetter.setLastMessageTimeStampNs(message.timeStampNs());
//        #TODO tout le reste hehe
    }
}
