package org.db1.etetest;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

public class SupportUpdateApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
	// include spring boot supported properties into
	// the application properties
	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		Support.instance().setEnvironment(event.getEnvironment());
	}
}
