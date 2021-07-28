package vn.viettel.core.security.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

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

//	@Bean
//    public RequestInterceptor requestInterceptor() {
//        return (RequestTemplate requestTemplate) -> {
//            String token = AuthorizationType.FEIGN_AUTH + " " + secretKey;
//            requestTemplate.header(HttpHeaders.AUTHORIZATION, token);
//        };
//
//    }

	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		return new JdbcTemplateLockProvider(dataSource);
	}

//	public class AutowireCapableBeanJobFactory extends SpringBeanJobFactory {
//
//		private final AutowireCapableBeanFactory beanFactory;
//
//		@Autowired
//		public AutowireCapableBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
//			Assert.notNull(beanFactory, "Bean factory must not be null");
//			this.beanFactory = beanFactory;
//		}
//
//		@Override
//		protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
//			Object jobInstance = super.createJobInstance(bundle);
//			this.beanFactory.autowireBean(jobInstance);
//			this.beanFactory.initializeBean(jobInstance, null);
//			return jobInstance;
//		}
//	}
//
//	@Bean
//	public SchedulerFactoryBean schedulerFactory(ApplicationContext applicationContext) {
//		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
//		schedulerFactoryBean.setJobFactory(new AutowireCapableBeanJobFactory(applicationContext.getAutowireCapableBeanFactory()));
//		return schedulerFactoryBean;
//	}
//
//	@Bean
//	public Scheduler scheduler(ApplicationContext applicationContext) throws SchedulerException {
//		Scheduler scheduler = schedulerFactory(applicationContext).getScheduler();
//		scheduler.start();
//		return scheduler;
//	}
}