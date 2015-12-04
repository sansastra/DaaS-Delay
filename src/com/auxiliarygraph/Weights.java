package com.auxiliarygraph;

import com.auxiliarygraph.elements.LightPath;

/**
 * Created by Fran on 7/3/2015.
 */
public class Weights {

    private static int POLICY;
    private static double transponderEdgeCost;
    private static double seFactor;
    private static double lpeFactor1;
    private static double lpeFactor2;
    private static double timeFactor;
    private static double fragmentFactor;

    public Weights(int policy) {

        POLICY = policy;

        switch (POLICY) {
            /** First Fit */
            case 1:
                lpeFactor1 = 0;
                lpeFactor2 = 1;
                break;
            /** RandomFit */
            case 2:
                lpeFactor1 = 0;
                lpeFactor2 = 1;
                break;

        }
    }

    public static double getSpectrumEdgeCost(String edgeID, int spectrumLayerIndex, int hopsOfThePath, int bwWithGB, double ht) {
        if (POLICY == 1)
            return spectrumLayerIndex ;

        return 1;

    }

    public static double getLightPathEdgeCost(LightPath lp) {

        if (POLICY == 1)
            return 1;

        return lpeFactor1 + lp.getPathElement().getTraversedEdges().size() * lpeFactor2;
    }

    public static double getTransponderEdgeCost() {
        return transponderEdgeCost * 2;
    }

}
