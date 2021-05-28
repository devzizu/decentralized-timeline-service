package app.central.usernode;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Session {
    
    public LocalDateTime timeStart;
    public LocalDateTime timeEnd;

    public Session() {
        timeStart = null;
        timeEnd = null;
    }

    public void setTimeStart(LocalDateTime time) {
       
        this.timeStart = time;
    }

    public void setTimeEnd(LocalDateTime time) {
       
        this.timeEnd = time;
    }

    public long getTimeDiffSeconds() {

        return this.timeStart == null ? -1 : ChronoUnit.SECONDS.between(this.timeStart,this.timeEnd!=null?this.timeEnd:LocalDateTime.now());
    }
}
