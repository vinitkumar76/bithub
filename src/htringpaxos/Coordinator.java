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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Vinitkumar
 */
public class Coordinator extends Acceptor implements Runnable{
    protected static int i, lsn=0;
    protected static HashSet iSet=new HashSet();
    protected static ArrayList cList=new ArrayList();
    protected static boolean receivedPhase1b=false;
    protected boolean decided=false;
    protected String str;
    protected boolean instance;
    private Queue queue=new LinkedList();
    protected Queue Q=new LinkedList();
    protected int crnd,rnd,vrnd,sn,j;
    protected Queue cval=new LinkedList();
    protected Queue vval=new LinkedList();
    protected Queue rQ=new LinkedList();
    protected Queue sQ=new LinkedList();
    Coordinator(){
        instance=false;
        if(a_num==0){
            leader=true;
        }
    }
    Coordinator(int j){
        instance=true;
        this.j=j;
        rnd=0;
        vrnd=0;
    }
    @Override
    public void run(){
        if (instance==true){
            while(!decided){
                queue=(Queue) Q.remove();
                str=(String) queue.remove();
                if(null!=str)switch (str) {
                    case "1a":
                        crnd=(int) queue.remove();
                        if (rnd>crnd){
                            sQ.add("denial");
                        }else{
                            sQ.add("1ab");
                            sQ.add(j);
                            sQ.add(rnd);
                            sQ.add(vrnd);
                            sQ.add(vval);
                            sQ.add(sn);
                        }
                        break;
                    case "1ab":
                        rnd=(int) queue.remove();
                        crnd=(int) queue.remove();
                        vval=(Queue) queue.remove();
                        sn=(int) queue.remove();
                        break;
                    case "2a":
                        rnd=(int) queue.remove();
                        crnd=(int) queue.remove();
                        vval=(Queue) queue.remove();
                        sn=(int) queue.remove();
                        break;
                    case "2ab":
                        rnd=(int) queue.remove();
                        crnd=(int) queue.remove();
                        vval=(Queue) queue.remove();
                        sn=(int) queue.remove();
                        break;
                    case "denial":
                        leader=false;
                        break;
                    default:
                        break;
                }
            }
        }else if(leader){
            crnd=lsn=1;
            try {
                i=getI();
                sQ.add(++i);
                sQ.add("1a");
                sQ.add(crnd);
                save(sQ);
                send(sQ);
                sQ.clear();
            }catch(ClassNotFoundException | InterruptedException | IOException|SQLException e){}
            while(true){
                try{
                    if(leader&&receivedPhase1b&&batch.size()>=10){
                        ++i;
                        iSet.add(i);
                        Coordinator c=new Coordinator(i);
                        cList.add(i, c);
                        c.cval.addAll(batch);
                        batch.clear();
                        c.sQ.add(j);
                        c.sQ.add("2a");
                        c.sQ.add(crnd);
                        c.sQ.add(cval);
                        save(c.sQ);
                        send(c.sQ);
                        c.sQ.clear();
                        Thread t=new Thread(c);
                        t.setDaemon(true);
                        t.start();
                    }
                }catch(ClassNotFoundException | InterruptedException | IOException e){}
            }
        }
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
