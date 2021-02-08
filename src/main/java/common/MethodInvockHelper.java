package common;

import java.lang.reflect.Method;

public class MethodInvockHelper {

    public MethodInvockHelper(Class<?> c, Object cInstance, Method method) {
        this.c = c;
        this.cInstance = cInstance;
        this.method = method;
    }

    private Class<?> c;

    private Object cInstance;

    private Method method;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getC() {
        return c;
    }

    public void setC(Class<?> c) {
        this.c = c;
    }

    public Object getcInstance() {
        return cInstance;
    }

    public void setcInstance(Object cInstance) {
        this.cInstance = cInstance;
    }
}
