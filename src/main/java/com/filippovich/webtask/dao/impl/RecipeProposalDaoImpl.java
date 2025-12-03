package com.filippovich.webtask.dao.impl;

import com.filippovich.webtask.dao.RecipeProposalDao;
import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.RecipeProposal;

import javax.sql.DataSource;
import java.sql.*;

public class RecipeProposalDaoImpl implements RecipeProposalDao {

    private static final String SQL_INSERT =
            "INSERT INTO recipe_proposals (cocktail_id, proposed_by, status, created_at) VALUES (?, ?, ?, ?)";

    private final DataSource dataSource;

    public RecipeProposalDaoImpl(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public void save(RecipeProposal p) throws DaoException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, p.getCocktailId());
            ps.setLong(2, p.getProposedBy().getId());
            ps.setString(3, p.getStatus());
            ps.setTimestamp(4, Timestamp.valueOf(p.getCreatedAt()));

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
