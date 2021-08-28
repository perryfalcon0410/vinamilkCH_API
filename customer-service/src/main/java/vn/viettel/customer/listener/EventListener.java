package vn.viettel.customer.listener;

import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

@Component
public class EventListener implements 	PostInsertEventListener,
										PostUpdateEventListener,
										PostDeleteEventListener {
	
	public static final EventListener INSTANCE = new EventListener();

	@Override
	public boolean requiresPostCommitHanding(EntityPersister persister) {
		return true;
	}
	
	/*
	 * Called after inserting an item in the datastore
	 */
	@Override
	public void onPostInsert(PostInsertEvent event) {
		final Object entity = event.getEntity();
		ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();

	}

	/*
	 * Called after updating an item in the datastore
	 */
	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		final Object entity = event.getEntity();
		ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
		System.out.println("hi there");
	}

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		final Object entity = event.getEntity();
		ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();

	}
}
