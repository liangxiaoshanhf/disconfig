package com.baidu.disconfig;

import static java.util.Collections.emptySet;

import java.util.Set;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.baidu.disconf.client.DisconfMgrBean;
import com.baidu.disconf.client.DisconfMgrBeanSecond;


@ConditionalOnProperty(prefix = DisconfigUtils.DISCONFIG_CONFIG_PREFIX, name = "enabled", matchIfMissing = true, havingValue = "true")
@Configuration
public class DisconfigEndpointAutoConfiguration {
	private RelaxedPropertyResolver resolver = null;

	public String getEvnironment(String key, Environment environment) {
		if (resolver == null) {
			resolver = new RelaxedPropertyResolver(environment);
		}
		return resolver.getProperty(key, Set.class, emptySet()).iterator().next().toString();
	}

	@ConditionalOnProperty(name = DisconfigUtils.SCAN_PACKAGE)
	@Bean(destroyMethod = "destroy")
	public DisconfMgrBean setDisconfMgrBean(Environment environment) {
		DisconfMgrBean disconfMgrBean = new DisconfMgrBean();
		disconfMgrBean.setScanPackage(getEvnironment(DisconfigUtils.SCAN_PACKAGE, environment));
		return disconfMgrBean;
	}

	@Bean(destroyMethod = "destroy",initMethod="init")
	public DisconfMgrBeanSecond setDisconfMgrBeanSecond() {
		return new DisconfMgrBeanSecond();
	}
 

}
