package br.ufba.dcc.wiser.soft_iot.analytics.activator;

import br.ufba.dcc.wiser.soft_iot.analytics.edgent.ControllerEdgent;
import org.apache.edgent.connectors.mqtt.MqttStreams;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    
    public void start(BundleContext context) throws Exception {
        String url = "tcp://localhost:1883";
        ControllerEdgent controllerEdgent = new ControllerEdgent();
        
        Topology topology = controllerEdgent.createTopology();
        MqttStreams mqttStreams = controllerEdgent.createMqttStreams(topology, url);
        
        TStream<String> tStream = controllerEdgent.subscribeMqttStreams("dev/#", 0, mqttStreams);
        
        tStream.print();
        

        /*
        TStream<Double> streamDouble = tStream.map(s -> Double.valueOf(s));
        TStream<Double> streamDoubleFilter = streamDouble.filter(aDouble -> aDouble >= 20);

        streamDoubleFilter.print();
        */
        
        controllerEdgent.deployTopology(topology);
        System.out.println("Start Bundle");
    }

    
    public void stop(BundleContext context) throws Exception {
        // TODO add deactivation code here
        
    }

}
