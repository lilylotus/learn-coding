package cn.nihility.mybatis;

import cn.nihility.mybatis.entity.User;
import cn.nihility.mybatis.mapper.UserAnnotationMapper;
import cn.nihility.mybatis.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * MybatisStart
 * Mybatis 测试启动类
 * @author dandelion
 * @date 2020-04-01 12:55
 */
public class MybatisStart {

    private static final String MYBATIS_CONFIG = "mybatis/mybatis-config.xml";

    public static void main(String[] args) throws IOException {

        InputStream resource = Resources.getResourceAsStream(MYBATIS_CONFIG);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resource);

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            final List<User> allUser = mapper.getAllUser();

            System.out.println(allUser);
            System.out.println("===========================");

            final User userById = mapper.getUserById(1);
            System.out.println(userById);
            System.out.println("===========================");

            final List<User> users = mapper.selectByAge(20);
            System.out.println(users);

            System.out.println("-------------------------------");
            final UserAnnotationMapper annotationMapper = sqlSession.getMapper(UserAnnotationMapper.class);
            final User user = annotationMapper.getUserById(1);
            System.out.println(user);
        }

    }

}
