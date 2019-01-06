package com.borris.smt.aspect;

import com.borris.annotation.*;

@Aspect
@Component
@Order(1)
public class TestAspect2 {

    @Before
    public void testBefore(){
        System.out.println("testAspect testBefore   222");
    }

    @After
    public void testAfter(){
        System.out.println("testAspect testAfter   222");
    }
}
