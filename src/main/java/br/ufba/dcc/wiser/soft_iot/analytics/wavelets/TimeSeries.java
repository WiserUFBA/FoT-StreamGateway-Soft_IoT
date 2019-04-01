/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.wavelets;

import java.util.LinkedList;
import java.util.List;

//import org.apache.commons.logging.LogFactory;

//import br.usp.icmc.biocom.statistic.StatisticalMethods;

/**
 * @author ricardo
 *
 */
public class TimeSeries {
	
	private List<Double> observations;
	private double mean;
	private double stdev;
	private double variance;
	private double median;

	/**Constructor methods*/
	
	public TimeSeries() {
		super();
		this.observations = new LinkedList<Double>();
	}
	
	/**Getter and Setter methods*/

	public List<Double> getObservations() {
		return observations;
	}

	public void setObservations(List<Double> observations) {
		this.observations.clear();
		this.observations.addAll(observations);
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getStdev() {
		return stdev;
	}

	public void setStdev(double stddev) {
		this.stdev = stddev;
	}

	public double getVariance() {
		return variance;
	}

	public void setVariance(double variance) {
		this.variance = variance;
	}

	public double getMedian() {
		return median;
	}

	public void setMedian(double median) {
		this.median = median;
	}
	
	/**General methods*/
	
	public void calcMean(){
		this.mean = StatisticalMethods.calcMean(this.observations); 
	}
	
	public void calcVariance(){
		this.variance = StatisticalMethods.calcVariance(this.observations, this.mean);
	}
	
	public void calcStdev(){
		this.stdev = StatisticalMethods.calcStdev(this.variance);		
	}
	
	public void calcMedian(){
		this.median = StatisticalMethods.calcMedian(this.observations);		
	}
	
	public void basicStat(){
		this.calcMean();
		this.calcVariance();
		this.calcStdev();
		this.calcMedian();
	}
	
	public void addElement(Double element){
		this.observations.add(element);
	}
	
	public void copyElementList(List<Double> elements){
		//LogFactory.getLog(this.getClass()).info("Copying all elements to time series");
		this.observations.addAll(elements);
	}
	
	public void setElement(int index, double value){
		this.observations.set(index, value);
	}
	
	public void cleaningTimeSeries(){
		this.observations.clear();
		this.mean = 0D;
		this.stdev = 0D;
		this.variance = 0D;
	}
	
	public int size(){
		return this.observations.size();
	}
	
	public String toString(){
		//LogFactory.getLog(this.getClass()).info("Function toString called");
		
		String result = "\n\nMean: " + this.mean;
		
		result += "\nVariance: " + this.variance;
		
		result += "\nStdev: " + this.stdev;
		
		result += "\nMedian: " + this.median;
		
		result += "\nSize: " + this.observations.size();
		
		//result += "\nObservations: " + this.observations + "\n\n";
		result += "\n\n";
						
		return result;
	}

}
