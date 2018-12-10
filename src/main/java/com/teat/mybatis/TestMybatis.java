package com.teat.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class TestMybatis {

    public static void main(String[] args) throws IOException {
        String configPath = "mybatis.xml";
//        String path2 = "com/test/mybatis/test.xml";
        InputStream is = Resources.getResourceAsStream(configPath);
//        InputStream is2 = Resources.getResourceAsStream(path2);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
        SqlSession session = sqlSessionFactory.openSession(false);
        List<Map<String,String>> list =  session.selectList("selectUser");
        System.out.println(list);
    }
}
