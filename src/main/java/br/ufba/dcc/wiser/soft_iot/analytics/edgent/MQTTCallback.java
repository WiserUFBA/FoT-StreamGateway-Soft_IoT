/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.edgent;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author Brenno Mello <brennodemello.bm at gmail.com>
 */
public class MQTTCallback implements MqttCallback{

    @Override
    public void connectionLost(Throwable thrwbl) {
        
    }

    @Override
    public void messageArrived(String string, MqttMessage mm) throws Exception {
        
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        
    }
    
}
