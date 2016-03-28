package y2w.manage;

import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import y2w.db.SessionDb;
import y2w.db.SessionMemberDb;
import y2w.entities.SessionEntity;
import y2w.entities.SessionMemberEntity;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.SessionSrv;

/**
 * 会话管理
 * Created by maa2 on 2016/1/16.
 */
public class Sessions implements Serializable {
    private String TAG = Sessions.class.getSimpleName();
    private CurrentUser user;
    private Remote remote;
    private HashMap<String,Session> sessionHashMap;

    public Sessions(CurrentUser user){
        this.user=user;
        this.sessionHashMap = new HashMap<String,Session>();
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public CurrentUser getUser(){
        return user;
    }

    /**
     * 获取session哈希列表
     * @return
     */
    public HashMap<String, Session> getSessionHashMap() {
        return sessionHashMap;
    }

    public Remote getRemote(){
        if(remote == null){
            remote = new Remote();
        }
        return remote;
    }

    /**
     * 某个session信息保存到数据库
     * @param session
     */
    public void addSession(Session session){
        session.getEntity().setMyId(user.getEntity().getId());
        SessionDb.addSessionEntity(session.getEntity());
    }

    /**
     * session列表保存到数据库
     * @param sessionList
     */
    public void add(List<Session> sessionList){
        for(Session session : sessionList){
            addSession(session);
        }
    }

    /**
     * 根据目标Id获取session
     * @param targetId
     * @param type
     * @param result
     */
    public void getSessionByTargetId(final String targetId, final String type, final Back.Result<Session> result){
        if(EnumManage.SessionType.p2p.toString().equals(type)){
            getSession(targetId, type, result);
        }else{
            if(sessionHashMap.containsKey(targetId)){
                result.onSuccess(sessionHashMap.get(targetId));
            }else {
                getSession(targetId, type, result);
            }
        }

    }

    private void getSession(final String targetId, final String type, final Back.Result<Session> result){
        SessionEntity entity = SessionDb.queryByTargetId(user.getEntity().getId(),targetId,type);
        if(entity != null){
            Session session = new Session(this, entity);
            if(!sessionHashMap.containsKey(session.getEntity().getId())) {
                sessionHashMap.put(session.getEntity().getId(), session);
            }
            result.onSuccess(session);
        }else{
            getRemote().getSession(targetId, type, result);
        }
    }

    /**
     * 根据目标sessionId获取session
     * @param sessionId
     * @param result
     */
    public void getSessionBySessionId(final String sessionId,final Back.Result<Session> result){
        if(sessionHashMap.containsKey(sessionId)){
            result.onSuccess(sessionHashMap.get(sessionId));
        }else {
            SessionEntity entity = SessionDb.queryBySessionId(user.getEntity().getId(),sessionId);
            if(entity != null){
                if(EnumManage.SessionType.p2p.toString().equals(entity.getType())){
                    if(sessionHashMap.containsKey(entity.getOtherSideId())){
                        result.onSuccess(sessionHashMap.get(entity.getOtherSideId()));
                    }else{
                        Session session = new Session(this, entity);
                        if(!sessionHashMap.containsKey(sessionId)){
                            sessionHashMap.put(entity.getOtherSideId(),session);
                        }
                        result.onSuccess(session);
                    }
                }else{
                    Session session = new Session(this, entity);
                    if(!sessionHashMap.containsKey(sessionId)) {
                        sessionHashMap.put(sessionId, session);
                    }
                    result.onSuccess(session);
                }

            }else{
                result.onError(ErrorCode.UNKNOWN,"");
            }
        }
    }


    /*****************************remote*****************************/
    public class Remote implements Serializable{
        public Remote(){

        }

        /**
         * 根据session类型获取某个session
         * @param sessionId
         * @param type
         * @param result
         */
        public void getSession(final String sessionId, final String type, final Back.Result<Session> result){
            if(EnumManage.SessionType.p2p.toString().equals(type)){
                SessionSrv.getInstance().getP2PSession(user.getToken(), user.getEntity().getId(), sessionId, new Back.Result<SessionEntity>() {
                    @Override
                    public void onSuccess(SessionEntity entity) {
                     if(SessionMemberDb.queryCount(user.getEntity().getId(),entity.getId()) == 0){
                         new Session(Sessions.this, entity).getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
                             @Override
                             public void onSuccess(List<SessionMember> sessionMemberList) {
                                 LogUtil.getInstance().log(TAG, "sessionMemberList:" + sessionMemberList.size(), null);
                             }

                             @Override
                             public void onError(ErrorCode errorCode,String error) {

                             }
                         });
                     }
                    entity.setOtherSideId(sessionId);
                    addSession(new Session(Sessions.this, entity));
                    SessionEntity temp = SessionDb.queryByTargetId(user.getEntity().getId(), sessionId, type);
                    Session session = new Session(Sessions.this, temp);
                    if(!sessionHashMap.containsKey(sessionId)) {
                        sessionHashMap.put(sessionId, session);
                    }
                    result.onSuccess(session);
                    }

                    @Override
                    public void onError(ErrorCode errorCode,String error) {
                        result.onError(errorCode,error);
                    }
                });
            }else if(EnumManage.SessionType.group.toString().equals(type)){
                SessionSrv.getInstance().getSession(user.getToken(), sessionId, new Back.Result<SessionEntity>() {
                    @Override
                    public void onSuccess(SessionEntity entity) {
                        if(SessionMemberDb.queryCount(user.getEntity().getId(),entity.getId()) == 0){
                            new Session(Sessions.this, entity).getMembers().getRemote().sync(new Back.Result<List<SessionMember>>() {
                                @Override
                                public void onSuccess(List<SessionMember> sessionMemberList) {
                                    LogUtil.getInstance().log(TAG, "sessionMemberList:" + sessionMemberList.size(), null);
                                }

                                @Override
                                public void onError(ErrorCode errorCode,String error) {

                                }
                            });
                        }
                        addSession(new Session(Sessions.this, entity));
                        SessionEntity temp = SessionDb.queryByTargetId(user.getEntity().getId(), sessionId, type);
                        Session session = new Session(Sessions.this, temp);
                        if(!sessionHashMap.containsKey(sessionId)) {
                            sessionHashMap.put(sessionId, session);
                        }
                        result.onSuccess(new Session(Sessions.this, temp));
                    }

                    @Override
                    public void onError(ErrorCode errorCode,String error) {
                        result.onError(errorCode,error);
                    }
                });
            }
        }

        /**
         * 创建session
         * @param name
         * @param secureType
         * @param type
         * @param avatarUrl
         * @param result
         */
        public void sessionCreate(String name, String secureType, final String type, String avatarUrl, final Back.Result<Session> result){
            SessionSrv.getInstance().sessionCreate(user.getToken(), name, secureType, type, avatarUrl, new Back.Result<SessionEntity>() {
                @Override
                public void onSuccess(SessionEntity entity) {
                    addSession(new Session(Sessions.this, entity));
                    SessionEntity temp = SessionDb.queryByTargetId(user.getEntity().getId(), entity.getId(), entity.getType());
                    result.onSuccess(new Session(Sessions.this, temp));
                }

                @Override
                public void onError(ErrorCode errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

    }


}
