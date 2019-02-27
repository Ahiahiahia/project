package everthing.core.dao;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 数据源工厂 - 单例工厂
 */
// h2提供jdbcDataSource实现DataSource接口(不用)
// 使用阿里提供的 DruidDataSource 数据源
public class DataSourceFactory {
    private static volatile DruidDataSource dataSource;

    public DataSourceFactory() {
    }

    /**
     * 获取数据源 - 懒汉式单例双重检查
     * @return
     */
    public static DataSource getDataSource(){
        if(dataSource == null){
            synchronized (DataSourceFactory.class){
                if(dataSource == null){
                    // 实例化DataSource
                    dataSource = new DruidDataSource();
                    dataSource.setDriverClassName("org.h2.Driver");
                    // url, username, password
                    // 采用的是h2的嵌入式数据库，以本地文件方式存储，只需要提高url即可，不需要用户名和密码
                    // 获取当前工程路径(user.home获取用户目录)
                    String work = System.getProperty("user.dir");
                    dataSource.setUrl("jdbc:h2:"+work+ File.separator+"my_everthing");
                }
            }
        }
        return dataSource;
    }

    public static void initDatabase(){
        // 1.获取数据源
        DataSource dataSource = DataSourceFactory.getDataSource();
        // 2.获取SQL语句
        // 采取读取classpath路径下的文件，不采取绝对路径
        // try-with-resource方式自动关闭流
        try(InputStream in = DataSourceFactory.class.getClassLoader()
                .getResourceAsStream("my_everthing.sql");){
            if(in == null){
                throw new RuntimeException("Not read init databasescript please check it");
            }
            StringBuilder sqlBuilder = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in));){
                String line = null;
                while((line = reader.readLine()) != null){
                    if(!line.startsWith("--")){
                        sqlBuilder.append(line);
                    }
                }
            }
            // 3.获取数据库连接，执行SQL语句
            String sql = sqlBuilder.toString();
            // JDBC编程
            // 3.1获取数据库连接
            Connection connection = dataSource.getConnection();
            // 3.2创建命令
            PreparedStatement statement = connection.prepareStatement(sql);
            // 3.3执行SQL语句
            statement.execute();
            // 3.4关闭连接和命令
            statement.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        DataSourceFactory.initDatabase();
    }
}
