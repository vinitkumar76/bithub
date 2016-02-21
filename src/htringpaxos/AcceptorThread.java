/*
 * Copyright © 2016 Vinitkumar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package htringpaxos;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinitkumar
 */
public class AcceptorThread implements Runnable{
Acceptor a=new Acceptor();
@Override
public void run(){
    try {
        boolean forwardingReq=true;
        Thread t=new Thread(new Acceptor(forwardingReq));
        t.setDaemon(true);
        t.start();
        a.runAcceptor(); 
    } catch (Exception ex) {
        Logger.getLogger(AcceptorThread.class.getName()).log(Level.SEVERE, null, ex);
    }
}    
}
