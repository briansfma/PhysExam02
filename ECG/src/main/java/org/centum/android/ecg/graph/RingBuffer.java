package org.centum.android.ecg.graph;

/**
 * Created by Phani on 11/25/2014.
 */
/*
    This class handles the storage of ECG data that is used for plotting. See GraphSurfaceView.java for its
     implementation
 */
public class RingBuffer {

    private int bufferY[];
    private int bufferX[];
    private int numPoints = 0;
    private int head = 0;

    public RingBuffer(int size) {
        bufferY = new int[size];
        bufferX = new int[size];
    }

    public void add(int x, int y) {
        bufferY[head] = y;
        bufferX[head] = x;
        head = (head + 1) % bufferY.length;
        numPoints = Math.min(bufferY.length, numPoints + 1);
    }

    public int[] get(int i) {
        i = (head + i) % bufferX.length;
        return new int[]{bufferX[i], bufferY[i]};
    }

    public int getNumPoints() {
        return numPoints;
    }

    public int getCapacity() {
        return bufferY.length;
    }

    public int[] getMinMax() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int[] p;
        for (int i = 0; i < numPoints; i++) {
            p = get(i);
            minX = Math.min(minX, p[0]);
            maxX = Math.max(maxX, p[0]);
            minY = Math.min(minY, p[1]);
            maxY = Math.max(maxY, p[1]);
        }
        return new int[]{minX, maxX, minY, maxY};
    }

}
