/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.fotstream.soft_iot.gateway.edgent.controller;


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
        getDirectProvider().submit(topology);
    }
    
    public Topology createTopology(){
        return getDirectProvider().newTopology();
    }

    /**
     * @return the directProvider
     */
    public DirectProvider getDirectProvider() {
        return directProvider;
    }

    /**
     * @param directProvider the directProvider to set
     */
    public void setDirectProvider(DirectProvider directProvider) {
        this.directProvider = directProvider;
    }
    
      
    
}
