//package vn.viettel.customer.controller;
//
//import org.apache.commons.io.FileUtils;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import vn.viettel.core.util.XStreamTranslator;
//import vn.viettel.customer.entities.MemberCard;
//
//import java.io.File;
//import java.io.IOException;
//
//import static org.junit.Assert.*;
//
//public class XStreamTranslatorTest{
//    XStreamTranslator xStreamTranslatorInst;
//    MemberCard memberCard;
//    @Before
//    public void setUp() throws Exception {
//        memberCard = new MemberCard();
//        xStreamTranslatorInst = XStreamTranslator.getInstance();
//    }
//    /**
//     * @throws java.lang.Exception
//     */
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void memberCardToXMLStringNotNullTest() {
//        String xml = xStreamTranslatorInst.toXMLString(memberCard);
//        assertNotNull(xml);
//        System.out.print(xml);
//    }
//
//    @Test
//    public void memberCardToXMLStringVerifyTest() {
//        memberCard.setMemberCardName("Test");
//        assertEquals("Test",memberCard.getMemberCardName());
//        memberCard.setId(1L);
//        memberCard.setMemberCardCode("123");
//        memberCard.setStatus(1);
//        String xml = xStreamTranslatorInst.toXMLString(memberCard);
//        String expected = getExpectedStringOutOfMemberCard();
//        assertEquals(expected, xml.replaceAll("[\\n\\s\\t]+", ""));
//    }
//
//    @Test
//    public void xmlToMemberCardVerifyTest(){
//        String xml = getExpectedStringOutOfMemberCard();
//        MemberCard memberCard = (MemberCard) xStreamTranslatorInst.toObject(xml);
//        assertNotNull(memberCard);
//        System.out.print(memberCard.getId()+" "+memberCard.getMemberCardCode()+" "+memberCard.getMemberCardName());
//    }
//
//    @Test (expected=IOException.class)
//    public void xmlToMemberCardFromFileThatNotExists() throws IOException{
//        MemberCard memberCard = (MemberCard) xStreamTranslatorInst.toObject(new File("E:\\Vinamilk-KCH.API\\customer-service\\src\\test\\java\\vn\\viettel\\customer\\file\\test.xml"));
//        assertNotNull(memberCard);
//        assertEquals("Test",memberCard.getMemberCardName());
//    }
//
//    @Test
//    public void xmlToMemberCardFromFile() throws IOException{
//        MemberCard memberCard = (MemberCard) xStreamTranslatorInst.toObject(new File("E:\\Vinamilk-KCH.API\\customer-service\\src\\test\\java\\vn\\viettel\\customer\\file\\testMemberCard.xml"));
//        assertNotNull(memberCard);
//        assertEquals("Test",memberCard.getMemberCardName());
//        System.out.print(memberCard.getId()+" "+memberCard.getMemberCardCode()+" "+memberCard.getMemberCardName());
//    }
//
////    @Test
////    public void MemberCardToXmlFileTestForNotNull() throws IOException {
////        memberCard.setMemberCardName("Test");
////        assertEquals("Test",memberCard.getMemberCardName());
////        memberCard.setId(1L);
////        memberCard.setMemberCardCode("123");
////        memberCard.setStatus(1);
////        xStreamTranslatorInst.toXMLFile(memberCard);
////        File file = new File(memberCard.getClass().getName()+".xml");
////        assertTrue(file.exists());
////        String sample = FileUtils.readFileToString(file);
////        assertNotNull(sample);
////        System.out.print(sample);
////    }
//
//    private String getExpectedStringOutOfMemberCard() {
//        return "<vn.viettel.customer.entities.MemberCard><id>1</id><memberCardCode>123</memberCardCode><memberCardName>Test</memberCardName><status>1</status></vn.viettel.customer.entities.MemberCard>";
//    }
//}
