package com.auxiliarygraph.elements;

import com.auxiliarygraph.AuxiliaryGraph;
import com.auxiliarygraph.NetworkState;
import com.graph.elements.edge.EdgeElement;
import com.launcher.Launcher;
import com.launcher.SimulatorParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Fran on 6/11/2015.
 */
public class FiberLink {

    /**
     * Map<Integer, Integer> miniGrids
     * id, 0 ==> free
     * id, 1 ==> used
     * id, 2 ==> guard band
     * id, 3 ==> reserved
     */
    private Map<Integer, Integer> miniGrids;
    private EdgeElement edgeElement;
    private int totalNumberOfMiniGrids;

    private static final Logger log = LoggerFactory.getLogger(FiberLink.class);

    public FiberLink( int totalSlots, EdgeElement edgeElement) {
        this.edgeElement = edgeElement;
        miniGrids = new HashMap<>();
        totalNumberOfMiniGrids = totalSlots;
        for (int i = 1; i <= totalNumberOfMiniGrids; i++) {
            miniGrids.put(i, 0);
        }
    }
// get all initial minigrids that can hold the demand
    public List<Integer> getFreeMiniGrids(int n) {

        List<Integer> freeMiniGrids = new ArrayList<>();
        List<Integer> tempfreeMiniGrids = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : miniGrids.entrySet()) {
            if (entry.getValue() == 0)
                freeMiniGrids.add(entry.getKey());
        }

        if (n > 1) {
            int count;
            for (int i = 0; i < freeMiniGrids.size() -n + 1; i++) {
                count = 1;
                for (int j = i; j < i + n - 1; j++) {
                    if (freeMiniGrids.get(j) == freeMiniGrids.get(j + 1) - 1)
                        count++;
                    else
                        break;

                }
                if (count == n)
                    tempfreeMiniGrids.add(freeMiniGrids.get(i));
            }

            freeMiniGrids =tempfreeMiniGrids;

        }

        return freeMiniGrids;
    }

    public boolean areNextMiniGridsAvailable(int startingPoint, int additionalMiniGrids) {

        for (int i = startingPoint; i < startingPoint + additionalMiniGrids; i++) {
            if (!miniGrids.containsKey(i))
                return false;
            if (miniGrids.get(i) != 0)
                return false;
        }
        return true;
    }

    public boolean arePreviousMiniGridsAvailable(int startingPoint, int additionalMiniGrids) {

        for (int i = startingPoint; i > startingPoint - additionalMiniGrids; i--) {
            if (!miniGrids.containsKey(i))
                return false;
            if (miniGrids.get(i) != 0)
                return false;
        }
        return true;
    }

    public double getUtilization() {

        int usedMiniGrids = 0;

        for (Map.Entry<Integer, Integer> entry : miniGrids.entrySet())
            if (entry.getValue() == 1 || entry.getValue() == 2)
                usedMiniGrids++;

        return (double) usedMiniGrids / miniGrids.size();
    }

    public double getNetUtilization() {

        int usedMiniGrids = 0;

        for (Map.Entry<Integer, Integer> entry : miniGrids.entrySet())
            if (entry.getValue() == 1)
                usedMiniGrids++;

        return (double) usedMiniGrids / miniGrids.size();
    }

    public void setFreeMiniGrid(int id) {
//        if (miniGrids.get(id) == 0)
//            log.error("BUG: setting free an already free Mini-Grid");
        miniGrids.replace(id, miniGrids.get(id), 0);
    }

    public void setUsedMiniGrid(int id) {
//        if (miniGrids.get(id) == 1)
//            log.error("BUG: setting as used an already used Mini-Grid");
        miniGrids.replace(id, miniGrids.get(id), 1);
    }

    public int getMiniGrid(int index) {
        return miniGrids.get(index);
    }

    public void setGuardBandMiniGrid(int id) {
//        if (miniGrids.get(id) == 1)
//            log.error("BUG: setting as guard band an already used Mini-Grid");
        miniGrids.replace(id, miniGrids.get(id), 2);
    }

    public int getNumberOfMiniGridsUsed() {
        int usedMiniGrids = 0;

        for (Map.Entry<Integer, Integer> entry : miniGrids.entrySet())
            if (entry.getValue() == 1 || entry.getValue() == 2)
                usedMiniGrids++;

        return usedMiniGrids;
    }
    public int getNumberOfFreeMiniGrids() {return totalNumberOfMiniGrids - getNumberOfMiniGridsUsed();}
    public void setReservedMiniGrid(int id) {
        miniGrids.replace(id, miniGrids.get(id), 3);
    }

    public int getTotalNumberOfMiniGrids() {
        return totalNumberOfMiniGrids;
    }

    public EdgeElement getEdgeElement() {
        return edgeElement;
    }
}
