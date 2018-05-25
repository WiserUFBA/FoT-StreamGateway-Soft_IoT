/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.model;

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
    private final String deviceId;
    private String latitude;
    private String longitude;
    private List<FoTSensorStream> listFoTSensor;
    private String topicPrefix = "";
    
    
    private String clientId = "{mqttDevice.topic.prefix}id/{mqttDevice.id}";
    private String evtTopic = "{mqttDevice.topic.prefix}id/{mqttDevice.id}/evt/{EVENTID}/fmt/json";
    private String cmdTopic = "{mqttDevice.topic.prefix}id/{mqttDevice.id}/cmd/{COMMAND}/fmt/json";
    private int commandQoS = 0;
    private boolean retainEvents = false;
    private final MqttConfig mqttConfig;
        
   
    public FoTDeviceStream(Topology topology, Properties properties) {
        this(topology, properties, null);
    }

    
    public FoTDeviceStream(Topology topology, Properties properties, MqttConfig mqttConfig) {
        this.topology = topology;
        this.deviceId = properties.getProperty("mqttDevice.id");
        if (deviceId == null || deviceId.isEmpty())
            throw new IllegalArgumentException("mqttDevice.id");
        String cqos = properties.getProperty("mqttDevice.command.qos", Integer.valueOf(commandQoS).toString());
        commandQoS = Integer.valueOf(cqos); 
        String eretain = properties.getProperty("mqttDevice.events.retain", Boolean.valueOf(retainEvents).toString());
        retainEvents = Boolean.valueOf(eretain);
        topicPrefix = properties.getProperty("mqttDevice.topic.prefix", topicPrefix);
        clientId = properties.getProperty("mqttDevice.mqtt.clientId", clientId);
        evtTopic = properties.getProperty("mqttDevice.event.topic.pattern", evtTopic);
        
        initVars();
        
        
        if (mqttConfig == null) {
            mqttConfig = MqttConfig.fromProperties(properties);
            mqttConfig.setClientId(clientId);
        }
        this.mqttConfig = mqttConfig;
        
    }
    
    public void loadDeviceAndSensors(){
        
    }
    
    private void initVars() {
        clientId = clientId
                    .replace("{mqttDevice.topic.prefix}", topicPrefix)
                    .replace("{mqttDevice.id}", deviceId);
        evtTopic = evtTopic
                    .replace("{mqttDevice.topic.prefix}", topicPrefix)
                    .replace("{mqttDevice.id}", deviceId);
        cmdTopic = cmdTopic
                    .replace("{mqttDevice.topic.prefix}", topicPrefix)
                    .replace("{mqttDevice.id}", deviceId);
    }
    
    
    /**
     * Get the device's {@link MqttConfig}
     * @return the config
     */
    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    
    public Topology getTopology() {
        return topology;
    }

    
    public String getDeviceType() {
      // not part of this connector's device identifier model
      return "";
    }

    
    public String getDeviceId() {
      return deviceId;
    }
}
