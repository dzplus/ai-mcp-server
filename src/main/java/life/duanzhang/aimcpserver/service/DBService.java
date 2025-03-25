package life.duanzhang.aimcpserver.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DBService {

    @Resource
    private DataSource dataSource;

    @Tool(description = "获取数据库的架构信息")
    public List<String> getAllSchemas() {
        List<String> schemas = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getCatalogs();
            while (resultSet.next()) {
                schemas.add(resultSet.getString("TABLE_CAT"));
            }
            log.info("获取到的库名: {}", schemas);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return schemas;
    }

    @Tool(description = "获取架构内的所有表名")
    public List<Map<String, Object>> getAllTables(String schema) {
        List<Map<String, Object>> tables = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(schema, null, "%", new String[]{"TABLE"});
            while (resultSet.next()) {
                Map<String, Object> tableInfo = new HashMap<>();
                tableInfo.put("tableName", resultSet.getString("TABLE_NAME"));
                tableInfo.put("tableType", resultSet.getString("TABLE_TYPE"));
                tableInfo.put("remarks", resultSet.getString("REMARKS"));
                tables.add(tableInfo);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return tables;
    }

    @Tool(description = "获取表的元数据信息")
    public List<Map<String, Object>> getTableMetaInfo(String schema, String tableName) {
        List<Map<String, Object>> metadata = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(schema, null, tableName, "%");
            while (resultSet.next()) {
                Map<String, Object> columnInfo = new HashMap<>();
                columnInfo.put("columnName", resultSet.getString("COLUMN_NAME"));
                columnInfo.put("dataType", resultSet.getString("TYPE_NAME"));
                columnInfo.put("columnSize", resultSet.getInt("COLUMN_SIZE"));
                columnInfo.put("isNullable", resultSet.getString("IS_NULLABLE"));
                columnInfo.put("remarks", resultSet.getString("REMARKS"));
                metadata.add(columnInfo);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return metadata;
    }

    @Tool(description = "查询表内的数据")
    public List<Map<String, Object>> executeSqlQuery(String sql) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return resultList;
    }
}
