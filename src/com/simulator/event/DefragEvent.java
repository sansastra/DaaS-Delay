package com.simulator.event;

import com.launcher.SimulatorParameters;
import com.simulator.Entity;
import com.simulator.Event;

/**
 * Created by Sandeep on 03-Dec-15.
 */
public class DefragEvent extends Event {

    public DefragEvent(Entity entity){
        super(entity);
        }

    public void occur(){
        SimulatorParameters.setDefrag(false,1);
    }

}
