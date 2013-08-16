package cn.yueying.hairstyle;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import cn.yueying.hairstyle.HairStyle;
import cn.yueying.hairstyle.HairStyleContent;

import cn.yueying.hairstyle.HairStyleDao;
import cn.yueying.hairstyle.HairStyleContentDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig hairStyleDaoConfig;
    private final DaoConfig hairStyleContentDaoConfig;

    private final HairStyleDao hairStyleDao;
    private final HairStyleContentDao hairStyleContentDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        hairStyleDaoConfig = daoConfigMap.get(HairStyleDao.class).clone();
        hairStyleDaoConfig.initIdentityScope(type);

        hairStyleContentDaoConfig = daoConfigMap.get(HairStyleContentDao.class).clone();
        hairStyleContentDaoConfig.initIdentityScope(type);

        hairStyleDao = new HairStyleDao(hairStyleDaoConfig, this);
        hairStyleContentDao = new HairStyleContentDao(hairStyleContentDaoConfig, this);

        registerDao(HairStyle.class, hairStyleDao);
        registerDao(HairStyleContent.class, hairStyleContentDao);
    }
    
    public void clear() {
        hairStyleDaoConfig.getIdentityScope().clear();
        hairStyleContentDaoConfig.getIdentityScope().clear();
    }

    public HairStyleDao getHairStyleDao() {
        return hairStyleDao;
    }

    public HairStyleContentDao getHairStyleContentDao() {
        return hairStyleContentDao;
    }

}