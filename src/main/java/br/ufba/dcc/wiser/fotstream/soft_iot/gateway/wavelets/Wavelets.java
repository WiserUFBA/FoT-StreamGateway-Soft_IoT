package br.ufba.dcc.wiser.fotstream.soft_iot.gateway.wavelets;


import br.ufba.dcc.wiser.fotstream.soft_iot.gateway.wavelets.TimeSeries;
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