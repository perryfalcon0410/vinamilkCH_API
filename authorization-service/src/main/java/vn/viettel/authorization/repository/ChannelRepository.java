package vn.viettel.authorization.repository;

import vn.viettel.core.db.entity.Channel;
import vn.viettel.core.repository.BaseRepository;

import java.util.List;

public interface ChannelRepository extends BaseRepository<Channel> {
    List<Channel> findAllByTypeInAndDeletedAtIsNull(List<Long> type);

    List<Channel> findAllByParentInAndDeletedAtIsNull(List<Long> ids);

    List<Channel> findAllByIdInOrParentInAndDeletedAtIsNull(List<Long> ids, List<Long> parentIds);
}
