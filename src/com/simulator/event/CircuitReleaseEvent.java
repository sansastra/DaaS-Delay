package com.simulator.event;

import com.auxiliarygraph.NetworkState;
import com.auxiliarygraph.elements.Connection;
import com.auxiliarygraph.elements.LightPath;
import com.launcher.SimulatorParameters;
import com.simulator.Scheduler;
import com.simulator.elements.Generator;
import com.simulator.elements.TrafficFlow;
import com.simulator.Entity;
import com.simulator.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a new release event in the simulator
 *
 * @author Fran
 */
public class CircuitReleaseEvent extends Event {

    /**
     * Generator responsible of the event
     */
    private Generator generator;
    /**
     * Flow that remove its connection
     */
    private TrafficFlow trafficFlow;
    /**
     * Connection to remove
     */
    private Connection connectionToRelease;


    private static final Logger log = LoggerFactory.getLogger(CircuitReleaseEvent.class);

    /**
     * Constructor class
     */
    public CircuitReleaseEvent(Entity entity, Generator generator, TrafficFlow trafficFlow, Connection connectionToRelease) {
        super(entity);
        this.generator = generator;
        this.trafficFlow = trafficFlow;
        this.connectionToRelease = connectionToRelease;

    }

    @Override
    public void occur() {

        if(!SimulatorParameters.getDefrag())
            /** Look for the connection and remove it*/
            for (int i = 0; i < NetworkState.getListOfLightPaths().size(); i++) {
                LightPath lp = NetworkState.getListOfLightPaths().get(i);
                if (lp.getConnectionMap().containsKey(connectionToRelease.getStartingTime())) {
                    lp.removeConnectionAndCompress(connectionToRelease);
                    if (lp.getConnectionMap().isEmpty()) {
                        /**remove guard bands and delete light path*/
                        lp.releaseAllMiniGrids();
                        NetworkState.getListOfLightPaths().remove(i);
                        i--;
                    }
                    log.debug("Connection released: " + generator.getVertex().getVertexID() + "-" + trafficFlow.getDstNode().getVertexID());
                }
            }
        else{
            Event event = new CircuitReleaseEvent(new Entity(SimulatorParameters.getDefragTime()), generator, trafficFlow, connectionToRelease);
            Scheduler.schedule(event, SimulatorParameters.getDefragTime());
        }
    }

}
