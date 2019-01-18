/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.model;

import java.time.LocalDateTime;


/**
 *
 * @author brenno
 */
public class SensorData {
    private String value;
    private FoTDeviceStream device;
    private FoTSensorStream sensor;
    private LocalDateTime localDateTime;
    private long delay;

    
    public SensorData(String value, LocalDateTime localDateTime, 
            FoTSensorStream sensor, FoTDeviceStream device, long delay){
        this.value = value;
        this.localDateTime = localDateTime;
        this.sensor = sensor;
        this.device = device;
        this.delay = delay;
    }
    
    public SensorData(String value, LocalDateTime localDateTime, 
            FoTSensorStream sensor, FoTDeviceStream device){
        this.value = value;
        this.localDateTime = localDateTime;
        this.sensor = sensor;
        this.device = device;
        //this.delay = delay;
    }
    
    public SensorData(String value, FoTSensorStream sensor, FoTDeviceStream device){
        this.value = value;
        this.sensor = sensor;
        this.device = device;
    }
    
    
    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
    
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public FoTDeviceStream getDevice() {
        return device;
    }

    public void setDevice(FoTDeviceStream device) {
        this.device = device;
    }

    public FoTSensorStream getSensor() {
        return sensor;
    }

    public void setSensor(FoTSensorStream sensor) {
        this.sensor = sensor;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
    
    
    
	
}
