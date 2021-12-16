package cn.nihility.plugin.mybatis;

import cn.nihility.plugin.mybatis.mapper.FlowerMapper;
import cn.nihility.plugin.mybatis.pojo.Flower;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.*;
import org.springframework.util.StopWatch;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class MybatisTest {

    static Random random = new Random(System.currentTimeMillis());
    static String INSERT_SQL = "INSERT INTO flower (id, name_english, name_chinese, age, add_time, update_time) values (?, ?, ?, ?, ?, ?)";

    @Autowired
    private FlowerMapper flowerMapper;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testSearchAll() {
        List<Flower> flowers = flowerMapper.searchAll();
        Assertions.assertNotNull(flowers);
        System.out.println(Stream.of(flowers).map(Object::toString).collect(Collectors.joining()));
    }

    @Test
    void testInsert() {
        Assertions.assertNotNull(flowerMapper);
        Flower flower = new Flower();
        flower.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        flower.setAge(10);
        flower.setNameChinese("中文");
        flower.setNameEnglish("English");
        Integer integer = flowerMapper.insertByEntity(flower);
        Assertions.assertEquals(1, integer);
    }

    @Test
    void testBatchNormalInsert() {
        Assertions.assertNotNull(flowerMapper);
        final int size = 10;
        int result = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < size; i++) {
            result += flowerMapper.insertByEntity(buildFLower());
        }
        stopWatch.stop();
        System.out.println("Duration time [" + stopWatch.getTotalTimeMillis() + "]");
        Assertions.assertEquals(size, result);
    }

    @Test
    void testBatchListInsert() {
        Assertions.assertNotNull(flowerMapper);
        final int size = 10;
        StopWatch stopWatch = new StopWatch();
        List<Flower> list = buildFlowerList(size);
        stopWatch.start();
        Integer result = flowerMapper.batchInsertListEntity(list);
        stopWatch.stop();
        System.out.println("Duration time [" + stopWatch.getTotalTimeMillis() + "]");
        Assertions.assertEquals(size, result);
    }

    @Test
    void testSqlSessionBatchInsert() {
        Assertions.assertNotNull(sqlSessionTemplate);
        StopWatch stopWatch = new StopWatch();
        final int size = 10;
        try (SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false)) {
            FlowerMapper mapper = session.getMapper(FlowerMapper.class);
            stopWatch.start();

            for (int i = 0; i < size; i++) {
                mapper.insertByEntity(buildFLower());
            }

            session.commit();
            stopWatch.stop();
        }
        System.out.println("Duration time [" + stopWatch.getTotalTimeMillis() + "]");
    }

    @Test
    void restJdbcBatchInsert() {
        StopWatch stopWatch = new StopWatch();
        final int size = 10;
        List<Object[]> argsList = buildFlowerArgsList(size);
        int[] argTypes = buildJdbcVarType();
        stopWatch.start();
        int[] result = jdbcTemplate.batchUpdate(INSERT_SQL, argsList, argTypes);
        stopWatch.stop();
        System.out.println("JdbcBatch Duration time [" + stopWatch.getTotalTimeMillis() + "]");
        Assertions.assertEquals(size, result.length);
    }

    @Test
    void restJdbcBatchInsert2() {
        StopWatch stopWatch = new StopWatch();
        final int size = 10;
        stopWatch.start();
        int result = 0;

        for (int i = 0; i < size; i++) {
            result += jdbcTemplate.update(INSERT_SQL, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    Object[] args = buildFlowerArray();
                    for (int i = 0; i < args.length; i++) {
                        ps.setObject(i + 1, args[i]);
                    }
                }
            });
        }

        stopWatch.stop();
        System.out.println("JdbcBatch Duration time [" + stopWatch.getTotalTimeMillis() + "]");
        Assertions.assertEquals(size, result);
    }

    @Test
    void restJdbcBatchInsert3() {
        StopWatch stopWatch = new StopWatch();
        final int size = 10;
        List<Object[]> argsList = buildFlowerArgsList(size);
        stopWatch.start();
        int[] result = jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int index) throws SQLException {
                Object[] args = argsList.get(index);
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }

            @Override
            public int getBatchSize() {
                return size;
            }
        });
        stopWatch.stop();
        System.out.println("JdbcBatch Duration time [" + stopWatch.getTotalTimeMillis() + "]");
        Assertions.assertEquals(size, result.length);
    }

    @Test
    void testJdbcSelect() {
        Assertions.assertNotNull(jdbcTemplate);
        // "%"?"%"
        // CONCAT(CONCAT('%', ?, '%'))
        jdbcTemplate.query("SELECT id, name_english FROM flower WHERE name_english like CONCAT(CONCAT('%', ?, '%'))",
            new Object[]{"JDBC\\%"}, new int[]{Types.VARCHAR}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    System.out.println("[" + rs.getString(1) + "] : [" + rs.getString(2) + "]");
                }
            });
    }

    private List<Flower> buildFlowerList(int size) {
        List<Flower> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(buildFLower());
        }
        return list;
    }

    private Flower buildFLower() {
        Flower flower = new Flower();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String subId = uuid.substring(0, 6);
        flower.setId(uuid);
        flower.setAge(random.nextInt(8) + 4);
        flower.setNameChinese("中文=" + subId);
        flower.setNameEnglish("English=" + subId);
        flower.setAddTime(LocalDateTime.now());
        flower.setUpdateTime(LocalDateTime.now());
        return flower;
    }

    private Object[] buildFlowerArray() {
        Object[] args = new Object[6];
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String subId = uuid.substring(0, 6);
        args[0] = uuid;
        args[1] = "JDBC-English=" + subId;
        args[2] = "JDBC-中文=" + subId;
        args[3] = random.nextInt(8) + 4;
        args[4] = LocalDateTime.now();
        args[5] = LocalDateTime.now();
        return args;
    }

    private int[] buildJdbcVarType() {
        int[] args = new int[6];
        args[0] = Types.VARCHAR;
        args[1] = Types.VARCHAR;
        args[2] = Types.VARCHAR;
        args[3] = Types.INTEGER;
        args[4] = Types.TIMESTAMP;
        args[5] = Types.TIMESTAMP;
        return args;
    }

    private List<Object[]> buildFlowerArgsList(int size) {
        List<Object[]> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(buildFlowerArray());
        }
        return list;
    }

}
