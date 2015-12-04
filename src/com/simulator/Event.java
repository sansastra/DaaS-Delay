package com.simulator;

/**
 * Created by Sandeep on 02-Dec-15.
 */
public abstract class Event {
    private static long counter = 0L;
    private long eventId;
    protected Entity entity;
    private  int type;

    public Event(Entity ent) {
        this.eventId = (long)(counter++);
        this.entity = ent;
       // this.type = type; // 0 request; 1 release ; 2 defrag
    }

    public abstract void occur();
    //public abstract int getEventType();

    public Entity getEntity() {
        return entity;
    }
}
