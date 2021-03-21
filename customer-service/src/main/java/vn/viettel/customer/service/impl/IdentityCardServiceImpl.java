package vn.viettel.customer.service.impl;

import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.db.entity.Customer;
import vn.viettel.core.db.entity.IdentityCard;
import vn.viettel.core.exception.ValidateException;
import vn.viettel.core.messaging.Response;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.customer.messaging.*;
import vn.viettel.customer.repository.IdentityCardRepository;
import vn.viettel.customer.service.IdentityCardService;
import vn.viettel.customer.service.dto.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IdentityCardServiceImpl extends BaseServiceImpl<IdentityCard, IdentityCardRepository> implements IdentityCardService {

    @Override
    public Response<IdentityCardDTO> create(IdentityCardCreateRequest request, Long userId) {
        Optional<IdentityCard> identityCard = repository.getIdentityCardByIdentityCardCode(request.getIdentityCardCode());

        if (identityCard.isPresent()) {
            throw new ValidateException(ResponseMessage.IDENTITY_CARD_CODE_HAVE_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        IdentityCard card = modelMapper.map(request, IdentityCard.class);
        card.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        card.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        card = repository.save(card);

        IdentityCardDTO dto = modelMapper.map(card, IdentityCardDTO.class);

        return new Response<IdentityCardDTO>().withData(dto);
    }

    @Override
    public Response<IdentityCardDTO> update(IdentityCardUpdateRequest request, Long userId) {

        IdentityCard identityCard = repository.getIdentityCardByIdAndDeletedAtIsNull(request.getId());
        if (identityCard == null) {
            throw new ValidateException(ResponseMessage.CUSTOMER_IS_NOT_EXISTED);
        }

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        IdentityCard card = modelMapper.map(request, IdentityCard.class);
        card.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        card.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        card = repository.save(card);

        IdentityCardDTO dto = modelMapper.map(card, IdentityCardDTO.class);

        return new Response<IdentityCardDTO>().withData(dto);
    }

}
