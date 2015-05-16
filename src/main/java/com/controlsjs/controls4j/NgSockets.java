package com.controlsjs.controls4j;

import net.java.html.js.JavaScriptBody;
import org.netbeans.html.context.spi.Contexts;
import org.netbeans.html.json.spi.JSONCall;
import org.netbeans.html.json.spi.WSTransfer;

@Contexts.Id("controls4j-ws")
final class NgSockets
implements WSTransfer<LoadWS> {
    NgSockets() {
    }
    
    @Override
    public LoadWS open(String url, JSONCall onReply) {
        return new LoadWS(onReply, url);
    }

    @Override
    public void send(LoadWS socket, JSONCall data) {
        socket.send(data);
    }

    @Override
    public void close(LoadWS socket) {
        socket.close();
    }

    @JavaScriptBody(args = {}, body = "if (window['WebSocket']) return true; else return false;")
    static final boolean areWebSocketsSupported() {
        return false;
    }
}
