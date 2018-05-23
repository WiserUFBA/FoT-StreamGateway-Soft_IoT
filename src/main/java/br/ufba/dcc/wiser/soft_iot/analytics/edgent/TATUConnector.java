package br.ufba.dcc.wiser.soft_iot.analytics.edgent;


import br.ufba.dcc.wiser.soft_iot.entities.SensorData;
import br.ufba.dcc.wiser.soft_iot.tatu.TATUWrapper;
import java.util.Date;
import java.util.List;
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
public class TATUConnector implements Supplier<SensorData>{

    private static final Logger logger = LoggerFactory.getLogger(TATUConnector.class);
  
    public TATUConnector (Supplier<MqttConfig> config){
        
    }
    
    public TATUConnector(){
        
    }

    @Override
    public SensorData get() {
        
        
        
        
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
           new Thread(new Runnable() {
               
               
			public void run() {
				String messageContent = new String(message.getPayload());
				if(TATUWrapper.isValidTATUAnswer(messageContent)){
					try{	
						String deviceId = TATUWrapper.getDeviceIdByTATUAnswer(messageContent);
						Device device = fotDevices.getDeviceById(deviceId);
						
						String sensorId = TATUWrapper.getSensorIdByTATUAnswer(messageContent);
						Sensor sensor = device.getSensorbySensorId(sensorId);
						Date date = new Date();
						List<SensorData> listSensorData = TATUWrapper.parseTATUAnswerToListSensorData(messageContent,device,sensor,date);
						printlnDebug("answer received: device: " + deviceId +  " - sensor: " + sensor.getId() + " - number of data sensor: " + listSensorData.size());
						//storeSensorData(listSensorData, device);
                                                storageSource();
					}
					catch (ServiceUnavailableException e) {
						e.printStackTrace();
					}
				}else if(topic.contentEquals("CONNECTED")){
					try {
						Thread.sleep(2000);
						Device device = fotDevices.getDeviceById(messageContent);
						if(device != null)
                                                    sendFlowRequest(device);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ServiceUnavailableException e) {
						e.printStackTrace();
					}
					
				}
			}
		}).start();
            logger.trace(topic + message.getPayload());
            
            
        }
        
    }

   public Consumer<String> sink(){
      
       
      return null;
   }
   
   public Supplier<SensorData> source(){
       
       
       
      return null;
   }
   
}
