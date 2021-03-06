/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package idmr3;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Marlon
 */
public class ConnectionHandler extends RemoteObject implements InvocationHandler {
    //Responsável por fazer a conexão com o servidor através dos sockets
    
   
    
    private Socket skt;
    
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    
    private String name;
    
    private HashMap<String, Object> callbackObjects;
    

    
 
 
    public ConnectionHandler(String name, Socket skt, ObjectOutputStream oos, ObjectInputStream ois)
    {
    this.skt = skt;
    this.name = name;
    this.oos = oos;
    this.ois = ois;
    this.callbackObjects = new HashMap();
    }
    
    private Object getRemoteObject() throws IOException, ClassNotFoundException
    {
//        ObjectInputStream inFromServer = new ObjectInputStream(skt.getInputStream());
        return (ois.readObject());
    }
    
    private void sendRemoteObject(Object remoteObject) throws IOException{
//         ObjectOutputStream output = new ObjectOutputStream(skt.getOutputStream());
//         output.flush();
         oos.writeObject(remoteObject);
//         output.flush();
    }
  
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
    {
        //TODO - tudo
        Object[] newArgs = null;
        Object returnObject = null;
        Boolean isCallback;
        String methodName;
        Object [] argsAgain;
        
//       return method.invoke(target, args);
        
        //envia método e argumentos para o servidor executar no objeto

//        Person test = new Person ("marlon", "marques");
        this.sendRemoteObject(method.getName()); //envia nome do método
       
        //E se um dos args for um objeto remoto do cliente? 
        //Tem que fazer lógica do callback
        
        
        if (args!=null) {
            newArgs = new Object[args.length];
            for (int ii=0; ii<args.length;ii++) {
                if (args[ii] instanceof RemoteObject) {
                    newArgs[ii] = args[ii].getClass().getInterfaces()[0];
                    callbackObjects.put(args[ii].toString(), args[ii]);
                  
                }
                else {
                    newArgs[ii] = args[ii];
                  
                }
            }
            }
            this.sendRemoteObject(newArgs);
            if (newArgs!=null)
            for (int ii=0;ii<newArgs.length;ii++) {
                if (newArgs[ii].toString().startsWith("interface ")) {
               
                    oos.writeObject(args[ii].toString()); //envia nome que representa o objeto
                    oos.writeObject(newArgs[ii]); //envia interface por interface
                    
                }
                
            }
            
        
        
        returnObject = this.getRemoteObject();
        //Começa a testar pra ver se deve esperar por callback
        Object remoteCallbackObject;
        Class [] parameterTypes;
       
        
        if (returnObject.toString().contentEquals("Begin of callback")) {
            isCallback = true;
            while (isCallback) {
                methodName = ois.readObject().toString();
                
                if (methodName.contentEquals("End of callback")) {
                    isCallback = false;
                }
                else {
                    argsAgain = (Object[]) ois.readObject();
                    remoteCallbackObject = callbackObjects.get(ois.readObject().toString()); //mudar
                    if (argsAgain == null) {
                       parameterTypes = null;
                     }
                    else {
                        parameterTypes = new Class[argsAgain.length];
                        for (int ii=0;ii<parameterTypes.length;ii++) {
                            parameterTypes[ii] = argsAgain[ii].getClass();
                        }
                    }
                    Method callbackMethod = remoteCallbackObject.getClass().getMethod(methodName, parameterTypes);
                    oos.writeObject(callbackMethod.invoke(remoteCallbackObject, argsAgain)); //envia retorno do callback pro servidor
                }
            }   
        }
       
        
        
        

        
 
        //espera retorno do método que foi executado no server
       return this.getRemoteObject();
        
        
//        return new Object();
    }
    
    
    
}
