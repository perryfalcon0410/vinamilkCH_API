package vn.viettel.core.convert;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import com.thoughtworks.xstream.XStream;
import org.springframework.web.multipart.MultipartFile;

public final class XStreamTranslator extends XStream {
    private XStream xstream = null;
    private XStreamTranslator(){
        xstream = new XStream();
        xstream.ignoreUnknownElements();
        XStream.setupDefaultSecurity(xstream);
    }

    public void processAnnotations(Object o)
    {
        xstream.processAnnotations(o.getClass());
    }
    /**
     * Convert a any given Object to a XML String
     * @param object
     * @return
     */
    public String toXMLString(Object object) {
        return xstream.toXML(object);
    }
    /**
     * Convert given XML to an Object
     * @param xml
     * @return
     */
    public Object toObject(String xml) {
        return (Object) xstream.fromXML(xml);
    }
    /**
     * return this class instance
     * @return
     */
    public static XStreamTranslator getInstance(){
        return new XStreamTranslator();
    }
    /**
     * convert to Object from given File
     * @param xmlFile
     * @return
     * @throws IOException
     */
    public Object toObject(File xmlFile) throws IOException {
        return xstream.fromXML(new FileReader(xmlFile));
    }

    /**
     * convert to Object from given Multipart file
     * */
    public Object toObject(MultipartFile xmlFile) throws IOException {
        InputStream input = xmlFile.getInputStream();
        return xstream.fromXML(input);
    }
    /**
     * create XML file from the given object with custom file name
     * @param fileName
     * @throws IOException
     */
    public void toXMLFile(Object objTobeXMLTranslated, String fileName ) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        xstream.toXML(objTobeXMLTranslated, writer);
        writer.close();
    }
    public void toXMLFile(Object objTobeXMLTranslated, String fileName, List omitFieldsRegXList) throws IOException {
        xstreamInitializeSettings(objTobeXMLTranslated, omitFieldsRegXList);
        toXMLFile(objTobeXMLTranslated, fileName);
    }
    /**
     * @
     * @param objTobeXMLTranslated
     */
    public void xstreamInitializeSettings(Object objTobeXMLTranslated, List omitFieldsRegXList) {
        if(omitFieldsRegXList != null && omitFieldsRegXList.size() > 0){
            Iterator itr = omitFieldsRegXList.iterator();
            while(itr.hasNext()){
                String omitEx = (String) itr.next();
                xstream.omitField(objTobeXMLTranslated.getClass(), omitEx);
            }
        }
    }
    /**
     * create XML file from the given object, file name is generated automatically (class name)
     * @param objTobeXMLTranslated
     * @throws IOException
     */
    public void toXMLFile(Object objTobeXMLTranslated) throws IOException {
        toXMLFile(objTobeXMLTranslated,objTobeXMLTranslated.getClass().getName()+".xml");
    }

    public void allowTypes(Class[] classes) {
        xstream.allowTypes(classes);
    }
}