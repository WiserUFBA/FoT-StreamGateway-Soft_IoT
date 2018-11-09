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
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
//import moa.classifiers.core.driftdetection.ChangeDetector;
import moa.classifiers.core.driftdetection.CusumDM;
import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.connectors.mqtt.MqttStreams;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import org.osgi.service.blueprint.container.ServiceUnavailableException;
import org.apache.edgent.function.Functions;
import org.apache.edgent.topology.TWindow;
import org.apache.edgent.connectors.kafka.KafkaProducer;

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
    //private ChangeDetector changeDetector;
    private Path path = Paths.get("/home/brennomello/Documentos/Log-karaf/output.txt");
    private BufferedWriter writer;
    private boolean changeCusum;
    
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
        
        if(this.connector == null){
            System.out.println("Error starting Broker MQTT");
            throw new ExceptionInInitializerError("Error starting Broker MQTT");
        }
        this.qos = 0;
        
        try{
            writer = Files.newBufferedWriter(path);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }  
          
        //sendTatuFlow();
        //initGetSensorData();
        cusumConceptDriftStream();
        //init();
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
		if(this.collectionTime <= 0){
                    flowRequest = TATUWrapper.getTATUFlowValue(this.Sensorid, 2000, 2000);
		}else{
                    flowRequest = TATUWrapper.getTATUFlowValue(this.Sensorid, this.collectionTime, this.publishingTime);
		}
                
		UtilDebug.printDebugConsole("[topic: " + this.fotDeviceStream.getDeviceId()  +"] " + flowRequest);
		
                String topic = TATUWrapper.topicBase + this.fotDeviceStream.getDeviceId();
		
                
                TStream<String> cmdOutput = this.topology.strings(flowRequest); 
                cmdOutput.print();
                
                                
                this.connector.publish(cmdOutput, topic, this.qos, false);
                
        
	}catch (ServiceUnavailableException e) {
		e.printStackTrace();
	}
    }
    
   
    
   private TStream<String> initGetSensorData(){    
       UtilDebug.printDebugConsole(TATUWrapper.topicBase + this.fotDeviceStream.getDeviceId() + "/RES");
       TStream<String> tStream = this.connector.subscribe(TATUWrapper.topicBase + this.fotDeviceStream.getDeviceId() + "/RES", this.qos);
       tStream.print();
              
       return tStream;
   }
   
   private TStream<List<SensorData>> paserTatuStreamFlow(TStream<String> tStream){
      

       TStream<List<SensorData>> tStreamSensorData = tStream.map(tuple -> {
                    List<SensorData> listData = new ArrayList<SensorData>();
                    
                    try{
                        
                        if(TATUWrapper.isValidTATUAnswer(tuple)){
                                
                                
                            JsonParser parser = new JsonParser();

                            JsonElement element = parser.parse(tuple);
                            JsonObject jObject = element.getAsJsonObject();


                            JsonObject body = jObject.getAsJsonObject("BODY");
                            
                            JsonElement elementTimeStamp = body.get("TimeStamp");
                            
                            long delay = 0;
                            if(elementTimeStamp != null){
                             
                                delay = System.currentTimeMillis()-elementTimeStamp.getAsLong();
                                System.out.println("Delay Message " + this.Sensorid + ": " + delay);
                            
                            }
                            JsonArray jsonArray = body.getAsJsonArray(this.Sensorid);

                            if(jsonArray != null){
                                SensorData sensorData = null;
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JsonElement jsonElement = jsonArray.get(i);
                                    String value = String.valueOf(jsonElement.getAsDouble());
                                    sensorData = new SensorData(value, LocalDateTime.now(), this, fotDeviceStream, delay);  
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
      
       return tStreamSensorData;
   }
   
    private TStream<List<SensorData>> paserTatuStreamGet(TStream<String> tStream){
        //{"CODE":"POST","HEADER":{"NAME":"sc01"},"METHOD":"GET","BODY":{"humiditySensor":"37.12"}}
        //{"CODE":"POST","METHOD":"FLOW","HEADER":{"NAME":"ufbaino04"},"BODY":{"temperatureSensor":["36","26"],"FLOW":{"publish":10000,"collect":5000}}}
       TStream<List<SensorData>> tStreamSensorData = tStream.map(tuple -> {
                    List<SensorData> listData = new ArrayList<SensorData>();
                    
                    try{
                        
                        if(TATUWrapper.isValidTATUAnswer(tuple)){
                                
                                
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(tuple);
                            JsonObject jObject = element.getAsJsonObject();
                            JsonElement elementMethod = jObject.get("METHOD");
                            
                            JsonObject body = jObject.getAsJsonObject("BODY");        
                            
                            JsonElement elementTimeStamp = body.get("TimeStamp");

                            long delay = 0;
                            
                            if(elementTimeStamp != null){

                                delay = System.currentTimeMillis()-elementTimeStamp.getAsLong();
                                System.out.println("Delay Message " + this.Sensorid + ": " + delay);

                            }
                            
                            JsonElement jsonData = body.get(this.Sensorid);
                            
                            if(elementMethod.getAsString().equals("GET") && jsonData != null){    

                                String value = String.valueOf(jsonData.getAsDouble());
                                SensorData sensorData = new SensorData(value, LocalDateTime.now(), this, fotDeviceStream, delay);  
                                if(sensorData != null) 
                                    listData.add(sensorData);
                                
                            }
                        }
                        
                    }catch(Exception e){
                        System.out.println("Erro parser: " + e.getMessage());
                    }
                    
                    return listData;
		});
      
       return tStreamSensorData;
   }
 
   private void init(){
       UtilDebug.printDebugConsole(TATUWrapper.topicBase + this.fotDeviceStream.getDeviceId() + "/#");
       TStream<String> tStream = this.connector.subscribe(TATUWrapper.topicBase + this.fotDeviceStream.getDeviceId() + "/#", this.qos);
       
       tStream.print();
       
       
       TStream<List<SensorData>> tStreamSensorData = tStream.map(tuple -> {
                    List<SensorData> listData = new ArrayList<SensorData>();
                    
                    try{
                        
                        if(TATUWrapper.isValidTATUAnswer(tuple)){
                                
                                
                            JsonParser parser = new JsonParser();

                            JsonElement element = parser.parse(tuple);
                            JsonObject jObject = element.getAsJsonObject();


                            JsonObject body = jObject.getAsJsonObject("BODY");
                            
                            JsonElement elementTimeStamp = body.get("TimeStamp");
                            long delay = System.currentTimeMillis()-elementTimeStamp.getAsLong();
                            //System.out.println("Delay Message " + this.Sensorid + ": " + delay);
                            
                            JsonArray jsonArray = body.getAsJsonArray(this.Sensorid);

                            if(jsonArray != null){
                                SensorData sensorData = null;
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JsonElement jsonElement = jsonArray.get(i);
                                    String value = String.valueOf(jsonElement.getAsDouble());
                                    sensorData = new SensorData(value, LocalDateTime.now(), this, fotDeviceStream, delay);  
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
      
       
       
       
       TWindow<List<SensorData>, Integer> windowSeconds = tStreamSensorData.last(60, TimeUnit.SECONDS, Functions.unpartitioned());
       TStream<Integer> readings = windowSeconds.aggregate((List, integer) -> {
             
       
        int qtdMenssage = 0; 
        //Use try-with-resource to get auto-closeable writer instance
        try{
             
            for (List<SensorData> listData : List) {    
               for (SensorData sensorData : listData) {
                   System.out.println("Data " + this.Sensorid + ": " + sensorData.getValue());
                   System.out.println("Delay " + this.Sensorid + ": " + sensorData.getDelay());
                   qtdMenssage++;
                   writer.append("Data " + this.Sensorid + ": " + sensorData.getValue() + "\n");
                   writer.append("Delay " + this.Sensorid + ": " + sensorData.getDelay() + "\n");
               }
            }
            System.out.println("Quantidade de dados " + this.Sensorid +": " + qtdMenssage);
            writer.append("Quantidade de dados " + this.Sensorid +": " + qtdMenssage + "\n");
            //writer.close();
       }catch(IOException e){
          System.out.println(e.getMessage());
        }  
            
            return qtdMenssage;
       });
       
       readings.print();
       
       
       
       
       /*
       * Implementar Wavelet
       */
       
       /*
       this.cusumStream = new CusumStream(0.05, 0.5);       
            
       TWindow<List<SensorData>, Integer> window = tStreamSensorData.last(10, Functions.unpartitioned());
             
       TStream<List<Double>> readings = window.aggregate((List, integer) -> {
            
            for (List<SensorData> listData : List) {    
               for (SensorData sensorData : listData) {
                   this.cusumStream.newData(Double.valueOf(sensorData.getValue()));
               }
            }
            
           List<Double> output = null; 
           if(this.cusumStream.isChange()){
               output = this.cusumStream.getListData();
               this.cusumStream.reset();
           }
           if(output != null){
                for (Double double1 : output) {
                    System.out.println(double1);
                }
           }else{
                    System.out.println("output null");
           } 
           return output; 
      });
       
      readings.print();
      

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
        
       */
       //tStreamOutputStream.print();
   }
   
   public void cusumConceptDriftStream(){
       TStream<String> tStream = initGetSensorData();
       TStream<List<SensorData>> tStreamSensorData = paserTatuStreamGet(tStream);
       
       TWindow<List<SensorData>, Integer> window = tStreamSensorData.last(10, Functions.unpartitioned());
       
       //this.changeDetector = new CusumDM();
       CusumDM detector = new CusumDM();
       detector.lambdaOption.setValue(1);
       this.changeCusum = false;
       
       
       
       TStream<List<SensorData>> readings = window.batch((List, integer) -> {
           List<SensorData> output = new ArrayList<>();  
           try{
                //boolean change = false;     

                for (List<SensorData> listData : List) { 
                  //System.out.println("List");
                  for (SensorData sensorData : listData) {
                     double value = Double.valueOf(sensorData.getValue());
                     //System.out.println(value);
                     //this.changeDetector.input(value);
                     detector.input(value);
                     output.add(sensorData);
                     if(detector.getChange()){
                        this.changeCusum = true;
                     }
                  }
                }
                
                /*
                if(change){
                    System.out.println("Concept Drift detected " + this.Sensorid);
                }else{
                    System.out.println("Concept Drift not detected " + this.Sensorid);   
                } 
                */
                
            }catch(Exception e){
                System.out.print(e.getMessage());
            }
           
            return output;
           
      });
      

      //if(this.changeCusum){
        //System.out.println("Concept Drift detected " + this.Sensorid);
        //this.changeCusum = false;
        TStream<List<JsonObject>> sensorDataJsonStream =  readings.map((t) -> {
              List<JsonObject> output = new ArrayList<JsonObject>();  
              for (SensorData sensorData : t) {
                JsonObject sensorDataJson = new JsonObject();
                String deviceId = sensorData.getDevice().getDeviceId();
                String sensorId = sensorData.getSensor().getSensorid();
                String valueSensor = sensorData.getValue();
                sensorDataJson.addProperty("deviceId", deviceId);
                sensorDataJson.addProperty("senorId", sensorId);
                sensorDataJson.addProperty("valueSenor", valueSensor);
                output.add(sensorDataJson);
              }
              return output; 
          });
        
         sensorDataJsonStream.asString().print();
         
      //}
      sendMessageKafka(sensorDataJsonStream);
      //readings.print();
   }
   
    public void sendMessageKafka(TStream<List<JsonObject>> readings){
      String topic = TATUWrapper.topicBase + this.fotDeviceStream.getDeviceId() + "/" + this.Sensorid;

      Map<String,Object> config = new HashMap<>();
      config.put("bootstrap.servers", this.fotDeviceStream.getBootstrapServers());

      KafkaProducer kafka = new KafkaProducer(this.topology, () -> config);
      
      
      //TStream<JsonObject> sensorReadings = t.poll(
      //             () -> getSensorReading(), 5, TimeUnit.SECONDS);

      //publish as sensor readings as JSON
      kafka.publish(readings, null,  
              (t) -> {return readings.asString().toString();}, 
              (t) -> {return topic;}, null);
      
   }
   
     
    
   public void verifyValue(TStream<List<SensorData>> tStreamSensorData){
       
       TStream<String> tStreamOutputStream = tStreamSensorData.map((list) -> {
           
           String output = "No data";
           
           for (SensorData sensorData : list) {    
               if(Double.valueOf(sensorData.getValue()) >= this.dataMax || Double.valueOf(sensorData.getValue()) <= this.dataMin){
                output = "Alarm Sensor: " + true + " Sensor: " + this.Sensorid + " value: " + sensorData.getValue();
               }
           }
           
           return output;
       });
       
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
   
   public String getDeviceTopic(){
       
       return null;
   }
	
}
