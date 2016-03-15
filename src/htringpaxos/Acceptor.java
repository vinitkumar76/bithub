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

import static htringpaxos.HTRingPaxos.a_num;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Vinitkumar
 */
public class Acceptor extends DatabaseHandeler implements Runnable{
    static int crnd,rnd,vrnd,i,sn,lsn=0;
    static Queue cval=new LinkedList();
    static Queue vval=new LinkedList();
    static Queue queue=new LinkedList();
    static int port;
    protected final static Object lock1=new Object();
    protected final static Object lock2=new Object();
    @Override
    public void run(){
        try {
            synchronized (this){
                while(a_num<0||a_total<0){
                  wait(2000);  
                }
            }
            port=5000+a_num;
            fwdMsgs();
            callCoordinator();
            receiveMsgs();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    public void fwdMsgs() throws Exception{
        Thread t1=new Thread(new AcceptorFwdMsgs());
        t1.setDaemon(true);
        t1.start();
    }
    public void receiveMsgs() throws Exception {
        ServerSocket ss = new ServerSocket (port);
        while(true) 
        { 
            Socket socket = ss.accept();
            String stg=new String().concat(" PROPOSER"+" "+
            socket.getInetAddress() +":"+socket.getPort()+" IS CONNECTED ");
            System.out.println(stg);
            Thread t=new Thread(new AcceptorReceiveMsgs(socket));
            t.setDaemon(true);
            t.start();
        }
    }
    public void callCoordinator()throws Exception{
        Thread t2=new Thread(new Coordinator());
        t2.setDaemon(true);
        t2.start();
    }
}
