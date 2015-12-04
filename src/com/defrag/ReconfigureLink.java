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
    List<LightPath> lightpaths;
    List<LightPath> tempLightpathList;
        public ReconfigureLink(){
            lightpathMap = new HashMap<>();
            lightpaths = new ArrayList<>();
            tempLightpathList = new ArrayList<>();
            reconfigLightpath();
        }

        public void  reconfigLightpath(){
            Map<Double, Connection> connectionTomap;
            int intialMinigridID;
            int demand;

            lightpaths = NetworkState.getListOfLightPaths();


            boolean check =false;
            for (LightPath lp : lightpaths) {
                lightpathMap.put(lp.getFirstMiniGrid(), lp);
            }
            // sort the lightpath in decreasing order of their first minigrid
            sorting();

            for (LightPath lp : lightpaths) {
                lp.releaseAllMiniGrids();
                lp.removeAllMinigridIDs();
            }

            // set minigrid for lightpaths

            int f=1;
            for (int i= tempLightpathList.size(); i>0; i--) {
                LightPath lp= tempLightpathList.get(i-1);
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

        }

    public List sorting(){
        Map<Integer,LightPath> treeMap = new TreeMap<Integer, LightPath>(new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }

        });
        treeMap.putAll(lightpathMap);
        for (Map.Entry<Integer, LightPath> entry : treeMap.entrySet()){
            tempLightpathList.add(entry.getValue());
        }

        return tempLightpathList;
    }
}


