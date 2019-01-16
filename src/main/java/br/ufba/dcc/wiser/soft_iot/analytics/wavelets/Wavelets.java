import java.util.List;

import br.usp.icmc.biocom.timeseries.TimeSeries;

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