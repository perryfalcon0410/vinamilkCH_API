package vn.viettel.authorization.service.impl;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.repository.DistributorRepository;
import vn.viettel.authorization.service.DistributorService;
import vn.viettel.authorization.service.UserService;
import vn.viettel.authorization.service.dto.DistributorDTO;
import vn.viettel.authorization.service.dto.user.UserDTO;
import vn.viettel.core.db.entity.Distributor;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DistributorServiceImpl extends BaseServiceImpl<Distributor, DistributorRepository> implements DistributorService {

    Logger logger = LoggerFactory.getLogger(DistributorServiceImpl.class);

    private final UserService userService;

    public DistributorServiceImpl(@Lazy UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public List<Distributor> getAll() {
        List<Distributor> distributors = repository.findByDeletedAtIsNull();
        return distributors;
    }
    
    @Override
    public Response<Distributor> update(Distributor distributor) {
        Response<Distributor> response = new Response<>();
        Distributor record = repository.save(distributor);
        response.setData(record);
        return response;
    }

    @Override
    public Optional<Distributor> getByUserId(Long userId) {
        try {
            return repository.findByUserIdAndDeletedAtIsNull(userId);
        } catch (Exception ex) {
            logger.error("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Response<DistributorDTO> getByDistributorNumber(String distributorNumber) {
        logger.info("#getByDistributorNumber - start");
        try {
            Optional<Distributor> distributor = repository.findByDistributorNumberAndDeletedAtIsNull(distributorNumber);
            if (distributor.isPresent()) {
                UserDTO userDTO = userService.getById(distributor.get().getUserId());
                if (userDTO == null) {
                    return new Response<DistributorDTO>().withError(ResponseMessage.SHOP_DISTRIBUTOR_DOES_NOT_EXIST);
                }
                DistributorDTO data = modelMapper.map(distributor.get(), DistributorDTO.class);
                data.setPhoneNumber(userDTO.getName());
                return new Response<DistributorDTO>().withData(data);
            }
            return new Response<DistributorDTO>().withError(ResponseMessage.SHOP_DISTRIBUTOR_DOES_NOT_EXIST);
        } catch (Exception ex) {
            logger.error("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Response<DistributorDTO> getDistributorById(Long distributorId) {
        logger.info("#getDistributorById - start");
        try {
            Distributor distributor = repository.findByIdAndDeletedAtIsNull(distributorId);
            if (distributor == null) {
                return new Response<DistributorDTO>().withError(ResponseMessage.SHOP_DISTRIBUTOR_DOES_NOT_EXIST);
            }
            DistributorDTO data = modelMapper.map(distributor, DistributorDTO.class);
            return new Response<DistributorDTO>().withData(data);
        } catch (Exception ex) {
            logger.error("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Response<DistributorDTO> getDistributorByUserId(Long userId) {
        logger.info("#getDistributorByUserId - start");
        try {
            Optional<Distributor> distributor = repository.findByUserIdAndDeletedAtIsNull(userId);
            if (distributor.isPresent()) {
                return new Response<DistributorDTO>().withData(modelMapper.map(distributor.get(), DistributorDTO.class));
            }
            return new Response<DistributorDTO>().withError(ResponseMessage.SHOP_DISTRIBUTOR_DOES_NOT_EXIST);
        } catch (Exception ex) {
            logger.error("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Response<List<DistributorDTO>> getAllDistributor() {
        logger.info("#getAllDistributor - start");
        try {
            List<Distributor> distributors = repository.findByDeletedAtIsNull();
            if (distributors != null && distributors.size() > 0) {
                List<DistributorDTO> result = distributors.stream().map(distributor -> modelMapper.map(distributor, DistributorDTO.class)).collect(Collectors.toList());
                return new Response<List<DistributorDTO>>().withData(result);
            }
        } catch (Exception ex) {
            logger.error("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Response<List<DistributorDTO>> getDistributorByEmail(String email) {
        try {
            if (email != null && !email.isEmpty()) {
                List<Distributor> distributors = repository.getByEmail(email);
                List<DistributorDTO> result = distributors.stream().map(distributor -> modelMapper.map(distributor, DistributorDTO.class)).collect(Collectors.toList());
                return new Response<List<DistributorDTO>>().withData(result);
            }
        } catch (Exception ex) {
            logger.error("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        return new Response<List<DistributorDTO>>().withData(new ArrayList<>());
    }

    @Override
    public Response<List<DistributorDTO>> getDistributorByName(String name) {
        try {
            if (name != null && !name.isEmpty()) {
                List<Distributor> distributors = repository.getByName(name);
                List<DistributorDTO> result = distributors.stream().map(distributor -> modelMapper.map(distributor, DistributorDTO.class)).collect(Collectors.toList());
                return new Response<List<DistributorDTO>>().withData(result);
            }
        } catch (Exception ex) {
            logger.error("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        return new Response<List<DistributorDTO>>().withData(new ArrayList<>());
    }
}
