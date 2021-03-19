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
        ReceiptImport reci = receiptImportRepository.findById(idRe).get();
        if (reci.getReceiptImportType()==null)
            reci.setReceiptImportType(0);

        Map<String,Object> parameters = new HashMap<>();

        if(reci.getReceiptImportType() ==0 ){
            parameters.put("reType","Nhập hàng");
        }else if(reci.getReceiptImportType() ==1){
            parameters.put("reType","Nhập điều chỉnh");
        }else if(reci.getReceiptImportType() ==2){
            parameters.put("reType","Nhập vay mượn");
        }else if (reci.getReceiptImportType()==3) {
            parameters.put("reType","Nhập khuyến mãi");
        }else parameters.put("reType","");
        if(reci.getReceiptImportCode()!=null){
            parameters.put("reCode",reci.getReceiptImportCode());
        }else parameters.put("reCode","");
        if(reci.getPoNumber()!=null){
            parameters.put("rePoNum",reci.getPoNumber());
        }else parameters.put("rePoNum","");
        if(reci.getInvoiceNumber()!=null){
            parameters.put("reInvoiceNum",reci.getInvoiceNumber());
        }else parameters.put("reInvoiceNum","");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//        String reDate = reci.getReceiptImportDate().format(formatter);
        String reDate = reci.getReceiptImportDate().toString();
        if(reDate!=null){
            parameters.put("reDate",reDate);
        }else parameters.put("reDate","");
        if(reci.getInternalNumber()!=null){
            parameters.put("reInternal",reci.getInternalNumber());
        }else parameters.put("reInternal","");
        String reInDate = reci.getInvoiceDate().toString();
        if(reInDate!=null){
            parameters.put("reInvoiceDate",reInDate);
        }else parameters.put("reInvoiceDate","");
        if (reci.getNote()!=null){
            parameters.put("reNote",reci.getNote());
        }else parameters.put("reNote","");

        if(reci.getReceiptImportType() == 0 ){
            POConfirm po = poConfirmRepository.findPOConfirmByPoNumber(reci.getPoNumber());
            if(po == null) return "po = null";
            List<SOConfirm> so = soConfirmRepository.findAllByPoConfirmId(po.getId());
            List<SOConfirm> so0 = soConfirmRepository.findAllByPoConfirmId(po.getId());
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
        if(reci.getReceiptImportType() == 1 ){
            POAdjusted pa = poAdjustedRepository.findByPoAdjustedNumber(reci.getPoNumber());
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
        if(reci.getReceiptImportType() == 2 ){
            POBorrow pb = poBorrowRepository.findPOBorrowByPoBorrowNumber(reci.getPoNumber());
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
        if(reci.getReceiptImportType() == 3 ){
            PoPromotional pp = poPromotionalRepository.findPoPromotionalByPoPromotionalNumber(reci.getPoNumber());
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
