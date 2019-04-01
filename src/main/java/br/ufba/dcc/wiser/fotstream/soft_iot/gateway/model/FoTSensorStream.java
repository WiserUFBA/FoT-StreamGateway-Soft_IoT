/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.fotstream.soft_iot.gateway.model;



import br.ufba.dcc.wiser.fotstream.soft_iot.gateway.conceptDrift.CusumDM;
import br.ufba.dcc.wiser.fotstream.soft_iot.gateway.conceptDrift.CusumStream;
import br.ufba.dcc.wiser.fotstream.soft_iot.gateway.kafka.ProducerCreatorKafka;
import br.ufba.dcc.wiser.fotstream.soft_iot.gateway.util.UtilDebug;
import br.ufba.dcc.wiser.fotstream.soft_iot.gateway.wavelets.Haar;
import br.ufba.dcc.wiser.fotstream.soft_iot.gateway.wavelets.TimeSeries;
import br.ufba.dcc.wiser.soft_iot.tatu.TATUWrapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
//import moa.classifiers.core.driftdetection.ChangeDetector;
//import moa.classifiers.core.driftdetection.CusumDM;
import moa.classifiers.core.driftdetection.EWMAChartDM;
import moa.classifiers.core.driftdetection.GeometricMovingAverageDM;
import moa.classifiers.core.driftdetection.HDDM_A_Test;
import moa.classifiers.core.driftdetection.PageHinkleyDM;
//import moa.classifiers.core.driftdetection.SEEDChangeDetector;
import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.connectors.mqtt.MqttStreams;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import org.osgi.service.blueprint.container.ServiceUnavailableException;
import org.apache.edgent.function.Functions;
import org.apache.edgent.topology.TWindow;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
//import org.apache.edgent.connectors.kafka.KafkaProducer;
import java.text.NumberFormat;
import java.util.Locale;



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
    private Path pathLog;
    private Path newFilePath;
    private BufferedWriter writer;
    private boolean changeCusum;
    private boolean isInitialized;
    //private KafkaProducer kafkaProducer;
    private Producer<Long, String> producerKafka;
    private String topicKafka;
    private int throughputSensor;
    private ProducerCreatorKafka producerCreatorKafka;
    
     /**
     * Wavelet
     */
    private Haar haar;
    
    /**
     *  Armengue, melhorar
     *
     */
    private double dataMax = 25;
    private double dataMin = 17;
    
    
    
    public FoTSensorStream(Topology topology, MqttConfig mqttConfig, 
            String Sensorid, FoTDeviceStream fotDeviceStream, Path pathLog){
        this.topology = topology;
        this.Sensorid = Sensorid;
        this.isInitialized = true;
        this.fotDeviceStream = fotDeviceStream;
	UtilDebug.printDebugConsole(mqttConfig.getServerURLs()[0]);
        this.pathLog = pathLog;
        this.connector = new MqttStreams(topology, mqttConfig.getServerURLs()[0], this.fotDeviceStream.getDeviceId()+Sensorid);
        this.haar = new Haar();
        
        
        if(this.connector == null){
            System.out.println("Error starting Broker MQTT");
            throw new ExceptionInInitializerError("Error starting Broker MQTT");
        }
        this.qos = 0;
        
        
        
        try{
            Path dir;
            if(Files.isDirectory(pathLog)){
               dir = Paths.get(pathLog.toUri());
            }else{
               dir = Files.createDirectory(pathLog);
            }   
            this.newFilePath = dir.resolve("Log-" + this.fotDeviceStream.getDeviceId() + "-" + this.Sensorid + "-" + LocalDateTime.now().toString() + ".txt");
            //this.newFilePath = Files.createFile(fileToCreatePath);
            System.out.println("File " + newFilePath.toString());
            //writer = new BufferedWriter(Files.newBufferedWriter(newFilePath));
            
            //FileWriter fileWriter = new FileWriter(newFilePath.toFile(), true);
            //this.writer = new BufferedWriter(fileWriter);
            //writer.write(qos);
            //writer.append("Teste");
            //writer.close();
            
            Thread.currentThread().setContextClassLoader(null);
            this.producerCreatorKafka = new ProducerCreatorKafka();
            this.producerKafka = this.producerCreatorKafka.createProducer();
            if(this.producerKafka == null){
                System.out.println("Error starting Kafka Stream");
                throw new ExceptionInInitializerError("Error starting Kafka Stream");
            }
            
            //Map<String,Object> config = new HashMap<>();
            //config.put("bootstrap.servers", this.fotDeviceStream.getBootstrapServers());

            //this.kafkaProducer = new KafkaProducer(this.topology, () -> config);
            
            /*
            if(this.kafkaProducer == null){
                System.out.println("Error starting Kafka Stream");
                throw new ExceptionInInitializerError("Error starting Kafka Stream");
            }
           */
            
        }catch(IOException e){
            printError(e);
        }catch(Exception e) {
            printError(e);
        } 
         
        this.topicKafka = "dev" + "."  + this.fotDeviceStream.getDeviceId() + "." + this.Sensorid;
        System.out.println("Kafka topic: "+this.topicKafka);
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
                        //System.out.println("Erro parser: " + e.getMessage());
                        printError(e);
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
                       printError(e);
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
          //System.out.println(e.getMessage());
          printError(e);
       }  
            
            return qtdMenssage;
       });
       
       readings.print();
       
       
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
       
             
       TWindow<List<SensorData>, Integer> windowthroughput = tStreamSensorData.last(60, TimeUnit.SECONDS, Functions.unpartitioned());
       
       
       windowthroughput.batch((List, integer) -> {

           try{
            
            this.throughputSensor = 0;
            for (List<SensorData> listData : List) { 
                for (SensorData sensorData : listData) {
                      throughputSensor++;
                }
            }
            JsonObject sensorDataJson = new JsonObject();
            sensorDataJson.addProperty("deviceId", this.fotDeviceStream.getDeviceId());
            sensorDataJson.addProperty("sensorId", this.Sensorid);
            sensorDataJson.addProperty("throughputStream", throughputSensor);
            System.out.println("throughputSensor "+ this.fotDeviceStream.getDeviceId() + " " + this.Sensorid + " " + throughputSensor);
            
            FileWriter fileWriter;
            //if(Files.exists(this.newFilePath)){
              // this.writer = Files.newBufferedWriter(Paths.get(this.newFilePath.toUri()));
            //}else{
               fileWriter = new FileWriter(this.newFilePath.toFile(), true);
               this.writer = new BufferedWriter(fileWriter);
            //}   
            
            
            this.writer.append(sensorDataJson.toString()+ "\n");
            //this.writer.newLine();
            
            writer.close();
          }catch(Exception e){
              printError(e);
          }
            return null;
       });
       
       
       TWindow<List<SensorData>, Integer> windowData = tStreamSensorData.last(100, Functions.unpartitioned());
       
       TimeSeries timeSeries = new TimeSeries();
              
       //this.changeDetector = new CusumDM();
       CusumDM detector = new CusumDM();
       detector.setlambdaOption(50);
       //this.changeCusum = false;
       
       //PageHinkleyDM detector = new PageHinkleyDM ();
       //detector.lambdaOption.setValue(0.5);
       
       //GeometricMovingAverageDM detector = new GeometricMovingAverageDM ();
       //detector.lambdaOption.setValue(1);
       
       //EWMAChartDM detector = new EWMAChartDM();
       //detector.lambdaOption.setValue(1);
       
       //HDDM_A_Test detector = new HDDM_A_Test();
        
       //SEEDChangeDetector detector = new SEEDChangeDetector();
       
       System.out.println("Concept Drift Detector " + detector.getClass().toString());
       TStream<List<SensorData>> readings = windowData.batch((List, integer) -> {
           List<SensorData> output = new ArrayList<>();  
           this.changeCusum = false;
           LocalDateTime initDelay = null;
           boolean initCalcDelay = true;
           LocalDateTime delayTechniques = null;
           try{
                //boolean change = false;     
                //System.out.println("Detecting change " + this.Sensorid + ":"  + this.changeCusum);   
                //System.out.println("initCalcDelay " + initCalcDelay);
                delayTechniques = LocalDateTime.now();
                for (List<SensorData> listData : List) { 
                  //System.out.println("List");
                  for (SensorData sensorData : listData) {
                     double value = Double.valueOf(sensorData.getValue());
                     if(initCalcDelay){
                        initCalcDelay = false;
                        initDelay = sensorData.getLocalDateTime();
                     }
                     //System.out.println(value);
                     //this.changeDetector.input(value);
                     //System.out.println(this.fotDeviceStream.getDeviceId() + this.Sensorid + " Estimator x_mean: " + detector.getEstimator());
                     //System.out.println(this.fotDeviceStream.getDeviceId() + this.Sensorid + " Sum: " + detector.getSum());
                     //System.out.println(this.fotDeviceStream.getDeviceId() + this.Sensorid + " SumMin: " + detector.getSumMin());
                     timeSeries.addElement(value);
                     //detector.input(value);
                     //output.add(sensorData);
                     //if(detector.getChange()){
                     //   this.changeCusum = true;
                     //}
                  }
                }
                //System.out.println("initCalcDelay " + initCalcDelay);
                initCalcDelay = true;
                System.out.println("initDelay " + initDelay);
                if(timeSeries.size()>0){
                    System.out.println(this.fotDeviceStream.getDeviceId()+this.Sensorid + " Raw Data " + timeSeries.getObservations());
                    this.haar = new Haar();
                    this.haar.transform(timeSeries);
                    List<Double> haarTransformCoefficient = haar.getCoefficientLevel(haar.getLevels()-1);
                    List<Double> haarTransformDetail = haar.getDetailLevel(haar.getLevels()-1);
                    System.out.println(this.fotDeviceStream.getDeviceId()+this.Sensorid + " Haar Transform Coefficient" + haarTransformCoefficient.toString());
                    System.out.println(this.fotDeviceStream.getDeviceId()+this.Sensorid + " Haar Transform Detail" + haarTransformDetail.toString());
                   
                for (Double value : haarTransformCoefficient) {
                    System.out.println(this.fotDeviceStream.getDeviceId() + this.Sensorid + " Estimator x_mean: " + detector.getEstimator());
                    System.out.println(this.fotDeviceStream.getDeviceId() + this.Sensorid + " Sum: " + detector.getSum());
                    System.out.println(this.fotDeviceStream.getDeviceId() + this.Sensorid + " SumMin: " + detector.getSumMin());
                    
                    Locale enlocale  = new Locale("en", "US");
                    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(enlocale);
                    decimalFormat.applyPattern("0.000");
                    //DecimalFormat df = new DecimalFormat("0.000");
                    //DecimalFormat df = new DecimalFormat("##,000");
                    System.out.println("ERROr: " + decimalFormat.format(value));
                    Double newDouble = Double.valueOf(decimalFormat.format(value));
                    
                                        
                    /*
                    NumberFormat format = NumberFormat.getInstance();
                    format.setMaximumFractionDigits(2);
                    format.setMaximumIntegerDigits(2);
                    format.setRoundingMode(RoundingMode.HALF_UP);
                    System.out.println("ERROr: " + format.format(value));
                    Double newDouble = Double.valueOf(format.format(value));
                    */
                    
                    SensorData sensorData = new SensorData(newDouble.toString(), initDelay, this, fotDeviceStream);  
                    output.add(sensorData);
                    
                    detector.input(value);
                    if(detector.getChange()){
                        this.changeCusum = true;
                        //timeSeries.cleaningTimeSeries();
                    }
                  }
                }
                
                timeSeries.cleaningTimeSeries();
                
                System.out.println(this.fotDeviceStream.getDeviceId() + " " + this.Sensorid + " Delay Techniques: " + delayTechniques.until(LocalDateTime.now(), ChronoUnit.MILLIS));
                
                /*
                if(change){
                    System.out.println("Concept Drift detected " + this.Sensorid);
                }else{
                    System.out.println("Concept Drift not detected " + this.Sensorid);   
                } 
                */
                
            }catch(Exception e){
               printError(e);
            }
           
            return output;
           
      });
      
        //{"CODE":"POST","METHOD":"FLOW","HEADER":{"NAME":"ufbaino04"},"BODY":{"temperatureSensor":["36","26"],"FLOW":{"publish":10000,"collect":5000}}}
        //if(this.changeCusum){
        //System.out.println("Concept Drift detected " + this.Sensorid);
        //this.changeCusum = false;
        
        
        TStream<JsonObject> sensorDataJsonStream =  readings.map((sensorData) -> {
              //List<JsonObject> output = new ArrayList<JsonObject>();
              JsonObject sensorDataJson = new JsonObject();
              SensorData index = sensorData.get(1);
              String deviceId = index.getDevice().getDeviceId();
              String sensorId = index.getSensor().getSensorid();
              String dateTime = index.getLocalDateTime().toString();
              System.out.println("Delay " + this.Sensorid + dateTime);
              //String valueSensor = index.getValue();
              sensorDataJson.addProperty("deviceId", deviceId);
              sensorDataJson.addProperty("sensorId", sensorId);
              sensorDataJson.addProperty("localDateTime", dateTime);
              //sensorDataJson.addProperty("valueSensor", valueSensor);
              
              JsonArray arrayData = new JsonArray();
              for (SensorData sensorDataColect : sensorData) {
                arrayData.add(sensorDataColect.getValue());
                //output.add(sensorDataJson);
              }
              
              sensorDataJson.add("valueSensor", arrayData);
              
              return sensorDataJson; 
          });
        
         //sensorDataJsonStream.print();
         
      //}
      
      /*
      try{
          
            System.out.println("Send Message Kafka ");
            String topic = TATUWrapper.topicBase + "."  + this.fotDeviceStream.getDeviceId() + "." + this.Sensorid;
            System.out.println("Send Message before print ");
            
            this.kafkaProducer.publish(sensorDataJsonStream.asString(),topic);
            System.out.println("Send Message after print ");
            
      
      }catch(Exception e){
            System.out.println("Error Kafka method " + this.Sensorid + " " + e.getMessage());
            StackTraceElement[] stack = e.getStackTrace();
              for (StackTraceElement stackTraceElement : stack) {
                  System.out.println("Error class " + " " + stackTraceElement.getClassName());
                  System.out.println("Error file " + " " + stackTraceElement.getFileName());
                  System.out.println("Error method " + " " + stackTraceElement.getMethodName());
                  System.out.println("Error line " + " " + stackTraceElement.getLineNumber());
                  
              }
          }
      */
      
      System.out.println("Send Message Kafka TRUE");   
      sensorDataJsonStream.sink((JsonObject t) -> {
        //System.out.println("Detecting change " + this.Sensorid +  ": " + this.changeCusum);
        try{
           
            JsonElement delayElement = t.get("localDateTime");
            //System.out.println("Second delay " + delayElement);
            LocalDateTime date1 = LocalDateTime.now();
            System.out.println("TimeStamp " + date1.toString());
            LocalDateTime date = LocalDateTime.parse(delayElement.getAsString());
            long delayLong = date.until(date1, ChronoUnit.SECONDS);
            t.addProperty("delayFog", delayLong);
        
       
        System.out.println("Windows " + t.toString());
        System.out.println("Data Size " + t.toString().getBytes().length);
        t.addProperty("WindowSize", t.toString().getBytes().length);
        
            FileWriter fileWriter;
            //if(Files.exists(this.newFilePath)){
               //this.writer = Files.newBufferedWriter(Paths.get(this.newFilePath.toUri()));
            //}else{
               fileWriter = new FileWriter(this.newFilePath.toFile(), true);
               this.writer = new BufferedWriter(fileWriter);
            //}   
            
        this.writer.append(t.toString()+"\n");
        //this.writer.newLine();
        writer.close();
        if(this.changeCusum == true || this.isInitialized==true){
          //System.out.println("Detecting change isInitialized " + this.Sensorid +  ": " + this.isInitialized);     
          this.isInitialized = false;  
          
            System.out.println("Send Message Kafka " + t.toString());
            //String topic = TATUWrapper.topicBase + "."  + this.fotDeviceStream.getDeviceId() + "." + this.Sensorid;
            //System.out.println("Send Message before print ");
            //this.topology.strings(t.toString()).print();
            //this.topology.of(t.getAsString()).print();
            //if(this.changeCusum){
            ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(this.topicKafka,
					t.toString());
	    //Thread.sleep(10000);
            System.out.println("Data Record " + record.value());
            Thread.currentThread().setContextClassLoader(null);
	    if(this.producerKafka == null)
                this.producerKafka = this.producerCreatorKafka.createProducer();
            this.producerKafka.send(record);
	    //System.out.println("Record sent to partition " + metadata.partition() + " with offset " + metadata.offset());	

            //}
            
            //System.out.println("Send Message after print ");
            //this.kafkaProducer.publish(this.topology.of(t.toString()),topic);
         
            }  
         }catch(Exception e){
            printError(e);
          }  
      });
      
      //readings.print();
   }
   
   
   
   
    public void sendMessageKafka(TStream<JsonObject> readings){
      try{
        System.out.println("Send Message Kafka");
        //String topic = TATUWrapper.topicBase + "/" + this.fotDeviceStream.getDeviceId() + "/" + this.Sensorid;
        String topicKafka = TATUWrapper.topicBase + "."  + this.fotDeviceStream.getDeviceId() + "." + this.Sensorid;

        //TStream<JsonObject> sensorReadings = t.poll(
        //             () -> getSensorReading(), 5, TimeUnit.SECONDS);

        //publish as sensor readings as JSON
        /*
        this.kafkaProducer.publish(readings, null,  
                (t) -> {return t.toString();}, 
                (t) -> {return topic;}, null);
        */ 
      }catch(Exception e){
          System.out.println("Error Kafka method " + this.Sensorid + " " + e.getMessage());
      }
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
   
   public void printError(Exception e){
       System.out.println("Error Kafka method " + this.Sensorid + " " + e.getMessage());
        StackTraceElement[] stack = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stack) {
            System.out.println("Error class " + " " + stackTraceElement.getClassName());
            System.out.println("Error file " + " " + stackTraceElement.getFileName());
            System.out.println("Error method " + " " + stackTraceElement.getMethodName());
            System.out.println("Error line " + " " + stackTraceElement.getLineNumber());
                  
        }
   }
	
}
