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
public class CusumStream {
    
    private final static double DEFAULT_MAGNITUDE = 0.05;
    private final static double DEFAULT_THRESHOLD = 3;
    

    private double cusumPrev = 0;
    private double cusum;
    private double magnitude;
    private double threshold;
    private double magnitudeMultiplier;
    private double thresholdMultiplier;
    

    
    private double runningMean      = 0.0;
    private double runningVariance  = 0.0;
    
    private boolean change = false;
    
    
    private long qtData = 0;
    
    public CusumStream(double magnitude, double threshold){
        this.magnitudeMultiplier = magnitude;
        this.thresholdMultiplier = threshold;      
    }
    
    
    
    public void newData(Double data){
        
        ++qtData;

        // Instead of providing the target mean as a parameter as 
        // we would in an offline test, we calculate it as we go to 
        // create a target of normality.
        
        double newMean = runningMean + (data - runningMean) / qtData;
        runningVariance += (data - runningMean)* (data - newMean);
        runningMean = newMean;
        double std = Math.sqrt(runningVariance);

        magnitude = magnitudeMultiplier * std;
        threshold = thresholdMultiplier * std;

        cusum = Math.max(0, cusumPrev +(data - runningMean - magnitude));

       
        this.change = cusum > threshold;
       

        cusumPrev = cusum;
        
    }
    
     public boolean isChange() {
        return change;
    }
     
     
     public void reset() {
        this.cusum = 0;
        this.cusumPrev = 0;
        this.runningMean = 0;
        this.qtData = 0;
    }

    
}
