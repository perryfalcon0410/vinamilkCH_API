package vn.viettel.saleservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.viettel.core.db.entity.*;
import vn.viettel.core.messaging.Response;
import vn.viettel.saleservice.repository.*;
import vn.viettel.saleservice.service.PoService;
import vn.viettel.saleservice.service.dto.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class PoServiceImpl implements PoService {
    @Autowired
    POConfirmRepository poConfirmRepository;
    @Autowired
    POAdjustedRepository poAdjustedRepository;
    @Autowired
    POBorrowRepository poBorrowRepository;
    @Autowired
    POAdjustedDetailRepository poAdjustedDetailRepository;
    @Autowired
    SOConfirmRepository soConfirmRepository;
    @Autowired
    POBorrowDetailRepository poBorrowDetailRepository;


    @Override
    public Response<List<POConfirmDTO>> getAllPoConfirm() {
        List<POConfirm> poConfirms = poConfirmRepository.findAll();
        List<POConfirmDTO> poConfirmsList = new ArrayList<>();
        for (POConfirm pc : poConfirms) {
            POConfirmDTO poConfirm = new POConfirmDTO();
            poConfirm.setId(pc.getId());
            poConfirm.setPoNo(pc.getPoNo());
            poConfirm.setInternalNumber(pc.getInternalNumber());
            poConfirm.setPoDate(pc.getPoDate());
            poConfirmsList.add(poConfirm);
        }
        Response<List<POConfirmDTO>> response = new Response<>();
        response.setData(poConfirmsList);
        return response;
    }

    @Override
    public Response<List<POAdjustedDTO>> getAllPoAdjusted() {
        List<POAdjusted> poAdjusteds = poAdjustedRepository.findAll();
        List<POAdjustedDTO> poAdjustedList = new ArrayList<>();
        for (POAdjusted pa : poAdjusteds) {
            POAdjustedDTO poAdjusted = new POAdjustedDTO();
            poAdjusted.setId(pa.getId());
            poAdjusted.setPoLicenseNumber(pa.getPoLicenseNumber());
            poAdjusted.setPoNote(pa.getPoNote());
            poAdjusted.setPoDate(pa.getPoDate());
            poAdjustedList.add(poAdjusted);
        }
        Response<List<POAdjustedDTO>> response = new Response<>();
        response.setData(poAdjustedList);
        return response;
    }

    @Override
    public Response<List<POBorrowDTO>> getAllPoBorrow() {
        List<POBorrow> poBorrows = poBorrowRepository.findAll();
        List<POBorrowDTO> poBorrowList = new ArrayList<>();
        for (POBorrow pb : poBorrows) {
            POBorrowDTO poBorrow = new POBorrowDTO();
            poBorrow.setId(pb.getId());
            poBorrow.setPoBorrowNumber(pb.getPoBorrowNumber());
            poBorrow.setPoNote(pb.getPoNote());
            poBorrow.setPoDate(pb.getPoDate());
            poBorrowList.add(poBorrow);
        }
        Response<List<POBorrowDTO>> response = new Response<>();
        response.setData(poBorrowList);
        return response;
    }

    @Override
    public Response<List<PoAdjustedDetailDTO>> getPoAdjustedDetailDiscount(Long paId) {
        List<POAdjustedDetail> poAdjustedDetails = poAdjustedDetailRepository.getListProductDiscountPoAdjustedDetail(paId);
        List<PoAdjustedDetailDTO> poAdjustedDetailPromotionalproductList = new ArrayList<>();
        for (POAdjustedDetail pad : poAdjustedDetails) {
            PoAdjustedDetailDTO poAdjustedDetail = new PoAdjustedDetailDTO();
            poAdjustedDetail.setId(pad.getId());
            poAdjustedDetail.setPoAdjustedId(pad.getPoAdjustedId());
            poAdjustedDetail.setPoLicenseDetailNumber(pad.getPoLicenseDetailNumber());
            //poAdjustedDetail.setIsFreeItem(pad.getIsFreeItem());
            poAdjustedDetail.setPriceTotal(pad.getPriceTotal());
            poAdjustedDetail.setProductCode(pad.getProductCode());
            poAdjustedDetail.setProductName(pad.getProductName());
            poAdjustedDetail.setQuantity(pad.getQuantity());
            poAdjustedDetail.setProductPrice(pad.getProductPrice());
            poAdjustedDetailPromotionalproductList.add(poAdjustedDetail);
        }
        Response<List<PoAdjustedDetailDTO>> response = new Response<>();
        response.setData(poAdjustedDetailPromotionalproductList);
        return response;
    }

    @Override
    public Response<List<PoAdjustedDetailDTO>> getPoAdjustedDetail(Long paId) {
        List<POAdjustedDetail> poAdjustedDetails = poAdjustedDetailRepository.getListProductPoAdjustedDetail(paId);
        List<PoAdjustedDetailDTO> poAdjustedDetailList = new ArrayList<>();
        for (POAdjustedDetail pad : poAdjustedDetails) {
            PoAdjustedDetailDTO poAdjustedDetail = new PoAdjustedDetailDTO();
            poAdjustedDetail.setId(pad.getId());
            poAdjustedDetail.setPoAdjustedId(pad.getPoAdjustedId());
            poAdjustedDetail.setPoLicenseDetailNumber(pad.getPoLicenseDetailNumber());
            //poAdjustedDetail.setIsFreeItem(pad.getIsFreeItem());
            poAdjustedDetail.setPriceTotal(pad.getPriceTotal());
            poAdjustedDetail.setProductCode(pad.getProductCode());
            poAdjustedDetail.setProductName(pad.getProductName());
            poAdjustedDetail.setQuantity(pad.getQuantity());
            poAdjustedDetail.setProductPrice(pad.getProductPrice());
            poAdjustedDetailList.add(poAdjustedDetail);
        }
        Response<List<PoAdjustedDetailDTO>> response = new Response<>();
        response.setData(poAdjustedDetailList);
        return response;
    }

    @Override
    public Response<List<SoConfirmDTO>> getProductPromotinalSoConfirm(Long paId) {
        List<SOConfirm> soConfirms = soConfirmRepository.getListProductPromotional(paId);
        List<SoConfirmDTO> soConfirmList = new ArrayList<>();
        for (SOConfirm so : soConfirms) {
            SoConfirmDTO soConfirm = new SoConfirmDTO();
            soConfirm.setId(so.getId());
            soConfirm.setPoConfirmId(so.getPoConfirmId());
            soConfirm.setPriceTotal(so.getPriceTotal());
            soConfirm.setProductCode(so.getProductCode());
            soConfirm.setProductName(so.getProductName());
            soConfirm.setQuantity(so.getQuantity());
            soConfirm.setProductPrice(so.getProductPrice());
            soConfirm.setSoNo(so.getSoNo());
            soConfirm.setUnit(so.getUnit());
            soConfirmList.add(soConfirm);
        }
        Response<List<SoConfirmDTO>> response = new Response<>();
        response.setData(soConfirmList);
        return response;
    }

    @Override
    public Response<List<SoConfirmDTO>> getProductSoConfirm(Long paId) {
        List<SOConfirm> soConfirms = soConfirmRepository.getListProduct(paId);
        List<SoConfirmDTO> soConfirmList = new ArrayList<>();
        for (SOConfirm so : soConfirms) {
            SoConfirmDTO soConfirm = new SoConfirmDTO();
            soConfirm.setId(so.getId());
            soConfirm.setPoConfirmId(so.getPoConfirmId());
            soConfirm.setPriceTotal(so.getPriceTotal());
            soConfirm.setProductCode(so.getProductCode());
            soConfirm.setProductName(so.getProductName());
            soConfirm.setQuantity(so.getQuantity());
            soConfirm.setProductPrice(so.getProductPrice());
            soConfirm.setSoNo(so.getSoNo());
            soConfirm.setUnit(so.getUnit());
            soConfirmList.add(soConfirm);
        }
        Response<List<SoConfirmDTO>> response = new Response<>();
        response.setData(soConfirmList);
        return response;
    }

    @Override
    public Response<List<PoBorrowDetailDTO>> getProductPromotinalPoBorrowDetail(Long paId) {
        List<POBorrowDetail> poBorrowDetails = poBorrowDetailRepository.getListProductPromotional(paId);
        List<PoBorrowDetailDTO> poBorrowDetailList = new ArrayList<>();
        for (POBorrowDetail pbd : poBorrowDetails) {
            PoBorrowDetailDTO poBorrowDetail = new PoBorrowDetailDTO();
            poBorrowDetail.setId(pbd.getId());
            poBorrowDetail.setPoBorrowId(pbd.getPoBorrowId());
            poBorrowDetail.setPoBorrowDetailNumber(pbd.getPoBorrowDetailNumber());
            poBorrowDetail.setProductCode(pbd.getProductCode());
            poBorrowDetail.setProductName(pbd.getProductName());
            poBorrowDetail.setPriceTotal(pbd.getPriceTotal());
            poBorrowDetail.setProductPrice(pbd.getProductPrice());
            poBorrowDetail.setQuantity(pbd.getQuantity());
            poBorrowDetailList.add(poBorrowDetail);
        }
        Response<List<PoBorrowDetailDTO>> response = new Response<>();
        response.setData(poBorrowDetailList);
        return response;
    }

    @Override
    public Response<List<PoBorrowDetailDTO>> getProductPoBorrowDetail(Long paId) {
        List<POBorrowDetail> poBorrowDetails = poBorrowDetailRepository.getListProduct(paId);
        List<PoBorrowDetailDTO> poBorrowDetailList = new ArrayList<>();
        for (POBorrowDetail pbd : poBorrowDetails) {
            PoBorrowDetailDTO poBorrowDetail = new PoBorrowDetailDTO();
            poBorrowDetail.setId(pbd.getId());
            poBorrowDetail.setPoBorrowId(pbd.getPoBorrowId());
            poBorrowDetail.setPoBorrowDetailNumber(pbd.getPoBorrowDetailNumber());
            poBorrowDetail.setProductCode(pbd.getProductCode());
            poBorrowDetail.setProductName(pbd.getProductName());
            poBorrowDetail.setPriceTotal(pbd.getPriceTotal());
            poBorrowDetail.setProductPrice(pbd.getProductPrice());
            poBorrowDetail.setQuantity(pbd.getQuantity());
            poBorrowDetailList.add(poBorrowDetail);
        }
        Response<List<PoBorrowDetailDTO>> response = new Response<>();
        response.setData(poBorrowDetailList);
        return response;
    }


}
