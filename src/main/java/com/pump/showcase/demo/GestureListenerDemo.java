/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.showcase.demo;

import com.pump.desktop.error.ErrorManager;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.net.URL;
import java.util.LinkedList;

public class GestureListenerDemo extends ShowcaseDemo {

    JTextPane textPane = new JTextPane();
    JScrollPane scrollPane = new JScrollPane(textPane);

    public GestureListenerDemo() {
        setLayout(new GridBagLayout());
        scrollPane.setPreferredSize(new Dimension(400, 300));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
        add(scrollPane, c);

        try {
            List<Object> listeners = new LinkedList<>();
            listeners.add(instantiate("com.apple.eawt.event.GesturePhaseListener"));
            listeners.add(instantiate("com.apple.eawt.event.MagnificationListener"));
            listeners.add(instantiate("com.apple.eawt.event.RotationListener"));
            listeners.add(instantiate("com.apple.eawt.event.SwipeListener"));

            for (Object listener : listeners) {
                invokeStaticMethod("com.apple.eawt.event.GestureUtilities", "addGestureListenerTo", textPane, listener);
            }
        } catch(Throwable t) {
            t.printStackTrace();
            log(ErrorManager.getStackTrace(t));
        }
    }

    private void invokeStaticMethod(String className, String methodName, Object... args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Class c = Class.forName(className);
        for (Method method : c.getMethods()) {
            if (methodName.equals(method.getName())) {
                method.setAccessible(true);
                method.invoke(null, args);
                return;
            }
        }
        throw new RuntimeException("did not find " + className + "#" + methodName);
    }

    private void log(String msg) {
        String str = textPane.getText();
        str += "\n" + msg;
        textPane.setText(str);
    }

    private Object instantiate(String interfaceName) throws ClassNotFoundException {
        Class c = Class.forName(interfaceName);
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                StringBuilder sb = new StringBuilder();
                List<String> indentedLines = new LinkedList<>();
                sb.append("(");
                for (int a = 0; a < args.length; a++) {
                    sb.append(args[a].getClass().getSimpleName());
                    indentedLines.add(toString(args[a]));
                }
                sb.append(")");
                log(method.getName() + sb);
                for (String indentedLine : indentedLines) {
                    log("\t" + indentedLine);
                }

                return Void.TYPE;
            }

            private String toString(Object bean) {
                String str = "";
                Class z = bean.getClass();
                for (Method method : z.getMethods()) {
                    System.out.println(method.getReturnType() + " "+ method.getParameterCount()+" "+ method.getName());
                    if (method.getReturnType() != Void.TYPE && method.getParameterCount() == 0 &&
                            (method.getName().startsWith("get") || method.getName().startsWith("is")) &&
                            !method.getName().equals("getClass")) {
                        try {
                            str += " " + method.getName() + "() = " + method.invoke(bean);
                        } catch(Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
                return str.trim();
            }
        };
        return Proxy.newProxyInstance(c.getClassLoader(), new Class[] { c }, handler);
    }

    @Override
    public String getTitle() {
        return "GestureListener Demo";
    }

    @Override
    public String getSummary() {
        return "This demos listeners for special Mac touchpad events.";
    }

    @Override
    public URL getHelpURL() {
        return null;
    }

    @Override
    public String[] getKeywords() {
        // TODO
        return new String[0];
    }

    @Override
    public Class<?>[] getClasses() {
        // TODO
        return new Class[0];
    }
}