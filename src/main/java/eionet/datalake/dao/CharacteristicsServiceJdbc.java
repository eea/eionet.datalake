package eionet.datalake.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import eionet.datalake.model.TwoLevel;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service to store metadata for editions using JDBC.
 */
@Service
public class CharacteristicsServiceJdbc implements CharacteristicsService {

    @Autowired
    private DataSource dataSource;

    @Override
    public void insertTables(String editionId, List<String> tables) {
        String query = "INSERT INTO characteristics (editionid, scope, level1) VALUES (?, 'T', ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        for (String table : tables) {
            jdbcTemplate.update(query, editionId, table);
        }
    }

    @Override
    public void insertColumns(String editionId, List<TwoLevel> columns) {
        String query = "INSERT INTO characteristics (editionid, scope, level1, level2) VALUES (?, 'C', ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        for (TwoLevel column : columns) {
            jdbcTemplate.update(query, editionId, column.getLevel1(), column.getLevel2());
        }
    }

    @Override
    public List<String> getTablesForEdition(String editionId) {
        String query = "SELECT level1 FROM characteristics WHERE editionId = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<String>(String.class));
    }

    @Override
    public List<TwoLevel> getColumnsForEdition(String editionId) {
        String query = "SELECT level1, level2 FROM characteristics WHERE editionId = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<TwoLevel>(TwoLevel.class));
    }

    @Override
    public void deleteByEditionId(String editionId) {
        String query = "DELETE FROM characteristics WHERE editionid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, editionId);
    }


}
