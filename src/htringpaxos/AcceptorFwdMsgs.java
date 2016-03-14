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
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Vinitkumar
 */
public class AcceptorFwdMsgs extends Acceptor implements Runnable{
    AcceptorFwdMsgs() {
    }
    @Override
    public void run(){
        try {
            getRequests();
            forwardRequests();
        } catch (SQLException | ClassNotFoundException | InterruptedException | IOException ex) {
            System.out.println("Exception:"+ex);
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
                    //preparing batch
                    for (Iterator it = fwdRequests.iterator(); it.hasNext();) {
                        Request r = (Request) it.next();
                        ReqId rId=new ReqId();
                        rId.ip=r.ip;
                        rId.port=r.port;
                        rId.reqNum=r.reqNum;
                        batch.add(rId);
                    }
                    fwdRequests.clear();
                    socket.close();
                }catch(IOException e){
                    socket.close();
                }
            }
        }
    }
}
