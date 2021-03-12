package vn.viettel.saleservice.service.impl;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import vn.viettel.core.db.entity.*;
import vn.viettel.saleservice.repository.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceReport {
    @Autowired
    ReceiptImportRepository receiptImportRepository;
    @Autowired
    POConfirmRepository poConfirmRepository;
    @Autowired
    SOConfirmRepository soConfirmRepository;
    @Autowired
    POAdjustedRepository poAdjustedRepository;
    @Autowired
    POAdjustedDetailRepository poAdjustedDetailRepository;
    @Autowired
    POBorrowRepository poBorrowRepository;
    @Autowired
    POBorrowDetailRepository poBorrowDetailRepository;
    @Autowired
    PoPromotionalRepository poPromotionalRepository;
    @Autowired
    PoPromotionalDetailRepository poPromotionalDetailRepository;

    public String exportReport (String format, Long idRe)  throws FileNotFoundException, JRException {
        String path ="C:\\tmp";
        ReceiptImport re = receiptImportRepository.findById(idRe).get();
        Map<String,Object> parameters = new HashMap<>();

        if(re.getReceiptImportType() ==0 ){
            parameters.put("reType","Nhập hàng");
        }else if(re.getReceiptImportType() ==1){
            parameters.put("reType","Nhập điều chỉnh");
        }else if(re.getReceiptImportType() ==2){
            parameters.put("reType","Nhập vay mượn");
        }else if (re.getReceiptImportType()==3) {
            parameters.put("reType","Nhập khuyến mãi");
        }else parameters.put("reType","");
        if(re.getReceiptImportCode()!=null){
            parameters.put("reCode",re.getReceiptImportCode());
        }else parameters.put("reCode","");
        if(re.getPoNumber()!=null){
            parameters.put("rePoNum",re.getPoNumber());
        }else parameters.put("rePoNum","");
        if(re.getInvoiceNumber()!=null){
            parameters.put("reInvoiceNum",re.getInvoiceNumber());
        }else parameters.put("reInvoiceNum","");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String reDate = re.getReceiptImportDate().format(formatter);
        if(reDate!=null){
            parameters.put("reDate",reDate);
        }else parameters.put("reDate","");
        if(re.getInternalNumber()!=null){
            parameters.put("reInternal",re.getInternalNumber());
        }else parameters.put("reInternal","");
        String reInDate = re.getInvoiceDate().format(formatter);
        if(reInDate!=null){
            parameters.put("reInvoiceDate",reInDate);
        }else parameters.put("reInvoiceDate","");
        if (re.getNote()!=null){
            parameters.put("reNote",re.getNote());
        }else parameters.put("reNote","");

        if(re.getReceiptImportType() == 0 ){
            POConfirm po = poConfirmRepository.findPOConfirmByPoNo(re.getPoNumber());
            if(po == null) return "po = null";
            List<SOConfirm> so = soConfirmRepository.findAllByPoConfirmId(po.getId());
            List<SOConfirm> so0 = soConfirmRepository.getListProduct1ByPoId(po.getId());
            parameters.put("listProduct0",so0);

            if(so == null) return "so = null";
            File file = ResourceUtils.getFile("classpath:report-invoice.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(so);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters,dataSource);
            if(format.equalsIgnoreCase("html")){
                JasperExportManager.exportReportToHtmlFile(jasperPrint,path+"\\report.html");
            }
            if (format.equalsIgnoreCase("pdf")){
                JasperExportManager.exportReportToPdfFile(jasperPrint,path+"\\report.pdf");
            }
            return "report generated in path :" + path;
        }
        if(re.getReceiptImportType() == 1 ){
            POAdjusted pa = poAdjustedRepository.findByPoAdjustedNumber(re.getPoNumber());
            if(pa == null){
                return "pa = null";
            }
            List<POAdjustedDetail> pad = poAdjustedDetailRepository.findAllByPoAdjustedId(pa.getId());
            if(pad == null)
                return "pad = null";
            File file = ResourceUtils.getFile("classpath:report-invoice.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pad);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters,dataSource);
            if(format.equalsIgnoreCase("html")){
                JasperExportManager.exportReportToHtmlFile(jasperPrint,path+"\\report.html");
            }
            if (format.equalsIgnoreCase("pdf")){
                JasperExportManager.exportReportToPdfFile(jasperPrint,path+"\\report.pdf");
            }
            return "report generated in path :" + path;
        }
        if(re.getReceiptImportType() == 2 ){
            POBorrow pb = poBorrowRepository.findPOBorrowByPoBorrowNumber(re.getPoNumber());
            if(pb == null) return "pb = null";
            List<POBorrowDetail> pbd = poBorrowDetailRepository.findAllByPoBorrowId(pb.getId());
            if(pbd == null) return "pbd = null";
            File file = ResourceUtils.getFile("classpath:report-invoice.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pbd);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters,dataSource);
            if(format.equalsIgnoreCase("html")){
                JasperExportManager.exportReportToHtmlFile(jasperPrint,path+"\\report.html");
            }
            if (format.equalsIgnoreCase("pdf")){
                JasperExportManager.exportReportToPdfFile(jasperPrint,path+"\\report.pdf");
            }
            return "report generated in path :" + path;
        }
        if(re.getReceiptImportType() == 3 ){
            PoPromotional pp = poPromotionalRepository.findPoPromotionalByPoPromotionalNumber(re.getPoNumber());
            List<PoPromotionalDetail> ppd = poPromotionalDetailRepository.findPoPromotionalDetailsByPoPromotionalId(pp.getId());
            File file = ResourceUtils.getFile("classpath:report-invoice.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ppd);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters,dataSource);
            if(format.equalsIgnoreCase("html")){
                JasperExportManager.exportReportToHtmlFile(jasperPrint,path+"\\report.html");
            }
            if (format.equalsIgnoreCase("pdf")){
                JasperExportManager.exportReportToPdfFile(jasperPrint,path+"\\report.pdf");
            }
            return "report generated in path :" + path;
        }


        return "" ;
    }



}
