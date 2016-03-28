package y2w.manage;

import java.io.Serializable;

import y2w.model.MToken;
import y2w.model.User;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.UserSrv;

/**
 * 当前登录用户相关信息
 * Created by maa2 on 2016/1/16.
 */
public class CurrentUser extends User implements Serializable {

    private String TAG = CurrentUser.class.getSimpleName();
    private String appKey;
    private String secret;
    private String token;
    private MToken imToken;
    private Contacts contacts;
    private Sessions sessions;
    private UserConversations userConversations;
    private UserSessions userSessions;
    private IMBridges imBridges;
    private Remote remote;
    private CurrentUser user;

    public CurrentUser(){
        user = this;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    public String getAppKey(){
        return appKey;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }
    public String getSecret() {
        return secret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setImToken(MToken imToken) {
        this.imToken = imToken;
    }

    public void getImToken(Back.Result<MToken> result) {
        if(imToken != null){
            result.onSuccess(imToken);
        }else{
            getRemote().syncIMToken(result);
        }
    }

    public MToken getImToken() {
        return imToken;
    }

    /**
     * 获取通讯录管理类
     * @return
     */
    public Contacts getContacts(){
        if(contacts == null){
            contacts = new Contacts(this);
        }
        return contacts;
    }

    /**
     * 获取会话管理类
     * @return
     */
    public Sessions getSessions(){
        if(sessions == null){
            sessions = new Sessions(this);
        }
        return sessions;
    }

    /**
     * 获取用户会话管理类
     * @return
     */
    public UserConversations getUserConversations(){
        if(userConversations == null){
            userConversations = new UserConversations(this);
        }
        return userConversations;
    }
    /**
     * 获取群聊管理类
     * @return
     */
    public UserSessions getUserSessions() {
        if(userSessions == null){
            userSessions = new UserSessions(this);
        }
        return userSessions;
    }

    /**
     * 获取服务器连接管理类
     * @return
     */
    public IMBridges getImBridges() {
        if(imBridges == null){
            imBridges = new IMBridges(this);
        }
        return imBridges;
    }

    public Remote getRemote(){
        if(remote == null){
            remote = new Remote();
        }
        return remote;
    }

    /*****************************remote*****************************/

    public class  Remote implements Serializable{

        public Remote(){

        }
        /**
         * 获取token
         * @param result
         */
        public void syncIMToken(final Back.Result<MToken> result){
            UserSrv.getInstance().getIMToken(appKey, secret, new Back.Result<MToken>() {
                @Override
                public void onSuccess(MToken imToken) {
                    setImToken(imToken);
                    result.onSuccess(imToken);
                }
                @Override
                public void onError(ErrorCode errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });

        }
        /**
         * 同步通讯录与用户会话列表
         * @param callback
         */
        public void sync(final Back.Callback callback){
            UserSrv.getInstance().sync(callback);
        }

    }



}
