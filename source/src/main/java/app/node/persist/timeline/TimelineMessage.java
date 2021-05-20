package app.node.persist.timeline;

public class TimelineMessage {
    
    public String sender_id;
    public ClockStamp[] sender_clock;
    public String content;

    public TimelineMessage(String sender_id, ClockStamp[] sender_clock, String content) {
        this.sender_id = sender_id;
        this.sender_clock = sender_clock;
        this.content = content;
    }

}
