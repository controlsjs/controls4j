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
import net.java.html.js.JavaScriptBody;
import org.netbeans.html.boot.spi.Fn;
import org.netbeans.html.context.spi.Contexts;
import org.netbeans.html.json.spi.FunctionBinding;
import org.netbeans.html.json.spi.PropertyBinding;
import org.netbeans.html.json.spi.Technology;
import static com.controlsjs.controls4j.Controls4J.LOG;
/** This is an implementation package. Just include JAR in the CLASSPATH.
 *
 * @author controlsjs-team
 */
@Contexts.Id("controls4j")
class NgContext implements Technology.BatchInit<Object>, Technology.ValueMutated<Object>, Technology.ApplyId<Object> {
    private Object[] jsObjects;
    private int jsIndex;

    public NgContext(Controls4J self) {
        intializecontrolsjs(self);
    }
    
    @JavaScriptBody(args = { "self" }, javacall = true, body = ""
        // Init variables
        + "if (typeof window['ngJava'] !== 'undefined') return false;"
        + "window['ngJava'] = self;" // Set flag that we are in Java (for those who care)
            
        + "if(window['ngAndroid']) ng_URL = ng_URLCWP;" // Android local file system URL's don't support URL parameters (same as WP)
                                                     // Suprisingly this is not an issue in Cordova.
            
/*        + "window.ngStartParams = function() {"
        + "  for(var i in startparams) this[i]=startparams[i];"
        + "};"
*/
        + "document['ontouchmove'] = function(e){e.preventDefault();};"
            
        // Controls4j specific functions
        + "window['ngControls4j'] = function(def,parent,vm) {"
        + "  this['__ngControls']=window['ngControls'];"
        + "  this['ViewModel']=vm;"
        + "  try { this.__ngControls(def,parent); }"
        + "  finally { delete this['__ngControls']; }"
        + "};"            
            
        + "window['ngControls4j'].version = '1.1';" // LIBRARY VERSION

        // ngMain
        + "window['ngMain'] = function() {" 
        + "  window['Controls4jInitialized']=true;"
        + "  if(typeof window['JavaStartupModels'] !== 'undefined')"
        + "  {"
        + "    var sm;"
        + "    for(var i=0;i<window['JavaStartupModels'].length;i++)"
        + "    {"
        + "      sm=window['JavaStartupModels'][i];"
        + "      sm['JavaRef']=(function(JavaViewModel, JavaFormParent, JavaFormDef, JavaClass) {"
        + "        var def=new Function('JavaViewModel','JavaFormParent','JavaFormDef','JavaModel','JavaClass','return (' + JavaFormDef + ');');"
        + "        var JavaRef = new window.ngControls4j(def(JavaViewModel,JavaFormParent,JavaFormDef,JavaViewModel.JavaModel(),JavaClass), JavaFormParent, JavaViewModel);"
        + "        JavaViewModel['JavaForm']=JavaRef;"
        + "        return JavaRef;"
        + "      })(sm['JavaViewModel'],sm['JavaFormParent'],sm['JavaFormDef'],sm['JavaClass']);"
        + "    }"
        + "  }"

        + "  if(typeof window['JavaStartupModels'] !== 'undefined')"
        + "  {"
        + "    for(var i=0;i<window['JavaStartupModels'].length;i++)"
        + "    {"
        + "      sm=window['JavaStartupModels'][i];"
        + "      if(sm['JavaRef']) sm['JavaRef'].Update();"
        + "    }"
        + "  }"
        + "};"
            
        // Load and run
        + "if(typeof ngLoadApplication==='function') ngLoadApplication('ngApp');"
    )
    private static void intializecontrolsjs(Object self)
    {
      // NOP    
    }

    @JavaScriptBody(args = {}, javacall = false, body = ""
        + "return window['ngJava']"
    )
    public static Object isInitialized() {
        return null;
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
        Object ret = getJSObject();
        final NgKnockout ko = new NgKnockout(model, ret, propArr, funcArr);

        ko.wrapModel(
            model.getClass().getName(),
            ret, 
            propNames, propReadOnly, propValues,
            funcNames
        );
        return ret;
    }

    private Object getJSObject() {
        int len = 64;
        if (jsObjects != null && jsIndex < (len = jsObjects.length)) {
            Object ret = jsObjects[jsIndex];
            jsObjects[jsIndex] = null;
            jsIndex++;
            return ret;
        }
        jsObjects = NgKnockout.allocJS(len * 2);
        jsIndex = 1;
        Object ret = jsObjects[0];
        jsObjects[0] = null;
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
    public void valueHasMutated(Object data, String propertyName) {
        NgKnockout.cleanUp();
        NgKnockout.valueHasMutated(data, propertyName, null, null);
    }

    @Override
    public void valueHasMutated(Object data, String propertyName, Object oldValue, Object newValue) {
        NgKnockout.cleanUp();
        NgKnockout.valueHasMutated(data, propertyName, oldValue, newValue);
    }

    @Override
    public void expose(FunctionBinding fb, Object model, Object d) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyBindings(Object data) {
        applyBindings(null, data);
    }

    @Override
    public void applyBindings(String id, Object data) {
        Object ko = NgKnockout.applyBindings(id, data);
        if (ko instanceof NgKnockout) {
            ((NgKnockout)ko).hold();
        }
    }    
    @Override
    public Object wrapArray(Object[] arr) {
        return arr;
    }

    @Override
    public void runSafe(final Runnable r) {
        LOG.warning("Technology.runSafe has been deprecated. Use BrwsrCtx.execute!");
        r.run();
    }

    @Override
    public <M> M toModel(Class<M> modelClass, Object data) {
        return modelClass.cast(NgKnockout.toModel(data));
    }
}
