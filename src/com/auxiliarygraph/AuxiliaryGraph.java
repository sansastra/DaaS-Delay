package com.auxiliarygraph;

import com.auxiliarygraph.edges.LightPathEdge;
import com.auxiliarygraph.edges.SpectrumEdge;
import com.auxiliarygraph.elements.Connection;
import com.auxiliarygraph.elements.FiberLink;
import com.auxiliarygraph.elements.LightPath;
import com.auxiliarygraph.elements.Path;
import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.path.PathElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class AuxiliaryGraph {

    private List<SpectrumEdge> listOfSE;
    private final int GUARD_BAND = NetworkState.getNumOfMiniGridsPerGB();
    private int bwWithGB;
    private int bw;
    private Connection newConnection;
    private double currentTime;
    private double ht;
    private boolean feature;
    private boolean blockedDueToResource;
    private static final Logger log = LoggerFactory.getLogger(AuxiliaryGraph.class);

    /**
     * Constructor class
     */
    public AuxiliaryGraph(String src, String dst, int b, double currentTime, double ht, boolean feature) {
        listOfSE = new ArrayList<>();
        this.bw = b;
        this.bwWithGB = bw + GUARD_BAND;
        this.currentTime = currentTime;
        this.ht = ht;
        this.feature = feature;



        /** Search for candidate paths between S and D*/
        List<Path> listOfCandidatePaths = NetworkState.getListOfPaths(src, dst);
        int count;
        blockedDueToResource = false;
        /** For each candidate path, create new spectrum edges*/
        for (Path p : listOfCandidatePaths) {
            count = 0;
            for (EdgeElement e : p.getPathElement().getTraversedEdges()) {
                List<Integer> freeMiniGrids = NetworkState.getFiberLink(e.getEdgeID()).getFreeMiniGrids(bwWithGB);
                if (NetworkState.getFiberLink(e.getEdgeID()).getNumberOfFreeMiniGrids() < bwWithGB)
                    count++;
                for (Integer i : freeMiniGrids)
                    listOfSE.add(new SpectrumEdge(e, i, p.getPathElement().getTraversedEdges().size(), bwWithGB, ht));

            }
            if(count != 0) // true only for single path
                blockedDueToResource =true; // the request possibly be blocked due to fragmentation and not due to unavailability of resources

        }


    }

    public boolean runShortestPathAlgorithm(List<Path> listOfCandidatePaths) {

        double cost;
        double minCost = Double.MAX_VALUE;
        Path selectedPath = null;
        int selectedMiniGrid = 0;

        /** For each possible path, calculate the costs*/
        for (Path path : listOfCandidatePaths) {
            int numOfMiniGrids = NetworkState.getTotalNumerOfSlots();
            for (int i = 1; i <= numOfMiniGrids; i++) {
                cost = calculateTheCostForMiniGrid(path, i);
                if (cost < minCost) {
                    minCost = cost;
                    selectedPath = path;
                    selectedMiniGrid = i;
                }
            }
        }

        if (minCost != Double.MAX_VALUE) {
            setConnection(selectedPath, selectedMiniGrid);
            return true;
        } else
            return false;
    }

    public double calculateTheCostForMiniGrid(Path p, int miniGrid) {

        double layerCost=0;
        SpectrumEdge se;
        int count =0;
        /**Add spectrum edges costs*/
        for (EdgeElement e : p.getPathElement().getTraversedEdges())
            if((se = getSpectrumEdge(e, miniGrid)) != null) {
                layerCost += se.getCost();
                count++;
            }
         if (!(count == p.getPathElement().getTraversedEdges().size()))
             layerCost = Double.MAX_VALUE; // minigrid is not available on all the path elements
        return layerCost;
    }


    public void setConnection(Path path, int miniGrid) {

        Set<LightPath> newLightPaths = new HashSet<>();
        List<SpectrumEdge> selectedSpectrumEdges = new ArrayList<>();

        newConnection = new Connection(currentTime, ht, bw, feature, miniGrid);


        SpectrumEdge se;
        for (EdgeElement e : path.getPathElement().getTraversedEdges())
            if ((se = getSpectrumEdge(e, miniGrid)) != null)
                selectedSpectrumEdges.add(se);


        /** If the path contains spectrum edges then establish new lightpath **/
        if (!selectedSpectrumEdges.isEmpty()) {
            List<VertexElement> vertexes = new ArrayList<>();
            if (selectedSpectrumEdges.size() == 1) {
                vertexes.add(selectedSpectrumEdges.get(0).getEdgeElement().getSourceVertex());
                vertexes.add(selectedSpectrumEdges.get(0).getEdgeElement().getDestinationVertex());
                newLightPaths.add(new LightPath(NetworkState.getPathElement(vertexes), miniGrid, bwWithGB, bw, newConnection));

            } else {
                vertexes.add(selectedSpectrumEdges.get(0).getEdgeElement().getSourceVertex());
                for (int i = 1; i < selectedSpectrumEdges.size(); i++) {
                    if (selectedSpectrumEdges.get(i).getEdgeElement().getSourceVertex().equals(selectedSpectrumEdges.get(i - 1).getEdgeElement().getDestinationVertex()))
                        vertexes.add(selectedSpectrumEdges.get(i).getEdgeElement().getSourceVertex());
                }
                        vertexes.add(selectedSpectrumEdges.get(selectedSpectrumEdges.size() - 1).getEdgeElement().getDestinationVertex());
                        newLightPaths.add(new LightPath(NetworkState.getPathElement(vertexes), miniGrid, bwWithGB, bw, newConnection));


            }
        }

        NetworkState.getListOfLightPaths().addAll(newLightPaths);
    }


    public SpectrumEdge getSpectrumEdge(EdgeElement e, int spectrumLayerIndex) {
        for (SpectrumEdge se : listOfSE)
            if (se.getEdgeElement().equals(e) && se.getSpectrumLayerIndex() == spectrumLayerIndex)
                return se;
        return null;
    }


    public boolean comparePaths(PathElement mainPath, PathElement pathToCompare) {

        for (EdgeElement e : pathToCompare.getTraversedEdges()) {
            if (!mainPath.containsEdge(e))
                return false;
        }
        return true;
    }

    public Connection getNewConnection() {
        return newConnection;
    }
    public boolean getIfblockedDueToResourceUnavailability(){return blockedDueToResource;}

    /**
     * Experimental
     */

    public double applyCorrectorFactor(Path p, int miniGrid) {

        double correctorFactor = 0;
        for (FiberLink fl : NetworkState.getNeighborsFiberLinks(p.getPathElement().getSource(), p.getPathElement().getDestination()))
            for (int i = miniGrid; i < miniGrid + bwWithGB; i++)
                if (fl.getMiniGrid(miniGrid) != 0)
                    correctorFactor += 1;

        return correctorFactor;
    }
}
