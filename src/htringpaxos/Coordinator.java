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

import static htringpaxos.Acceptor.port;
import static htringpaxos.HTRingPaxos.a_num;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Vinitkumar
 */
public class Coordinator extends Acceptor implements Runnable{
    Queue phase1a=new LinkedList();
    Queue phase1b=new LinkedList();
    Queue phase2ab=new LinkedList();
    boolean receivePhase1b=false;
    Coordinator(){
    }
    static boolean leader=false;
    @Override
    public void run(){
        if (a_num==0){
            leader=true;
            lsn=1;
            crnd=lsn;
            callLeader();
            while(true){
                try{
                
                }catch(Exception e){}    
            }
        }
    }
    void preparePhase1a(int sn){
        phase1a.add("1a");
        phase1a.add(i);
        phase1a.add(crnd);
        phase1a.add(sn);
    }
    void preparePhase1ab(){
        phase1b.add("1b");
        phase1b.add(i);
        phase1b.add(rnd);
        phase1b.add(vrnd);
        phase1b.add(vval);
        phase1b.add(sn);
    }
    void preparePhase2ab(int sn){
        phase2ab.add("2ab");
        phase2ab.add(i);
        phase2ab.add(crnd);
        phase2ab.add(cval);
        phase2ab.add(sn);
    }
    void callLeader(){
        Thread t1=new Thread(new Leader());
        t1.setDaemon(true);
        t1.start();  
    }
    void send(Queue q)throws ClassNotFoundException, InterruptedException, IOException{
        int nextPort,next;
        Socket socket;
        ObjectOutputStream out;
        next=(a_num+1)%(a_total);
        nextPort=5000+next;
        while(true){
            try{
                if (nextPort!=port){
                    socket = new Socket("localhost",nextPort);
                    break;
                }
            }catch (IOException e){}
            next=(next+1)%(a_total);
            nextPort=5000+next;
        }
        try{
            out= new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            out.writeObject(q);
            out.flush();
            socket.close();
        }catch(IOException e){}
    }   
}
