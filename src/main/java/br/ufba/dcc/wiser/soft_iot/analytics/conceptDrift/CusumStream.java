/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.conceptDrift;

import java.util.LinkedList;
import java.util.List;

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
    
    private List<Double> listData;
    
    
    public CusumStream(double magnitude, double threshold){
        this.magnitudeMultiplier = magnitude;
        this.thresholdMultiplier = threshold;      
        this.listData = new LinkedList<>();
    }

    public List<Double> getListData() {
        return listData;
    }

    public void setListData(List<Double> listData) {
        this.listData = listData;
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

        if(!isChange()){
           
           this.change = cusum > threshold;
           System.out.println("Mudanças: " + this.change);        
           
        }
        
         if(isChange()){
          
            System.out.println("Dados add List: " + data);
            this.listData.add(data);
            
        } 
        
        System.out.println("-----------------------------");
        System.out.println("Dados de entrada: " + data);
        
        System.out.println("newMean: " + newMean);
        System.out.println("runningVariance: " + data);
        System.out.println("std: " + std);
        System.out.println("magnitude: " + magnitude);
        
        System.out.println("Mudança: " + this.change);
        System.out.println("Threshold: " + this.threshold);
        System.out.println("CUSUM: " + this.cusum);
        System.out.println("CUSUM PREV: " + this.cusumPrev);
        System.out.println("runningMean: " + this.runningMean);
        System.out.println("runningVariance: " + this.runningVariance);
        System.out.println("qtData: " + this.qtData);
        System.out.println("magnitudeMultiplier: " + this.magnitudeMultiplier);
        System.out.println("thresholdMultiplier: " + this.thresholdMultiplier);
        System.out.println("------------------------------"); 
        
        cusumPrev = cusum;
        
    }
    
     public boolean isChange() {
        return change;
    }
     
     
     public void reset() {
        System.out.println("-----------------------------");
        System.out.println("Reset");
        System.out.println("-----------------------------");
        this.cusum = 0;
        this.cusumPrev = 0;
        this.runningMean = 0;
        this.qtData = 0;
        this.listData.clear();
        this.change = false;
    }

    
}
