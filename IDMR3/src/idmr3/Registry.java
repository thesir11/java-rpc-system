/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package idmr3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marlon
 */
public class Registry extends RemoteObject {
    
    
    private Socket skt;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private int port;
    private String addr;
    private ConnectionHandler connectionHandler;
    
    public Registry() {
        this.skt = null;
        this.port = 0;
        this.addr = null;
        this.oos = null;
        this.ois = null;   
    }
    
    public Registry (String addr, int port) {
        this.connectToRemote(addr, port);
    }
    
    public final void connectToRemote(String addr, int port) {
        this.addr = addr;
        this.port = port;
        try {
            skt = new Socket(addr, port);
            oos = new ObjectOutputStream(skt.getOutputStream());
            ois = new ObjectInputStream(skt.getInputStream());
            
        } catch (IOException ex) {
            Logger.getLogger(Registry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void disconnectFromRemote()
    {
        if (skt!=null)
            try {
                oos.writeObject("Bye Bye Server!");
                oos.close();
                ois.close();
                skt.close();
                
        } catch (IOException ex) {
            Logger.getLogger(Registry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public Object lookup(String name) {
        Object obj = null;
        Class[] interfaces = null;
        this.connectionHandler = new ConnectionHandler(name, this.skt, this.oos, this.ois);
        interfaces = getInterfaces() ; //Lê as interfaces
        System.out.println(interfaces.toString());
        obj =  Proxy.newProxyInstance(interfaces[0].getClassLoader(),
                interfaces,
                connectionHandler); //Cria o proxy
        
        return obj;
        
    }
    
    private  Class[] getInterfaces()
    {
        Class[] interfaces = null;
        try {
//            ObjectInputStream inFromServer = new ObjectInputStream(skt.getInputStream());
            
            interfaces = (Class[]) ois.readObject();
            
            
         
        } catch (Exception ex) {
            ex.printStackTrace();
        }  
        return interfaces;
    }
    
    public void bind (String name, Object object) {
        
        
        
    }
    
}
