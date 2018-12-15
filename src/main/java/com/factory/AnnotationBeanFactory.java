package com.factory;

import com.borris.annotation.*;
import com.borris.context.ApplicationContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

public class AnnotationBeanFactory extends AbstractBeanFactory {
    List<String> scanPaths;
    List<String> allClasses;
    File rootDir;

    @Getter
    @Setter
    private Map<String, Object> beanMap;
    @Getter
    @Setter
    private Map<String, Object> controllerMap;
    @Getter
    @Setter
    private Map<String, Object> serviceMap;
    @Getter
    @Setter
    private Map<String, Object> repositoryMap;
    @Getter
    @Setter
    private Map<String, RequestMapEntry> requestUrlMap;

    private AnnotationBeanFactory() {
        beanMap = new HashMap<String, Object>();
        controllerMap = new HashMap<String, Object>();
        serviceMap = new HashMap<String, Object>();
        repositoryMap = new HashMap<String, Object>();
        requestUrlMap = new HashMap<String, RequestMapEntry>();
    }

    public static AnnotationBeanFactory getInstance() {
        if (beanFactory == null) {
            synchronized (AnnotationBeanFactory.class) {
                if (beanFactory == null) {
                    beanFactory = new AnnotationBeanFactory();
                }
            }
        }
        return (AnnotationBeanFactory) beanFactory;
    }

    @Override
    public Object getBean(String beanName) {


        return null;
    }

    @Override
    public void initBeanFactoryByAnnotation(String classLocPath) {
        try {
            scanPaths = checkPaths(classLocPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //2.根据初始化参数中填写的文件路径，遍历其子路径下所有class文件
        // 读取相关文件的bean配置查找路径下所有具有component的class文件，将其加入bean管理
        //懒得（划掉）没精力写xml解析器，此处只实现注解方式的编程式配置
        try {
            allClasses = scanAllClasses(scanPaths);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //3.使用所获得的所有class，用工厂通过单例的方式初始化所有类，并保存进ApplicationContext中
        initClasses(allClasses);

        //4.所有类初始化之后，扫描所有类的成员变量，通过autowired识别并注入

        //5.初始化映射器HandlerMapping

    }


    /**
     * 遍历包路径列表，扫描所有class文件
     * */
    public List<String> scanAllClasses(List<String> paths) throws Exception {
        //获得的root路径，即com的父级目录
        rootDir = new File(ApplicationContext.class.getResource("/").toURI());
        allClasses = new ArrayList<>();
        paths.forEach(classpath -> {
            String pacToPath = classpath.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
            File childPath = new File(rootDir.getAbsoluteFile() + File.separator + pacToPath);
            allClasses.addAll(scanAllClassFiles(childPath));
        });
        return allClasses;
    }

    /**
     * 根据classLoc读取内容，并将所配置的需要读取的文件scanPackage放入数组
     * 因为懒（划掉）精力有限，所以只支持properties文件读取了
     * 支持classpath相对路径或者绝对路径读取
     */
    public List<String> checkPaths(String classLoc) throws IOException {
        String[] paths = classLoc.split(",");
        List<String> result = new ArrayList<String>();
        for (String filePath : paths) {
            InputStream is;
            if (filePath.indexOf("classpath:") == 0) {
                filePath = filePath.substring("classpath:".length());
                is = getClass().getClassLoader().getResourceAsStream(filePath);
            } else {
                is = new BufferedInputStream(new FileInputStream(filePath));
            }
            Properties properties = getProperties(is);
            is.close();
            String scanPackage = properties.getProperty("scanPackage");
            if (StringUtils.isNotEmpty(scanPackage)) {
                result.add(scanPackage.trim());
            }
        }
        return result;
    }

    private Properties getProperties(InputStream is) throws IOException {
        Properties properties = new Properties();
        properties.load(is);
        return properties;
    }


    /**
     * 递归扫描所有class文件
     * */
    private List<String> scanAllClassFiles(File parentDir) {
        List<String> classFiles = new ArrayList<String>();
        File[] flist = parentDir.listFiles();
        for (File child : flist) {
            if (child.isDirectory()) {
                classFiles.addAll(scanAllClassFiles(child));
            } else {
                if (child.getName().endsWith(".class")) {
                    String absPath = child.getAbsolutePath();
                    absPath = absPath.substring(rootDir.getAbsolutePath().length() + 1);
                    classFiles.add(absPath.replaceAll(Matcher.quoteReplacement(File.separator), "\\.").replace(".class",""));
                }
            }
        }
        return classFiles;
    }

    /**
     * 将class文件装载入jvm
     * */
    public void initClasses(List<String> allClasses) {
        ClassLoader cl = this.getClass().getClassLoader();
        allClasses.forEach(className -> {
            try {
                Class clazz = cl.loadClass(className);
                Object bean = clazz.newInstance();
                this.putByAnno(clazz,bean);
            } catch (Exception e) {
                System.out.println("error while loading class "+className);
                e.printStackTrace();
                return;
            }
        });
    }

    private void putByAnno(Class clazz, Object bean) {
        Component component = (Component) clazz.getAnnotation(Component.class);
        String beanName = null;
        if(component != null){
            beanName = component.value();
            if(StringUtils.isEmpty(beanName)){
                beanName = firstCharLower(clazz.getName());
            }
            beanMap.put(beanName,bean);
        }
        Controller controller = (Controller) clazz.getAnnotation(Controller.class);
        if(controller!=null){
            String value = controller.value();
            String controllerName = StringUtils.isEmpty(value)? beanName:value;
            controllerMap.put(controllerName,bean);
            analyseRequestMapping(clazz,bean,controllerName);
        }
        Service service = (Service) clazz.getAnnotation(Service.class);
        if(service!=null){
            String value = service.value();
            String serviceName = StringUtils.isEmpty(value)? beanName:value;
            serviceMap.put(serviceName,bean);
        }
        Repository repository = (Repository) clazz.getAnnotation(Repository.class);
        if(repository!=null){
            String value = repository.value();
            String repositoryName = StringUtils.isEmpty(value)? beanName:value;
            repositoryMap.put(repositoryName,bean);
        }
    }

    private void analyseRequestMapping(Class clazz, Object targetObj, String controllerName) {
        String baseUrl = "";
        RequestMapping rmBase = targetObj.getClass().getAnnotation(RequestMapping.class);
        if (rmBase != null) {
            if (StringUtils.isNotEmpty(rmBase.value())) {
                baseUrl = rmBase.value();
            } else {
                baseUrl = controllerName;
            }
        }
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            RequestMapping rmMethod = method.getAnnotation(RequestMapping.class);
            String finalUrl = rmMethod.value();
            if (StringUtils.isEmpty(finalUrl)) {
                finalUrl = method.getName();
            }
            RequestMapEntry rme = new RequestMapEntry(finalUrl, targetObj, method);
            requestUrlMap.put(finalUrl,rme);
        }
    }

    private String firstCharLower(String name) {
        char[] temp = name.toCharArray();
        if(temp[0]>='A' && temp[0]<='Z'){
            temp[0] += 32;
        }
        return new String(temp);
    }
}

class RequestMapEntry{
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private Object mapObj;
    @Getter
    @Setter
    private Method method;

    public RequestMapEntry(String url, Object mapObj, Method method){
        this.url = url;
        this.mapObj = mapObj;
        this.method = method;
    }
}