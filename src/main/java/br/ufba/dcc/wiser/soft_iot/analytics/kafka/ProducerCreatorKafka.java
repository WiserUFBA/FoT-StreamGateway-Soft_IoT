/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.kafka;

import br.ufba.dcc.wiser.soft_iot.analytics.edgent.StreamControllerImpl;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;


/**
 *
 * @author brenno
 */
public class ProducerCreatorKafka {
     
    //Settings kafka
    private String KAFKA_BROKERS;
    private Integer MESSAGE_COUNT=1000;
    private String CLIENT_ID="client1";
    private String TOPIC_NAME="demo";
    private String GROUP_ID_CONFIG="consumerGroup1";
    private Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
    private String OFFSET_RESET_LATEST="latest";
    private String OFFSET_RESET_EARLIER="earliest";
    private Integer MAX_POLL_RECORDS=1;
    
    
    public void init(){
    
    }
    
    public void disconnect(){
        
    }
    
     public Producer<Long, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getKAFKA_BROKERS());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, this.getCLIENT_ID());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
        return new KafkaProducer<>(props);
    }

    /**
     * @return the KAFKA_BROKERS
     */
    public String getKAFKA_BROKERS() {
        return KAFKA_BROKERS;
    }

    /**
     * @param KAFKA_BROKERS the KAFKA_BROKERS to set
     */
    public void setKAFKA_BROKERS(String KAFKA_BROKERS) {
        this.KAFKA_BROKERS = KAFKA_BROKERS;
    }

    /**
     * @return the MESSAGE_COUNT
     */
    public Integer getMESSAGE_COUNT() {
        return MESSAGE_COUNT;
    }

    /**
     * @param MESSAGE_COUNT the MESSAGE_COUNT to set
     */
    public void setMESSAGE_COUNT(Integer MESSAGE_COUNT) {
        this.MESSAGE_COUNT = MESSAGE_COUNT;
    }

    /**
     * @return the CLIENT_ID
     */
    public String getCLIENT_ID() {
        return CLIENT_ID;
    }

    /**
     * @param CLIENT_ID the CLIENT_ID to set
     */
    public void setCLIENT_ID(String CLIENT_ID) {
        this.CLIENT_ID = CLIENT_ID;
    }

    /**
     * @return the TOPIC_NAME
     */
    public String getTOPIC_NAME() {
        return TOPIC_NAME;
    }

    /**
     * @param TOPIC_NAME the TOPIC_NAME to set
     */
    public void setTOPIC_NAME(String TOPIC_NAME) {
        this.TOPIC_NAME = TOPIC_NAME;
    }

    /**
     * @return the GROUP_ID_CONFIG
     */
    public String getGROUP_ID_CONFIG() {
        return GROUP_ID_CONFIG;
    }

    /**
     * @param GROUP_ID_CONFIG the GROUP_ID_CONFIG to set
     */
    public void setGROUP_ID_CONFIG(String GROUP_ID_CONFIG) {
        this.GROUP_ID_CONFIG = GROUP_ID_CONFIG;
    }

    /**
     * @return the MAX_NO_MESSAGE_FOUND_COUNT
     */
    public Integer getMAX_NO_MESSAGE_FOUND_COUNT() {
        return MAX_NO_MESSAGE_FOUND_COUNT;
    }

    /**
     * @param MAX_NO_MESSAGE_FOUND_COUNT the MAX_NO_MESSAGE_FOUND_COUNT to set
     */
    public void setMAX_NO_MESSAGE_FOUND_COUNT(Integer MAX_NO_MESSAGE_FOUND_COUNT) {
        this.MAX_NO_MESSAGE_FOUND_COUNT = MAX_NO_MESSAGE_FOUND_COUNT;
    }

    /**
     * @return the OFFSET_RESET_LATEST
     */
    public String getOFFSET_RESET_LATEST() {
        return OFFSET_RESET_LATEST;
    }

    /**
     * @param OFFSET_RESET_LATEST the OFFSET_RESET_LATEST to set
     */
    public void setOFFSET_RESET_LATEST(String OFFSET_RESET_LATEST) {
        this.OFFSET_RESET_LATEST = OFFSET_RESET_LATEST;
    }

    /**
     * @return the OFFSET_RESET_EARLIER
     */
    public String getOFFSET_RESET_EARLIER() {
        return OFFSET_RESET_EARLIER;
    }

    /**
     * @param OFFSET_RESET_EARLIER the OFFSET_RESET_EARLIER to set
     */
    public void setOFFSET_RESET_EARLIER(String OFFSET_RESET_EARLIER) {
        this.OFFSET_RESET_EARLIER = OFFSET_RESET_EARLIER;
    }

    /**
     * @return the MAX_POLL_RECORDS
     */
    public Integer getMAX_POLL_RECORDS() {
        return MAX_POLL_RECORDS;
    }

    /**
     * @param MAX_POLL_RECORDS the MAX_POLL_RECORDS to set
     */
    public void setMAX_POLL_RECORDS(Integer MAX_POLL_RECORDS) {
        this.MAX_POLL_RECORDS = MAX_POLL_RECORDS;
    }
    
}
