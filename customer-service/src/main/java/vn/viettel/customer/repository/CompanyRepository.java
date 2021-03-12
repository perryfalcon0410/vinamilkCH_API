package vn.viettel.customer.repository;

import vn.viettel.core.db.entity.Company;
import vn.viettel.core.repository.BaseRepository;

public interface CompanyRepository extends BaseRepository<Company> {
    Company findByNameAndAddress(String name, String address);
    Company findByName(String name);
}
