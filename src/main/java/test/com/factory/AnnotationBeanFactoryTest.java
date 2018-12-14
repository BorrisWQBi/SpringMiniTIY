package test.com.factory;

import com.factory.AnnotationBeanFactory;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.lang.reflect.Method;

public class AnnotationBeanFactoryTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getInstance()
     */
    @Test
    public void testGetInstance() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getBean(String beanName)
     */
    @Test
    public void testGetBean() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: initBeanFactoryByAnnotation(String classLocPath)
     */
    @Test
    public void testInitBeanFactoryByAnnotation() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: scanAllClasses(List<String> paths)
     */
    @Test
    public void testScanAllClasses() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: checkPaths(String classLoc)
     */
    @Test
    public void testCheckPaths() throws Exception {
//TODO: Test goes here... 
    }


    /**
     * Method: getProperties(InputStream is)
     */
    @Test
    public void testGetProperties() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = AnnotationBeanFactory.getClass().getMethod("getProperties", InputStream.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: scanAllClassFiles(File parentDir)
     */
    @Test
    public void testScanAllClassFiles() throws Exception {
        AnnotationBeanFactory beanFactory = (AnnotationBeanFactory) AnnotationBeanFactory.getInstance();
        beanFactory.initBeanFactoryByAnnotation("classpath:application.properties");
    }

    @Test
    public void firstCharLower(){
//        AnnotationBeanFactory beanFactory = (AnnotationBeanFactory) AnnotationBeanFactory.getInstance();
//        String test = beanFactory.firstCharLower("testAAAA");
//        System.out.println(test);
    }

} 
