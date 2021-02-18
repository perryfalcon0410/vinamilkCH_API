package vn.viettel.authorization.service;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.service.dto.DistributorDTO;
import vn.viettel.core.db.entity.Distributor;
import vn.viettel.core.service.BaseService;

import java.util.List;
import java.util.Optional;

public interface DistributorService extends BaseService {

    List<Distributor> getAll();

    Response<Distributor> update(Distributor distributor);

    Optional<Distributor> getByUserId(Long userId);

    Response<DistributorDTO> getByDistributorNumber(String distributorNumber);

    Response<DistributorDTO> getDistributorById(Long distributorId);

    Response<DistributorDTO> getDistributorByUserId(Long userId);

    Response<List<DistributorDTO>> getAllDistributor();

    Response<List<DistributorDTO>> getDistributorByEmail(String email);

    Response<List<DistributorDTO>> getDistributorByName(String name);
}
