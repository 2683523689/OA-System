package com.zhang.oa.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

/**
 * @author Gavin
 */
public class MyBatisUtils {
    private static SqlSessionFactory sqlSessionFactory = null;


    static {
        Reader resource = null;
        try {
            resource = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(resource);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 执行select操作sql
     *
     * @param function 要执行写操作的代码块
     * @return 读操作后返回的结果
     */
    public static Object executeQuery(Function<SqlSession, Object> function) {

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            return function.apply(sqlSession);
        }


    }

    /**
     * 执行innsert/update/delete写操作sql
     *
     * @param function 要执行写操作的代码块
     * @return 写操作后返回的结果
     */
    public static Object executeUpdate(Function<SqlSession, Object> function) {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            Object object = function.apply(sqlSession);
            sqlSession.commit();
            return object;
        } catch (RuntimeException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }

    }
}
