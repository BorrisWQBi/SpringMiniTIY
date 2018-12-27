package test.com.proxytest;

import com.borris.proxy.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

public class TestAspect {

    public Object  aroundMethod(MethodInvoker mi) throws InvocationTargetException, IllegalAccessException {
        System.out.println("around test begin");
        Object result = mi.preceed();
        System.out.println("around test end");
        return result;
    }
}
