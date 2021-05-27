package app.central.usernode;

import java.io.Serializable;
import java.util.Objects;

public class IpPort implements Serializable {

    private static final long serialversionUID = 129348938L;
    
    public String ip;
    public long port;

    public IpPort(String ip, long port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof IpPort)) {
            return false;
        }
        IpPort ipPort = (IpPort) o;
        return Objects.equals(ip, ipPort.ip) && port == ipPort.port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}