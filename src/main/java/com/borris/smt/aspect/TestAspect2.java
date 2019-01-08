package com.borris.smt.aspect;

import com.borris.annotation.*;
import com.borris.proxy.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

@Aspect
@Component
@Order(1)
public class TestAspect2 {

    @Before
    public void testBefore(){
        System.out.println("testAspect testBefore   222");
    }

    @Around
    public Object  aroundMethod(MethodInvoker mi) throws InvocationTargetException, IllegalAccessException {
        System.out.println("around method 222 begin");
        Object result = mi.preceed();
        System.out.println("around method 222 end");
        return result;
    }

    @After
    public void testAfter(){
        System.out.println("testAspect testAfter   222");
    }
}
