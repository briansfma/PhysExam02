package org.centum.android.ecg.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Phani on 11/25/2014.
 */

/* This class deals with the displaying of ECG signals. each GraphSurfaceView handles 1 graph.
    See GraphFragment.java for how this class is used overall.
*/
public class GraphSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder holder;
    private Thread updateThread;
    //amount of samples to display on the graph. i.e. if samp rate is 250 sps, and numPoints = 500, then show 2 seconds of data
    private int numPoints = 750;
    //Ringbuffer holds the samples which are used for displaying.
    private RingBuffer buffer = new RingBuffer(numPoints);

    private int lineColor = Color.BLACK;
    private int backgroundColor = Color.WHITE;
    private int gridColor = Color.GRAY;
    private float lineThickness = 5;

    private Paint backgroundPaint = new Paint();
    private Paint foregroundPaint = new Paint();
    private Paint borderPaint = new Paint();
    private Paint gridPaint = new Paint();

    public GraphSurfaceView(Context context) {
        super(context);
        init();
    }

    public GraphSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    //initialize the background.
    private void init() {
        holder = getHolder();
        holder.addCallback(this);

        updateBackgroundPaint();
        updateForegroundPaint();

        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);

        gridPaint.setColor(gridColor);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);
    }
    /*Function that is executed to draw the graph. This is recalled for every time the LCD display is
        refreshed.
    */
    @Override
    public void draw(Canvas canvas) {
        float height = getHeight();
        float width = getWidth();
        //draw the white background
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        //draw the gridlines
        for (int x = 0; x < width; x += 100) {
            canvas.drawLine(x, 0, x, getHeight(), gridPaint);
        }
        for (int y = 0; y < height; y += 50) {
            canvas.drawLine(0, y, getWidth(), y, gridPaint);
        }
        //get the min and maxes of the x and y axis data
        int[] minMaxs = buffer.getMinMax();

        float maxDY = (minMaxs[3] - minMaxs[2]); //max difference for y axis
        float maxDX = (minMaxs[1] - minMaxs[0]); //max difference for x axis

        float lastY = height - (height * (buffer.get(0)[1] - minMaxs[2]))/maxDY;
        float lastX = width * (buffer.get(0)[0] - minMaxs[0]) / maxDX;
        //draw the points of the ECG signal.
        for (int i = 1; i < buffer.getNumPoints(); i++) {
            int p[] = buffer.get(i);
            //NOTE: the y-axis is inverted on the surface view.
            //float y = height * (p[1] - minMaxs[2]) / maxDY
            float y = height - (height * (p[1] - minMaxs[2]))/maxDY; //height * (newYValue - minY)/maxDY
            float x = width * (p[0] - minMaxs[0]) / maxDX; //width * (newXValue - minX)/maxDX
            canvas.drawLine(lastX, lastY, x, y, foregroundPaint);
            lastY = y;
            lastX = x;
        }
        canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1, borderPaint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (updateThread == null || !updateThread.isAlive()) {
            updateThread = new Thread(this);
            updateThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        updateThread.interrupt();
        updateThread = null;
    }

    @Override
    public void run() {
        Canvas canvas = null;
        while (!Thread.interrupted()) {
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    synchronized (holder) {
                        draw(canvas);
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }

    public int getNumPoints() {
        return numPoints;
    }

    public void setNumPoints(int maxPoints) {
        this.numPoints = maxPoints;
        RingBuffer newBuffer = new RingBuffer(maxPoints);
        for (int i = 0; i < buffer.getNumPoints(); i++) {
            int p[] = buffer.get(i);
            newBuffer.add(p[0], p[1]);
        }
        buffer = newBuffer;
    }

    private void updateBackgroundPaint() {
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    private void updateForegroundPaint() {
        foregroundPaint.setColor(lineColor);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(lineThickness);
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        updateForegroundPaint();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        updateBackgroundPaint();
    }

    public float getLineThickness() {
        return lineThickness;
    }

    public void setLineThickness(float lineThickness) {
        this.lineThickness = lineThickness;
        updateForegroundPaint();
    }

    public void add(int x, int y) {
        buffer.add(x, y);
    }
}
