package vn.viettel.core.service.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.viettel.core.db.entity.BaseEntity;
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
