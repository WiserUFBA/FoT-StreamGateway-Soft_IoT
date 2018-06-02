/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.model;



import br.ufba.dcc.wiser.soft_iot.analytics.util.UtilDebug;
import br.ufba.dcc.wiser.soft_iot.entities.SensorData;
import br.ufba.dcc.wiser.soft_iot.tatu.TATUWrapper;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.connectors.mqtt.MqttStreams;
import org.apache.edgent.topology.TSink;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author brenno
 */
public class FoTSensorStream {
    
    private String Sensorid;
    private String type;
    private int collectionTime;
    private int publishingTime;
    private MqttStreams connector;
    private Topology topology;
    private String topicPrefix = "";
    private int qos;
    private FoTDeviceStream fotDeviceStream; 
    
    public FoTSensorStream(Topology topology, MqttConfig mqttConfig, String Sensorid){
        this.topology = topology;
        this.Sensorid = Sensorid;
	UtilDebug.printDebugConsole(mqttConfig.getServerURLs()[0]);
        this.connector = new MqttStreams(topology, mqttConfig.getServerURLs()[0], Sensorid);
        
        if(this.connector == null)
            throw new ExceptionInInitializerError("Error starting sensor");
        this.qos = 0;
        initGetSensorData();
    }   
    
    
    public String getSensorid() {
        return Sensorid;
    }

    public void setSensorid(String Sensorid) {
        this.Sensorid = Sensorid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(int collectionTime) {
        this.collectionTime = collectionTime;
    }

    public int getPublishingTime() {
        return publishingTime;
    }

    public void setPublishingTime(int publishingTime) {
        this.publishingTime = publishingTime;
    }

    public MqttStreams getConnector() {
        return connector;
    }

    public void setConnector(MqttStreams connector) {
        this.connector = connector;
    }

    public Topology getTopology() {
        return topology;
    }

    public void setTopology(Topology topology) {
        this.topology = topology;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public FoTDeviceStream getFotDeviceStream() {
        return fotDeviceStream;
    }

    public void setFotDeviceStream(FoTDeviceStream fotDeviceStream) {
        this.fotDeviceStream = fotDeviceStream;
    }
	
   private void initGetSensorData(){
       UtilDebug.printDebugConsole(TATUWrapper.topicBase + getSensorid() + "/#");
       TStream<String> tStream = this.connector.subscribe(TATUWrapper.topicBase + getSensorid() + "/#", this.qos);
       TStream<List<SensorData>> tStreamData = tStream.map((tuple) -> {
                                                
                                                List<SensorData> listSensorData = parseTatuMessage(tuple);
                                                return listSensorData; 
                                            });
       
       
       
       tStream.print();
       
   }
   
   public List<SensorData> parseTatuMessage(String message){
       List<SensorData> listSensorData = new ArrayList<SensorData>();
		try{
			JSONObject json = new JSONObject(answer);
			JSONArray sensorValues = json.getJSONObject("BODY").getJSONArray(
					sensor.getId());
			int collectTime = json.getJSONObject("BODY").getJSONObject("FLOW")
					.getInt("collect");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(baseDate);
			for (int i = 0; i < sensorValues.length(); i++) {
				Integer valueInt = sensorValues.getInt(i);
				String value = valueInt.toString();
				SensorData sensorData = new SensorData(device, sensor,value,calendar.getTime(),calendar.getTime());
				listSensorData.add(sensorData);
				calendar.add(Calendar.MILLISECOND, collectTime);
			}
		}catch(org.json.JSONException e){
                    
		}
		return listSensorData;
   }
   
   public String getDeviceTopic(){
       
       return null;
   }
	
}
