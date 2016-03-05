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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    boolean leader=false;
    Socket s;
    int port;
    ObjectInputStream in;
    ObjectOutputStream out;
    BufferedReader inFromUser;
    private boolean forwardingReq;
    private final static Object lock1=new Object();
    private final static Object lock2=new Object();
    Acceptor() {
    }
    Acceptor(boolean forwardingReq) throws IOException {
        port=5000+a_num;    
        this.forwardingReq = forwardingReq;
    }
    Acceptor(Socket s) throws IOException {
        try{
        this.s = s;
        in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
        inFromUser= new BufferedReader(new InputStreamReader(System.in));
        } catch(IOException e){
            System.out.println("Exception:"+e);
        }
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
        if(forwardingReq==true){
            try {
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
    private void forwardRequests() throws SQLException, ClassNotFoundException, InterruptedException, IOException{
        //Finding next acceptor in a ring
        int nextPort,next;
        forwardingReq=false;
        HashSet reqs;
        Socket socket;
        while(true){
            next=(a_num+1)%(a_total);
            nextPort=5000+next;
            System.out.println("Vinit 1");
            while(true){
                System.out.println("Vinit 2");
                try{
                    if (nextPort!=port){
                        System.out.println("Vinit 3");
                        socket = new Socket("localhost",nextPort);
                        System.out.println("Vinit 4");
                        break;
                    }else{
                        synchronized(lock2){
                            System.out.println("Vinit 5");
                            lock2.wait();
                        }
                    }
                }catch (IOException | InterruptedException ex){}
                System.out.println("Vinit 6");
                next=(next+1)%(a_total);
                nextPort=5000+next;
            }
            //forwarding requests
            try{
                out= new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                if (countRequests()>0){
                    System.out.println("Vinit 7");
                    reqs=getRequests();
                    sendRequests(reqs);
                }else{
                    try {
                        synchronized(lock1){
                            System.out.println("Vinit 8");
                            lock1.wait();
                        }
                    }catch (InterruptedException ex) {
                        System.out.println("Exception:"+ex);
                    }
                }
            }catch(IOException e){
                System.out.println("Exception:"+e);
            }
        }
    }
    //receiving requests from proposers
    private void receiveRequests()throws Exception {
        while(true){
            try {
                System.out.println("Vinit 9");
                requests = (HashSet) in.readObject();
                for (Iterator it = requests.iterator(); it.hasNext();) {
                    request = (Request) it.next();
                    System.out.print("Request Received from "+s.getInetAddress()+":"+ s.getPort()+"->"+request.str);
                    System.out.println(" [Request Number#"+request.reqNum+"]");
                    if ((request.ip)==null) request.ip=s.getInetAddress();
                    if ((request.port)==0) request.port=s.getPort();
                }
                //saving requests into database
                saveRequests(requests);
                synchronized(lock1){        
                    lock1.notify();
                    System.out.println("Vinit 10");
                }
                synchronized(lock2){        
                    lock2.notify();
                    System.out.println("Vinit 11");
                }
            }catch(ClassNotFoundException | IOException ex) {
                System.out.println("Exception:"+ex);
            } 
        }
    }
    private void sendRequests(HashSet requests) throws IOException{
        out.writeObject(requests);
        out.flush();
    }
}