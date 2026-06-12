
package com.mycompany.message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
public class MessageTest {
    //RESET ALL STATIC STATE BEFORE EVERY TEST//
    @BeforeEach
    public void resetStatics() {
        Message.MessageData.totalSent = 0;
        Message.MessageData.sentMessages.clear();
        Message.MessageData.storedMessages.clear();
        Message.MessageData.disregardMessages.clear();
        Message.MessageData.messageHashes.clear();
        Message.MessageData.messageIDs.clear();
    }
    //LOGIN TESTS//
    @Test
    public void testUsernameValid() {
//kyl_1 CONTAINS UNDERSCORE AND IS 5 CHARS - SHOULD PASS//
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!",
                "+27838968976");
        assertTrue(user.checkUserName());
    }
    @Test
    public void testUsernameInvalid() {
//NO UNDERSCORE AND MORE THAN 5 CHARS - SHOULD FAIL//
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyle!!!!!", "Ch&&sec@ke99!",
                "+27838968976");
        assertFalse(user.checkUserName());
    }
    @Test
    public void testPasswordValid() {
//Ch&&sec@ke99! HAS CAPITAL, NUMBER, SPECIAL - SHOULD PASS//
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!",
                "+27838968976");
        assertTrue(user.checkPasswordComplexity());
    }
    @Test
    public void testPasswordInvalid() {
//password IS ALL LOWERCASE - SHOULD FAIL//
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "password", "+27838968976");
        assertFalse(user.checkPasswordComplexity());
    }
    @Test
    public void testCellValid() {
//+27838968976 MATCHES +27 FORMAT - SHOULD PASS//
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!",
                "+27838968976");
        assertTrue(user.checkCellPhoneNumber());
    }
    @Test
    public void testCellInvalid() {
//08966553 HAS NO INTERNATIONAL CODE - SHOULD FAIL//
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!", "08966553");
        assertFalse(user.checkCellPhoneNumber());
    }
    @Test
    public void testLoginSuccess() {
//CORRECT CREDENTIALS - SHOULD RETURN TRUE//
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!",
                "+27838968976");
        assertTrue(user.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }
    @Test
    public void testLoginFail() {




        //WRONG PASSWORD - SHOULD RETURN FALSE//
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!",
                "+27838968976");
        assertFalse(user.loginUser("kyl_1", "wrongpassword"));
    }
    @Test
    public void testLoginStatusSuccess() {
//CORRECT CREDENTIALS - SHOULD RETURN WELCOME MESSAGE//
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!",
                "+27838968976");
        assertEquals(
                "Welcome Kyle Jackson, it is great to see you again!",
                user.returnLoginStatus("kyl_1", "Ch&&sec@ke99!")
        );
    }
    @Test
    public void testLoginStatusFail() {
//WRONG CREDENTIALS - SHOULD RETURN FAILURE MESSAGE//
        Message.Login user = new Message.Login("Kyle", "Jackson", "kyl_1", "Ch&&sec@ke99!",
                "+27838968976");
        assertEquals(
                "Username or password incorrect, please try again.",
                user.returnLoginStatus("kyl_1", "wrongpassword")
        );
    }
    //MESSAGE LENGTH TESTS//
    @Test
    public void testMessageLengthValid() {
//SHORT MESSAGE - SHOULD RETURN READY TO SEND//
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us
        for dinner tonight?");
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }
    @Test
    public void testMessageLengthTooLong() {
//260 CHARS IS 10 OVER LIMIT - SHOULD RETURN ERROR WITH OVERCOUNT//
        String longMsg = "A".repeat(260);
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", longMsg);
        assertTrue(msg.checkMessageLength().contains("exceeds 250 characters by 10"));
    }
    //RECIPIENT TESTS//
    @Test
    public void testRecipientValid() {
//VALID +27 NUMBER - SHOULD RETURN SUCCESS//
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us
        for dinner tonight?");
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }
    @Test
    public void testRecipientInvalid() {
//NO INTERNATIONAL CODE - SHOULD RETURN FAILURE//
        Message.MessageData msg = new Message.MessageData(2, "08575975889", "Hi Keegan, did you receive
                the payment?");
                assertTrue(msg.checkRecipientCell().contains("incorrectly formatted or does not contain an
                        international code"));
    }
    //MESSAGE HASH TESTS//
    @Test
    public void testMessageHashUppercase() {
//HASH MUST BE ALL UPPERCASE//
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us
        for dinner tonight?");
        String hash = msg.getMessageHash();
        assertEquals(hash, hash.toUpperCase());
    }
    @Test
    public void testMessageHashFormat() {
//HASH MUST CONTAIN COLONS AND CORRECT WORD COMBINATION//
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us
        for dinner tonight?");
        assertTrue(msg.getMessageHash().contains(":1:HITONIGHT?"));
    }
    //SENTMESSAGE OPTION TESTS//
    @Test
    public void testSentMessageSend() {
//CHOICE 1 - SHOULD RETURN SENT MESSAGE//




        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us
        for dinner tonight?");
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }
    @Test
    public void testSentMessageDisregard() {
//CHOICE 2 - SHOULD RETURN DISREGARD MESSAGE//
        Message.MessageData msg = new Message.MessageData(2, "08575975889", "Hi Keegan, did you receive
                the payment?");
                assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }
    @Test
    public void testSentMessageStore() {
//CHOICE 3 - SHOULD RETURN STORED MESSAGE//
        Message.MessageData msg = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us
        for dinner tonight?");
        assertEquals("Message successfully stored.", msg.sentMessage(3));
    }
    //TOTAL MESSAGES TEST//
    @Test
    public void testReturnTotalMessages() {
//SEND TWO MESSAGES - TOTAL SHOULD EQUAL 2//
        Message.MessageData m1 = new Message.MessageData(1, "+27718693002", "Hi Mike, can you join us
        for dinner tonight?");
        m1.sentMessage(1);
        Message.MessageData m2 = new Message.MessageData(2, "+27718693002", "Did you get the cake?");
        m2.sentMessage(1);
        assertEquals(2, Message.MessageData.returnTotalMessages());
    }
    //PART 3 - HELPER THAT LOADS THE FIVE OFFICIAL TEST MESSAGES INTO THE ARRAYS//
    private void loadPart3TestData() {
        Message.MessageData m1 = new Message.MessageData(1, "+27834557896", "Did you get the cake?",
                "1000000001");
        Message.MessageData m2 = new Message.MessageData(2, "+27838884567", "Where are you? You are late!
                I have asked you to be on time.", "1000000002");
                Message.MessageData m3 = new Message.MessageData(3, "+27834484567", "Yohoooo, I am at your gate.",
                "1000000003");
        Message.MessageData m4 = new Message.MessageData(4, "0838884567", "It is dinner time !",
                "1000000004");
        Message.MessageData m5 = new Message.MessageData(5, "+27838884567", "Ok, I am leaving without
                you.", "1000000005");
                m1.sentMessage(1);
        m2.sentMessage(3);
        m3.sentMessage(2);
        m4.sentMessage(1);
        m5.sentMessage(3);
    }
    //PART 3 - SENT MESSAGES ARRAY TEST//
    @Test
    public void testSentMessagesArrayPopulated() {
//SENT MESSAGES ARRAY SHOULD CONTAIN MESSAGE 1 AND MESSAGE 4 TEXT - SHOULD PASS//
        loadPart3TestData();
        boolean hasCake = false;
        boolean hasDinner = false;
        for (Message.MessageData m : Message.MessageData.sentMessages) {
            if (m.getMessageText().equals("Did you get the cake?")) hasCake = true;
            if (m.getMessageText().equals("It is dinner time !")) hasDinner = true;
        }
        assertTrue(hasCake);
        assertTrue(hasDinner);
    }
    //PART 3 - LONGEST MESSAGE TEST//
    @Test
    public void testDisplayLongestMessage() {
//MESSAGE 2 IS THE LONGEST OF ALL FIVE MESSAGES - SHOULD MATCH MESSAGE 2 TEXT//
        loadPart3TestData();
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.MessageData.displayLongestMessage());
    }
    //PART 3 - SEARCH BY MESSAGE ID TEST//
    @Test
    public void testSearchByMessageID() {
//SEARCHING FOR MESSAGE 4 ID SHOULD RETURN "It is dinner time !" - SHOULD PASS//
        loadPart3TestData();
        assertTrue(Message.MessageData.searchByMessageID("1000000004").contains("It is dinner time !"));
    }




    //SEARCH BY RECIPIENT TEST//
    @Test
    public void testSearchByRecipient() {
//+27838884567 HAS TWO MESSAGES - SHOULD RETURN BOTH//
        loadPart3TestData();
        String result = Message.MessageData.searchByRecipient("+27838884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("Ok, I am leaving without you."));
    }
    //PART 3 - DELETE BY HASH TEST//
    @Test
    public void testDeleteMessageByHash() {
//DELETING MESSAGE 2 BY ITS HASH SHOULD RETURN A SUCCESS MESSAGE - SHOULD PASS//
        loadPart3TestData();
        String hashToDelete = null;
        for (Message.MessageData m : Message.MessageData.storedMessages) {
            if (m.getMessageText().equals("Where are you? You are late! I have asked you to be on time.")) {
                hashToDelete = m.getMessageHash();
                break;
            }
        }
        assertNotNull(hashToDelete);
        assertTrue(Message.MessageData.deleteMessageByHash(hashToDelete).contains("successfully
                deleted"));
    }
    //PART 3 - DISPLAY REPORT TEST//
    @Test
    public void testDisplayReport() {
//REPORT SHOULD INCLUDE HASH, RECIPIENT AND MESSAGE FOR ALL SENT MESSAGES - SHOULD PASS//
        loadPart3TestData();
        String report = Message.MessageData.displayReport();
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time !"));
        assertTrue(report.contains("Message Hash"));
        assertTrue(report.contains("Recipient"));
    }
} 