package cn.nihilty.gateway.nacos;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.core.env.StandardEnvironment;

import java.util.Scanner;
import java.util.concurrent.Executor;

public class NacosWatchFileListener {

    public static void main(String[] args) throws NacosException {
        NacosConfigProperties properties = new NacosConfigProperties();
        properties.setServerAddr("127.0.0.1:8848");
        properties.setFileExtension(".yml");
        properties.setEnvironment(new StandardEnvironment());
        ConfigService configService = NacosFactory.createConfigService(properties.assembleConfigServiceProperties());

        Listener listener = new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("--------------------");
                System.out.println(configInfo);
                System.out.println("--------------------");
            }
        };
        configService.addListener("dynamic-gateway.yml", Constants.DEFAULT_GROUP, listener);


        Scanner scanner = new Scanner(System.in);
        System.out.println(scanner.nextLine());
        configService.shutDown();

    }

}
