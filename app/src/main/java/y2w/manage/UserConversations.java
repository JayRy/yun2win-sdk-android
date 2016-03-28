package y2w.manage;

import com.y2w.uikit.utils.StringUtil;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import y2w.common.Constants;
import y2w.db.TimeStampDb;
import y2w.db.UserConversationDb;
import y2w.entities.TimeStampEntity;
import y2w.entities.UserConversationEntity;
import y2w.model.UserConversation;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.UserConversationSrv;

/**
 * 用户会话管理类
 * Created by maa2 on 2016/1/16.
 */
public class UserConversations implements Serializable {
    private String TAG = UserConversations.class.getSimpleName();
    private CurrentUser user;
    private String updatedAt;
    private Remote remote;

    /**
     * 获取某个用户会话
     * @param targetId
     * @return
     */
    public UserConversation get(String targetId){
        UserConversationEntity entity = UserConversationDb.queryByTargetId(user.getEntity().getId(),targetId);
        return new UserConversation(this,entity);
    }
    /**
     * 删除某个用户会话
     * @param targetId
     * @return
     */
    public void delete(String targetId){
        UserConversationEntity entity = UserConversationDb.queryByTargetId(user.getEntity().getId(), targetId);
        if(entity!=null)
         UserConversationDb.delete(entity);
    }

    /**
     * 获取本地用户会话列表
     * @return
     */
    public List<UserConversationEntity> getUserConversations(){
        return UserConversationDb.query(user.getEntity().getId());
    }

    /**
     * 将用户会话列表保存到本地数据库
     * @param userConversationList
     */
    public void add(List<UserConversation> userConversationList){
        for(UserConversation userConversation:userConversationList){
            addUserConversation(userConversation);
        }
    }

    /**
     * 将单个用户会话信息保存到数据库
     * @param userConversation
     */
    public void addUserConversation(UserConversation userConversation){
        userConversation.getEntity().setMyId(user.getEntity().getId());
        refreshTimeStamp(userConversation);
        UserConversationDb.addUserConversation(userConversation.getEntity());
    }

    private void refreshTimeStamp(UserConversation userConversation){

        TimeStampEntity entity= TimeStampDb.queryByType(user.getEntity().getId(), TimeStampEntity.TimeStampType.userConversation.toString());
        if(entity != null){
            if(StringUtil.timeCompare(entity.getTime(),userConversation.getEntity().getUpdatedAt()) > 0){
                entity.setTime(userConversation.getEntity().getUpdatedAt());
                TimeStampDb.addTimeStampEntity(entity);
                LogUtil.getInstance().log(TAG, "bigger :" + userConversation.getEntity().getUpdatedAt()+" : "+userConversation.getEntity().getLastContext(), null);
            }
        }else{
            entity = TimeStampEntity.parse(userConversation.getEntity().getUpdatedAt(),TimeStampEntity.TimeStampType.userConversation.toString(),"");
            entity.setMyId(user.getEntity().getId());
            TimeStampDb.addTimeStampEntity(entity);
        }

    }
    /**
     * 有参构造
     * @param user
     */
    public UserConversations(CurrentUser user){
        this.user=user;
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public CurrentUser getUser() {
        return user;
    }

    /**
     * 获取同步更新时间戳
     * @return
     */
    public String getUpdatedAt() {
        TimeStampEntity entity= TimeStampDb.queryByType(user.getEntity().getId(), TimeStampEntity.TimeStampType.userConversation.toString());
        if(entity != null){
            updatedAt = entity.getTime();
        }else{
            updatedAt = Constants.TIME_ORIGIN;
        }
        LogUtil.getInstance().log(TAG, "TimeStamp :"+updatedAt, null);
        return updatedAt;
    }

    public Remote getRemote(){
        if(remote == null){
            remote = new Remote(this);
        }
        return remote;
    }

    /*****************************remote*****************************/

    public class Remote implements Serializable{

        private UserConversations userConversations;
        public Remote(UserConversations userConversations){
            this.userConversations = userConversations;
        }

        /**
         * 同步更新用户会话列表
         * @param result
         */
        public void sync(final Back.Result<List<UserConversation>> result){
            UserConversationSrv.getInstance().getUserConversations(user.getToken(), getUpdatedAt(), user.getEntity().getId(), new Back.Result<List<UserConversationEntity>>() {
                @Override
                public void onSuccess(List<UserConversationEntity> entities) {
                    List<UserConversation> userConversationList = new ArrayList<UserConversation>();
                    for(UserConversationEntity entity:entities){
                        userConversationList.add(new UserConversation(userConversations,entity));
                    }
                    add(userConversationList);
                    result.onSuccess(userConversationList);
                }

                @Override
                public void onError(ErrorCode errorCode,String error) {

                }
            });
        }

        /**
         * 获取某个用户会话信息
         * @param userId
         * @param userConversationId
         * @param result
         */
        public void getUserConversation(String userId,String userConversationId,Back.Result<UserConversation> result){
            UserConversationSrv.getInstance().getUserConversation(user.getToken(), userId,userConversationId, new Back.Result<UserConversationEntity>() {
                @Override
                public void onSuccess(UserConversationEntity entity) {

                }

                @Override
                public void onError(ErrorCode errorCode,String error) {

                }
            });
        }
        /**
         * 删除某个用户会话
         * @param userConversationId
         * @param result
         */

        public void delete(String userConversationId,Back.Callback result){
            UserConversationSrv.getInstance().deleteUserConversation(user.getToken(), user.getEntity().getId(), userConversationId, result);
        }
    }





}
