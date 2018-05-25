package br.ufba.dcc.wiser.soft_iot.analytics.edgent;


import br.ufba.dcc.wiser.soft_iot.entities.SensorData;
import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.function.Consumer;
import org.apache.edgent.function.Supplier;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author brenno
 */
public class TATUConnector implements Consumer<Consumer<SensorData>>, AutoCloseable{
    
    
    
    private static final Logger logger = LoggerFactory.getLogger(TATUConnector.class);
  
    public TATUConnector (Supplier<MqttConfig> config){
        
    }
    
    public TATUConnector(){
        
    }

    @Override
    public void accept(Consumer<SensorData> t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

 
    private class MQTTCallback implements MqttCallback {

        @Override
        public void connectionLost(Throwable t) {
           
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
           
            
            
        }
        
    }

   
   
}
