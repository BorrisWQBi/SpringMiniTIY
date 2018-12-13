package com.factory;

import com.borris.context.ApplicationContext;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

public class AnnotationBeanFactory extends AbstractBeanFactory {
    List<String> scanPaths;
    List<String> allClasses;
    File rootDir;


    private AnnotationBeanFactory() {
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
        ApplicationContext.initClasses(allClasses);

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
                    classFiles.add(absPath.replaceAll(Matcher.quoteReplacement(File.separator), "\\."));
                }
            }
        }
        return classFiles;
    }
}
