package vn.viettel.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.repository.BaseRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer> {
    @Query(value = "SELECT * FROM CUSTOMERS "
            + "WHERE ((CONCAT(CONCAT(LAST_NAME, ' '),FIRST_NAME)) LIKE %:searchKeywords% "
            + "OR CUS_CODE LIKE %:searchKeywords%) "
            + "AND CREATED_AT BETWEEN :fromDate AND :toDate "
            + "AND DELETED_AT IS NULL", nativeQuery = true)
    Page<Customer> getAllCustomers(@Param("searchKeywords") String searchKeywords, @Param("fromDate") String fromDate, @Param("toDate") String toDate, Pageable pageable);
    /* END QUERIES FOR SEARCH CUSTOMER */

    Optional<Customer> getCustomerByCusCode(String cusCode);

    Customer getCustomerById(Long id);
}
