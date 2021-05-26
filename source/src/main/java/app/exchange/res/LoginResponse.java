package app.exchange.res;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import app.central.usernode.IpPort;
import app.exchange.MessageWrapper;

public class LoginResponse extends MessageWrapper implements Serializable {
    
    private static final long serialversionUID = 129348938L;
    
    public Set<IpPort> connections;
    public Map<String, IpPort> recoveryPorts;

    public LoginResponse(Set<IpPort> connections, Map<String,IpPort> recoveryPorts) {
        this.connections = connections;
        this.recoveryPorts = recoveryPorts;
    }

}
