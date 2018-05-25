/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.edgent;

import br.ufba.dcc.wiser.soft_iot.analytics.model.FoTDeviceStream;
import java.util.List;
import org.apache.edgent.connectors.mqtt.MqttConfig;

/**
 *
 * @author Brenno Mello <brennodemello.bm at gmail.com>
 */
public class StreamControllerImpl {
    private String Url;
    private String serverId;
    private String port;
    private MqttConfig mqttConfig;
    private String username;
    private String password;
    private boolean debugModeValue;
    private String jsonDevices;
    private List<FoTDeviceStream> listFoTDeviceStream;
    
    public StreamControllerImpl(){
        
    }
    
    public void init(){
        try{
            this.mqttConfig = new MqttConfig(this.Url, this.serverId);
            if(!this.username.isEmpty())
                this.mqttConfig.setUserName(username);
            if(!this.password.isEmpty())
                this.mqttConfig.setPassword(password.toCharArray());
            
            loadFoTDeviceStream(this.jsonDevices);
            
        }catch(Exception e){
            System.out.print(e.getMessage());
        }
    }
    
    public void disconnect(){
        
    }
    
    public void loadFoTDeviceStream(String jsonDevices){
        
    }
}
