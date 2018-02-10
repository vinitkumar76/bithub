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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Vinitkumar
 */
final public class AcceptorReceiveMsgs extends Coordinator implements Runnable {
    private Socket s;
    private int i;
    private LinkedList list=new LinkedList();
    AcceptorReceiveMsgs(Socket s){
        this.s = s;
    }
    @Override
    public void run(){
    try {
        receive();
        } catch (Exception ex) {
        System.out.println("Exception:"+ex);
        }
    }
private void receive()throws Exception {
        HashSet requests = new HashSet();
        Object obj; 
        Request req;
        Coordinator c;
        ObjectInputStream in=new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
        while(true){
            try {
                obj = in.readObject();
                if (obj.getClass()==requests.getClass()){
                    requests=(HashSet) obj;
                    System.out.println("Requests received"); 
                    for (Iterator it = requests.iterator(); it.hasNext();) {
                        req = (Request) it.next();
                        if ((req.ip)==null) req.ip=s.getInetAddress();
                        if ((req.port)==0) req.port=s.getPort();
                        System.out.println(req);
                    }
                    //saving requests into database
                    synchronized(lock1){ 
                        saveRequests(requests);
                        lock1.notify();
                    }
                    synchronized(lock2){        
                        lock2.notify();
                    }
                }else if(obj.getClass()==list.getClass()){
                    list =(LinkedList) obj;
                    i=(int) list.remove();
                    if(!iSet.contains(i)&&!dSet.contains(i)){
                        c = new Coordinator(i);
                        cList.add(i, c);
                        iSet.add(i);
                        c.Q.add(list);
                        Thread t=new Thread(c);
                        t.setDaemon(true);
                        t.start();
                    }else if(!dSet.contains(i)){
                        c=(Coordinator) cList.get(i);
                        c.Q.add(list);}
                    list.clear();
                }
            }catch(ClassNotFoundException | IOException| SQLException ex) {} 
        }
    }    
}
