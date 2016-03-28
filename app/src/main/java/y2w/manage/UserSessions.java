package y2w.manage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import y2w.db.UserSessionDb;
import y2w.entities.UserSessionEntity;
import y2w.model.UserSession;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.UserSessionSrv;

/**
 * 群聊管理类
 * Created by maa2 on 2016/2/18.
 */
public class UserSessions implements Serializable{

    private CurrentUser user;
    private Remote remote;

    public UserSessions(CurrentUser user){
        this.user=user;
    }

    /**
     * 获取当前登录用户信息
     * @return
     */
    public CurrentUser getUser(){
        return user;
    }

    public Remote getRemote(){
        if(remote == null){
            remote = new Remote(this);
        }
        return remote;
    }

    public List<UserSession> getUserSessions(){

        List<UserSessionEntity> entities = UserSessionDb.query(user.getEntity().getId());



        return  new ArrayList<UserSession>();
    }

    /**
     * 某个群聊信息保存到数据库
     * @param userSession
     */
    public void addUserSession(UserSession userSession){
        userSession.getEntity().setMyId(user.getEntity().getId());
        UserSessionDb.addUserSessionEntity(userSession.getEntity());
    }

    /**
     * 群聊列表保存到数据库
     * @param sessionList
     */
    public void add(List<UserSession> sessionList){
        for(UserSession userSession : sessionList){
            addUserSession(userSession);
        }
    }

    /**
     * 获取某个群聊信息
     * @param targetId
     * @param result
     */
    public void getUserSession(final String targetId,final Back.Result<UserSession> result){
        UserSessionEntity entity = UserSessionDb.queryBySessionId(user.getEntity().getId(),targetId);
        if(entity != null){
            result.onSuccess(new UserSession(this, entity));
        }else{

        }
    }

    /*****************************remote*****************************/
    public class Remote implements Serializable{
        private UserSessions userSessions;
        public Remote(UserSessions userSessions){
            this.userSessions = userSessions;
        }

        /**
         * 将某个session添加到自己的群聊列表
         * @param sessionId
         * @param name
         * @param avatarUrl
         * @param result
         */
        public void sessionStore(String sessionId, String name, String avatarUrl, final Back.Result<UserSession> result){
             UserSessionSrv.getInstance().sessionStore(user.getToken(), user.getEntity().getId(), sessionId, name, avatarUrl, new Back.Result<UserSessionEntity>() {
                @Override
                public void onSuccess(UserSessionEntity entity) {
                    addUserSession(new UserSession(userSessions, entity));
                    UserSessionEntity temp = UserSessionDb.queryBySessionId(user.getEntity().getId(), entity.getSessionId());
                    result.onSuccess(new UserSession(userSessions, temp));
                }

                @Override
                public void onError(ErrorCode errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

        /**
         * 同步自己的群聊列表
         * @param result
         */
        public void sync(final Back.Result<List<UserSession>> result){
            UserSessionSrv.getInstance().sync(user.getToken(), user.getEntity().getId(), new Back.Result<List<UserSessionEntity>>() {
                @Override
                public void onSuccess(List<UserSessionEntity> entities) {
                    List<UserSession> sessionList = new ArrayList<UserSession>();
                    for(UserSessionEntity entity:entities){
                        sessionList.add(new UserSession(userSessions, entity));
                    }
                    add(sessionList);
                    result.onSuccess(sessionList);
                }

                @Override
                public void onError(ErrorCode errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

        /**
         * 删除某个群聊
         * @param id
         * @param callback
         */
        public void userSessionDelete(String id, final Back.Callback callback){
            UserSessionSrv.getInstance().sessionDelete(user.getToken(), user.getEntity().getId(), id, new Back.Callback() {
                @Override
                public void onSuccess() {
                    callback.onSuccess();
                }

                @Override
                public void onError(ErrorCode errorCode) {
                    callback.onError(errorCode);
                }
            });
        }

    }


}
