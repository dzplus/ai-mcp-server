package life.duanzhang.aimcpserver.service;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import life.duanzhang.aimcpserver.model.TableRelationship;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

@Slf4j
@Service
public class DBService {

    @Resource
    private DataSource dataSource;

    @Tool(description = "获取数据库的版本信息")
    public String getDbVersion() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            // 获取完整版本信息字符串（如"8.0.23"）
            String fullVersion = metaData.getDatabaseProductVersion();
            // 获取主版本号（如8）
            int majorVersion = metaData.getDatabaseMajorVersion();
            // 获取次版本号（如0）
            int minorVersion = metaData.getDatabaseMinorVersion();
            return "Full Version: " + fullVersion + ", Major Version: " + majorVersion + ", Minor Version: " + minorVersion;
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return "未获取到版本信息";
    }


    @Tool(description = "获取数据库的schema信息")
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

    @Tool(description = "根据schema获取架构内的所有表信息")
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

    @Tool(description = "指定SQL语句查询数据")
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

    @Tool(description = "保存表与表之间的字段关联")
    public void saveTableLink(String sourceTable, String sourceColumn, String targetTable, String targetColumn) {
        TableRelationship tableRelationship = new TableRelationship();
        tableRelationship.setSourceTable(sourceTable);
        tableRelationship.setSourceColumn(sourceColumn);
        tableRelationship.setTargetTable(targetTable);
        tableRelationship.setTargetColumn(targetColumn);
        appendToJsonFile("src/main/resources/db/table_relationship.json", tableRelationship);
    }

    @Tool(description = "获取所有的字段关联")
    public Set<TableRelationship> getTableLink() {
        String filePath = "src/main/resources/db/table_relationship.json";
        File file = new File(filePath);
        Set<TableRelationship> existingData = Sets.newHashSet();
        if (!file.exists()) {
            return existingData;
        }
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            if (!content.isEmpty()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                existingData = gson.fromJson(content, new TypeToken<Set<TableRelationship>>() {
                }.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return existingData;
    }

    public static void appendToJsonFile(String filePath, TableRelationship data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Set<TableRelationship> existingData = Sets.newHashSet();
        File file = new File(filePath);
        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(filePath)));
                if (!content.isEmpty()) {
                    existingData = gson.fromJson(content, new TypeToken<Set<TableRelationship>>() {
                    }.getType());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        existingData.add(data);
        String json = gson.toJson(existingData);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
