修复zookeeper断网后再连接不能回调问题，并支持springboot

1.加入disconfig配置文件
     enable.remote.conf=true
     #disconfig服务地址,根据自已的环境休息
     conf_server_host=192.168.0.144:8080
     #版本
     version=1
     #应用名
     app=qdtec
     #环境
     env=rd
    # debug
     debug=true
    conf_server_url_retry_times=1
    conf_server_url_retry_sleep_seconds=1
    disconf.enable_local_download_dir_in_class_path=true

2.在springboot的application.properties新增disconfig配置
#disconfig
#模块
disconfig.moduleName=mall
#扫描路径
spring.disconfig.scanPackage=com
#属性配置
spring.disconfig.configproperties=springboot-elasticsearch.properties
spring.disconfig.ignoreResourceNotFound=true
spring.disconfig.ignoreUnresolvablePlaceholders=true

3.在disconfig-web中新建配置文件springboot-elasticsearch.properties
  这里的mall和第二步的disconfig.moduleName值是一样的
  mall.address=192.168.0.142:9300
  mall.clusterName=elasticsearch
  mall.sniff=true
  mall.timeout=30s
  
4.需要动态注入属性的类
  
@ConfigurationProperties
@DisconfFile(filename = "springboot-elasticsearch.properties")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ElasticsearchAutoConfigurationProperties {
	public static final String ADDRESS = "address";
	public static final String CLUSTER_NAME = "clusterName";
	public static final String SNIFF = "sniff";
	public static final String TIME_OUT = "timeout";
	public static final String POINT = ".";

	private String address = "192.168.0.142:9300";
	private String clusterName = "elasticsearch";
	private String sniff = "true";
	private String timeout = "30";

	@DisconfFileItem(associateField=ADDRESS,name="?."+ADDRESS)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	@DisconfFileItem(associateField=CLUSTER_NAME,name="?."+CLUSTER_NAME)
	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	@DisconfFileItem(associateField=SNIFF,name="?."+SNIFF)
	public String getSniff() {
		return sniff;
	}

	public void setSniff(String sniff) {
		this.sniff = sniff;
	}
	@DisconfFileItem(associateField=TIME_OUT,name="?."+TIME_OUT)
	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

}
  
5.回调：
package com.qdtec.elasticsearch.springboot.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfUpdateService;
import com.baidu.disconf.client.common.update.IDisconfUpdate;
import com.qdtec.common.elasticsearch.ElasticSearchConfig;
import com.qdtec.common.elasticsearch.ElasticSearchTemplate;
import com.qdtec.common.logger.LoggerFactory;

@Service
@DisconfUpdateService(classes = ElasticsearchAutoConfigurationProperties.class)
public class UpdateCallBackElasticSearchService implements IDisconfUpdate, ApplicationContextAware {
	private ApplicationContext applicationContext;
	private static final com.qdtec.common.logger.Logger LOG = LoggerFactory
			.getLogger(UpdateCallBackElasticSearchService.class);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void reload() throws Exception {
		ElasticsearchAutoConfigurationProperties esacp = applicationContext
				.getBean(ElasticsearchAutoConfigurationProperties.class);
		esacp = (ElasticsearchAutoConfigurationProperties) com.baidu.disconf.client.aop.AopTargetUtils.getTarget(esacp);
		ElasticSearchTemplate elasticSearchTemplate = applicationContext.getBean(ElasticSearchTemplate.class);
		ElasticSearchConfig eelasticSearchConfig = applicationContext.getBean(ElasticSearchConfig.class);
		if (!esacp.getAddress().equals(eelasticSearchConfig.getAddress())
				|| !esacp.getClusterName().equals(eelasticSearchConfig.getClusterName())
				|| Boolean.valueOf(esacp.getSniff()) != eelasticSearchConfig.getSniff()
				|| !esacp.getTimeout().equals(eelasticSearchConfig.getTimeout())

		) {
			eelasticSearchConfig.setAddress(esacp.getAddress());
			eelasticSearchConfig.setClusterName(esacp.getClusterName());
			eelasticSearchConfig.setSniff(Boolean.valueOf(esacp.getSniff()));
			eelasticSearchConfig.setTimeout(esacp.getTimeout());
			elasticSearchTemplate.init();
			LOG.info("reload sccuess ElasticSearch:" + eelasticSearchConfig.toString());

		}

	}

}

4和5步需要自己根据实际来处理，4，5步是我这边的ES的模块
  
