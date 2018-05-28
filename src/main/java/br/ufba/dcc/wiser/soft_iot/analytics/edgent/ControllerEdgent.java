/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.edgent;


import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.Topology;


/**
 *
 * @author brenno
 */
public class ControllerEdgent {
    private DirectProvider directProvider;
    
    
    public ControllerEdgent(){
        directProvider = new DirectProvider();
               
    }
 
    public void deployTopology(Topology topology){
        directProvider.submit(topology);
    }
    
    public Topology createTopology(){
        return directProvider.newTopology();
    }
    
      
    
}
