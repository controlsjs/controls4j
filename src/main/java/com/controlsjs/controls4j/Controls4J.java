/*!
 * Controls.js
 * http://controlsjs.com/
 *
 * Copyright (c) 2014 Position s.r.o.  All rights reserved.
 *
 * The contents of this file are licensed under the terms of GNU General Public License v3.
 * http://www.gnu.org/licenses/gpl-3.0.html
 *
 * The commercial license can be purchased at Controls.js website.
 */
package com.controlsjs.controls4j;

import static com.controlsjs.controls4j.NgContext.isJavaScriptEnabled;
import org.netbeans.html.boot.spi.Fn;
import org.netbeans.html.context.spi.Contexts;
import org.netbeans.html.json.spi.Technology;
import org.openide.util.lookup.ServiceProvider;

/** Controls.js 4 Java enables usage of Controls.js in HTML/Java applications. 
 * It utilizes Java bindings for <a href="http://knockoutjs.com">knockout.js</a>
 * via {@link Model model classes} and links them to 
 * <a href="http://controlsjs.com">Controls.js</a> application/form.
 * Registers {@link Provider}, so {@link java.util.ServiceLoader} can find it.
 * 
 * @author controlsjs-team
 */
@ServiceProvider(service = Contexts.Provider.class)
public final class Controls4J implements Contexts.Provider {

    /** Registers technologies at position 90.
     * @param context the context to register to
     * @param requestor the class requesting the registration
     */
    @Override
    public void fillContext(Contexts.Builder context, Class<?> requestor) {
        if (isJavaScriptEnabled()) {
            NgContext c = new NgContext(Fn.activePresenter());

            context.register(Technology.class, c, 90);
        }
    }
}    
