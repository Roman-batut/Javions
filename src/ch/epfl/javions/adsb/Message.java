package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 *  Interface representing a message
 *  @author Roman Batut (356158)
 *  @author Guillaume Chevallier (360709)
 */
public interface Message {

   /**
    *  Returns the time stamp of the message in nanoseconds
    */
   long timeStampNs();

   /**
    *  Returns the ICAO address of the aircraft
    */
   IcaoAddress icaoAddress();
}
