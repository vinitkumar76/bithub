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
    public class Acceptor extends HTRingPaxos implements Runnable {
    boolean leader=false;
    Socket s;
    ObjectInputStream in;
    ObjectOutputStream out;
    BufferedReader inFromUser;
    private boolean forwardingReq;
    Acceptor() {
    }
    Acceptor(boolean forwardingReq) throws IOException {
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
        System.out.println("Vinit");
        int i=5000+a_num;
        ServerSocket ss = new ServerSocket (i);
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
            } catch (SQLException | ClassNotFoundException | InterruptedException ex) {
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
    HashSet requests=new HashSet();
    Request request;
    private void forwardRequests() throws SQLException, ClassNotFoundException, InterruptedException{
        try {
            forwardingReq=false;
            HashSet reqs;
            Socket socket;
            int next=5000+a_num;
            while(true){
            try{    
            socket = new Socket("localhost",next);
            break;
            }catch(Exception e){
                next=(next+1)%4;
            }
            }
            out= new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DatabaseHandeler dbh=new DatabaseHandeler();
            while(true){
                while(true)
                {   
                    try{
                        if (dbh.countRequests()==0)
                            try {
                                waitForRequests();
                            } catch (InterruptedException ex) {
                                System.out.println("Exception:"+ex);
                            }
                        reqs=dbh.getRequests();
                        sendRequests(reqs);
                        try {
                                waitForRequests();
                            } catch (InterruptedException ex) {
                                System.out.println("Exception:"+ex);
                            }
                    }catch(IOException e){
                        System.out.println("Exception:"+e);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Exception:"+ex);
        }
    }
    private void receiveRequests()throws Exception {
        while ( true ) {
            try {
                requests = (HashSet) in.readObject();
                for (Iterator it = requests.iterator(); it.hasNext();) {
                    request = (Request) it.next();
                    System.out.print("Request Received from "+s.getInetAddress()+":"+ s.getPort()+"->"+request.str);
                    System.out.println(" [Request Number#"+request.reqNum+"]");
                    if ((request.ip)==null) request.ip=s.getInetAddress();
                    if ((request.port)==0) request.port=s.getPort();
                }
                //saving requests into database
                DatabaseHandeler dbh=new DatabaseHandeler();
                dbh.saveRequests(requests);
                synchronized(this){        
                    notifyAll();
                }
            } catch (ClassNotFoundException | IOException ex) {
                System.out.println("Exception:"+ex);
            } 
        }
    }
    private void sendRequests(HashSet requests) throws IOException{
        out.writeObject(requests);
        out.flush();
    }
    private void waitForRequests()throws InterruptedException{
        synchronized(this){        
            wait();
        }
    }
}