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

import java.io.IOException;

/**
 *
 * @author Vinitkumar
 */
public class Leader extends Coordinator implements Runnable {
    int count;
    Leader(){
    }
    @Override
    public void run() {
        if (leader==true){
            count=0;
            try {
                sendPhase1aMsg(count);
            } catch (ClassNotFoundException | InterruptedException | IOException ex) {}
            while(true){
                if(receivePhase1b){
                    while(true){
                        try {
                            sendPhase2aMsg(count);
                        } catch (ClassNotFoundException | InterruptedException | IOException ex) {}
                    }
                }
                synchronized(this){
                    try {
                        wait(2000);
                    } catch (InterruptedException e) {}
                } 
            }
        }
    }
    void sendPhase1aMsg(int count) throws ClassNotFoundException, InterruptedException, IOException{
        preparePhase1a(count);
        send(phase1a);
    }
    void sendPhase2aMsg(int count) throws ClassNotFoundException, InterruptedException, IOException{
       preparePhase2ab(count);
       send(phase2ab);
    }
}
