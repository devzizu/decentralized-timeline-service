package app.exchange.res;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import app.central.usernode.IpPort;
import app.exchange.MessageWrapper;

public class LoginResponse extends MessageWrapper {
    
    public Set<IpPort> connections;
    public Map<String, IpPort> recoveryPorts;

    public LoginResponse(Set<IpPort> connections, Map<String,IpPort> recoveryPorts) {
        this.connections = connections;
        this.recoveryPorts = recoveryPorts;
    }

    public LoginResponse() {
        this.connections = new HashSet<>();
        this.recoveryPorts = new HashMap<>();
    }

    @Override
    public String toString() {
        return "{" +
            " connections='" + this.connections + "'" +
            ", recoveryPorts='" + this.recoveryPorts + "',\n" +
            super.toString() + 
            "\n}";
    }

}
