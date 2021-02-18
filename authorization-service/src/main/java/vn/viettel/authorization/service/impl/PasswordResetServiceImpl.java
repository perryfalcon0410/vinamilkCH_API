package vn.viettel.authorization.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.repository.PasswordResetRepository;
import vn.viettel.authorization.service.PasswordResetService;
import vn.viettel.authorization.service.dto.PasswordResetDTO;
import vn.viettel.core.db.entity.PasswordReset;
import vn.viettel.core.service.BaseServiceImpl;

@Service
public class PasswordResetServiceImpl extends BaseServiceImpl<PasswordReset, PasswordResetRepository>
    implements PasswordResetService {

    /* GET RESET PASSWORD BY ID */
    @Override
    public Response<PasswordResetDTO> getByUserId(Long userId) {
        Response<PasswordResetDTO> response = new Response<PasswordResetDTO>();
        Optional<PasswordReset> optPasswordReset = repository.getByUserId(userId);
        if (optPasswordReset.isPresent()) {
            response.setData(modelMapper.map(optPasswordReset.get(), PasswordResetDTO.class));
        }
        return response;
    }

    @Override
    public Response<PasswordResetDTO> getByAdminUserId(Long userId) {
        Response<PasswordResetDTO> response = new Response<PasswordResetDTO>();
        Optional<PasswordReset> optPasswordReset = repository.getByMemberId(userId);
        if (optPasswordReset.isPresent()) {
            response.setData(modelMapper.map(optPasswordReset.get(), PasswordResetDTO.class));
        }
        return response;
    }

    @Override
    public Response<PasswordResetDTO> getByMemberIdAndCompanyId(Long userId, Long companyId) {
        Response<PasswordResetDTO> response = new Response<PasswordResetDTO>();
        Optional<PasswordReset> optPasswordReset = repository.getByMemberIdAndCompanyId(userId, companyId);
        if (optPasswordReset.isPresent()) {
            response.setData(modelMapper.map(optPasswordReset.get(), PasswordResetDTO.class));
        }
        return response;
    }

    /* GET RESET PASSWORD BY TOKEN */
    @Override
    public Response<PasswordResetDTO> getByToken(String token) {
        Response<PasswordResetDTO> response = new Response<PasswordResetDTO>();
        Optional<PasswordReset> optPasswordReset = repository.getByToken(token);
        if (optPasswordReset.isPresent()) {
            response.setData(modelMapper.map(optPasswordReset.get(), PasswordResetDTO.class));
        }
        return response;
    }

    @Override
    public Response<PasswordResetDTO> getByManagementUserId(Long userId) {
        Response<PasswordResetDTO> response = new Response<PasswordResetDTO>();
        Optional<PasswordReset> optPasswordReset = repository.getByManagementUserId(userId);
        if (optPasswordReset.isPresent()) {
            response.setData(modelMapper.map(optPasswordReset.get(), PasswordResetDTO.class));
        }
        return response;
    }

    @Override
    public Response<PasswordResetDTO> getByTokenAndCompanyId(String token, Long companyId) {
        Response<PasswordResetDTO> response = new Response<PasswordResetDTO>();
        Optional<PasswordReset> optPasswordReset = repository.getByTokenAndCompanyId(token, companyId);
        if (optPasswordReset.isPresent()) {
            response.setData(modelMapper.map(optPasswordReset.get(), PasswordResetDTO.class));
        }
        return response;
    }

    @Override
    public List<PasswordReset> getByMember(Long memberId) {
        return repository.getByMember(memberId);
    }


}
