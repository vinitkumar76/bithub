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
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Vinitkumar
 */
public class Acceptor implements Runnable {
boolean leader=false;
Socket s;
ObjectInputStream in,fileIn;
ObjectOutputStream out,fileOut;
BufferedReader inFromUser;
private boolean forwardingReq;
    Acceptor() {
    }
    Acceptor(boolean forwardingReq) throws FileNotFoundException, IOException {
            this.forwardingReq = forwardingReq;
    }
    Acceptor(Socket s) throws IOException {
        try{
        this.s = s;
        in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
        fileOut= new ObjectOutputStream(new FileOutputStream("RequestFile"));
        inFromUser= new BufferedReader(new InputStreamReader(System.in));
        } catch(IOException e){
            System.out.println("Exception:"+e);
        }
    }
    /**
     * @throws java.lang.Exception
     */
    public void runAcceptor() throws Exception {
        ServerSocket ss = new ServerSocket (5000);
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
            forwardRequests();
        }
        //getting requests from proposers
        else{
            try {
                receiveRequests();
                } catch (Exception ex) {
                Logger.getLogger(Acceptor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
    ArrayList requests;
    Request request;
    
    
    private void forwardRequests(){
        try {
            forwardingReq=false;
            ArrayList reqs = new ArrayList();
            Request req;
            Socket socket;
            socket = new Socket("localhost",5000);
            out= new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            FileInputStream filestream=new FileInputStream("RequestFile");
            fileIn=new ObjectInputStream(filestream);
            while(true){
                try{
                    while(true)
                    {
                        try{
                            req=(Request) fileIn.readObject();
                            reqs.add(req);
                            if(reqs.size()>=5){
                                ArrayList r=(ArrayList) reqs.clone();
                                send(r);
                                reqs.clear();
                            }
                        }catch(EOFException|ClassNotFoundException e){
                            while(true){
                                synchronized(this){
                                    try {
                                        this.wait(1000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Acceptor.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                if(filestream.available()>10) break;
                            }
                        }
                    }
                }catch(IOException e){
                    System.out.println("Exception:"+e);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Acceptor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void receiveRequests()throws Exception {
        while ( true ) {
            try {
                requests = (ArrayList) in.readObject();
                for (Iterator it = requests.iterator(); it.hasNext();) {
                    request = (Request) it.next();
                    System.out.print("Request Received from "+s.getInetAddress()+":"+ s.getPort()+"->"+request.str);
                    System.out.println(" [Request Number#"+request.reqNum+"]");
                    if ((request.ip)==null) request.ip=s.getInetAddress();
                    if ((request.port)==0) request.port=s.getPort();
                    fileOut.writeObject(request);
                }
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(Acceptor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private void send(ArrayList requests) throws IOException{
        out.writeObject(requests);
        out.flush();
    }
    ArrayList reqIds = new ArrayList();
    ReqId reqId=new ReqId();
}