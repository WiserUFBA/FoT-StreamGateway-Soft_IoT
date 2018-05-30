/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.model;



import br.ufba.dcc.wiser.soft_iot.analytics.util.UtilDebug;
import br.ufba.dcc.wiser.soft_iot.tatu.TATUWrapper;
import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.connectors.mqtt.MqttStreams;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;

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

   private void initGetSensorData(){
       UtilDebug.printDebugConsole(TATUWrapper.topicBase + getSensorid() + "/#");
       TStream<String> tStream = this.connector.subscribe(TATUWrapper.topicBase + getSensorid() + "/#", this.qos); 
       tStream.print();
       
   }
   
   public String getDeviceTopic(){
       
       return null;
   }
	
}
