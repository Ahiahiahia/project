package everthing.core.dao.impl;

import everthing.core.dao.DataSourceFactory;
import everthing.core.dao.FileDatabaseDao;
import everthing.core.model.Condition;
import everthing.core.model.FileType;
import everthing.core.model.Thing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hunter on 2019/2/27
 */
public class FileDatabaseDaoImpl implements FileDatabaseDao {
    // final防止修改（3种初始化方法：立即赋值；构造方法；构造块）
    private final DataSource dataSource;
    public FileDatabaseDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Thing thing) {
        Connection connection = null;       // 连接
        PreparedStatement statement = null; // 命令
        try{
            // 1.获取数据库连接
            connection = dataSource.getConnection();
            // 2.准备SQL语句
            String sql = "insert into thing(name, path, depth, file_type) values(?,?,?,?)";
            // 3.准备命令
            statement = connection.prepareStatement(sql);
            // 4.设置参数（4个）：1 2 3 4
            statement.setString(1, thing.getName());
            statement.setString(2, thing.getPath());
            statement.setInt(3, thing.getDepth());
            statement.setString(4, thing.getFileType().name());
            // 5.执行命令
            statement.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            releaseResource(null, statement, connection);
        }
    }

    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> listThing = new ArrayList<>();
        Connection connection = null;       // 连接
        PreparedStatement statement = null; // 命令
        ResultSet resultSet = null;         // 结果集
        try{
            // 1.获取数据库连接
            connection = dataSource.getConnection();
            // 2.准备SQL语句
            // 方法在栈上，线程独享，不存在线程安全问题
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" select name, path, depth, file_type from thing ");
            // name匹配原则：前模糊 后模糊 前后模糊
            sqlBuilder.append( " where ")
                    .append(" name like '%")
                    .append(condition.getName())
                    .append("%' ");
            // file_type匹配
//            System.out.println(condition.getFileType());

            if(condition.getFileType() != null){
                sqlBuilder.append(" and file_type = '")
                        .append(condition.getFileType().toUpperCase())
                        .append("' ");
            }
            // limit, orderby必选
            sqlBuilder.append(" order by depth ")
                    .append(condition.getOrderByAsc() ? "asc" : "desc")
                    .append(" limit ")
                    .append(condition.getLimit())
                    .append(" offset 0 ");
            System.out.println(sqlBuilder.toString());

            // 3.准备命令
            statement = connection.prepareStatement(sqlBuilder.toString());
            // 4.执行命令
            resultSet = statement.executeQuery();
            // 5.处理结果集
            while (resultSet.next()){
                // 数据库中的行记录变成Thing对象
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                String fileType = resultSet.getString("file_type");
                thing.setFileType(FileType.findFileTypeByName(fileType));
                listThing.add(thing);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            releaseResource(resultSet, statement, connection);
        }
        return listThing;
    }

    // 重构 - 解决大量代码重复问题
    private void releaseResource(ResultSet resultSet,
                 PreparedStatement statement, Connection connection){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FileDatabaseDao fileDatabaseDao = new
                FileDatabaseDaoImpl(DataSourceFactory.getDataSource());
        Thing thing = new Thing();
        thing.setName("简历");
        thing.setPath("E:\\class\\简历\\简历.docx");
        thing.setDepth(3);
        thing.setFileType(FileType.DOC);
//        fileDatabaseDao.insert(thing);

        Condition connection = new Condition();
        connection.setName("简历");
        connection.setLimit(1);
        connection.setOrderByAsc(true);
        connection.setFileType("DOC");

        List<Thing> list = fileDatabaseDao.search(connection);
        for(Thing t : list){
            System.out.println(t);
        }
    }
}
