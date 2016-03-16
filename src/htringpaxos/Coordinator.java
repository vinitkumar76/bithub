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
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Vinitkumar
 */
public class Coordinator extends Acceptor implements Runnable{
    static int i, lsn=0,count=0;
    static boolean leader=false;
    static boolean receivedPhase1b=false;
    int crnd,rnd,vrnd,sn,j;
    private String str;
    Queue cval=new LinkedList();
    Queue vval=new LinkedList();
    Queue receiveQ=new LinkedList();
    Queue sendQ=new LinkedList();
    
    Coordinator(){
    }
    Coordinator(int j){
        this.j=j;
    }
    @Override
    public void run(){
        if (a_num==0){
            leader=true;
            crnd=lsn=1;
            try {
                i=getI();
                preparePhase1a(count);
                save(sendQ);
                send(sendQ);
                sendQ.clear();
            } catch (ClassNotFoundException | InterruptedException | IOException|SQLException e) {}
        }
        while(true){
            try{
                if(leader&&receivedPhase1b){ 
                    
                    if(cval.size()>=10){
                        ++i;
                        Thread t=new Thread(new Coordinator(i));
                        t.setDaemon(true);
                        t.start();
                        preparePhase2ab(count);
                        save(sendQ);
                        send(sendQ);
                        sendQ.clear();
                        cval.clear();
                    }
                }
                if(queue.size()>0){
                    receiveQ=(Queue) queue.remove();
                    str=(String) receiveQ.remove();
                    if(null!=str)switch (str) {
                        case "1a":
                            i=(int) receiveQ.remove();
                            crnd=(int) receiveQ.remove();
                            sn=(int) receiveQ.remove();
                            break;
                        case "1ab":
                            i=(int) receiveQ.remove();
                            rnd=(int) receiveQ.remove();
                            crnd=(int) receiveQ.remove();
                            vval=(Queue) receiveQ.remove();
                            sn=(int) receiveQ.remove();
                            break;
                        case "2ab":
                            i=(int) receiveQ.remove();
                            rnd=(int) receiveQ.remove();
                            crnd=(int) receiveQ.remove();
                            vval=(Queue) receiveQ.remove();
                            sn=(int) receiveQ.remove();
                            break;
                        default:
                            break;
                    }
                }
            }catch(ClassNotFoundException | InterruptedException | IOException e){}    
        }
    }
    void preparePhase1a(int sn){
        sendQ.add("1a");
        sendQ.add(j);
        sendQ.add(crnd);
        sendQ.add(sn);
    }
    void preparePhase1ab(){
        sendQ.add("1ab");
        sendQ.add(j);
        sendQ.add(rnd);
        sendQ.add(vrnd);
        sendQ.add(vval);
        sendQ.add(sn);
    }
    void preparePhase2ab(int sn){
        sendQ.add("2ab");
        sendQ.add(j);
        sendQ.add(crnd);
        sendQ.add(cval);
        sendQ.add(sn);
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
    void save(Queue q)throws ClassNotFoundException, InterruptedException, IOException{
        
    }
}
