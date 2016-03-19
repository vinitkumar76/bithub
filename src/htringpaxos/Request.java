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

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;
/**
 *
 * @author Vinitkumar
 */
final public class Request implements Serializable{
    int reqNum,port;
    String str;
    InetAddress ip;
    public Request(int reqNum, String str) {
        this.ip = null;
        this.port = 0;
        this.reqNum = reqNum;
        this.str = str;
    }
    /**
     *
     * @param obj
     * @return
     */
   
    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.reqNum;
        hash = 89 * hash + this.port;
        hash = 89 * hash + Objects.hashCode(this.str);
        hash = 89 * hash + Objects.hashCode(this.ip);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Request other = (Request) obj;
        if (this.reqNum != other.reqNum) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        if (!Objects.equals(this.str, other.str)) {
            return false;
        }
        return Objects.equals(this.ip, other.ip);
    }
    @Override
    public String toString(){
        return "["+this.ip+":"+this.port+"/"+this.reqNum+"/"+this.str+"]:Hash code->"+this.hashCode();
    }
}