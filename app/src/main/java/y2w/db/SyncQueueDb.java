package y2w.db;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.y2w.uikit.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppContext;
import y2w.entities.SyncQueueEntity;
import y2w.entities.UserEntity;

/**
 * Created by maa2 on 2016/3/22.
 */
public class SyncQueueDb {

    public static void delete(String myId, int type){
        if(!StringUtil.isEmpty(myId)){
            try{
                DeleteBuilder<SyncQueueEntity, Integer> deleteBuilder = DaoManager.getInstance(AppContext.getAppContext()).dao_syncQueue.deleteBuilder();
                deleteBuilder.where().eq("type", type).and().eq("myId", myId);
                DaoManager.getInstance(AppContext.getAppContext()).dao_syncQueue.delete(deleteBuilder.prepare());
            }catch(Exception e){

            }
        }
    }
    public static void addSyncQueue(SyncQueueEntity entity){
        if(entity != null){
            try{
                SyncQueueEntity temp = queryByType(entity.getMyId(),entity.getType());
                if(temp ==null || "waiting".equals(temp.getStatus())){
                    delete(entity.getMyId(), entity.getType());
                    DaoManager.getInstance(AppContext.getAppContext()).dao_syncQueue.create(entity);
                }
            }catch(Exception e){

            }
        }
    }

    public static SyncQueueEntity queryByType(String myId,int type){
        SyncQueueEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_syncQueue.queryBuilder()
                    .where().eq("type",type).and().eq("myId", myId).queryForFirst();
        } catch (Exception e) {
        }
        return entity;
    }

    public static List<SyncQueueEntity> query(String myId){
        List<SyncQueueEntity> entities = null;
        try {
            entities = DaoManager.getInstance(AppContext.getAppContext()).dao_syncQueue.queryBuilder()
                    .where().eq("myId", myId).query();
        } catch (Exception e) {
            entities = new ArrayList<SyncQueueEntity>();
        }
        return entities;
    }
    public static boolean isSyncIng(String myId){
        long count = 0;
        try {
            count = DaoManager.getInstance(AppContext.getAppContext()).dao_syncQueue.queryBuilder()
                    .where().eq("status", "syncing").and().eq("myId", myId).countOf();
        } catch (Exception e) {
        }
        return count > 0 ? true : false;
    }


    public static SyncQueueEntity queryNextSync(String myId){
        SyncQueueEntity entity = null;
        try {
            entity = DaoManager.getInstance(AppContext.getAppContext()).dao_syncQueue.queryBuilder().orderBy("time",true)
                    .where().eq("status", "waiting").and().eq("myId", myId).queryForFirst();
        } catch (Exception e) {
        }
        return entity;
    }




}
