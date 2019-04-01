/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.fotstream.soft_iot.gateway.conceptDrift;

/**
 *
 * @author Brenno 
 */
public interface Cusum<T> {
    
    void newData(T data);
    
    void reset();
    
    void tempChange();
    
    void isChange();
    
    
    
}
