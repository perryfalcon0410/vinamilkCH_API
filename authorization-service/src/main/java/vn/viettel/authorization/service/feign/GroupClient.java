package vn.viettel.authorization.service.feign;

import vn.viettel.core.messaging.Response;
import vn.viettel.authorization.service.dto.group.GroupDTO;
import vn.viettel.core.security.anotation.FeignClientAuthenticate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClientAuthenticate(name = "group-service")
public interface GroupClient {

    @GetMapping("/get-group-by-group-id")
    Response<GroupDTO> getGroupByGroupId(@RequestParam("groupId") Long groupId);

    @GetMapping("/get-group-by-id-and-user-id")
    Response<GroupDTO> getGroupByIdAndUserId(@RequestParam("groupId") Long groupId, @RequestParam("userId") Long userId);

    @GetMapping("/get-by-distributor-id")
    Response<GroupDTO> getByDistributorId(@RequestParam("groupId") Long groupId, @RequestParam(name = "distributorId") Long distributorId);

}
