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

/**
 *
 * @author Vinitkumar
 */
public class Coordinator extends Acceptor implements Runnable{
    private static int i, lsn;
    protected static HashSet iSet=new HashSet();
    protected static HashSet dSet=new HashSet();
    protected static ArrayList cList=new ArrayList();
    private static boolean phase1;
    private boolean instance;
    private boolean decided=false;
    private String str;
    private LinkedList list=new LinkedList();
    protected LinkedList Q=new LinkedList();
    private int crnd,rnd,vrnd,sn,j;
    private LinkedList cval=new LinkedList();
    private LinkedList vval=new LinkedList();
    private LinkedList sQ=new LinkedList();
    Coordinator(){
        instance=false;
        if(a_num==0){//leader election protocol will change this logic
            leader=true;
        }
    }
    Coordinator(int j){
        instance=true;
        this.j=j;
        rnd=0;
        vrnd=0;
        crnd=lsn;
    }
    @Override
    public void run(){
        int rec_crnd;
        if (instance==true){
            while(!decided){
                list=(LinkedList) Q.remove();
                str=(String) list.remove();
                if(null!=str)switch (str) {
                    case "1a":
                        rec_crnd=(int) list.remove();
                        if (rnd>rec_crnd){
                            sQ.add("denial");
                            sQ.add(rec_crnd);
                            try {
                                save(sQ);
                                send(sQ);
                                sQ.clear();
                            } catch (ClassNotFoundException | InterruptedException | IOException ex) {}
                        }else if (rnd<rec_crnd){
                            rnd=rec_crnd;
                            sQ.add(j);
                            sQ.add("1ab");
                            sQ.add(rec_crnd);
                            sQ.add(vrnd);
                            sQ.add(vval);
                            sQ.add(2);
                            try {
                                save(sQ);
                                send(sQ);
                                sQ.clear();
                            } catch (ClassNotFoundException | InterruptedException | IOException ex) {}
                        }
                        break;
                       
                    case "1ab":
                        rec_crnd=(int) list.remove();
                        crnd=(int) list.remove();
                        vval=(LinkedList) list.remove();
                        sn=(int) list.remove();
                        break;
                    case "2a":
                        rnd=(int) list.remove();
                        crnd=(int) list.remove();
                        vval=(LinkedList) list.remove();
                        sn=(int) list.remove();
                        break;
                    case "2ab":
                        rnd=(int) list.remove();
                        crnd=(int) list.remove();
                        vval=(LinkedList) list.remove();
                        sn=(int) list.remove();
                        break;
                    case "denial":
                        rec_crnd=(int) list.remove();
                        if(rec_crnd==lsn)
                            leader=false;
                        break;
                    default:
                        break;
                }
            }
        }else if(leader){//from 0 to i th instance logic not included till now.......
            lsn=1; //Leader election protocol will calculate this value....... 
            phase1=true;
            try {//Preparing and Sending phase 1a Msg......
                i=getI();
                getDecided();
                for(int count=0;count<=i+1;count++){
                    if(!dSet.contains(count))
                        iSet.add(count);}
                Coordinator c=new Coordinator(i+1);
                cList.add(i+1, c);
                c.sQ.add(i+1);
                c.sQ.add("1a");
                c.sQ.add(c.crnd);
                save(c.sQ);
                iSet.add(i+1);
                while(phase1){
                    LinkedList q2=new LinkedList(c.sQ);
                    send(q2);
                    synchronized(this){
                        try {
                            wait(2000);
                        } catch (InterruptedException ex) {}
                    }
                }
                c.sQ.clear();
            }catch(ClassNotFoundException | InterruptedException | IOException|SQLException e){}
            while(leader){
                try{//Preparing and Sending phase 2a Msg......
                    if(batch.size()>=10){
                        Coordinator c=new Coordinator(++i);
                        cList.add(i, c);
                        iSet.add(i);
                        c.crnd=lsn;
                        c.cval.addAll(batch);
                        batch.clear();
                        c.sQ.add(c.j);
                        c.sQ.add("2a");
                        c.sQ.add(c.crnd);
                        c.sQ.add(c.cval);
                        save(c.sQ);
                        send(c.sQ);
                        c.sQ.clear();
                        Thread t=new Thread(c);
                        t.setDaemon(true);
                        t.start();
                        synchronized(this){
                        wait(2000);
                        }
                    }
                }catch(ClassNotFoundException | InterruptedException | IOException e){}
            }
        }
    }
    void send(LinkedList q)throws ClassNotFoundException, InterruptedException, IOException{
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
    void save(LinkedList q)throws ClassNotFoundException, InterruptedException, IOException{
        
    }
    private void getDecided() {
    }
}