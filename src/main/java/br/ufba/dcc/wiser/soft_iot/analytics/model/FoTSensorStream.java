/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.model;



import br.ufba.dcc.wiser.soft_iot.analytics.data.CusumStream;
import br.ufba.dcc.wiser.soft_iot.analytics.util.UtilDebug;
import br.ufba.dcc.wiser.soft_iot.tatu.TATUWrapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.connectors.mqtt.MqttStreams;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import org.osgi.service.blueprint.container.ServiceUnavailableException;


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
    private CusumStream cusumStream;
    
    /**
     *  Armengue, melhorar
     *
     */
    private double dataMax = 25;
    private double dataMin = 17;
    
    
    public FoTSensorStream(Topology topology, MqttConfig mqttConfig, String Sensorid, FoTDeviceStream fotDeviceStream){
        this.topology = topology;
        this.Sensorid = Sensorid;
        this.fotDeviceStream = fotDeviceStream;
	UtilDebug.printDebugConsole(mqttConfig.getServerURLs()[0]);
        this.connector = new MqttStreams(topology, mqttConfig.getServerURLs()[0], Sensorid);
        
        if(this.connector == null)
            throw new ExceptionInInitializerError("Error starting sensor");
        this.qos = 0;
        //sendTatuFlow();
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
    
    public void sendTatuFlow(){
        try{
			
		String flowRequest;
		if(this.collectionTime >= 0){
                    flowRequest = TATUWrapper.getTATUFlowValue(this.Sensorid, 3000, 3000);
		}else{
                    flowRequest = TATUWrapper.getTATUFlowValue(this.Sensorid, this.collectionTime, this.publishingTime);
		}
                
		UtilDebug.printDebugConsole("[topic: " + this.fotDeviceStream.getDeviceId()  +"] " + flowRequest);
		
                String topic = TATUWrapper.topicBase + this.fotDeviceStream.getDeviceId();
		
                
                TStream<String> cmdOutput = this.topology.strings(flowRequest);;
                cmdOutput.print();
                
                                
                this.connector.publish(cmdOutput, topic, this.qos, true);
                
        
	}catch (ServiceUnavailableException e) {
		e.printStackTrace();
	}
    }
	
   private void initGetSensorData(){
       UtilDebug.printDebugConsole(TATUWrapper.topicBase + this.fotDeviceStream.getDeviceId() + "/#");
       TStream<String> tStream = this.connector.subscribe(TATUWrapper.topicBase + this.fotDeviceStream.getDeviceId() + "/#", this.qos);
       
       //tStream.print();
       
       TStream<List<SensorData>> tStreamSensorData = tStream.map(tuple -> {
                    List<SensorData> listData = new ArrayList<SensorData>();
                    
                    try{
                        
                        if(TATUWrapper.isValidTATUAnswer(tuple)){
                                
                                
                            JsonParser parser = new JsonParser();

                            JsonElement element = parser.parse(tuple);
                            JsonObject jObject = element.getAsJsonObject();


                            JsonObject body = jObject.getAsJsonObject("BODY");

                            JsonArray jsonArray = body.getAsJsonArray(this.Sensorid);

                            if(jsonArray != null){
                                SensorData sensorData = null;
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JsonElement jsonElement = jsonArray.get(i);
                                    String value = String.valueOf(jsonElement.getAsDouble());
                                    sensorData = new SensorData(value, LocalDateTime.now(), this, fotDeviceStream);  
                                    if(sensorData != null) 
                                        listData.add(sensorData);
                                }
                                
                            }
                            
                           
                        }
                        
                    }catch(Exception e){
                        System.out.println("Erro parser: " + e.getMessage());
                    }
                    
                    return listData;
		});
       
       /*
        Implementar Wavelet
       
       */
       
       
       this.cusumStream = new CusumStream();       
       
       //tStreamSensorData.last(qos, fnctn)
       
       
      tStreamSensorData = tStreamSensorData.filter((list) -> {
           for (SensorData sensorData : list) {
               
               if(Double.valueOf(sensorData.getValue()) >= this.dataMax || Double.valueOf(sensorData.getValue()) <= this.dataMin)
                   return true;
           }
           
           return false; 
       });
      
       
       TStream<String> tStreamOutputStream = tStreamSensorData.map((list) -> {
           
           String output = "No data";
           
           for (SensorData sensorData : list) {    
               if(Double.valueOf(sensorData.getValue()) >= this.dataMax || Double.valueOf(sensorData.getValue()) <= this.dataMin){
                output = "Alarm Sensor: " + true + " Sensor: " + this.Sensorid + " value: " + sensorData.getValue();
               }
           }
           
           return output;
       });
      
       tStreamOutputStream.print();
   }
   
   public void printSensorData(List<SensorData> listData){
       
       System.out.println("--------------------------------------------------");
       for (SensorData sensorDatas : listData) {
                             
            System.out.println("Device ==> " + sensorDatas.getDevice().getDeviceId());
            System.out.println("Sensor ==> " + sensorDatas.getSensor().getSensorid());
            System.out.println("Data ==> " + sensorDatas.getValue());
            System.out.println("Time ==> " + sensorDatas.getLocalDateTime().toString());
                                
       }
       System.out.println("--------------------------------------------------");
       
                            
   }
   
   public List<SensorData> parseTatuMessage(String message){
    
       return null;  
   }
   
   public String getDeviceTopic(){
       
       return null;
   }
	
}
