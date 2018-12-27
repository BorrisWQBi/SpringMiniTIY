package test.com.proxytest;

import com.borris.proxy.AspectImpl;
import com.borris.proxy.JavaDynamicProxy;
import com.borris.proxy.MethodInvoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class TestProxy {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        testdigui();

    }

    private static void testdigui() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TestBeanInf tbi = new TestBeanImpl("abc");
        Method targetMethod = tbi.getClass().getMethod("print1");
        Method targetMethod2 = tbi.getClass().getMethod("print2");
        MethodInvoker targetInvoker = new MethodInvoker(tbi, targetMethod);

        TestAspect aspect = new TestAspect();
        List<Method> milist = new ArrayList<>();
        Method around = aspect.getClass().getMethod("aroundMethod", MethodInvoker.class);
        for (int i = 0; i < 3; i++) {
            milist.add(around);
        }

        AspectImpl ai = new AspectImpl();
        ai.setAroundMethodList(milist);
        ai.setTargetAspect(aspect);
        ai.buildAroundStacks();
        ai.invokeAround(tbi, targetMethod);
        ai.invokeAround(tbi, targetMethod2);

    }

    public static void test1(){
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
