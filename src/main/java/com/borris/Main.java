package com.borris;

import java.io.*;
import java.net.URISyntaxException;
import java.util.regex.Matcher;

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
        File file = new File(Main.class.getResource("/").toURI());
        String test = "com.borris.smt";
        String pacToPath = test.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
        File childPath = new File(file.getAbsoluteFile()+ File.separator+pacToPath);

        String absPath = childPath.getAbsolutePath();
        absPath = absPath.substring(file.getAbsolutePath().length()+1);
        absPath = absPath.replaceAll(Matcher.quoteReplacement(File.separator),"\\.");

        System.out.println(absPath);
    }
}
