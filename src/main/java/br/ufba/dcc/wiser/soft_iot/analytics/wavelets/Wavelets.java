package br.ufba.dcc.wiser.soft_iot.analytics.wavelets;


import br.ufba.dcc.wiser.soft_iot.analytics.wavelets.TimeSeries;
import java.util.List;



/**
 * @author ricardo
 *
 */
public interface Wavelets {
	
	public void transform(TimeSeries ts);
	
	public List<Double> inverse();
	
	public List<Double> getCoefficients();
	
	public List<Double> getDetails();

}