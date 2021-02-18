package vn.viettel.authorization.service.impl;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.messaging.user.UserBulkDeleteRequest;
import vn.viettel.authorization.messaging.user.UserCreateRequest;
import vn.viettel.authorization.messaging.user.UserExistResponse;
import vn.viettel.authorization.messaging.user.UserProfileUpdateRequest;
import vn.viettel.authorization.repository.UserRepository;
import vn.viettel.authorization.service.DistributorService;
import vn.viettel.authorization.service.RoleService;
import vn.viettel.authorization.service.UserService;
import vn.viettel.authorization.service.dto.DistributorDTO;
import vn.viettel.authorization.service.dto.user.UserDTO;
import vn.viettel.authorization.service.dto.user.UserEditDTO;
import vn.viettel.authorization.service.dto.user.UserIndexDTO;
import vn.viettel.authorization.service.dto.user.UserProfileDTO;
import vn.viettel.core.db.entity.Distributor;
import vn.viettel.core.db.entity.Role;
import vn.viettel.core.db.entity.User;
import vn.viettel.core.db.entity.role.UserRole;
import vn.viettel.core.db.entity.status.UserStatus;
import vn.viettel.core.service.BaseServiceImpl;
import vn.viettel.core.service.UploadImageService;
import vn.viettel.core.ResponseMessage;
import vn.viettel.core.exception.ValidateException;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserRepository> implements UserService {

    private final String USER_IMAGE_FOLDER = "user";
    private final int DISTRIBUTOR_NUMBER_LENGTH = 10;
    private final Long DISTRIBUTOR_ROLE_ID = 8L;
    private Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

    private final RoleService roleService;
    private final UploadImageService uploadImageService;
    private final DistributorService distributorService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(RoleService roleService,
                           UploadImageService uploadImageService,
                           DistributorService distributorService,
                           PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.uploadImageService = uploadImageService;
        this.distributorService = distributorService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean checkExistsUserByEmail(String email) {
        return repository.existsByEmail(email);
    }

    /* GET BY ID */
    @Override
    public UserDTO getById(Long id) {
        Optional<User> optUser = repository.getById(id);
        if (optUser.isPresent()) {
            return modelMapper.map(optUser.get(), UserDTO.class);
        }
        return null;
    }

    @Override
    public Response<UserDTO> findUserByEmail(String email) {
        Response<UserDTO> response = new Response<UserDTO>();
        Optional<User> optUser = repository.getByEmail(email);
        if (!optUser.isPresent()) {
            response.setFailure(ResponseMessage.NOT_EXISTS_EMAIL);
            return response;
        }
        response.setData(modelMapper.map(optUser.get(), UserDTO.class));
        return response;
    }

    @Override
    public Response<List<UserDTO>> findUsersByEmail(String email) {
        Response<List<UserDTO>> response = new Response<List<UserDTO>>();
        List<UserDTO> users = new ArrayList<>();
        Optional<List<User>> optUser = repository.getUsersByEmail(email);
        if (!optUser.isPresent()) {
            response.setFailure(ResponseMessage.NOT_EXISTS_EMAIL);
            return response;
        } else {
            users = optUser.get().stream().map(u -> modelMapper.map(u, UserDTO.class)).collect(Collectors.toList());
        }
        response.setData(users);
        return response;
    }

    @Override
    public UserExistResponse checkUserExist(Long id) {
        boolean isExists = exists(id);

        UserExistResponse response = new UserExistResponse();
        response.setData(isExists);
        return response;
    }

    /* GET USER BY ID */
    @Override
    public Response<UserDTO> getUserById(Long id) {
        Response<UserDTO> response = new Response<UserDTO>();
        Optional<User> optUser = repository.getById(id);
        if (optUser.isPresent()) {
            UserDTO user = modelMapper.map(optUser.get(), UserDTO.class);
            response.setData(user);
        } else {
            response.setFailure(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        return response;
    }

    @Override
    public User getCustomerUserForgotPasswordByEmail(String email) {
        Role role = roleService.getByRoleName(UserRole.CUSTOMER);
        Optional<User> optUser = repository.findByEmailAndRoleAndStatus(email, role.getId(), UserStatus.ACTIVE.status());
        User user = optUser.isPresent() ? optUser.get() : null;
        return user;
    }

    /* SHOP OWNER UPDATE PROFILE */
    @Override
    public Response<UserProfileDTO> updateProfile(UserProfileUpdateRequest request) {
        Response<UserProfileDTO> response = new Response<>();

        User oldUser = repository.getById(getUserId()).get();
        User newUser = modelMapper.map(request, User.class);
        newUser.setId(oldUser.getId());
        newUser.setEmail(oldUser.getEmail());
        newUser.setPassword(oldUser.getPassword());
        newUser.setRole(oldUser.getRole());
        newUser.setStatus(oldUser.getStatus());
        newUser.setActivationCode(oldUser.getActivationCode());
        newUser = uploadImageService.updateEntityImageUrlWhenUpdate(oldUser, newUser, USER_IMAGE_FOLDER);

        User userRecord = repository.save(newUser);
        UserProfileDTO userDTO = modelMapper.map(userRecord, UserProfileDTO.class);
        response.setData(userDTO);
        return response;
    }

    /* GET BY EMAIL AND ROLE ID */
    @Override
    public UserDTO getByEmailAndRoleId(String email, Long roleId) {
        Optional<User> optUser = repository.getByEmailAndRoleId(email, roleId);
        if (optUser.isPresent()) {
            return modelMapper.map(optUser.get(), UserDTO.class);
        }
        return null;
    }

    /* GET BY ID AND ROLE ID */
    @Override
    public UserDTO getByIdAndRoleId(Long id, Long roleId) {
        Optional<User> optUser = repository.getByIdAndRoleId(id, roleId);
        if (optUser.isPresent()) {
            return modelMapper.map(optUser.get(), UserDTO.class);
        }
        return null;
    }

    @Override
    public List<Long> findByRole(Long rid) {
        List<Long> ids = new ArrayList<>();
        List<User> users = (List<User>) repository.getByRoleId(rid);
        if (users.size() > 0) {
            for (User user : users) {
                ids.add(user.getId());
            }
        }
        return ids;
    }

    @Override
    public Response<List<String>> getListEmailById(long[] ids) {
        if (ids.length == 0) {
            return new Response<List<String>>().withError(null);
        }
        return new Response<List<String>>().withData(repository.getAllEmailById(ids));
    }

    @Override
    public Response<Page<UserIndexDTO>> usersIndex(Long roleId, String searchKeywords, Pageable pageable) {
        searchKeywords = StringUtils.defaultIfBlank(searchKeywords, StringUtils.EMPTY);
        Page<User> users = repository.getUserIndexBySearchKeywordsAndRoleId(roleId, searchKeywords, pageable);
        Page<UserIndexDTO> usersIndex = users.map(this::mapUserToUserIndexDTO);
        return new Response<Page<UserIndexDTO>>().withData(usersIndex);
    }

    @Override
    public Response<UserEditDTO> edit(Long id) {
        User user = repository.findById(id).orElse(null);
        if (user == null) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        UserEditDTO data = modelMapper.map(user, UserEditDTO.class);
        Optional<Distributor> distributor = distributorService.getByUserId(user.getId());
        distributor.ifPresent(value -> data.setDistributor(modelMapper.map(value, DistributorDTO.class)));
        return new Response<UserEditDTO>().withData(data);
    }

    @Override
    @Transactional
    public Response<UserDTO> create(UserCreateRequest request) {
        Optional<User> user = repository.getByEmail(request.getEmail());

        if (user.isPresent()) {
            throw new ValidateException(ResponseMessage.ALREADY_EXISTS_EMAIL);
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User userParams = modelMapper.map(request, User.class);
        userParams.setPassword(passwordEncoder.encode(request.getPassword()));
        userParams.setRole(getRole(request.getRoleId()));
        userParams.setStatus(UserStatus.getStatus(request.getStatus()));
        User newRecord = repository.save(userParams);
        if (DISTRIBUTOR_ROLE_ID.equals(request.getRoleId())) {
            DistributorDTO distributorNew = modelMapper.map(request, DistributorDTO.class);
            distributorNew.setUserId(newRecord.getId());
            DistributorDTO distributorDTO = distributorService.save(distributorNew, DistributorDTO.class);
            if (distributorDTO == null) {
                return new Response<UserDTO>().withError(ResponseMessage.CREATE_FAILED);
            }
            distributorDTO.setDistributorNumber(generateDistributorNumber(distributorDTO.getId()));
            distributorService.save(distributorDTO, DistributorDTO.class);
        }
        return new Response<UserDTO>().withData(modelMapper.map(newRecord, UserDTO.class));
    }

    @Override
    @Transactional
    public Response<UserDTO> update(Long id, UserCreateRequest request) {
        Optional<User> oldUser = repository.getById(id);
        if (!oldUser.isPresent()) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User newUser = oldUser.get();
        newUser.setName(request.getName());
        newUser.setRole(getRole(request.getRoleId()));
        newUser.setStatus(UserStatus.getStatus(request.getStatus()));
        if (!BCRYPT_PATTERN.matcher(request.getPassword()).matches()) {
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        User newRecord = repository.save(newUser);
        if (DISTRIBUTOR_ROLE_ID.equals(request.getRoleId())) {
            Optional<Distributor> distributor = distributorService.getByUserId(id);
            if (!distributor.isPresent()) {
                DistributorDTO distributorNew = modelMapper.map(request, DistributorDTO.class);
                distributorNew.setUserId(newRecord.getId());
                DistributorDTO distributorDTO = distributorService.save(distributorNew, DistributorDTO.class);
                if (distributorDTO == null) {
                    return new Response<UserDTO>().withError(ResponseMessage.CREATE_FAILED);
                }
                distributorDTO.setDistributorNumber(generateDistributorNumber(distributorDTO.getId()));
                distributorService.save(distributorDTO, DistributorDTO.class);
            } else {
                DistributorDTO distributorDTO = modelMapper.map(distributor.get(), DistributorDTO.class);
                distributorDTO.setCurrentPlanIncentiveRate(request.getCurrentPlanIncentiveRate());
                distributorDTO.setCurrentPlatformIncentiveRate(request.getCurrentPlatformIncentiveRate());
                distributorDTO.setNextPlanIncentiveRate(request.getNextPlanIncentiveRate());
                distributorDTO.setNextPlatformIncentiveRate(request.getNextPlatformIncentiveRate());
                distributorDTO.setPhoneNumber(request.getPhoneNumber());
                distributorDTO.setAddress(request.getAddress());
                distributorService.save(distributorDTO, DistributorDTO.class);
            }
        }
        return new Response<UserDTO>().withData(modelMapper.map(newRecord, UserDTO.class));
    }

    @Override
    @Transactional
    public Response<UserDTO> delete(Long id) {
        Optional<User> user = repository.getById(id);
        if (!user.isPresent()) {
            throw new ValidateException(ResponseMessage.USER_DOES_NOT_EXISTS);
        }
        // TODO: just delete when not select cancel
        User user1 = modelMapper.map(user.get(), User.class);
        user1.setDeletedAt(LocalDateTime.now());
        User deleteRecord = repository.save(user1);
        //Delete info of distributor
        Optional<Distributor> distributor = distributorService.getByUserId(user1.getId());
        if (distributor.isPresent()) {
            DistributorDTO distributorDTO = modelMapper.map(distributor.get(), DistributorDTO.class);
            distributorDTO.setDeletedAt(LocalDateTime.now());
            distributorService.save(distributorDTO, DistributorDTO.class);
        }
        return new Response<UserDTO>().withData(modelMapper.map(deleteRecord, UserDTO.class));
    }

    @Override
    @Transactional
    public Response<List<Response<UserDTO>>> bulkDelete(UserBulkDeleteRequest request) {
        Response<List<Response<UserDTO>>> response = new Response<>();
        // TODO: check has user can not delete in list and throw error message
        List<Response<UserDTO>> resData = Arrays.stream(request.getIds())
                .map(this::deleteUserById)
                .collect(Collectors.toList());
        return response.withData(resData);
    }

    @Override
    public Response<List<Long>> getAllUserIdByUserName(String name) {
        Optional<List<User>> userList = repository.findAllByCustomerName(name);
        List<Long> result = new ArrayList<>();
        if (userList.isPresent()) {
            for (User user : userList.get()) {
                result.add(user.getId());
            }
        }
        return new Response<List<Long>>().withData(result);
    }

    /* PRIVATE MOTHEDS */
    private UserIndexDTO mapUserToUserIndexDTO(User user) {
        UserIndexDTO userIndexDTO = modelMapper.map(user, UserIndexDTO.class);
        if (user.getCustomerInfomation() != null) {
            userIndexDTO.setPhone(user.getCustomerInfomation().getTelephone());
        } else {
            Optional<Distributor> distributor = distributorService.getByUserId(user.getId());
            distributor.ifPresent(value -> {
                userIndexDTO.setPhone(value.getPhoneNumber());
                userIndexDTO.setDistributor(value.getDistributorNumber());
            });
        }
        return userIndexDTO;
    }

    private Response<UserDTO> deleteUserById(Long id) {
        return this.delete(id);
    }

    private String generateDistributorNumber(Long distributorId) {
        int remainingLength = DISTRIBUTOR_NUMBER_LENGTH - distributorId.toString().length();
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.repeat("0", remainingLength));
        sb.append(distributorId);
        return sb.toString();
    }

    private Role getRole(Long roleId) {
        return roleService.getById(roleId);
    }
}
