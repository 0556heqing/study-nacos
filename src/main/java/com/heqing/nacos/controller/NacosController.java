package com.heqing.nacos.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * @author heqing
 * @date 2021/7/16 16:34
 */
@RestController
@RequestMapping("/study")
public class NacosController {

    private  final Logger log = LoggerFactory.getLogger(NacosController.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    NacosDiscoveryProperties nacosDiscoveryProperties;

    @Autowired
    NacosRegistration nacosRegistration;

    @GetMapping("/nacos")
    public String nacos(){
        log.info("Controller --> hello nacos");
        return "hello nacos";
    }

    @GetMapping("/getShard")
    public String getShard() {
        List<ServiceInstance> instances = discoveryClient.getInstances("study-nacos");
        StringBuilder sb = new StringBuilder(0);
        instances.stream().map(i -> {
            String host = i.getHost();
            int port = i.getPort();
            return sb.append(host).append(":").append(port);
        }).collect(joining(", "));
        return "nacos-client 的服务地址为：" + sb.toString();
    }

    /**
     * 服务注册接口
     *
     * @return
     */
    @GetMapping(value = "/nacos/register")
    public String registerInstance() {
        String serviceName = nacosDiscoveryProperties.getService();
        String groupName = nacosDiscoveryProperties.getGroup();
        String clusterName = nacosDiscoveryProperties.getClusterName();
        String ip = nacosDiscoveryProperties.getIp();
        int port = nacosDiscoveryProperties.getPort();

        log.info("register from nacos, serviceName:{}, groupName:{}, clusterName:{}, ip:{}, port:{}", serviceName, groupName, clusterName, ip, port);
        try {
            nacosRegistration.getNacosNamingService().registerInstance(serviceName, groupName, ip, port, clusterName);

        } catch (NacosException e) {
            log.error("register from nacos error", e);
            return "error";
        }
        return "success";
    }

    /**
     * 服务注销接口
     *
     * @return
     */
    @GetMapping(value = "/nacos/deregister")
    public String deregisterInstance() {
        String serviceName = nacosDiscoveryProperties.getService();
        String groupName = nacosDiscoveryProperties.getGroup();
        String clusterName = nacosDiscoveryProperties.getClusterName();
        String ip = nacosDiscoveryProperties.getIp();
        int port = nacosDiscoveryProperties.getPort();

        log.info("deregister from nacos, serviceName:{}, groupName:{}, clusterName:{}, ip:{}, port:{}", serviceName, groupName, clusterName, ip, port);
        try {
            nacosRegistration.getNacosNamingService().deregisterInstance(serviceName, groupName, ip, port, clusterName);

        } catch (NacosException e) {
            log.error("deregister from nacos error", e);
            return "error";
        }
        return "success";
    }
}
