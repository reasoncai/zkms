package com.cai.zkmsapi;

import dom.cai.zkms.ServiceRegistry;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by reason on 17/5/29.
 */
@Component
public class ServiceRegistryImpl implements ServiceRegistry,Watcher {
    private static Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);
    private static CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;

    private static final String REGISTRY_PATH = "/registry";
    private static final int SESSION_TIMEOUT = 5000;

    public ServiceRegistryImpl(){}

    public ServiceRegistryImpl(String zkServers){
        try {
             zk = new ZooKeeper(zkServers, SESSION_TIMEOUT, this);
             latch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务注册
     * @param serviceName 服务名字
     * @param serviceAddress 服务地址
     */
    @Override
    public void register(String serviceName, String serviceAddress) {

        try {
            //创建根节点(持久节点)
            String registryPath = REGISTRY_PATH;
            if(zk.exists(registryPath,false)==null){
                zk.create(registryPath,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("create regisry node :{}",registryPath);
            }
            //创建服务节点(持久节点)
            String servicePath = registryPath + "/" + serviceName;
            if(zk.exists(servicePath,false)==null){
                zk.create(servicePath,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("create service node :{}",servicePath);
            }
            //创建地址节点（临时顺序节点）
            String addressPath = servicePath + "/address-";
            String addressNode = zk.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("create address node :{} => {}",addressNode,serviceAddress);

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
            latch.countDown();
        }
    }
}
