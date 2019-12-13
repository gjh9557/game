package com.ali.dao;

import com.ali.entity.GameAnalysisResultBean;

/**
 * 数据访问接口
 */
public interface iGameAnalysisResult {
    /**
     * 保存数据到表中
     * @param bean
     *
     */
    void saveday(GameAnalysisResultBean bean);
}
