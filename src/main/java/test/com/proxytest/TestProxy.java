package test.com.proxytest;

import com.borris.proxy.JavaDynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class TestProxy {
    public static void main(String[] args){
        TestBeanInf tb = new TestBeanImpl("445");
        InvocationHandler proxy = new JavaDynamicProxy(tb, null);
        TestBeanInf tb___Proxy = (TestBeanInf) Proxy.newProxyInstance(tb.getClass().getClassLoader(),tb.getClass().getInterfaces(),proxy);
        InvocationHandler proxy___2 = new JavaDynamicProxy(tb___Proxy, null);
        TestBeanInf tb___Proxy___2 = (TestBeanInf) Proxy.newProxyInstance(tb.getClass().getClassLoader(),tb.getClass().getInterfaces(),proxy___2);

        tb.print1();
        System.out.println();
        tb___Proxy.print1();
        System.out.println();
        tb___Proxy___2.print1();
    }
}
