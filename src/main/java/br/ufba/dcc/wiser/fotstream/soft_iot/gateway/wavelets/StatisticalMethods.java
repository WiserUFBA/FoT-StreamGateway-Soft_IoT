/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.fotstream.soft_iot.gateway.wavelets;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//import org.apache.commons.logging.LogFactory;

/**
 * @author ricardo
 *
 */
public class StatisticalMethods {

	public static double calcMean(List<Double> observations){
		double sum = 0D;

		//LogFactory.getLog(StatisticalMethods.class).debug("Calculating the sample mean");

		for(Double d : observations)
			sum += d;

		return (sum/observations.size()); 
	}

	public static double calcVariance(List<Double> observations, double mean){
		double sum = 0D;

		//LogFactory.getLog(StatisticalMethods.class).debug("Calculating the sample variance");

		for(Double d : observations)
			sum += Math.pow((d - mean), 2);

		return (sum/observations.size());
	}

	public static double calcStdev(double variance){
		//LogFactory.getLog(StatisticalMethods.class).debug("Calculating the sample standard deviation");
		return (Math.sqrt(variance));		
	}

	public static double calcMedian(List<Double> elements){

		List<Double> observations = new LinkedList<Double>();
		int mean;

		observations.addAll(elements);
		mean = (int) Math.ceil(observations.size()/2);

		Collections.sort(observations);

		if((observations.size()%2) != 0)
			return observations.get(mean);
		else
			return ((observations.get(mean-1)+observations.get(mean))/2);
	}

}
