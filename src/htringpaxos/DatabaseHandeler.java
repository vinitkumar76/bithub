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
    int row_count,id;
    Request request;
    HashSet requests=new HashSet();
    HashSet requests2=new HashSet();
    final String URL="jdbc:oracle:thin:@localhost:1521:XE";
    /**
     *
     * @param requests
     * @throws SQLException
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public void saveRequests(HashSet requests)throws SQLException, IOException, ClassNotFoundException {
        ObjectOutputStream out;
        synchronized(DatabaseHandeler.class){
            //insert the requests into database
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            requests2=getRequests();
            for (Iterator it = requests.iterator(); it.hasNext();) {
                request=(Request) it.next();
                requests2.add(request);
             }
            Connection con;
            con = DriverManager.getConnection(URL, "vinit76","vkb1234");
            Blob blob;
            blob = con.createBlob();
            out=new ObjectOutputStream(blob.setBinaryStream(1));
            out.writeObject(requests2);
            out.flush();
            PreparedStatement psmt;
            psmt=con.prepareStatement("INSERT INTO REQUESTS VALUES(?,?)");
            psmt.setObject(1,blob);
            psmt.setInt(2,id+1);
            psmt.executeUpdate();
            con.close();
        }
    }
    /**
     *
     * @return HashSet requests 
     * @throws SQLException
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    protected HashSet getRequests()throws SQLException, IOException, ClassNotFoundException {
        
        synchronized(DatabaseHandeler.class){
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            ObjectInputStream in;
            InputStream inn;
            ResultSet rs1;
            Connection con;
            con = DriverManager.getConnection(URL, "vinit76","vkb1234");
            Blob blob;
            Statement stm=con.createStatement();
            id=countRequests();
            rs1=stm.executeQuery("SELECT * FROM REQUESTS WHERE ID="+id);
            while(rs1.next()){
                blob=(Blob) rs1.getObject(1);
                inn=blob.getBinaryStream();
                in=new ObjectInputStream(inn);
                requests=(HashSet) in.readObject();
            }
            con.close();  
        }
        return requests;
    }
    /**
     *
     * @return total number of available requests in database
     * @throws SQLException
     * 
     */
    public int countRequests() throws SQLException{
        ResultSet rs2;
        Statement stm;
        Connection con;
        con = DriverManager.getConnection(URL, "vinit76","vkb1234");
        stm=con.createStatement();
        rs2=stm.executeQuery("SELECT COUNT(*) FROM REQUESTS");
        while(rs2.next()){
            row_count=rs2.getInt(1);
        }
        con.close();
        return row_count;
    }
}