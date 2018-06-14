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
    
    private long qtData;
    private double mean;
    
    
    
    public void newData(T data){
        /*
        ++observationCount;

        // Instead of providing the target mean as a parameter as 
        // we would in an offline test, we calculate it as we go to 
        // create a target of normality.
        
        double newMean = runningMean + (xi - runningMean) / observationCount;
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
        */
    }
    
    
}
