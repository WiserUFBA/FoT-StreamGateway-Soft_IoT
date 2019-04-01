/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.fotstream.soft_iot.gateway.kafka;

import com.google.gson.JsonObject;
import java.util.Map;
import org.apache.kafka.common.serialization.Serializer;

/**
 *
 * @author brenno
 */
public class CustomSerializer implements Serializer<JsonObject>{

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] serialize(String topic, JsonObject data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
