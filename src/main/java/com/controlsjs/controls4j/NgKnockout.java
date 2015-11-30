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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
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
final class NgKnockout extends WeakReference<Object> {

    private static final ReferenceQueue<Object> QUEUE = new ReferenceQueue();
    
    private PropertyBinding[] props;
    private FunctionBinding[] funcs;
    private Object js;
    private Object strong;
    
    public NgKnockout(Object model, Object js, PropertyBinding[] props, FunctionBinding[] funcs) {
        super(model, QUEUE);
        this.js = js;
        this.props = new PropertyBinding[props.length];
        for (int i = 0; i < props.length; i++) {
            this.props[i] = props[i].weak();
        }
        this.funcs = new FunctionBinding[funcs.length];
        for (int i = 0; i < funcs.length; i++) {
            this.funcs[i] = funcs[i].weak();
        }
    }
    
    static void cleanUp() {
        for (;;) {
            NgKnockout ko = (NgKnockout)QUEUE.poll();
            if (ko == null) {
                return;
            }
            clean(ko.js);
            ko.js = null;
            ko.props = null;
            ko.funcs = null;
        }
    }
    
    final void hold() {
        strong = get();
    }
    
    final Object getValue(int index) {
        return props[index].getValue();

    }
    
    final void setValue(int index, Object v) {
        if (v instanceof NgKnockout) {
            v = ((NgKnockout)v).get();
        }
        props[index].setValue(v);
    }
    
    final void call(int index, Object data, Object ev) {
        funcs[index].call(data, ev);
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

    @JavaScriptBody(args = {"JavaFormParent", "JavaViewModel"}, javacall=true, body = ""
        + "var undefined;"
        + "if((!JavaViewModel)||(typeof JavaViewModel.JavaModel !== 'function')) return null;"
        + "var JavaModel = JavaViewModel.JavaModel();\n"
        + "if(!JavaModel) return null;"
        + "var JavaClass = JavaViewModel.JavaModel.getClass();"
        + "if(typeof JavaClass === 'undefined') return JavaModel;"
        + "if((JavaFormParent===null)||(JavaFormParent=='')) JavaFormParent=undefined;"
        + "if(typeof window['JavaViewModels'] === 'undefined') window['JavaViewModels']={};"
        + "window['JavaViewModels'][JavaClass]=JavaViewModel;"
        + "var JavaFormDef = @com.controlsjs.controls4j.NgKnockout::getDefsFromClass(Ljava/lang/Object;)(JavaModel);"
        + "if(window['Controls4jInitialized'] !== true) {"
        + "  if(typeof window['JavaStartupModels'] === 'undefined') window['JavaStartupModels']=[];"
        + "  window['JavaStartupModels'].push({"
        + "    JavaViewModel: JavaViewModel,"
        + "    JavaFormParent: JavaFormParent,"
        + "    JavaFormDef: JavaFormDef,"
        + "    JavaClass: JavaClass"
        + "  });"
        + "  return JavaModel;"
        + "}"
        + "return (function(jvm,jfp,jfd,jm,jc) {"
        + "  eval('var JavaViewModel=arguments[0],JavaFormParent=arguments[1],JavaFormDef=arguments[2],JavaModel=arguments[3],JavaClass=arguments[4];');" // fix obfuscation
        + "  var JavaRef = new window.ngControls4j(eval('(' + JavaFormDef + ')'), JavaFormParent, JavaViewModel);"
        + "  jvm['JavaForm']=JavaRef;"
        + "  if(JavaRef) JavaRef.Update();"
        + "  return JavaRef;"
        + "})(JavaViewModel,JavaFormParent,JavaFormDef,JavaModel,JavaClass);"
    )
    static native Object applyBindings(String id, Object bindings);

    public static void disposeBindings(Object bindings) {
        jsdisposebindings(bindings.getClass().getName());
    }    

    @JavaScriptBody(args = {"JavaClass"}, body = ""
        + "if((typeof window['JavaViewModels'] === 'undefined')"
        + " ||(window['JavaViewModels'][JavaClass] === 'undefined')"
        + " ||(window['JavaViewModels'][JavaClass]['JavaForm'] === 'undefined'))"
        + "  return;"
        + "window['JavaViewModels'][JavaClass]['JavaForm'].Dispose();"
    )
    private static native void jsdisposebindings(String classname);

    @JavaScriptBody(args = {"JavaClass"}, body = ""
        + "if((typeof window['JavaViewModels'] === 'undefined')"
        + " ||(window['JavaViewModels'][JavaClass] === 'undefined'))"
        + "  return null;"
        + "return window['JavaViewModels'][JavaClass];"
    )
    public static native Object jsModelByClassName(String classname);

    public static Object jsModelByClass(Object bindings) {
        return jsModelByClassName(bindings.getClass().getName());
    }    
    
    public static Object javaModelByClassName(String classname) {
        Object ret;
        ret = jsModelByClassName(classname);
        if(ret!=null) ret=toModelImpl(ret);
        return ret;
    }
    
    @JavaScriptBody(args = {"JavaClass"}, body = ""
        + "if((typeof window['JavaViewModels'] === 'undefined')"
        + " ||(window['JavaViewModels'][JavaClass] === 'undefined')"
        + " ||(window['JavaViewModels'][JavaClass]['JavaForm'] === 'undefined'))"
        + "  return null;"
        + "return window['JavaViewModels'][JavaClass]['JavaForm'];"
    )
    public static native Object jsRefsByClassName(String classname);

    public static Object jsRefsByClass(Object bindings) {
        return jsRefsByClassName(bindings.getClass().getName());
    }    

    public static String getDefsFromClass(Object cls)
    {
        if (cls instanceof NgKnockout)
            cls=((NgKnockout)cls).get();
        if (cls==null) return null;

        return getDefsFromResource(cls, cls.getClass().getSimpleName()+".ng");
    }

    public static String getDefsFromResource(Object cls, String res)
    {       
        if (cls instanceof NgKnockout)
            cls=((NgKnockout)cls).get();
        if (cls==null) return null;
        try
        {            
          Class c=cls.getClass();
          if(c==null) return null;
          InputStream resourceAsStream = c.getResourceAsStream(res);
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

    @JavaScriptBody(args = { "cnt" }, body = 
        "var arr = new Array(cnt);\n" +
        "for (var i = 0; i < cnt; i++) arr[i] = new Object();\n" +
        "return arr;\n"
    )
    native static Object[] allocJS(int cnt);
    
    @JavaScriptBody(
        javacall = true,
        keepAlive = false,
        wait4js = false,
        args = { "clsName", "ret", "propNames", "propReadOnly", "propValues", "funcNames" },
        body = 
          "var _this=this;\n"
        + "ret.JavaModel = function() { return _this; };\n"
        + "ret.JavaModel.getClass = function() { return clsName; };\n"
        + "function koComputed(index, name, readOnly, value) {\n"
        + "  var trigger = ko['observable']()['extend']({'notify':'always'});"
        + "  function realGetter() {\n"
        + "    var self = typeof ret.JavaModel === 'function' ? ret.JavaModel() : null;\n"
        + "    try {\n"
        + "      var v = self ? self.@com.controlsjs.controls4j.NgKnockout::getValue(I)(index) : null;\n"
        + "      return v;\n"
        + "    } catch (e) {\n"
        + "      alert(\"Cannot call getValue on \" + self + \" prop: \" + name + \" error: \" + e);\n"
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
        + "      var self = typeof ret.JavaModel === 'function' ? ret.JavaModel() : null;\n"
        + "      if (!self) return;\n"
        + "      var model = typeof val.JavaModel === 'function' ? val.JavaModel() : null;\n"
        + "      self.@com.controlsjs.controls4j.NgKnockout::setValue(ILjava/lang/Object;)(index, model ? model : val);\n"
        + "    };\n"
        + "  };\n"
        + "  var cmpt = ko['computed'](bnd);\n"
        + "  cmpt['valueHasMutated'] = function(val) {\n"
        + "    if (arguments.length === 1) activeGetter = function() { return val; };\n"
        + "    trigger['valueHasMutated']();\n"
        + "  };\n"
        + "  ret[name] = cmpt;\n"
        + "}\n"
        + "for (var i = 0; i < propNames.length; i++) {\n"
        + "  koComputed(i, propNames[i], propReadOnly[i], propValues[i]);\n"
        + "}\n"
        + "function koExpose(index, name) {\n"
        + "  ret[name] = function(data, ev) {\n"
        + "    var self = typeof ret['JavaModel'] === 'function' ? ret.JavaModel() : null;\n"
        + "    if (!self) return;\n"
        + "    self.@com.controlsjs.controls4j.NgKnockout::call(ILjava/lang/Object;Ljava/lang/Object;)(index, data, ev);\n"
        + "  };\n"
        + "}\n"
        + "for (var i = 0; i < funcNames.length; i++) {\n"
        + "  koExpose(i, funcNames[i]);\n"
        + "}\n"
    )
    native void wrapModel(
        String clsName,
        Object ret, 
        String[] propNames, boolean[] propReadOnly, Object propValues,
        String[] funcNames
    );
    
    @JavaScriptBody(args = { "js" }, wait4js = false, body = 
        "delete js.JavaModel;\n" +
        "for (var p in js) {\n" +
        "  delete js[p];\n" +
        "};\n" +
        "\n"
    )
    private static native void clean(Object js);
    
    @JavaScriptBody(args = { "o" }, body = "return typeof o.JavaModel === 'function' ? o.JavaModel() : o;")
    private static native Object toModelImpl(Object wrapper);
    static Object toModel(Object wrapper) {
        Object o = toModelImpl(wrapper);
        if (o instanceof NgKnockout) {
            return ((NgKnockout)o).get();
        } else {
            return o;
        }
    }
    
}
