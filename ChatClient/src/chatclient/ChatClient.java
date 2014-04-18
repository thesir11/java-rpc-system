/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chatclient;

import chatinterface.ClientInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Marlon
 */
public class ChatClient extends UnicastRemoteObject implements ClientInterface {
    
    private final String userName;
    
    public ChatClient(String userName) throws RemoteException
    {
        this.userName = userName;
    }
    
   @Override
   public String getClientName() throws RemoteException
   {
       return this.userName;
   }
   @Override
   public void displayMsg(String sender, String msg) throws RemoteException
   {
       System.out.println(sender + ": " + msg);
   }
    
    
    
}
