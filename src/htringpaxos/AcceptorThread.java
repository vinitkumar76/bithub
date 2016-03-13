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

/**
 *
 * @author Vinitkumar
 */
public class AcceptorThread extends Acceptor implements Runnable{
    @Override
    public void run(){
        try {
            synchronized (this){
                while(a_num<0||a_total<0){
                  wait(2000);  
                }
            }
            Thread t1=new Thread(new Acceptor());
            t1.setDaemon(true);
            t1.start();
            if (leader==true){
                Thread t2=new Thread(new Acceptor());
                t2.setDaemon(true);
                t2.start();
            }
            runAcceptor();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }    
}
