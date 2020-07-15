package cn.nihility.mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

/**
 * BatchOperation
 *
 * @author dandelion
 * @date 2020-05-07 11:32
 */
public class BatchOperation {

    private static final String BATCH_INSERT_SQL = "INSERT INTO orders_history(order_name,type,field001_name,field002_name,field003_name,field004_name,field005_name,field006_name,field007_name,field008_name,field009_name,field010_name,field011_name,field012_name,field013_name,field014_name,field015_name,field016_name,field017_name,field018_name,field019_name,field020_name,field021_name,field022_name,field023_name,field024_name,field025_name,field026_name,field027_name,field028_name,field029_name,field030_name,field031_name,field032_name,field033_name,field034_name,field035_name,field036_name,field037_name,field038_name,field039_name,field040_name) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String BATCH_INSERT_SQL_VALUES = "INSERT INTO orders_history(order_name,type,field001_name,field002_name,field003_name,field004_name,field005_name,field006_name,field007_name,field008_name,field009_name,field010_name,field011_name,field012_name,field013_name,field014_name,field015_name,field016_name,field017_name,field018_name,field019_name,field020_name,field021_name,field022_name,field023_name,field024_name,field025_name,field026_name,field027_name,field028_name,field029_name,field030_name,field031_name,field032_name,field033_name,field034_name,field035_name,field036_name,field037_name,field038_name,field039_name,field040_name) VALUES";
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        final BatchOperation batchOperation = new BatchOperation();
//        batchOperation.batchInsert();
        batchOperation.batchInsertValues();
    }

    /**
     * 100 - avg commit time : 420 | avg commit time : 349
     * 1000 - avg commit time : 834 | avg commit time : 462
     * 10000 - avg commit time : 4340 | 1840
     * 5000 - avg commit time : 2356 | 1415
     */

    public void batchInsert() {

        int loop = 100000;
        int commitCnt = 10000;
        Connection connection = null;
        PreparedStatement statement = null;

        long totalCommitTime = 0;
        long totalPrepareTime = 0;
        try {
            connection = DBUtil.getConnection();
            statement = connection.prepareStatement(BATCH_INSERT_SQL);

            connection.setAutoCommit(false);

            long prepareStartTime = System.currentTimeMillis();
            for (int i = 0; i < loop; i++) {

                statement.setString(1, UUID.randomUUID().toString().substring(0, 16));
                statement.setInt(2, RANDOM.nextInt(6) + 5);

                for (int j = 1; j <= 40; j++) {
                    statement.setString(j + 2, UUID.randomUUID().toString().substring(0, 16));
                }

                statement.addBatch();

                if ((i > 0 && (i % commitCnt == 0)) || (i + 1) == loop) {
                    long commitStartTime = System.currentTimeMillis();
                    statement.executeBatch();
                    connection.commit();
                    long endTime = System.currentTimeMillis();

                    totalCommitTime += (endTime - commitStartTime);
                    totalPrepareTime += (commitStartTime - prepareStartTime);

                    System.out.println("index : " + i + " commit time : " + (endTime - commitStartTime) + " ms, prepare Time : " + (commitStartTime - prepareStartTime) + " ms");
                    prepareStartTime = System.currentTimeMillis();

                    statement.clearBatch();
                }
            }

            System.out.println("totalCommitTime : " + totalCommitTime + " totalPrepareTime : " + totalPrepareTime);
            System.out.println("avg commit time : " + (totalCommitTime / (loop / commitCnt)));

        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBUtil.release(connection, statement, null);
        }

    }

    public void batchInsertValues() {

        int loop = 3000000;
        int commitCnt = 10000;
        Connection connection = null;
        PreparedStatement statement = null;

        long totalCommitTime = 0;
        long totalPrepareTime = 0;
        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false);

            StringBuilder valueBuilder = new StringBuilder(2048);
            valueBuilder.append(BATCH_INSERT_SQL_VALUES);

            long prepareStartTime = System.currentTimeMillis();
            for (int i = 0; i < loop; i++) {

                valueBuilder.append("('").append(UUID.randomUUID().toString(), 0, 16).append("'");
                valueBuilder.append(",").append(RANDOM.nextInt(6) + 5);

                for (int j = 1; j <= 40; j++) {
                    valueBuilder.append(",'").append(UUID.randomUUID().toString(), 0, 16).append("'");
                }

                if ((i > 0 && (i % commitCnt == 0)) || (i + 1) == loop) {
                    long commitStartTime = System.currentTimeMillis();

                    valueBuilder.append("),");

                    valueBuilder.deleteCharAt(valueBuilder.length() - 1);
                    statement = connection.prepareStatement(valueBuilder.toString());
                    statement.execute();
                    connection.commit();

                    DBUtil.release(null, statement, null);
                    statement = null;

                    long endTime = System.currentTimeMillis();

                    totalCommitTime += (endTime - commitStartTime);
                    totalPrepareTime += (commitStartTime - prepareStartTime);

                    System.out.println("index : " + i + " commit time : " + (endTime - commitStartTime) + " ms, prepare Time : " + (commitStartTime - prepareStartTime) + " ms");
                    prepareStartTime = System.currentTimeMillis();

                    valueBuilder = new StringBuilder(2048);
                    valueBuilder.append(BATCH_INSERT_SQL_VALUES);
                } else {
                    valueBuilder.append("),");
                }
            }

            System.out.println("totalCommitTime : " + totalCommitTime + " totalPrepareTime : " + totalPrepareTime);
            System.out.println("avg commit time : " + (totalCommitTime / (loop / commitCnt)));

        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DBUtil.release(connection, statement, null);
        }

    }

}
