package com.borris.smt.aspect;

import com.borris.annotation.*;

@Aspect
@Component
@Order(2)
public class TestAspect {

    @Before
    public void testBefore(){
        System.out.println("testAspect testBefore 111");
    }

    @After
    public void testAfter(){
        System.out.println("testAspect testAfter 111");
    }
}
