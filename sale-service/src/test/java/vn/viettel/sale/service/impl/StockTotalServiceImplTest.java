package vn.viettel.sale.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import vn.viettel.sale.BaseTest;
import vn.viettel.sale.entities.StockTotal;
import vn.viettel.sale.repository.StockTotalRepository;

import javax.persistence.EntityManager;

import java.util.HashMap;

import static org.junit.Assert.*;

public class StockTotalServiceImplTest extends BaseTest {

    @InjectMocks
    StockTotalServiceImpl service;

    @Mock
    StockTotalRepository repository;

    @Mock
    public EntityManager entityManager;
    Long stockTotalId = 1L;
    Integer value = 1;
    StockTotal newEntity = new StockTotal();
    Long shopId = 1L;
    Long wareHouseId = 1L;
    Long productId = 1L;

    @Before
    public void setUp() throws Exception {
        Mockito.when(repository.findById(stockTotalId)).thenReturn(java.util.Optional.ofNullable(newEntity));
        Mockito.when(repository.findByProductIdAndWareHouseTypeIdAndShopId(productId, wareHouseId,shopId))
                .thenReturn(newEntity);
    }

    @Test
    public void updateWithLock() {
        service.updateWithLock(stockTotalId, value);
    }

    @Test
    public void testUpdateWithLock() {
        HashMap<Long,Integer> idAndValues = new HashMap<>();
        service.updateWithLock(idAndValues);
    }

    @Test
    public void testUpdateWithLock1() {

        service.updateWithLock( shopId,  wareHouseId,  productId,  value);
    }

    @Test
    public void testUpdateWithLock2() {
        service.updateWithLock( shopId,  wareHouseId,  productId,  value,1);
    }

    @Test
    public void createStockTotal() {
        service.createStockTotal( shopId,  wareHouseId,  productId,  value,  true);
    }

    @Test
    public void updateEntity() {
        service.updateEntity(newEntity,  value);
    }

    @Test
    public void validateStockTotal() {
    }

    @Test
    public void showMessage() {
    }
}