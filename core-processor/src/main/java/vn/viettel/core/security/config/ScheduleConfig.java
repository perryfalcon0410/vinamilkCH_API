package vn.viettel.core.security.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import vn.viettel.core.security.context.SecurityContexHolder;
import vn.viettel.core.security.context.UserContext;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@EnableTransactionManagement
@ComponentScan(basePackages = "vn.viettel.core.security.config")
public class ScheduleConfig {

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(10);

		return threadPoolTaskScheduler;
	}

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Bean
	public PlatformTransactionManager transactionManager()
	{
		return new JpaTransactionManager(entityManagerFactory);
	}

	@Bean
	public SecurityContexHolder securityContexHolder() {
		UserContext context = new UserContext();
		context.setUserName("schedule");
		SecurityContexHolder holder = new SecurityContexHolder();
		holder.setContext(context);
		return holder;
	}

	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		return new JdbcTemplateLockProvider(dataSource);
	}

}