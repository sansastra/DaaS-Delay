package com.defrag;

import com.auxiliarygraph.NetworkState;
import com.auxiliarygraph.elements.Connection;
import com.auxiliarygraph.elements.LightPath;
import com.inputdata.InputParameters;

import java.util.*;

/**
 * Created by Sandeep on 30-Nov-15.
 */
public class ReconfigureLink {
    Map<Integer, LightPath> lightpathMap;
    //  Map<Integer, Integer> linkFragmentationMap;
    List<LightPath> lightpaths;
    List<LightPath> sortedLightpathList;
    public ReconfigureLink(){
        lightpathMap = new HashMap<>();
        lightpaths = new ArrayList<>();
        sortedLightpathList = new ArrayList<>();
        // linkFragmentationMap = new HashMap<>();
    }

    public int  reconfigLightpaths(){
        Map<Double, Connection> connectionTomap;
        int intialMinigridID;
        int demand;

        lightpaths = NetworkState.getListOfLightPaths();



        for (LightPath lp : lightpaths) {
            lightpathMap.put(lp.getFirstMiniGrid(), lp);
            // linkFragmentationMap.put(lp.getFirstMiniGrid(),lp.getLPbandwidth());
        }
        // sort the lightpath in decreasing order of their first minigrid
        sortingLightpaths();

        // get the link fragmentation measure
        int count=sortedLightpathList.get(0).getFirstMiniGrid()-1;
        for (int i= 1; i<sortedLightpathList.size(); i++) {
            count += sortedLightpathList.get(i).getFirstMiniGrid()-(sortedLightpathList.get(i-1).getFirstMiniGrid()+sortedLightpathList.get(i-1).getLPbandwidth())+1;
        }

        for (LightPath lp : lightpaths) {
            lp.releaseAllMiniGrids();
            lp.removeAllMinigridIDs();
        }

        // set minigrid for lightpaths

        int f=1;
        for (int i=0; i< sortedLightpathList.size(); i++) {
            LightPath lp= sortedLightpathList.get(i);
            intialMinigridID = f; // make it from 1 to total number of slots
            // demand = lp.getLPbandwidth(); // will not work since lp minigrids are released before
            demand =0;
            connectionTomap= lp.getConnectionMap();
            for (Map.Entry<Double,Connection> entry : connectionTomap.entrySet()) {
                demand += entry.getValue().getBw();
            }
            lp.setMinigridIDs(intialMinigridID, demand);
            lp.setAllMiniGrids();
            // lp.reconfigureAllConnections(intialMinigridID, demand);
            f = f+ demand;
        }

        return count;
    }


    // sorted in increasing order or 1st arguement
    public void sortingLightpaths(){
        Map<Integer,LightPath> treeMap = new TreeMap<Integer, LightPath>(new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }

        });
        treeMap.putAll(lightpathMap);
        for (Map.Entry<Integer, LightPath> entry : treeMap.entrySet()){
            sortedLightpathList.add(entry.getValue());
        }
        Collections.reverse(sortedLightpathList);
    }
}


