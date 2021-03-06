package com.filemanager;

/**
 * Created by Fran on 4/29/2015.
 */
public class Counter {

    /** Counter used to count the number of blocked request for this distribution of port*/
    private int blockingCounter;
    /** Counter used to count the number of blocked requests due to Resource unavailability for this distribution of port*/
    private int resourceBlockingCounter;
    /** Counter used to count the number of blocked requests due to fragmentation for this distribution of port*/
    private int fragmentBlockingCounter;
    private int defragBlockingcounter;
    /** Counter used to count the number of processed requests for this distribution of port*/
    private int flowRequestCounter;
    /** TrafficClass distribution*/
    private int portType;

    /** Constructor class*/
    public Counter(int portType) {
        this.portType = portType;
        this.blockingCounter = 0;
        this.resourceBlockingCounter = 0;
        this. fragmentBlockingCounter =0;
        this.defragBlockingcounter =0;
        this.flowRequestCounter = 0;
    }

    /** Methods to increase the counters*/
    public void increaseBlockingCounter(){
        blockingCounter++;
    }

    public void increaseResourceBlockingCounter(){
        resourceBlockingCounter++;
    }

    public void increaseFragmentBlockingCounter(){
         fragmentBlockingCounter++;
    }

    public void increaseDefragBlockingCounter(){defragBlockingcounter++;}
    public void increaseFlowRequestCounter(){
        flowRequestCounter++;
    }

    /** Methods to reset the counters*/
    public void resetBlockingCounter(){
        blockingCounter = 0;
    }
    public void resetResourceBlockingCounter(){
        resourceBlockingCounter = 0;
    }
    public void resetFragmentBlockingCounter(){
        fragmentBlockingCounter = 0;
    }
    public void resetDefragmentBlockingCounter(){
        defragBlockingcounter = 0;
    }
    public void resetFlowRequestCounter(){
        flowRequestCounter = 0;
    }

    /** Getters*/
    public int getPortType() {
        return portType;
    }

    public int getBlockingCounter() {
        return blockingCounter;
    }
    public int getResourceBlockingCounter() {
        return resourceBlockingCounter;
    }
    public int getFragmentBlockingCounter() {
        return fragmentBlockingCounter;
    }
    public int getFlowRequestCounter() {
        return flowRequestCounter;
    }
    public int getDefragmentBlockingCounter(){return defragBlockingcounter;}

}
