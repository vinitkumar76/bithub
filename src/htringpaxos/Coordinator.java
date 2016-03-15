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

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Vinitkumar
 */
public class Coordinator extends Acceptor implements Runnable{
    Coordinator(){
    }
    static boolean leader=false;
    @Override
    public void run(){
        if (a_num==0){
            leader=true;
            lsn=1;
            crnd=lsn;
            sn=0;
            callLeader();
            while(true){
                try{
                
                }catch(Exception e){}    
            }
        }
    }
    void preparePhase1a(){
        Queue phase1a=new LinkedList();
        phase1a.add("1a");
        phase1a.add(i);
        phase1a.add(crnd);
    }
    void preparePhase1b(){
        Queue phase1b=new LinkedList();
        phase1b.add("1b");
        phase1b.add(i);
        phase1b.add(rnd);
        phase1b.add(vrnd);
        phase1b.add(vval);
    }
    void preparePhase2a(){
        Queue phase2a=new LinkedList();
        phase2a.add("2a");
        phase2a.add(i);
        phase2a.add(crnd);
        phase2a.add(cval);
    }
    void preparePhase2a2b(){
        Queue phase2a2b=new LinkedList();
        phase2a2b.add("2a/2b");
        phase2a2b.add(i);
        phase2a2b.add(crnd);
        phase2a2b.add(cval);
        phase2a2b.add(sn);
    }
    void callLeader(){
        Thread t1=new Thread(new Leader());
        t1.setDaemon(true);
        t1.start();  
    }
}
