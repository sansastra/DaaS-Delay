package com.simulator.event;

import com.auxiliarygraph.AuxiliaryGraph;
import com.defrag.Reconfig;
import com.defrag.ReconfigureLink;
import com.filemanager.Results;
import com.inputdata.elements.TrafficClass;
import com.launcher.SimulatorParameters;
import com.rng.Distribution;
import com.rng.distribution.ExponentialDistribution;
import com.simulator.Scheduler;
import com.simulator.elements.Generator;
import com.simulator.elements.TrafficFlow;
import com.simulator.Event;
import com.simulator.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a new request event in the simulator
 *
 * @author Fran
 */
public class CircuitRequestEvent extends Event {

    /**
     * Generator responsible of the event
     */
    private Generator generator;
    /**
     * TrafficClass that generates the request
     */
    private TrafficClass trafficClass;

    private static final Logger log = LoggerFactory.getLogger(CircuitRequestEvent.class);

    /**
     * Constructor class
     */
    public CircuitRequestEvent(Entity entity, Generator generator,
                               TrafficClass trafficClass) {
        super(entity);
        this.generator = generator;
        this.trafficClass = trafficClass;

    }

    @Override
    public void occur() {
        int counter = 0;
        double holdingTime;
        // double defragEndTime = SimulatorParameters.getSimulationTime();
        boolean isUnKnown = true;  //generator.getRandomUnknown(trafficClass.getType());

        holdingTime = trafficClass.getHoldingTimeDistribution().execute();

        /** If it is unknown, get the mean holding time*/
//        if (isUnKnown)
//            holdingTime = trafficClass.getMeanHoldingTime();

        /** Get a random destination following a uniform distribution */
        TrafficFlow selectedFlow = generator.getRandomFlow(trafficClass.getType());

        int numberOfMiniGrids = (int) (trafficClass.getBw());
        /** Create a new Auxiliary Graph*/
        AuxiliaryGraph auxiliaryGraph = new AuxiliaryGraph(generator.getVertex().getVertexID(), selectedFlow.getDstNode().getVertexID(), numberOfMiniGrids, Scheduler.currentTime(), holdingTime, isUnKnown);
        if (!SimulatorParameters.getDefrag())
        /**If path is found, then add release event*/
            if (auxiliaryGraph.runShortestPathAlgorithm(selectedFlow.getListOfPaths())) {
                Event event = new CircuitReleaseEvent(new Entity(holdingTime), generator, selectedFlow, auxiliaryGraph.getNewConnection());
                Scheduler.schedule(event, holdingTime);
                log.debug("Added release event: " + generator.getVertex().getVertexID() + "-" + selectedFlow.getDstNode().getVertexID());
                //            Results.writeHoldingTime(generator,selectedFlow,trafficClass.getType(),isUnKnown,holdingTime);
            }
            else { /**if not, increase blocking counter*/
                /** Apply Defragmentation */
                // if blocked due to fragmentation
                if (!auxiliaryGraph.getIfblockedDueToResourceUnavailability()) {
                    ReconfigureLink reconfigureLink = new ReconfigureLink();
                    int fragment = reconfigureLink.reconfigLightpaths();
                    SimulatorParameters.setDefrag(true,fragment);
                    double defragTime = SimulatorParameters.getDefragTime();
                    Event event = new DefragEvent(new Entity(defragTime));
                    Scheduler.schedule(event, defragTime);
                    AuxiliaryGraph auxiliaryGraph1 = new AuxiliaryGraph(generator.getVertex().getVertexID(), selectedFlow.getDstNode().getVertexID(), numberOfMiniGrids, Scheduler.currentTime(), holdingTime, isUnKnown);
                    if(auxiliaryGraph1.runShortestPathAlgorithm(selectedFlow.getListOfPaths())) {
                        Event event1 = new CircuitReleaseEvent(new Entity(holdingTime+ defragTime + 1E-6), generator, selectedFlow, auxiliaryGraph1.getNewConnection());
                        Scheduler.schedule(event1, holdingTime + defragTime + 1E-6);
                        log.debug("Added release event: " + generator.getVertex().getVertexID() + "-" + selectedFlow.getDstNode().getVertexID());
                    }
//                    else{
//                        log.error("reconfiguration of link is wrong");
//                    }
                } else {
                    selectedFlow.increaseBlockingCounter(trafficClass.getType(), true);
                    log.debug("Connection is blocked due to resource unavailability");
                    //increaseCounters(selectedFlow, this.generator, this.trafficClass);
                }
            }
        else {
            selectedFlow.increaseDefragBlockingcounter(trafficClass.getType());
            log.debug("Connection is blocked due to defragmentation");
            //increaseCounters(selectedFlow,this.generator,this.trafficClass);
        }

        increaseCounters(selectedFlow,this.generator,this.trafficClass);
        /** Add a new request event */
        TrafficClass nextTrafficClass = generator.getRandomPort();
        double nextInterArrivalTime = generator.getRequestDistribution().execute();
        Event event = new CircuitRequestEvent(new Entity(nextInterArrivalTime), generator, nextTrafficClass);
        Scheduler.schedule(event, nextInterArrivalTime);
        log.debug("Added request event: " + generator.getVertex().getVertexID() + "-" + selectedFlow.getDstNode().getVertexID());
//        Results.writeInterArrivalTime(generator, selectedFlow,trafficClass.getType(),nextInterArrivalTime);
    }

    public void increaseCounters(TrafficFlow selectedFlow, Generator generator, TrafficClass trafficClass){
        /** Increase request counter for this flow */
        selectedFlow.increaseFlowRequestCounter(trafficClass.getType());

        /*********************** Results *************************/
        Results.writeBlockingResults(generator, selectedFlow);
        Results.writeLinkUtilizationResults();
        Results.increaseRequestCounter();
    }

}
