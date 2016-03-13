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
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
/**
 *
 * @author Vinitkumar
 */
    public class Acceptor extends DatabaseHandeler implements Runnable {
    static boolean leader=false, fwdReq=true;
    static int crnd,rnd,vrnd;
    static HashSet cval, vval;
    Socket s;
    int port;
    private final static Object lock1=new Object();
    private final static Object lock2=new Object();
    Acceptor() {
        port=5000+a_num;  
    }
    Acceptor(Socket s){
        this.s = s;
    }
    /**
     * @throws java.lang.Exception
     */
    public void runAcceptor() throws Exception {
        port=5000+a_num;
        ServerSocket ss = new ServerSocket (port);
        while(true) 
        { 
            Socket socket = ss.accept();
            String stg=new String().concat(" PROPOSER"+" "+
            socket.getInetAddress() +":"+socket.getPort()+" IS CONNECTED ");
            System.out.println(stg);
            Thread t=new Thread(new Acceptor(socket));
            t.setDaemon(true);
            t.start();
	}
    }
    /**
     *
     */
    @Override
    public void run(){
        //forwarding requests to other acceptors
        if(fwdReq==true){
            try {
                fwdReq=false;
                getRequests();
                forwardRequests();
            } catch (SQLException | ClassNotFoundException | InterruptedException | IOException ex) {
                System.out.println("Exception:"+ex);
            }
        }
        //getting requests from proposers
        else{
            try {
                receiveRequests();
                } catch (Exception ex) {
                System.out.println("Exception:"+ex);
                }
            }
    }
    private void forwardRequests() throws ClassNotFoundException, InterruptedException, IOException{
        int nextPort,next;
        Socket socket;
        ObjectOutputStream out;
        HashSet reqs;
        while(true){
            next=(a_num+1)%(a_total);
            nextPort=5000+next;
            reqs=(HashSet) fwdRequests.clone();
            if (reqs.isEmpty()){
                try {
                    synchronized(lock1){
                        lock1.wait();
                    }
                }catch (InterruptedException ex) {
                }
            }else{
                while(true){
                    try{
                        if (nextPort!=port){
                            socket = new Socket("localhost",nextPort);
                            break;
                        }else{
                            synchronized(lock2){
                                lock2.wait();
                            }
                        }
                    }catch (IOException | InterruptedException ex){}
                    next=(next+1)%(a_total);
                    nextPort=5000+next;
                }
                try{
                    out= new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    out.writeObject(reqs);
                    out.flush();
                    fwdRequests.clear();
                    socket.close();
                }catch(IOException e){
                    socket.close();
                }
            }
        }
    }
    private void receiveRequests()throws Exception {
        HashSet requests;
        Request req;
        ObjectInputStream in=new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
        while(true){
            try {
                requests = (HashSet) in.readObject();
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
            }catch(ClassNotFoundException | IOException| SQLException ex) {} 
        }
    }
}