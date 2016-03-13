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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
/**
 *
 * @author Vinitkumar
 */
public class DatabaseHandeler extends HTRingPaxos{
    private static HashSet requestsDb=new HashSet();
    protected static HashSet fwdRequests=new HashSet();
    final String URL="jdbc:oracle:thin:@localhost:1521:XE";
    /**
     * Saves requests into database
     * @param requests 
     * @throws SQLException
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public void saveRequests(HashSet requests)throws SQLException, IOException, ClassNotFoundException {
        ObjectOutputStream out;
        Request req;
        synchronized(DatabaseHandeler.class){
            Connection con;
            Blob blob;
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            con = DriverManager.getConnection(URL, "vinit76","vkb1234");
            if (requestsDb.isEmpty()){
                System.out.println("RequestDb Empty");
                requestsDb=(HashSet) requests.clone();
                fwdRequests=(HashSet) requestsDb.clone();
            }else{
                for (Iterator it = requests.iterator(); it.hasNext();) {
                    req=(Request) it.next();
                    if(!requestsDb.contains(req)){
                        System.out.println(req+" is not an element of REQ_SET. Inserting now.....");
                        requestsDb.add(req);
                        fwdRequests.add(req);
                    }else System.out.println(req+" is already an element of REQ_SET");
                }
            }
            blob = con.createBlob();
            out=new ObjectOutputStream(blob.setBinaryStream(1));
            out.writeObject(requestsDb);
            out.flush();
            //System.out.println(requestsDb);
            PreparedStatement psmt;
            psmt=con.prepareStatement("UPDATE SETS SET REQ_SET=? WHERE A_NUM=?");
            psmt.setObject(1,blob);
            psmt.setInt(2,a_num);
            psmt.executeUpdate();
            con.close();
        }
    }
    /**
     * 
     * @throws SQLException
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    protected void getRequests()throws SQLException, IOException, ClassNotFoundException {
        synchronized(DatabaseHandeler.class){
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            ObjectInputStream in;
            InputStream inn;
            ResultSet rs1;
            Connection con;
            con = DriverManager.getConnection(URL, "vinit76","vkb1234");
            Blob blob;
            Statement stm=con.createStatement();
            rs1=stm.executeQuery("SELECT * FROM SETS WHERE A_NUM="+a_num);
            while(rs1.next()){
                blob=(Blob) rs1.getObject(2);
                if (blob!=null){
                    inn=blob.getBinaryStream();
                    in=new ObjectInputStream(inn);
                    requestsDb=(HashSet) in.readObject();
                    fwdRequests=(HashSet) requestsDb.clone();
                }
            }
            con.close();  
        }
    }
}