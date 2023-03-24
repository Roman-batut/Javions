package ch.epfl.javions.adsb;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {

    private T aircraftstatesetter;

    public AircraftStateAccumulator(T stateSetter){
        if(stateSetter == null){
            throw new NullPointerException();
        }
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
/*
 *	Author:      Br4v0r
 *	Date:
 */
