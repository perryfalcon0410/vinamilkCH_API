package vn.viettel.core.service;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;

public interface BaseReportService {
  void executeQuery(StoredProcedureQuery query ,String procedureName, String parram );
}
