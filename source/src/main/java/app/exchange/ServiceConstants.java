
package app.exchange;

public class ServiceConstants {

    // node request handler names
    public static final String NODE_LOGIN_REQUEST = "node_login_request";      
    public static final String NODE_REGISTER_REQUEST = "node_register_request";      
    public static final String NODE_LOGOUT_REQUEST = "node_logout_request";
    public static final String NODE_SUBSCRIBE_REQUEST = "node_subscribe_request";        

    // peer request handler names
    public static final String PEER_CLOCK_REQUEST = "peer_clock_request";        
    public static final String PEER_RECOVER_REQUEST = "peer_recover_request";

    // peer request handler names
    public static final String PEER_CLOCK_RESPONSE = "peer_clock_response";        
    public static final String PEER_RECOVER_RESPONSE = "peer_recover_response";

    // central response handler names
    public static final String CENTRAL_LOGIN_RESPONSE = "central_login_response";
    public static final String CENTRAL_REGISTER_RESPONSE = "central_register_response";        
    public static final String CENTRAL_LOGOUT_RESPONSE = "central_logout_response";        
    public static final String CENTRAL_SUBSCRIBE_RESPONSE = "central_subscribe_response";        

    // node inproc server name
    public static final String INPROC_PUB = "pubInprocProcess";
    public static final String INPROC_TIMELINE = "pushInprocTimeline";
}