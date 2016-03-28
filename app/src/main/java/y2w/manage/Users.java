package y2w.manage;

import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.List;

import y2w.base.AppContext;
import y2w.db.UserDb;
import y2w.entities.ContactEntity;
import y2w.entities.UserEntity;
import y2w.model.User;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.UserSrv;
import com.y2w.uikit.utils.StringUtil;
import y2w.common.UserInfo;

/**
 * 用户管理类
 * Created by maa2 on 2016/1/16.
 */
public class Users {
    private String TAG = Users.class.getSimpleName();
    private static Users users = null;
    private CurrentUser currentUser = null;
    private Remote remote;
    public static Users getInstance(){
        if(users == null){
            users = new Users();
        }
        return  users;
    }

    public void setCurrentUser(CurrentUser currentUser){
        this.currentUser = currentUser;
    }

    /**
     * 获取当前登录用户
     * @return
     */
    public CurrentUser getCurrentUser(){
        if(currentUser == null){
            currentUser = new CurrentUser();
        }
        return currentUser;
    }

    public Remote getRemote(){
        if(remote == null){
            remote = new Remote();
        }
        return remote;
    }

    /**
     * 将用户基本信息写入数据库
     * @param user
     */
    public void addUser(User user){
        user.getEntity().setMyId(getCurrentUser().getEntity().getId());
        UserDb.addUserEntity(user.getEntity());
    }

    /**
     * 获取用户基本信息
     * @param userId
     * @param result
     */
    public void getUser(String userId,Back.Result<User> result){
        UserEntity entity = UserDb.queryById(getCurrentUser().getEntity().getId(),userId);
        if(entity != null){
            result.onSuccess(new User(entity));
        }else{
            getRemote().userGet(userId,result);
        }
    }

    /**
     * 创建用户基本信息
     * @param id
     * @param name
     * @param avatarUrl
     * @return
     */
    public User createUser(String id, String name, String avatarUrl){
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setAvatarUrl(avatarUrl);
        return new User(entity);
    }

    /**
     * 创建一个登录用户
     * @param loginInfo
     * @return
     */
    public CurrentUser createCurrentUser(UserInfo.LoginInfo loginInfo){
        CurrentUser currentUser = new CurrentUser();
        if(loginInfo != null){
            currentUser.setAppKey(loginInfo.getKey());
            currentUser.setSecret(loginInfo.getSecret());
            currentUser.setToken(loginInfo.getToken());
            currentUser.getEntity().setId(loginInfo.getEntity().getId());
            currentUser.getEntity().setName(loginInfo.getEntity().getName());
            currentUser.getEntity().setAccount(loginInfo.getEntity().getEmail());
            currentUser.getEntity().setAvatarUrl(loginInfo.getEntity().getAvatarUrl());
        }
        return currentUser;
    }


    /***************************************************remote********************************************************/
    public class Remote implements Serializable {

        public Remote(){

        }

        /**
         * 注册
         * @param account
         * @param password
         * @param name
         * @param result
         */
        public void register(String account,String password,String name, final Back.Result<User> result){
            UserSrv.getInstance().register(AppContext.getAppContext().getAppKey(), account, name, StringUtil.get32MD5(password), new Back.Result<UserEntity>() {
                @Override
                public void onSuccess(UserEntity entity) {
                    result.onSuccess(new User(entity));
                }

                @Override
                public void onError(ErrorCode errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

        /**
         * 登录
         * @param account
         * @param password
         * @param result
         */
        public void login(final String account, final String password, final Back.Result<CurrentUser> result){
            UserSrv.getInstance().login(AppContext.getAppContext().getAppKey(), account, StringUtil.get32MD5(password), new Back.Result<UserInfo.LoginInfo>() {
                @Override
                public void onSuccess(UserInfo.LoginInfo loginInfo) {
                    LogUtil.getInstance().log(TAG, "token=" + loginInfo.getToken(), null);
                    LogUtil.getInstance().log(TAG, "uerId=" + loginInfo.getEntity().getId(), null);
                    currentUser = createCurrentUser(loginInfo);
                    UserInfo.setCurrentInfo(currentUser, password);
                    result.onSuccess(currentUser);
                }

                @Override
                public void onError(ErrorCode errorCode, String error) {

                }
            });
        }

        /**
         * 搜索
         * @param key
         * @param result
         */
        public void search(String key,Back.Result<List<ContactEntity>> result){
            UserSrv.getInstance().search(currentUser.getToken(), key, result);
        }

        /**
         * 保存更新登录用户信息
         * @param entity
         * @param Result
         */
        public void store(ContactEntity entity,Back.Result<ContactEntity> Result){
            UserSrv.getInstance().userUpdate(currentUser.getToken(), entity.getUserId(), entity.getEmail(), entity.getName(), entity.getRole(), entity.getJobTitle(), entity.getPhone(), entity.getAddress(), entity.getStatus(), entity.getAvatarUrl(), Result);
        }

        /**
         * 获取某个用户信息
         * @param userId
         */
        public void userGet(String userId, final Back.Result<User> result){
            UserSrv.getInstance().userGet(currentUser.getToken(), userId, new Back.Result<UserEntity>() {
                @Override
                public void onSuccess(UserEntity entity) {
                    LogUtil.getInstance().log(TAG, "user  account=" + entity.getAccount(), null);
                    addUser(new User(entity));
                    UserEntity userEntity = UserDb.queryById(currentUser.getEntity().getId(),entity.getId());
                    result.onSuccess(new User(userEntity));

                }

                @Override
                public void onError(ErrorCode Code, String error) {
                    result.onError(Code, error);

                }
            });
        }

        public void delete(User user,Back.Callback callback){
            //TODO
        }

    }

}
