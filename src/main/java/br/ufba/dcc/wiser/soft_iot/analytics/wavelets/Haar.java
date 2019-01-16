/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.wavelets;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import br.usp.icmc.biocom.timeseries.TimeSeries;

/**
 * @author ricardo
 *
 */
public class Haar implements Wavelets {
	
	private List<WaveletLevel> transforms;

	private int levels;
	
	public Haar(){
		this.transforms = new LinkedList<WaveletLevel>();
		this.levels = 0;
	}
	
	public void pyramid(TimeSeries ts){
		List<Double> coefBase, detBase;
		do{
						
			if(this.levels == 0){
				coefBase = ts.getObservations();
				detBase = ts.getObservations();
			}else{
				coefBase = this.transforms.get(this.levels-1).getCoefficients();
				detBase = this.transforms.get(this.levels-1).getDetails();
			}
			
			this.levels++;
			
			WaveletLevel newLevel = new WaveletLevel();
			
			for(int i = 0; i <= coefBase.size()-2; i +=2){
				newLevel.addCoefficient((coefBase.get(i+1) + coefBase.get(i))/2);
				newLevel.addDetail((detBase.get(i+1) - detBase.get(i))/2);
			}
			
			this.transforms.add(newLevel);
			
		}while(this.transforms.get(this.levels-1).getDetails().size() > 1);
	}
	
	public int getLevels(){
		return this.levels;		
	}
	
	public void setLevels(int levels){
		this.levels = levels;		
	}
	
	public List<Double> getCoefficientLevel(int level){
		if(!allowedLevel(level))
			return null;
		
		return this.transforms.get((this.levels-1)-level).getCoefficients();
	}
		
	public List<Double> getDetailLevel(int level){
		if(!allowedLevel(level))
			return null;
		
		return this.transforms.get((this.levels-1)-level).getDetails();
	}
	
	public boolean allowedLevel(int level){
		if(level >= this.levels || level < 0){
			LogFactory.getLog(this.getClass()).error("You cannot choose this level. Allowed levels: 0 - " + (this.levels-1));
			return false;
		}
		return true;
	}
	
	public List<WaveletLevel> getTransforms() {
		return transforms;
	}

	public void setTransforms(List<WaveletLevel> transforms) {
		this.transforms = transforms;
	}
	
	public List<Double> inverse(int level){
		List<Double> signal = new LinkedList<Double>();
		List<Double> coefficients, details;
		
		if(!allowedLevel(level))
			return null;
		
		coefficients = this.getCoefficientLevel(level);
		details = this.getDetailLevel(level);
		
		for(int i = 0; i < coefficients.size(); i++){
			signal.add(coefficients.get(i) - details.get(i));
			signal.add(coefficients.get(i) + details.get(i));
		}
		
		return signal;
	}

	@Override
	public List<Double> inverse() {
		return inverse(this.levels-1);
	}

	@Override
	public void transform(TimeSeries ts) {
		this.pyramid(ts);
	}

	@Override
	public List<Double> getCoefficients() {
		return this.getCoefficientLevel(this.getLevels()-1);
	}

	@Override
	public List<Double> getDetails() {
		return this.getDetailLevel(this.getLevels()-1);
	}
}
