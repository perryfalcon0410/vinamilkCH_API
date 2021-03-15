package vn.viettel.saleservice.repository;

import vn.viettel.core.db.entity.Company;
import vn.viettel.core.repository.BaseRepository;

public interface CompanyRepository extends BaseRepository<Company> {
    Company findById(long id);
}
