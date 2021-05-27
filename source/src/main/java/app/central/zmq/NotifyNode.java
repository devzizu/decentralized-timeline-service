package app.central.zmq;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import app.central.usernode.IpPort;
import app.exchange.zmq.Notification;
import app.util.data.Serialization;

public class NotifyNode{

    public static void notify(String host, long port, String subscription, IpPort newConection){
        try (ZContext context = new ZContext();
             ZMQ.Socket socket = context.createSocket(SocketType.PUSH))
        {
            socket.connect("tcp://"+host+":" + port);

            Notification message = new Notification(subscription,newConection);

            message.setStatusCode(true);
            message.setStatusMessage("your provider of the subscriptions "+subscription+" went offline.");
            
            socket.send(Serialization.serialize(message));

            socket.close();

            context.close();
        
        } catch(Exception e){
            e.printStackTrace();
        }
    }


}