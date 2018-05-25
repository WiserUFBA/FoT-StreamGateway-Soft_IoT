/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.model;

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
	
    public FoTSensorStream(Topology topology, MqttConfig mqttConfig){
        this.topology = topology;
	this.connector = new MqttStreams(topology, mqttConfig.getServerURLs()[1], Sensorid);
        if(this.connector == null)
            throw new ExceptionInInitializerError("Error starting sensor");
        InitGetSensorData();
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

   private void InitGetSensorData(){
       TStream<String> tStream = this.connector.subscribe(Sensorid, collectionTime);
       tStream.print();
   }
	
	
}
