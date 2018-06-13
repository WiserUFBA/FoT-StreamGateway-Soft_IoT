/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.analytics.data;

/**
 *
 * @author Brenno Mello <brennodemello.bm at gmail.com>
 */
public interface Cusum<T> {
    
    void newData(T data);
    
    void reset();
    
    void tempChange();
    
    void isChange();
    
    
    
}