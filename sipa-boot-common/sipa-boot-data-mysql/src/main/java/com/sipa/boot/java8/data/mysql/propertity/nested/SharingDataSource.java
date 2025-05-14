package com.sipa.boot.java8.data.mysql.propertity.nested;

/**
 * @author zhouxiajie
 * @date 2019-06-08
 */
public class SharingDataSource {
    private ShardingRule sharding = new ShardingRule();

    private MasterSlaveRule masterslave = new MasterSlaveRule();

    private EncryptRule encrypt = new EncryptRule();

    public EncryptRule getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(EncryptRule encrypt) {
        this.encrypt = encrypt;
    }

    public ShardingRule getSharding() {
        return sharding;
    }

    public void setSharding(ShardingRule sharding) {
        this.sharding = sharding;
    }

    public MasterSlaveRule getMasterslave() {
        return masterslave;
    }

    public void setMasterslave(MasterSlaveRule masterslave) {
        this.masterslave = masterslave;
    }
}
