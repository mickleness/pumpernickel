/**
 * The x-ray project produces a jar whose method signatures resemble existing
 * java.lang.Class objects.
 * <p>
 * The goal is to create a jar that you can compile against even if you don't actually
 * have access to the real jar/war files. For example: if you don't host a server,
 * but you can execute code inside in, then x-ray can export a jar that resembles some
 * class/method/field signatures in the original JVM.
 * <p>
 * All the methods/constructors are completely empty. They match in the return value,
 * parameter types, throwable clauses, etc., but they do nothing. All fields are null
 * (or zero), except for simple constants (such as Strings, chars and numbers).
 * <p>
 * The intention here to both help 3rd party developers write code against a library
 * while not sharing any internal code.
 * <p>
 * By default this omits private methods/fields/classes, but you can customize
 * which methods/fields/classes are considered (either adding or removing from the default).
 * <p>
 * <h3>Future Versions</h3>
 * <p>
 * With the help of a 3rd party parse (like Eclipse's), we could easily parse actual javadoc from methods
 * in environments where we also have access to the java source code. We could then
 * transfer that javadoc to the x-ray export. This would keep method bodies empty, but
 * it would give developers the advantage of javadoc and argument names.
 * <p>
 * For example, it may be invaluable to know that the 4 parameters in a function are
 * labeled: x, y, width, and height. But x-ray version 1.0 will only ever identify these
 * parameters as: arg0, arg1, arg2, and arg3.
 */
package com.pump.xray;

