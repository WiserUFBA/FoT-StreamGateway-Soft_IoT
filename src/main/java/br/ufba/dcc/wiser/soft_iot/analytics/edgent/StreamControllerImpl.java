/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.edgent;

import br.ufba.dcc.wiser.soft_iot.analytics.model.FoTDeviceStream;
import br.ufba.dcc.wiser.soft_iot.analytics.model.FoTSensorStream;
import br.ufba.dcc.wiser.soft_iot.analytics.util.UtilDebug;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.topology.Topology;

/**
 *
 * @author Brenno Mello <brennodemello.bm at gmail.com>
 */
public class StreamControllerImpl implements StreamController{
    
    private String Url;
    private String serverId;
    private String port;
    private MqttConfig mqttConfig;
    private String username;
    private String password;
    private boolean debugModeValue;
    private String jsonDevices;
    private Topology topology;
    private List<FoTDeviceStream> listFoTDeviceStream;
    
    public StreamControllerImpl(){
        
    }
    
    public void init(){
        System.out.println("br.ufba.dcc.wiser.soft_iot.analytics.edgent.StreamControllerImpl.init()");
        try{
            
            //UtilDebug.printDebugConsole("Init Stream Controller");
            this.mqttConfig = new MqttConfig(this.Url, this.serverId);
            if(!this.username.isEmpty())
                this.mqttConfig.setUserName(username);
            if(!this.password.isEmpty())
                this.mqttConfig.setPassword(password.toCharArray());
            
            ControllerEdgent controllerEdgent = new ControllerEdgent();
            this.topology = controllerEdgent.createTopology();
            loadFoTDeviceStream();
            
            
            controllerEdgent.deployTopology(this.topology);
        }catch(Exception e){
            UtilDebug.printDebugConsole(e.getLocalizedMessage());
        }
    }
    
    public void disconnect(){
        
    }
    
    public void loadFoTDeviceStream(){
        Gson gson = new Gson();
        JsonElement tree  = gson.toJsonTree(this.jsonDevices);
        JsonArray jarray = tree.getAsJsonArray();
        for (JsonElement jsonElement : jarray) {
            if(jsonElement.isJsonObject()){
                FoTDeviceStream fotDeviceStream = new FoTDeviceStream(this.topology, this.mqttConfig);
                JsonObject fotElement = jsonElement.getAsJsonObject();
                fotDeviceStream.setDeviceId(fotElement.get("id").getAsString());
                fotDeviceStream.setLatitude(fotElement.get("latitude").getAsFloat());
                fotDeviceStream.setLongitude(fotElement.get("latitude").getAsFloat());
                
                JsonArray jsonArraySensors = fotElement.getAsJsonArray("sensors");
                List<FoTSensorStream> listFoTSensorStream = new ArrayList<FoTSensorStream>();
                
               
                
                for (JsonElement jsonElementSensor : jsonArraySensors) {
                    if(jsonElementSensor.isJsonObject()){
                        FoTSensorStream fotSensorStream = new FoTSensorStream(this.topology, this.mqttConfig);
                        JsonObject fotSensor = jsonElementSensor.getAsJsonObject();
                        fotSensorStream.setSensorid(fotSensor.get("id").getAsString());
                        fotSensorStream.setType(fotSensor.get("type").getAsString());
                        fotSensorStream.setCollectionTime(fotSensor.get("collection_time").getAsInt());
                        fotSensorStream.setPublishingTime(fotSensor.get("publishing_time").getAsInt());
                        listFoTSensorStream.add(fotSensorStream);
                    }   
                }
                
                   
               fotDeviceStream.setListFoTSensorStream(listFoTSensorStream);
               this.listFoTDeviceStream.add(fotDeviceStream);
            }
        }
    }
}
