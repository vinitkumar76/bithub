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

import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Vinitkumar
 */
public class DatabaseHandelerIT {
    
    public DatabaseHandelerIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of saveRequests method, of class DatabaseHandeler.
     */
    @Test
    public void testSaveRequests() throws Exception {
        System.out.println("saveRequests");
        HashSet requests = null;
        DatabaseHandeler instance = new DatabaseHandeler();
        instance.saveRequests(requests);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRequests method, of class DatabaseHandeler.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetRequests() throws Exception {
        System.out.println("getRequests");
        DatabaseHandeler instance = new DatabaseHandeler();
        HashSet expResult = null;
        HashSet result = instance.getRequests();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of countRequests method, of class DatabaseHandeler.
     * @throws java.lang.Exception
     */
    @Test
    public void testCountRequests() throws Exception {
        System.out.println("countRequests");
        DatabaseHandeler instance = new DatabaseHandeler();
        int expResult = 0;
        int result = instance.countRequests();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
