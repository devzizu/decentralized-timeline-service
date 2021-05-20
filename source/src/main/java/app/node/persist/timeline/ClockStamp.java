package app.node.persist.timeline;

public class ClockStamp {
    
    public String stamp_id;
    public long stamp_val;

    public ClockStamp(String stamp_id, long stamp_val) {
        this.stamp_id = stamp_id;
        this.stamp_val = stamp_val;
    }

}
