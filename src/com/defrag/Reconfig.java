package com.defrag;

import com.auxiliarygraph.NetworkState;
import com.auxiliarygraph.elements.Connection;
import com.auxiliarygraph.elements.LightPath;
import com.graph.elements.edge.EdgeElement;
import com.inputdata.InputParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sandeep on 17-Nov-15.
 */
public class Reconfig {

    public Reconfig(){
        reconfigLightpath();
    }

    
    ArrayList<Integer> maxSlotList;
    Set<String> nodenames;
    List<String> nodelist;
    // Set<VertexElement> nodeElementsSet;
    // List<VertexElement> nodeElementsList;
    
    // List<Integer> miniGridIds;




    public void  reconfigLightpath(){
        Map<Double, Connection> connectionTomap;
        int intialMinigridID;
        
        nodenames = InputParameters.getSetOfVertexIDSets();
        //   nodeElementsSet =InputParameters.getSetOfVertices();
        nodelist = new ArrayList<>(nodenames);

        int demand;
        // VertexElement ver1 =nodeElementsList.get(0), ver2=nodeElementsList.get(0);
        List<LightPath> lightpaths;
        //InputParameters.getIfConnectiongEdge(node,node1)
        for (String node : nodelist) {
            for (String node1 : nodelist) {
                if (!node.equals(node1)) {
                    lightpaths = NetworkState.getListOfLightPaths(InputParameters.getGraph().getVertex(node), InputParameters.getGraph().getVertex(node1));
                    for (LightPath lp : lightpaths) {
                        lp.releaseAllMiniGrids();
                        lp.removeAllMinigridIDs();
                    }
                }
            }

        }

// set minigrid for lightpaths
        int f=1;
        for (String node : nodelist) {
            for (String node1 : nodelist) {
                if (!node.equals(node1)) {
                    lightpaths= NetworkState.getListOfLightPaths(InputParameters.getGraph().getVertex(node), InputParameters.getGraph().getVertex(node1));
                    for (LightPath lp : lightpaths){
                        intialMinigridID = f; // make it from 1 to total number of slots
                       demand =0;
//                        connectionTomap= lp.getConnectionMap();
//                        //getPathElement().getTraversedEdges().get(i)..getConnectionMap();
//                        for (Map.Entry<Double,Connection> entry : connectionTomap.entrySet()) {
//                            demand += entry.getValue().getBw();
//                        }

                        lp.setMinigridIDs(intialMinigridID, demand);
                        lp.setAllMiniGrids();
                       // lp.reconfigureAllConnections(intialMinigridID, demand);
                        f = f+ demand;
                    }

                }

            }
        }

    }
}
//while (start <= end) {
//        check = false;
//        for (LightPath lp : listOfLPs)
//        if (lp.containsMiniGrid(start)) {
//        max=0;