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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.java.html.js.JavaScriptBody;
import org.netbeans.html.json.spi.FunctionBinding;
import org.netbeans.html.json.spi.PropertyBinding;

/** This is an implementation package.  Just include JAR in the CLASSPATH.
 *
 * @author controlsjs-team
 */
final class NgKnockout {

    private static final Logger LOG = Logger.getLogger(NgKnockout.class.getName());

    public static void applyBindings(Object bindings) {        
        jsapplybindings(bindings, null);
    }

    @JavaScriptBody(args = {"JavaViewModel","JavaFormParent"}, javacall=true, body = ""
        + "var undefined;"
        + "if(!JavaViewModel) return;"
        + "var JavaClass = JavaViewModel['ko-fx.model.getClass']();"
        + "if(JavaFormParent===null) JavaFormParent=undefined;"
        + "var JavaFormDef = @com.controlsjs.controls4j.NgKnockout::getDefsFromResource(Ljava/lang/Object;Ljava/lang/String;)(JavaViewModel['ko-fx.model'], JavaClass+'.ng');"
        + "if(window['Controls4jInitialized'] !== true) {"
        + "  if(typeof window['JavaStartupModels'] === 'undefined') window['JavaStartupModels']=[];"
        + "  window['JavaStartupModels'].push({"
        + "    JavaViewModel: JavaViewModel,"
        + "    JavaFormParent: JavaFormParent,"
        + "    JavaFormDef: JavaFormDef,"
        + "    JavaClass: JavaClass"
        + "  });"
        + "  return;"
        + "}"
        + "var JavaRef = new window.ngControls4j(eval('(' + JavaFormDef + ')'), JavaFormParent, JavaViewModel);"
        + "if(typeof window['JavaViewModels'] === 'undefined') window['JavaViewModels']={};"
        + "window['JavaViewModels'][JavaClass]=JavaViewModel;"
        + "JavaViewModel['JavaForm']=JavaRef;"
        + "if(JavaRef) JavaRef.Update();"
    )
    private static native void jsapplybindings(Object viewmodel, String parent);

    public static String getDefsFromClass(Object cls)
    {
        return getDefsFromResource(cls, cls.getClass().getSimpleName()+".ng");
    }

    public static String getDefsFromResource(Object cls, String res)
    {       
        try
        {            
          InputStream resourceAsStream = cls.getClass().getResourceAsStream(res);
          try
          {
              return getDefsFromStream(resourceAsStream);
          }    
          finally
          {
              resourceAsStream.close();
          }
        }
        catch(IOException e)
        {
            Logger.getLogger(NgKnockout.class.getName()).log(Level.SEVERE, "Error loading Controls.js resource \"{0}\".", res);
            return "";
        }       
    }
    
    public static String getDefsFromStream(InputStream file) throws IOException
    {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(file, "UTF-8"));
        for (int c = br.read(); c != -1; c = br.read()) sb.append((char)c);
        return sb.toString().replaceAll("\\r\\n?", "\n"); // normalize line endings (Android and iOS accepts only LF)
    }

    @JavaScriptBody(args = { "model", "prop", "oldValue", "newValue" }, 
        wait4js = false,
        body =
          "if (model) {\n"
        + "  var koProp = model[prop];\n"
        + "  if (koProp && koProp['valueHasMutated']) {\n"
        + "    if ((oldValue !== null || newValue !== null)) {\n"
        + "      koProp['valueHasMutated'](newValue);\n"
        + "    } else if (koProp['valueHasMutated']) {\n"
        + "      koProp['valueHasMutated']();\n"
        + "    }\n"
        + "  }\n"
        + "}\n"
    )
    public native static void valueHasMutated(
        Object model, String prop, Object oldValue, Object newValue
    );

    @JavaScriptBody(args = { "cnt" }, body = 
        "var arr = new Array(cnt);\n" +
        "for (var i = 0; i < cnt; i++) arr[i] = new Object();\n" +
        "return arr;\n"
    )
    native static Object[] allocJS(int cnt);
    
    @JavaScriptBody(
        javacall = true,
        wait4js = false,
        args = {"ret", "model", "clsname","propNames", "propReadOnly", "propValues", "propArr", "funcNames", "funcArr"},
        body = 
          "Object.defineProperty(ret, 'ko-fx.model', { 'configurable': true, 'writable': true, 'value': model });\n"
        + "Object.defineProperty(ret, 'ko-fx.model.getClass', { 'configurable': true, 'writable': true, 'value': function() { return clsname; } });\n"
        + "function koComputed(name, readOnly, value, prop) {\n"
        + "  var trigger = ko.observable().extend({notify:'always'});"
        + "  function realGetter() {\n"
        + "    try {\n"
        + "      var v = prop.@org.netbeans.html.json.spi.PropertyBinding::getValue()();\n"
        + "      return v;\n"
        + "    } catch (e) {\n"
        + "      alert(\"Cannot call getValue on \" + model + \" prop: \" + name + \" error: \" + e);\n"
        + "    }\n"
        + "  }\n"
        + "  var activeGetter = function() { return value; };\n"
        + "  var bnd = {\n"
        + "    'read': function() {\n"
        + "      trigger();\n"
        + "      var r = activeGetter();\n"
        + "      activeGetter = realGetter;\n"
        + "      if (r) try { var br = r.valueOf(); } catch (err) {}\n"
        + "      return br === undefined ? r: br;\n"
        + "    },\n"
        + "    'owner': ret\n"
        + "  };\n"
        + "  if (!readOnly) {\n"
        + "    bnd['write'] = function(val) {\n"
        + "      var model = val['ko-fx.model'];\n"
        + "      prop.@org.netbeans.html.json.spi.PropertyBinding::setValue(Ljava/lang/Object;)(model ? model : val);\n"
        + "    };\n"
        + "  };\n"
        + "  var cmpt = ko['computed'](bnd);\n"
        + "  cmpt['valueHasMutated'] = function(val) {\n"
        + "    if (arguments.length === 1) activeGetter = function() { return val; };\n"
        + "    trigger.valueHasMutated();\n"
        + "  };\n"
        + "  ret[name] = cmpt;\n"
        + "}\n"
        + "for (var i = 0; i < propNames.length; i++) {\n"
        + "  koComputed(propNames[i], propReadOnly[i], propValues[i], propArr[i]);\n"
        + "}\n"
        + "function koExpose(name, func) {\n"
        + "  ret[name] = function(data, ev) {\n"
        + "    func.@org.netbeans.html.json.spi.FunctionBinding::call(Ljava/lang/Object;Ljava/lang/Object;)(data, ev);\n"
        + "  };\n"
        + "}\n"
        + "for (var i = 0; i < funcNames.length; i++) {\n"
        + "  koExpose(funcNames[i], funcArr[i]);\n"
        + "}\n"
    )
    static native void wrapModel(
        Object ret, Object model, String clsname,
        String[] propNames, boolean[] propReadOnly, Object propValues, PropertyBinding[] propArr,
        String[] funcNames, FunctionBinding[] funcArr
    );
    
    @JavaScriptBody(args = { "o" }, body = "return o['ko-fx.model'] ? o['ko-fx.model'] : o;")
    private static native Object toModelImpl(Object wrapper);
    static Object toModel(Object wrapper) {
        return toModelImpl(wrapper);
    }
    
    @JavaScriptBody(args = {}, body = "if (window.WebSocket) return true; else return false;")
    static final boolean areWebSocketsSupported() {
        return false;
    }
}
