package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.CustomerInformation;
import vn.viettel.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerInfomationRepository extends BaseRepository<CustomerInformation> {

    /**
     * get CustomerInformation by phone number of customer
     *
     * @param phone
     * @return CustomerInformation
     */
    @Query(value = "SELECT * FROM customer_information "
            + "WHERE (telephone = :phone OR mobilephone = :phone) "
            + "AND deleted_at IS NULL", nativeQuery = true)
    CustomerInformation findByPhone(@Param("phone") String phone);

    /**
     * get customer information by user id
     *
     * @param userId
     * @return Optional<CustomerInformation>
     */
    @Query(value = "SELECT * FROM customer_information "
            + "WHERE user_id = :userId "
            + "AND deleted_at IS NULL", nativeQuery = true)
    CustomerInformation findByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM customer_information "
            + "WHERE customer_number = :customerNumber "
            + "AND deleted_at IS NULL", nativeQuery = true)
    Optional<CustomerInformation> findByCustomerNumber(@Param("customerNumber") String customerNumber);

    // Get all user id by customer name
    @Query(value = "SELECT * FROM customer_information "
            + "WHERE name LIKE %:name% ", nativeQuery = true)
    Optional<List<CustomerInformation>> findAllByCustomerName(@Param("name") String name);
}
