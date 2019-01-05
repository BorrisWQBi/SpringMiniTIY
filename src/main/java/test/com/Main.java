package test.com;

import com.borris.annotation.Component;
import com.borris.annotation.Controller;
import com.borris.smt.controller.TestController;
import com.borris.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.function.BinaryOperator;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        /*
        String path = "classpath:application.properties";
        InputStream is;
        if(path.indexOf("classpath:")==0){
            path = path.substring("classpath:".length());
            is = Main.class.getClassLoader().getResourceAsStream(path);
        }else{
            is = new BufferedInputStream(new FileInputStream(path));
        }
        byte[] b = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int i = 0;
        while((i=is.read(b))>0){
            bos.write(b,0,i);
        }
        is.close();
        b = bos.toByteArray();
        System.out.println(new String(b));
        */
//        File file = new File(Main.class.getResource("/").toURI());
//        String test = "com.borris.smt";
//        String pacToPath = test.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
//        File childPath = new File(file.getAbsoluteFile()+ File.separator+pacToPath);
//
//        String absPath = childPath.getAbsolutePath();
//        absPath = absPath.substring(file.getAbsolutePath().length()+1);
//        absPath = absPath.replaceAll(Matcher.quoteReplacement(File.separator),"\\.");

        BinaryOperator<Long> add = (x, y)->x+y;
        System.out.println(add.apply(10L,20L));

        ProtectionDomain pd = StringUtils.class.getProtectionDomain();
        CodeSource cs = pd.getCodeSource();
        System.out.println(cs.getLocation());

        System.out.println(Arrays.toString(TestController.class.getAnnotations()));

        System.out.println(Utils.checkHasAnnotation(TestController.class,Component.class));
    }
}
