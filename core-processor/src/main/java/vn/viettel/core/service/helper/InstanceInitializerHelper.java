package vn.viettel.core.service.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import vn.viettel.core.db.entity.BaseEntity;
import vn.viettel.core.logging.LogFile;
import vn.viettel.core.logging.LogLevel;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.service.dto.BaseDTO;

public class InstanceInitializerHelper {

	private static Logger logger = LoggerFactory.getLogger(InstanceInitializerHelper.class);

	/**
	 * Create new instance of type class (DTO)
	 * 
	 * @param clazz
	 * @return
	 */
	public static <D extends BaseDTO> D newDTOIntance(Class<D> clazz) {
		D newInstance = null;
		try {
			newInstance = clazz.newInstance();
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		}

		return newInstance;
	}

	/**
	 * Create new instance of type class (Entity)
	 * 
	 * @param clazz
	 * @return
	 */
	public static <E extends BaseEntity> E newEntityIntance(Class<E> clazz) {
		E newInstance = null;
		try {
			newInstance = clazz.newInstance();
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		}
		return newInstance;
	}

}
