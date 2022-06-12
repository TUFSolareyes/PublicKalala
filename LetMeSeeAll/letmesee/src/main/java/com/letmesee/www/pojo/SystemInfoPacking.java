package com.letmesee.www.pojo;

public class SystemInfoPacking {

    private Integer cores;

    private Long freeMemory;

    private Long maxMemory;

    private Long hasUsedMemory;

    private Integer invCount;

    private Integer forcCount;

    private Integer textCount;

    private Boolean redisOk;

    private Boolean databaseOk;

    public SystemInfoPacking(Integer cores, Long freeMemory, Long maxMemory, Long hasUsedMemory, Integer invCount, Integer forcCount, Integer textCount, Boolean redisOk, Boolean databaseOk) {
        this.cores = cores;
        this.freeMemory = freeMemory;
        this.maxMemory = maxMemory;
        this.hasUsedMemory = hasUsedMemory;
        this.invCount = invCount;
        this.forcCount = forcCount;
        this.textCount = textCount;
        this.redisOk = redisOk;
        this.databaseOk = databaseOk;
    }

    public Integer getCores() {
        return cores;
    }

    public void setCores(Integer cores) {
        this.cores = cores;
    }

    public Long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(Long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public Long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(Long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public Long getHasUsedMemory() {
        return hasUsedMemory;
    }

    public void setHasUsedMemory(Long hasUsedMemory) {
        this.hasUsedMemory = hasUsedMemory;
    }

    public Integer getInvCount() {
        return invCount;
    }

    public void setInvCount(Integer invCount) {
        this.invCount = invCount;
    }

    public Integer getForcCount() {
        return forcCount;
    }

    public void setForcCount(Integer forcCount) {
        this.forcCount = forcCount;
    }

    public Integer getTextCount() {
        return textCount;
    }

    public void setTextCount(Integer textCount) {
        this.textCount = textCount;
    }

    public Boolean getRedisOk() {
        return redisOk;
    }

    public void setRedisOk(Boolean redisOk) {
        this.redisOk = redisOk;
    }

    public Boolean getDatabaseOk() {
        return databaseOk;
    }

    public void setDatabaseOk(Boolean databaseOk) {
        this.databaseOk = databaseOk;
    }
}
