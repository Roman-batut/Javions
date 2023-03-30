package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

class AircraftState implements AircraftStateSetter {

    @Override
    public void setCallSign(CallSign callSign) {
        System.out.println("indicatif : " + callSign);
    }

    @Override
    public void setPosition(GeoPos position) {
        System.out.println("position : " + position);
    }

    public void setLastMessageTimeStampNs(long timeStampNs){}

    /**
     * Changes Aircraft category to the given value
     *
     * @param category
     */
    public void setCategory(int category){
//        System.out.println("category" + category);
    }

    /**
     * Changes Aircraft altitude to the given value
     *
     * @param altitude
     */
    public void setAltitude(double altitude){

//        System.out.println("altitude : "  +altitude);
    }

    /**
     * Changes Aircraft velocity to the given value
     *
     * @param velocity
     */
    public void setVelocity(double velocity){
//        System.out.println("Vitessse : " + velocity);
    }

    /**
     * Changes Aircraft track or heading to the given value
     *
     * @param trackOrHeading
     */
    public void setTrackOrHeading(double trackOrHeading){
//        System.out.println("Track or heading" + trackOrHeading);
    }
}
