/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.fotstream.soft_iot.gateway.util;

/**
 *
 * @author brenno
 */
public class UtilDebug {
    
    
    public static void printDebugConsole(String message){
        System.out.println(message);
    }
    
    public static void printDebugConsole(String message, boolean print){
        if(print)
            System.out.println(message);
    }
    
    public static void printError(Exception e){
        System.out.println("Error " + e.getMessage());
        StackTraceElement[] stack = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stack) {
            System.out.println("Error class " + " " + stackTraceElement.getClassName());
            System.out.println("Error file " + " " + stackTraceElement.getFileName());
            System.out.println("Error method " + " " + stackTraceElement.getMethodName());
            System.out.println("Error line " + " " + stackTraceElement.getLineNumber());
                  
        }
   }
}
