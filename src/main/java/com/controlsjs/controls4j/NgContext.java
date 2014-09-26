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

import java.util.logging.Logger;
import net.java.html.js.JavaScriptBody;
import org.netbeans.html.boot.spi.Fn;
import org.netbeans.html.json.spi.FunctionBinding;
import org.netbeans.html.json.spi.PropertyBinding;
import org.netbeans.html.json.spi.Technology;

/** This is an implementation package. Just include JAR in the CLASSPATH.
 *
 * @author controlsjs-team
 */
class NgContext implements Technology.BatchInit<Object>, Technology.ValueMutated<Object> {
    private static final Logger LOG = Logger.getLogger(NgContext.class.getName());
    private Object[] jsObjects;
    private int jsIndex;
    private static Boolean javaScriptEnabled;
    private final Fn.Presenter browserContext;
    private static Boolean initialized = false;

    public NgContext(Fn.Presenter browserContext) {
        this.browserContext = browserContext;

        if(!initialized)
        {
            initialized=true;
            intializecontrolsjs();
        }
    }
    
    @JavaScriptBody(args = {}, javacall = true, body = ""
        // Init variables
        + "window.ngJava = true;" // Set flag that we are in Java (for those who care)
            
        + "if(window.ngAndroid) ng_URL = ng_URLCWP;" // Android local file system URL's don't support URL parameters (same as WP)
                                                     // Suprisingly this is not an issue in Cordova.
            
/*        + "window.ngStartParams = function() {"
        + "  for(var i in startparams) this[i]=startparams[i];"
        + "};"
*/
        + "document.ontouchmove = function(e){e.preventDefault();};"
            
        // Controls4j specific functions
        + "window.ngControls4j = function(def,parent,vm) {"
        + "  this.__ngControls=window.ngControls;"
        + "  this.ViewModel=vm;"
        + "  try { this.__ngControls(def,parent); }"
        + "  finally { delete this.__ngControls; }"
        + "};"            

        // ngMain
        + "window.ngMain = function() {" 
        + "  window.Controls4jInitialized=true;"
        + "  if(typeof window.JavaStartupModels !== 'undefined')"
        + "  {"
        + "    if(typeof window.JavaViewModels === 'undefined') window.JavaViewModels={};"
        + "    var sm;"
        + "    for(var i=0;i<window.JavaStartupModels.length;i++)"
        + "    {"
        + "      sm=window.JavaStartupModels[i];"
        + "      sm.JavaRef=(function(JavaViewModel, JavaFormParent, JavaFormDef, JavaClass) {"
        + "        var JavaRef = new window.ngControls4j(eval('(' + JavaFormDef + ')'), JavaFormParent, JavaViewModel);"
        + "        window.JavaViewModels[JavaClass]=JavaViewModel;"
        + "        JavaViewModel.JavaForm=JavaRef;"
        + "        return JavaRef;"
        + "      })(sm.JavaViewModel,sm.JavaFormParent,sm.JavaFormDef,sm.JavaClass);"
        + "    }"
        + "  }"

        + "  if(typeof window.JavaStartupModels !== 'undefined')"
        + "  {"
        + "    for(var i=0;i<window.JavaStartupModels.length;i++)"
        + "    {"
        + "      sm=window.JavaStartupModels[i];"
        + "      if(sm.JavaRef) sm.JavaRef.Update();"
        + "    }"
        + "  }"
        + "};"
            
        // Load and run
        + "if(typeof ngLoadApplication==='function') ngLoadApplication('ngApp');"
    )
    private static native void intializecontrolsjs();
  
    @JavaScriptBody(args = {}, body = "return true;")
    private static boolean isJavaScriptEnabledJs() {
        return false;
    }
    
    static boolean isJavaScriptEnabled() {
        if (javaScriptEnabled != null) {
            return javaScriptEnabled;
        }
        return javaScriptEnabled = isJavaScriptEnabledJs();
    }

    @Override
    public Object wrapModel(Object model, PropertyBinding[] propArr, FunctionBinding[] funcArr) {
        String[] propNames = new String[propArr.length];
        boolean[] propReadOnly = new boolean[propArr.length];
        Object[] propValues = new Object[propArr.length];
        for (int i = 0; i < propNames.length; i++) {
            propNames[i] = propArr[i].getPropertyName();
            propReadOnly[i] = propArr[i].isReadOnly();
            propValues[i] = propArr[i].getValue();
        }
        String[] funcNames = new String[funcArr.length];
        for (int i = 0; i < funcNames.length; i++) {
            funcNames[i] = funcArr[i].getFunctionName();
        }
        Object ret;
        int len = 64;
        if (jsObjects != null && jsIndex < (len = jsObjects.length)) {
            ret = jsObjects[jsIndex++];
        } else {
            jsObjects = NgKnockout.allocJS(len * 2);
            jsIndex = 1;
            ret = jsObjects[0];
        }
        NgKnockout.wrapModel(ret, model, model.getClass().getSimpleName(),
            propNames, propReadOnly, propValues, propArr,
            funcNames, funcArr
        );
        return ret;
    }
    
    @Override
    public Object wrapModel(Object model) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bind(PropertyBinding b, Object model, Object data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void valueHasMutated(Object data, String propertyName, Object oldValue, Object newValue) {
        NgKnockout.valueHasMutated(data, propertyName, oldValue, newValue);
    }

    @Override
    public void valueHasMutated(Object data, String propertyName) {
        NgKnockout.valueHasMutated(data, propertyName, null, null);
    }

    @Override
    public void expose(FunctionBinding fb, Object model, Object d) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyBindings(Object data) {
        NgKnockout.applyBindings(data);
    }

    @Override
    public Object wrapArray(Object[] arr) {
        return arr;
    }

    @Override
    public <M> M toModel(Class<M> modelClass, Object data) {
        return modelClass.cast(NgKnockout.toModel(data));
    }

    @Override
    public void runSafe(final Runnable r) {
        LOG.warning("Technology.runSafe has been deprecated. Use BrwsrCtx.execute!");
        r.run();
    }
}
