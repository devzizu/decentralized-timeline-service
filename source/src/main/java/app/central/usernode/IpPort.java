package app.central.usernode;

import java.net.InetAddress;
import java.util.Objects;

public class IpPort{
    public InetAddress ip;
    public int port;

    public IpPort(InetAddress ip, int port) {
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