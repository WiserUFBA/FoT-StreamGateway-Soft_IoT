/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.wavelets;

import java.util.LinkedList;
import java.util.List;

/**
 * @author ricardo
 *
 */
public class WaveletLevel {
	private List<Double> coefficients;
	private List<Double> details;
	
	public WaveletLevel() {
		super();
		this.coefficients = new LinkedList<Double>();
		this.details = new LinkedList<Double>();
	}

	public List<Double> getCoefficients() {
		return coefficients;
	}

	public void setCoefficients(List<Double> coefficients) {
		this.coefficients = coefficients;
	}

	public List<Double> getDetails() {
		return details;
	}

	public void setDetails(List<Double> details) {
		this.details = details;
	}
	
	public void addCoefficient(Double c){
		this.coefficients.add(c);
	}
	
	public void addDetail(Double d){
		this.details.add(d);
	}
	
}
