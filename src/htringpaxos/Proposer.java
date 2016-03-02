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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
/**
 *
 * @author Vinitkumar
 */
public class Proposer extends HTRingPaxos{
Proposer() {
    }
ObjectOutputStream out=null;
public void runProposer() throws Exception {
    Socket s;
    int j=5000+a_num;
    s = new Socket("localhost",j);
    out = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
    Request request;
    while(true)
        {    
            while (true) { 
                try {
                    HashSet requests=new HashSet();
                    request=getRequest();
                    requests.add(request);
                    send(requests);
                    }
                catch (IOException e) {
                System.out.println("Exception:"+e);
                }
            }   
        }
    }
private void send(HashSet requests) throws IOException{
    out.writeObject(requests);
    out.flush();
}
BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
int i=0;
private Request getRequest() throws IOException{
    String fromuser;
    System.out.print("Request Value? ");
    fromuser = inFromUser.readLine();
    ++i;
    Request r = new Request(i,fromuser);
    return r;  
    }
}
