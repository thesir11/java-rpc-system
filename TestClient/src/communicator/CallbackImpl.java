/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package communicator;

import idmr3.RemoteObject;

/**
 *
 * @author Marlon
 */
public class CallbackImpl extends RemoteObject implements Callback  {
    
    public void callMeBack(String text) {
        System.out.println(text);
    }
    
}
