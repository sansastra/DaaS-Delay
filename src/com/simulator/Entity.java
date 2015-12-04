package com.simulator;

/**
 * Created by Sandeep on 02-Dec-15.
 */

public class Entity {
    private static long counter = 0L;
    private long entityId;
    private double createTime;

    public Entity(double createTime) {
        this.entityId = ++counter;
        this.createTime = createTime;
    }

    public double getCreateTime() {
        return this.createTime;
    }

    //public void modifyCreatetime(double deltaT){this.createTime = createTime + deltaT;}

    public long getEntityId() {
        return this.entityId;
    }
}
