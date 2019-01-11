/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.conceptDrift;

/**
 * Drift detection method based in Cusum
 *
 *
 * @author Manuel Baena (mbaena@lcc.uma.es)
 * @version $Revision: 7 $
 */
public class CusumDM {

    private static final long serialVersionUID = -3518369648142099719L;

    private int minNumInstancesOption = 30;
    
    /*
    public IntOption minNumInstancesOption = new IntOption(
            "minNumInstances",
            'n',
            "The minimum number of instances before permitting detecting change.",
            30, 0, Integer.MAX_VALUE);
    */        
    
    private double deltaOption =  0.005;
    
    /*
    public FloatOption deltaOption = new FloatOption("delta", 'd',
            "Delta parameter of the Cusum Test", 0.005, 0.0, 1.0);
    */
    
    private double lambdaOption = 50;
    
    /*
    public FloatOption lambdaOption = new FloatOption("lambda", 'l',
            "Threshold parameter of the Cusum Test", 50, 0.0, Float.MAX_VALUE);
    */        
    
    private int m_n;

    private double sum;
    
    private double sumMin;

    private double x_mean;

    private double alpha;

    private double delta;

    private double lambda;

    //abstract class parameters 
    
     /**
     * Change was detected
     */
    private boolean isChangeDetected;

    /**
     * Warning Zone: after a warning and before a change 
     */
    private boolean isWarningZone;

    /**
     * Prediction for the next value based in previous seen values
     */
    private double estimation;

    /**
     * Delay in detecting change
     */
    private double delay;

    /**
     * The change detector has been initialized with the option values
     */
    private boolean isInitialized;

    /**
     * Resets this change detector. It must be similar to starting a new change
     * detector from scratch.
     *
     */
    /*
    public void resetLearning() {
        this.isChangeDetected = false;
        this.isWarningZone = false;
        this.estimation = 0.0;
        this.delay = 0.0;
        this.isInitialized = false;
    }
    */
    
    public CusumDM() {
        resetLearning();
    }

    
    public void resetLearning() {
        m_n = 1;
        x_mean = 0.0;
        sum = 0.0;
        sumMin = 0.0;
        delta = this.deltaOption;
        lambda = this.lambdaOption;
    }

    
    public void input(double x) {
        // It monitors the error rate
        if (this.isChangeDetected == true || this.isInitialized == false) {
            resetLearning();
            this.isInitialized = true;
        }

        x_mean = x_mean + (x - x_mean) / (double) m_n;
        sum = Math.max(0, sum + x - x_mean - this.delta);
        
        sumMin = Math.min(0, x + (x_mean - this.delta) - sumMin);
        
        m_n++;

        // System.out.print(prediction + " " + m_n + " " + (m_p+m_s) + " ");
        this.estimation = x_mean;
        this.isChangeDetected = false;
        this.isWarningZone = false;
        this.delay = 0;

        if (m_n < this.minNumInstancesOption) {
            return;
        }
        
        System.out.print(-this.lambda);
        if (sum > this.lambda || sumMin < -this.lambda) {
            this.isChangeDetected = true;
        } 
    }
    
    public void setlambdaOption(double lambdaOption){
        this.lambdaOption = lambdaOption;
    }
    
    public boolean getChange(){
        return this.isChangeDetected;
    }
    
    public double getEstimator(){
        return this.estimation;
    }
    
    public double getSum(){
        return this.sum;
    }
    
    public double getSumMin(){
        return this.sumMin;
    }
}