/*!
 * Controls.js
 * http://controlsjs.com/
 *
 * Copyright (c) 2014-2015 Position s.r.o.  All rights reserved.
 *
 * The contents of this file are licensed under the terms of GNU General Public License v3.
 * http://www.gnu.org/licenses/gpl-3.0.html
 *
 * The commercial license can be purchased at Controls.js website.
 */
package com.controlsjs.controls4j;

import java.util.logging.Logger;
import org.netbeans.html.boot.spi.Fn;
import org.netbeans.html.context.spi.Contexts;
import org.netbeans.html.context.spi.Contexts.Provider;
import org.netbeans.html.json.spi.Technology;
import org.netbeans.html.json.spi.Transfer;
import org.netbeans.html.json.spi.WSTransfer;
import org.openide.util.lookup.ServiceProvider;

/** Controls.js 4 Java enables usage of Controls.js in HTML/Java applications. 
 * It utilizes Java bindings for <a href="http://knockoutjs.com">knockout.js</a>
 * via {@link Model model classes} and links them to 
 * <a href="http://controlsjs.com">Controls.js</a> application/form.
 * Registers {@link Provider}, so {@link java.util.ServiceLoader} can find it.
 * 
 * @author controlsjs-team
 */
@ServiceProvider(service = Provider.class)
public final class Controls4J implements Provider {
    static final Logger LOG = Logger.getLogger(Controls4J.class.getName());
    private NgContext  controls4j;
    private NgTransfer trans;
    private NgSockets  socks;

    public Controls4J() {
        this(null);
    }

    @Deprecated
    public Controls4J(Fn.Presenter presenter) {
    }
    
    /** Return instance of the knockout.js for Java technology.
     * @return non-null instance
     */
    public Technology controls() {
        if (controls4j == null) {
            controls4j = new NgContext(this);
        }
        return controls4j;
    }
    
    /** Disposes ViewModel's bindings
     * @param bindings the ViewModel which bindings will be disposed
     */
    public static void disposeBindings(Object bindings) {
      NgKnockout.disposeBindings(bindings);
    }

    /** Returns reference to JavaScript representation of Java Model specified by its classname
     * @param classname the Model's class name which JavaScript representation is required
     * @return JavaScript object or null if not exist
     */
    public static Object jsModelByClassName(String classname) {
        return NgKnockout.jsModelByClassName(classname);
    }

    /** Returns reference to JavaScript representation of Java Model
     * @param bindings the Model which JavaScript representation is required
     * @return JavaScript object or null if not exist
     */
    public static Object jsModelByClass(Object bindings) {
        return NgKnockout.jsModelByClass(bindings);
    }    
    
    /** Returns Java Model which bindings was applied specified by its classname
     * @param classname the Model's class name which instance is required
     * @return Model or null if not exist
     */
    public static Object javaModelByClassName(String classname) {
        return NgKnockout.javaModelByClassName(classname);
    }
    
    /** Returns JavaScript object which contains references to GUI controls created by Java Model specified by its classname
     * @param classname the Model's class name which GUI references are required
     * @return JavaScript object or null if not exist
     */
    public static Object jsRefsByClassName(String classname) {
        return NgKnockout.jsRefsByClassName(classname);
    }

    /** Returns JavaScript object which contains references to GUI controls created by Java Model
     * @param classname the Model which GUI references are required
     * @return JavaScript object or null if not exist
     */
    public static Object jsRefsByClass(Object bindings) {
        return NgKnockout.jsRefsByClass(bindings);
    }
    
    public Transfer transfer() {
        if (trans == null) {
            trans = new NgTransfer();
        }
        return trans;
    }
    
    /** Returns browser based implementation of websocket transfer.
     * If available (for example JavaFX WebView on JDK7 does not have
     * this implementation).
     * 
     * @return an instance or <code>null</code>, if there is no
     *   <code>WebSocket</code> object in the browser
     */
    public WSTransfer<?> websockets() {
        if (!NgSockets.areWebSocketsSupported()) {
            return null;
        }
        if (socks == null) {
            socks = new NgSockets();
        }
        return socks;
    }

    /** Registers technologies at position 200.
     * @param context the context to register to
     * @param requestor the class requesting the registration
     */
    @Override
    public void fillContext(Contexts.Builder context, Class<?> requestor) {
        Object ctx = NgContext.isInitialized();
        if (!(ctx instanceof Controls4J)) {
            ctx = this;
        }
        Controls4J c4j = (Controls4J)ctx;
        
        context.register(Technology.class, c4j.controls(), 200);
        context.register(Transfer.class, c4j.transfer(), 200);
        context.register(WSTransfer.class, c4j.websockets(), 200);
    }
}    
