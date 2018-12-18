package com.borris.servlet;

import com.factory.AbstractBeanFactory;
import com.factory.RequestMapEntry;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DispatcherServlet extends HttpServlet {

    private AbstractBeanFactory beanFactory;

    @Override
    public void init(ServletConfig config) {
        beanFactory = (AbstractBeanFactory) config.getServletContext().getAttribute("BeanFactory");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)  {
        String url = req.getPathInfo();
        RequestMapEntry rme = beanFactory.getRequestMapEntry(url);
        if(rme !=null){
            Method method = rme.getMethod();
            try {
                rme.invokeMethod(new Object[]{req,resp});
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
