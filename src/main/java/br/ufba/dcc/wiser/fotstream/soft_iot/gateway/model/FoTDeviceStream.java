/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.fotstream.soft_iot.gateway.model;

import java.util.List;
import java.util.Properties;
import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.topology.Topology;


/**
 *
 * @author brenno
 */
public class FoTDeviceStream {
   
    private final Topology topology;

   
    private String deviceId;
    //private String clientMqttId;
    private float latitude;
    private float longitude;
    private List<FoTSensorStream> listFoTSensorStream;
    private String bootstrapServers;

    private final MqttConfig mqttConfig;
        
   
    
    public FoTDeviceStream(Topology topology, MqttConfig mqttConfig) {
        this(topology, null, mqttConfig, null);
    }
    
     public FoTDeviceStream(Topology topology, MqttConfig mqttConfig, String bootstrapServers) {
        this(topology, null, mqttConfig, bootstrapServers);
    }
    
    public FoTDeviceStream(Topology topology, Properties properties) {
        this(topology, properties, null, null);
    }

    
    public FoTDeviceStream(Topology topology, Properties properties, MqttConfig mqttConfig, String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
        this.topology = topology;
        //this.clientMqttId = "FoTDeviceStream";
        this.deviceId = "";
        if(this.topology == null)
            throw new IllegalArgumentException("Error starting FoTDeviceStream");
           
        
        if (mqttConfig == null) {
            mqttConfig = MqttConfig.fromProperties(properties);
        // mqttConfig.setClientId(this.clientMqttId);
        }
        this.mqttConfig = mqttConfig;
        
    }

   
    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public List<FoTSensorStream> getListFoTSensorStream() {
        return listFoTSensorStream;
    }

    public void setListFoTSensorStream(List<FoTSensorStream> listFoTSensorStream) {
        this.listFoTSensorStream = listFoTSensorStream;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return the bootstrapServers
     */
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    /**
     * @param bootstrapServers the bootstrapServers to set
     */
    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }
   
   
  
}
