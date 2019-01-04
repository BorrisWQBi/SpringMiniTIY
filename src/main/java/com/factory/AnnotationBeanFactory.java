package com.factory;

import com.borris.annotation.*;
import com.borris.context.ApplicationContext;
import com.borris.proxy.AspectImpl;
import com.borris.utils.LambdaExceptionHandler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.borris.utils.Utils.getMethodNode;

public class AnnotationBeanFactory extends AbstractBeanFactory {
    List<String> scanPaths;
    List<String> allClassNames;
    List<Class> allClasses;
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

    @Getter
    @Setter
    private List<Class> aspectClassList;
    @Getter
    @Setter
    private List<AspectImpl> aspectList;

    private AnnotationBeanFactory() {
        beanMap = new HashMap<String, Object>();
        controllerMap = new HashMap<String, Object>();
        serviceMap = new HashMap<String, Object>();
        repositoryMap = new HashMap<String, Object>();
        requestUrlMap = new HashMap<String, RequestMapEntry>();
        aspectList = new ArrayList<AspectImpl>();
        aspectClassList = new ArrayList<Class>();
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
        return beanFactory.getBean(beanName);
    }

    public static Object getBeanByName(String beanName) {
        return beanFactory.getBean(beanName);
    }

    @Override
    public void initBeanFactoryByAnnotation(String classLocPath) {
        try {
            //1.扫描指定路径下所有class
            scanPaths = checkPaths(classLocPath);
            //2.根据初始化参数中填写的文件路径，遍历其子路径下所有class文件
            // 读取相关文件的bean配置查找路径下所有具有component的class文件，将其加入bean管理
            //懒得（划掉）没精力写xml解析器，此处只实现注解方式的编程式配置
            allClassNames = scanAllClasses(scanPaths);
            //3.使用所获得的所有className列表，获得所有的class对象并放入列表中
            List<Class> tempClasses = initClasses(allClassNames);
            //将load到的class全部筛选出aspect class列表
            aspectClassList.addAll(tempClasses.stream().filter(clazz -> clazz.isAnnotationPresent(Aspect.class)).collect(Collectors.toList()));
            //将load到的其他component class全部筛选出放入普通的class列表
            allClasses.addAll(tempClasses.stream()
                    .filter(clazz -> clazz.isAnnotationPresent(Component.class) && !clazz.isAnnotationPresent(Aspect.class))
                    .collect(Collectors.toList()));
            //4.找到aspect注解标记类，并初始化代理接口列表
            aspectList = initAspectList(aspectClassList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 找出所有配置了@Aspect的类，实例化并创建代理接口类
     * 简化AOP类的创建，不考虑依赖与注入，也不实现aspectj的切点表达式
     * 设定aspect接口的规范是指实现方法，并且里面的before after方法只接收切点信息作为参数
     * @param aspectClassList
     */
    private List<AspectImpl> initAspectList(List<Class> aspectClassList) {
        List<AspectImpl> aspectList = new ArrayList<>();
        aspectClassList.forEach(LambdaExceptionHandler.throwingConsumerWrapper(aspectClass ->
                aspectList.add(new AspectImpl(aspectClass))
        ));
        AspectImpl[] sortArray = new AspectImpl[aspectList.size()];
        sortArray = aspectList.toArray(sortArray);
        Arrays.sort(sortArray);
        aspectList.clear();
        aspectList.addAll(Arrays.stream(sortArray).collect(Collectors.toList()));
        return aspectList;
    }


    /**
     * 遍历包路径列表，扫描所有class文件
     * */
    public List<String> scanAllClasses(List<String> paths) throws Exception {
        //获得的root路径，即com的父级目录
        rootDir = new File(ApplicationContext.class.getResource("/").toURI());
        allClassNames = new ArrayList<>();
        paths.forEach(classpath -> {
            String pacToPath = classpath.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
            File childPath = new File(rootDir.getAbsoluteFile() + File.separator + pacToPath);
            allClassNames.addAll(scanAllClassFiles(childPath));
        });
        return allClassNames;
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
    public List<Class> initClasses(List<String> classNameList) {
        ClassLoader cl = this.getClass().getClassLoader();
        List<Class> classList = new ArrayList<>(classNameList.size());
        classNameList.forEach(className -> {
            try {
                Class clazz = cl.loadClass(className);
                classList.add(clazz);
            } catch (Exception e) {
                System.out.println("error while loading class " + className);
                e.printStackTrace();
                return;
            }
        });
        return classList;
    }

    private void putByAnno(Class clazz, Object targetBean) throws Exception {
        String beanName = handleComponent(clazz,targetBean);
        boolean isServiceInf = false;
        isServiceInf = handleController(clazz, targetBean, beanName) || handleService(clazz, targetBean, beanName) || handleRepository(clazz, targetBean, beanName);
//        if(clazz.isAnnotationPresent(Aspect.class)){
//            if(isServiceInf){
//                throw new Exception("cannot register a component as the aspect interface.");
//            }
//            aspectList.add(targetBean);
//        }
    }

    private boolean handleRepository(Class clazz, Object targetBean, String beanName) {
        Repository repository = (Repository) clazz.getAnnotation(Repository.class);
        if(repository!=null){
            String value = repository.value();
            String repositoryName = StringUtils.isEmpty(value)? beanName:value;
            repositoryMap.put(repositoryName,targetBean);
            return true;
        }
        return false;
    }

    private boolean handleService(Class clazz, Object targetBean, String beanName) {
        Service service = (Service) clazz.getAnnotation(Service.class);
        if(service!=null){
            String value = service.value();
            String serviceName = StringUtils.isEmpty(value)? beanName:value;
            serviceMap.put(serviceName,targetBean);
            return true;
        }
        return false;
    }

    private boolean handleController(Class clazz, Object targetBean, String beanName) throws IOException {
        Controller controller = (Controller) clazz.getAnnotation(Controller.class);
        if(controller!=null){
            String value = controller.value();
            String controllerName = StringUtils.isEmpty(value)? beanName:value;
            controllerMap.put(controllerName,targetBean);
            analyseRequestMapping(clazz,targetBean,controllerName);
            return true;
        }
        return false;
    }

    private String handleComponent(Class clazz, Object targetBean) {
        Component component = (Component) clazz.getAnnotation(Component.class);
        String beanName = null;
        if(component != null){
            beanName = component.value();
            if(StringUtils.isEmpty(beanName)){
                beanName = firstCharLower(clazz.getName());
            }
            beanMap.put(beanName,createProxyBean(targetBean));
        }
        return beanName;
    }

    private Object createProxyBean(Object targetBean) {
        Class clazz = targetBean.getClass();
        if(clazz.getInterfaces()!=null){
//            JavaDynamicProxy jdp = new JavaDynamicProxy(targetBean);
        }
        return null;
    }

    private void analyseRequestMapping(Class clazz, Object targetObj, String controllerName) throws IOException {
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
        ClassReader cr = new ClassReader(clazz.getClassLoader().getResourceAsStream(clazz.getName().replaceAll("\\.", "/") + ".class"));
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        List<MethodNode> methodNodes = cn.methods;

        for (Method method : methods) {
            MethodNode methodNode = getMethodNode(methodNodes,method);
            String[] paraNames = new String[methodNode.localVariables.size()-1];
            for (int i = 1; i < methodNode.localVariables.size(); i++) {
                paraNames[i-1] = ((LocalVariableNode)methodNode.localVariables.get(i)).name;
            }
            RequestMapping rmMethod = method.getAnnotation(RequestMapping.class);
            String finalUrl = rmMethod.value();
            if (StringUtils.isEmpty(finalUrl))
                finalUrl = method.getName();
            if (StringUtils.isNotEmpty(baseUrl))
                finalUrl = baseUrl + finalUrl;
            RequestMapEntry rme = new RequestMapEntry(finalUrl, targetObj, method,paraNames);
            requestUrlMap.put(finalUrl, rme);
        }
    }


    private String firstCharLower(String name) {
        char[] temp = name.toCharArray();
        if(temp[0]>='A' && temp[0]<='Z'){
            temp[0] += 32;
        }
        return new String(temp);
    }

    public RequestMapEntry getRequestMapEntry(String url){
        return requestUrlMap.get(url);
    }
}