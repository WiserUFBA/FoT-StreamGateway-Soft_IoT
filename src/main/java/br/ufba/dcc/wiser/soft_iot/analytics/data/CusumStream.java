/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.data;

/**
 *
 * @author brenno
 */
public class CusumStream<T> {
    
    private final static double DEFAULT_MAGNITUDE = 0.05;
    private final static double DEFAULT_THRESHOLD = 3;
    private final static long DEFAULT_READY_AFTER = 50;

    private double cusumPrev = 0;
    private double cusum;
    private double magnitude;
    private double threshold;
    private double magnitudeMultiplier;
    private double thresholdMultiplier;
    private long   readyAfter;

    
    private double runningMean      = 0.0;
    private double runningVariance  = 0.0;
    
    
    
    private long qtData = 0;
    private double mean;
    
    
    
    public void newData(Double data){
        
        ++qtData;

        // Instead of providing the target mean as a parameter as 
        // we would in an offline test, we calculate it as we go to 
        // create a target of normality.
        
        double newMean = runningMean + (data - runningMean) / qtData;
        runningVariance += (xi - runningMean)*(xi - newMean);
        runningMean = newMean;
        double std = Math.sqrt(runningVariance);

        magnitude = magnitudeMultiplier * std;
        threshold = thresholdMultiplier * std;

        cusum = Math.max(0, cusumPrev +(xi - runningMean - magnitude));

        if(isReady()) {
            this.change = cusum > threshold;
        }

        cusumPrev = cusum;
        
    }
    
    
}
