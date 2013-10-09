/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

package org.opensolaris.opengrok.history;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensolaris.opengrok.configuration.RuntimeEnvironment;
import org.opensolaris.opengrok.util.TestRepository;

/**
 *
 * @author michailf
 */
public class SSCMHistoryParserTest {
    
    SSCMHistoryParser instance;

    public SSCMHistoryParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        instance = new SSCMHistoryParser(new SSCMRepository());
    }

    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of parse method, of class GitHistoryParser.
     */
    @Test
    public void parseEmpty() throws Exception {
        History result = instance.parse("");
        assertNotNull(result);
        assertTrue("Should not contain any history entries", 0 == result.getHistoryEntries().size());
    }

    /**
     * Test of parsing output similar to that in Surround SCM own repository.
     */
    @Test 
    public void ParseALaSSCM() throws Exception {
        // This is the new line that is generated by sscm (2011) history command
        String newLine = "\r\r\n";
        
        String author1 = "Michailf";
        String author3 = "Joec";
        String author4 = "user4";
        String author5 = "user5";
        String date1 = "9/7/2013 1:10 PM";
        String date2 = "10/8/2013 1:13 PM";
        String date3 = "10/8/2013 1:14 PM";
        String date4 = "10/8/2013 1:15 PM";
        String date5 = "10/18/2013 11:16 PM";
        String comment3 = "Change with check in." + newLine +
                            "" + newLine +
                            "Third comment line";
        String comment5 = "Comment with promote";
        String output =
                "Full file path:       Branch1/Repo with spaces/readme.txt" + newLine +
                "Branch:               Branch1" + newLine +
                "Working directory:    C:\\scm\\Branch1\\Repo with spaces" + newLine +
                "Action:                               User:         Version:              Date:" + newLine +
                "add                                   Michailf      1          " + date1 + newLine +
                "add to branch[Bug#00000008 - Branch to work on changes.]" + newLine +
                "                                      Joec          1         " + date2 + newLine +
                "checkin                               Joec          2         " + date3 + newLine +
                " Comments - " + comment3 + newLine +
                "promote from[Bug#00000008 - Branch to work on changes. v. 2]" + newLine +
                "                                      user4         3         " + date4 + newLine +
                "promote from[Bug#00000008 - Branch to work on changes. v. 3]" + newLine +
                "                                      user5         4        " + date5 + newLine +
                " Comments - " + comment5 +  newLine;

        History result = instance.parse(output);
        assertNotNull(result);
        // History entries that do not increment version number are not included
        //  (no file changes)
        assertTrue("Should contain four history entries", 4 == result.getHistoryEntries().size());
        // History entries are reversed (newest first)
        {
            HistoryEntry e0 = result.getHistoryEntries().get(0);
            assertEquals("4", e0.getRevision());
            assertEquals(author5, e0.getAuthor());
            assertTrue(!e0.getMessage().isEmpty());
            assertEquals(0, e0.getFiles().size());
        }
        
        {
            HistoryEntry e1 = result.getHistoryEntries().get(1);
            assertEquals("3", e1.getRevision());
            assertEquals(author4, e1.getAuthor());
            assertTrue(!e1.getMessage().isEmpty());
            assertEquals(0, e1.getFiles().size());
        }
        
        {
            HistoryEntry e2 = result.getHistoryEntries().get(2);
            assertEquals("2", e2.getRevision());
            assertEquals(author3, e2.getAuthor());
            assertTrue(!e2.getMessage().isEmpty());
            assertEquals(0, e2.getFiles().size());
        }
        
        {
            HistoryEntry e3 = result.getHistoryEntries().get(3);
            assertEquals("1", e3.getRevision());
            assertEquals(author1, e3.getAuthor());
            assertTrue(e3.getMessage().isEmpty());
            assertEquals(0, e3.getFiles().size());
        }
    }
}
