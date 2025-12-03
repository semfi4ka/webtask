package com.filippovich.webtask.dao;

import com.filippovich.webtask.exception.DaoException;
import com.filippovich.webtask.model.RecipeProposal;

public interface RecipeProposalDao {
    void save(RecipeProposal proposal) throws DaoException;
}
