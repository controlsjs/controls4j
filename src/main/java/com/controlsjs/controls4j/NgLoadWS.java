package com.controlsjs.controls4j;

import net.java.html.js.JavaScriptBody;
import org.netbeans.html.json.spi.JSONCall;

final class LoadWS {
    private final Object ws;
    private final JSONCall call;
    LoadWS(JSONCall first, String url) {
        call = first;
        ws = initWebSocket(this, url);
        if (ws == null) {
            first.notifyError(new IllegalArgumentException("Wrong URL: " + url));
        }
    }
    
    void send(JSONCall call) {
        push(call);
    }
    
    private synchronized void push(JSONCall call) {
        send(ws, call.getMessage());
    }

    void onOpen(Object ev) {
        if (!call.isDoOutput()) {
            call.notifySuccess(null);
        }
    }
        
    @JavaScriptBody(args = { "data" }, body = "try {\n"
        + "    return eval('(' + data + ')');\n"
        + "  } catch (error) {;\n"
        + "    return data;\n"
        + "  }\n"
    )
    private static native Object toJSON(String data);
    
    void onMessage(Object ev, String data) {
        Object json = toJSON(data);
        call.notifySuccess(json);
    }
    
    void onError(Object ev) {
        call.notifyError(new Exception(ev.toString()));
    }

    void onClose(boolean wasClean, int code, String reason) {
        call.notifyError(null);
    }
    
    @JavaScriptBody(args = { "back", "url" }, javacall = true, body = ""
        + "if (window.WebSocket) {\n"
        + "  try {\n"
        + "    var ws = new window.WebSocket(url);\n"
        + "    ws.onopen = function(ev) {\n"
        + "      back.@com.controlsjs.controls4j.LoadWS::onOpen(Ljava/lang/Object;)(ev);\n"
        + "    };\n"
        + "    ws.onmessage = function(ev) {\n"
        + "      back.@com.controlsjs.controls4j.LoadWS::onMessage(Ljava/lang/Object;Ljava/lang/String;)(ev, ev.data);\n"
        + "    };\n"
        + "    ws.onerror = function(ev) {\n"
        + "      back.@com.controlsjs.controls4j.LoadWS::onError(Ljava/lang/Object;)(ev);\n"
        + "    };\n"
        + "    ws.onclose = function(ev) {\n"
        + "      back.@com.controlsjs.controls4j.LoadWS::onClose(ZILjava/lang/String;)(ev.wasClean, ev.code, ev.reason);\n"
        + "    };\n"
        + "    return ws;\n"
        + "  } catch (ex) {\n"
        + "    return null;\n"
        + "  }\n"
        + "} else {\n"
        + "  return null;\n"
        + "}\n"
    )
    private static Object initWebSocket(Object back, String url) {
        return null;
    }
    

    @JavaScriptBody(args = { "ws", "msg" }, body = ""
        + "ws.send(msg);"
    )
    private void send(Object ws, String msg) {
    }

    @JavaScriptBody(args = { "ws" }, body = "ws.close();")
    private static void close(Object ws) {
    }

    void close() {
        close(ws);
    }
}
