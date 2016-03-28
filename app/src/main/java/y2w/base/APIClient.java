package y2w.base;

import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ThreadPool;
import com.yun2win.imlib.Urls;
import com.yun2win.utils.HttpUtil;
import com.yun2win.utils.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import y2w.entities.ContactEntity;
import y2w.entities.MessageEntity;
import y2w.entities.SessionEntity;
import y2w.entities.SessionMemberEntity;
import y2w.entities.UserConversationEntity;
import y2w.entities.UserEntity;
import y2w.entities.UserSessionEntity;
import y2w.model.MToken;
import y2w.model.User;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.common.UserInfo;

/**
 * Created by maa2 on 2015/12/28.
 */
public class APIClient {

    /***********************************************************注册,登录**********************************************************/

    public void register(final String appkey ,final String email,final String name, final String password, final Back.Result<UserEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtil.isEmpty(appkey) || StringUtil.isEmpty(email) || StringUtil.isEmpty(name) || StringUtil.isEmpty(password)) {
                        resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("appKey", appkey);
                        params.put("email", email);
                        params.put("name", name);
                        params.put("password", password);
                        params.put("avatarUrl", Urls.User_Avatar_Def);
                        String result = HttpUtil.post(Urls.User_Register, params);
                        resultCallback.onSuccess(UserEntity.parse(new Json(result)));
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                }
            }
        });
    }

    public void login(final String appKey ,final String email,final String password, final Back.Result<UserInfo.LoginInfo> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtil.isEmpty(appKey) || StringUtil.isEmpty(email) || StringUtil.isEmpty(password)) {
                        resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("appKey", appKey);
                        params.put("email", email);
                        params.put("password", password);
                        String result = HttpUtil.post(Urls.User_Login, params);
                        resultCallback.onSuccess(UserInfo.LoginInfo.parseJson(new Json(result)));
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                }
            }
        });
    }

    /***********************************************************Token**********************************************************/

    public void getToken(final String grantType, final String appKey, final String appSecret, final Back.Result<MToken> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtil.isEmpty(grantType) || StringUtil.isEmpty(appKey) || StringUtil.isEmpty(appSecret)) {
                        resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("grant_type", grantType);
                        params.put("client_id", appKey);
                        params.put("client_secret", appSecret);
                        String result = HttpUtil.post(Urls.Token_Get, params);
                        resultCallback.onSuccess(MToken.parse(new Json(result)));
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                }
            }
        });
    }

    /***********************************************************通讯录**********************************************************/
    /**
     * 获取某个用户信息
     * @param userId 用户id
     * @param resultCallback 返回结果
     */
    public void contactGet(final String token, final String userId ,String id, final Back.Result<ContactEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)) {
                    resultCallback.onError(ErrorCode.UNKNOWN,"");
                } else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.get(token, Urls.User_Contact_Get + userId + Urls.User_Contacts_Last, params);
                        resultCallback.onSuccess(ContactEntity.parse(new Json(result)));
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }
            }
        });

    }

    /**
     * 获取本用户的通讯录列表.
     * @param userId
     * @param resultCallback
     */
    public void getContacts(final String token, final String updateAt, final int limit, final String userId,final Back.Result<List<ContactEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtil.isEmpty(token)|| StringUtil.isEmpty(updateAt) || StringUtil.isEmpty(userId)) {
                        resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("limit", ""+limit);
                        String result = HttpUtil.get(token,updateAt,Urls.User_Contacts_Get+userId+Urls.User_Contacts_Last, params);
                        List<ContactEntity> contacts = new ArrayList<ContactEntity>();
                        Json json = new Json(result);
                        int total = json.getInt("total_count");
                        List<Json> jSons = json.get("entries").toList();
                        for(Json j:jSons){
                            contacts.add(ContactEntity.parseSync(j,total));
                        }
                        resultCallback.onSuccess(contacts);
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                }
            }
        });

    }

    /**
     * 搜索用户
     * @param keyword
     * @param resultCallback
     */
    public void contactSearch(final String token,final String keyword , final Back.Result<List<ContactEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isEmpty(token) || StringUtil.isEmpty(keyword)) {
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                } else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.get(token,Urls.User_Contact_Search+keyword, params);
                        List<ContactEntity> entities = new ArrayList<ContactEntity>();
                        List<Json> jSons = new Json(result).get("entries").toList();
                        for(Json json:jSons){
                            entities.add(ContactEntity.parse(json));
                        }
                        resultCallback.onSuccess(entities);
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }
            }
        });

    }

    /**
     * 添加用户到通讯录
     * @param userId
     * @param resultCallback
     */
    public void contactAdd(final String token,final String userId , final String otherId, final String email,final String name,final String avatarUrl, final Back.Result<ContactEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(otherId) || StringUtil.isEmpty(email) || StringUtil.isEmpty(name) || StringUtil.isEmpty(avatarUrl)) {
                        resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userId", otherId);
                        params.put("email", email);
                        params.put("name", name);
                        params.put("avatarUrl", avatarUrl);
                        String result = HttpUtil.post(token, Urls.User_Contact_Add + userId + Urls.User_Contacts_Last, params);
                        Json json = new Json(result);
                        String status = json.getStr("status");
                        if(StringUtil.isEmpty(status)){
                            resultCallback.onSuccess(ContactEntity.parse(json));
                        }else {
                            int key = Integer.parseInt(status);
                            resultCallback.onError(ErrorCode.SERVER_TIP_TO_USER_ERROR,"");
                        }
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                }
            }
        });

    }

    /**
     * 更新用户的某个联系人
     * @param userId
     * @param otherId
     * @param id
     * @param name
     * @param title
     * @param remark
     * @param avatarUrl
     * @param callback
     */
    public void contactUpdate(final String token,final String userId,final String otherId,final String id,final String name,final String title,final String remark,final String avatarUrl, final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(otherId) || StringUtil.isEmpty(id) || StringUtil.isEmpty(name) || StringUtil.isEmpty(title) || StringUtil.isEmpty(remark) || StringUtil.isEmpty(avatarUrl)) {
                        callback.onError(ErrorCode.PARAMETER_ERROR);
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userId", otherId);
                        params.put("name", name);
                        params.put("title", title);
                        params.put("remark",remark);
                        params.put("avatarUrl", avatarUrl);
                        String result = HttpUtil.put(token, Urls.User_Contact_Update + userId + Urls.User_Contact_Update_Last + id, params);
                        callback.onSuccess();
                    }
                }catch (Exception e){
                    callback.onError(ErrorCode.TOKEN_ERROR);
                }
            }
        });

    }

    /**
     * 更新用户信息
     * @param userId
     * @param email
     * @param name
     * @param role
     * @param jobTitle
     * @param phone
     * @param address
     * @param status
     * @param avatarUrl
     * @param resultCallback
     */
    public void userUpdate(final String token,final String userId,final String email,final String name,final String role,final String jobTitle,final String phone,final String address,final String status,final String avatarUrl, final Back.Result<ContactEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(email) || StringUtil.isEmpty(name) || StringUtil.isEmpty(role) || StringUtil.isEmpty(jobTitle) || StringUtil.isEmpty(phone) || StringUtil.isEmpty(address) || StringUtil.isEmpty(status) || StringUtil.isEmpty(avatarUrl)) {
                        resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userId", userId);
                        params.put("email", email);
                        params.put("name", name);
                        params.put("role",role);
                        params.put("jobTitle", jobTitle);
                        params.put("phone", phone);
                        params.put("address", address);
                        params.put("status", status);
                        params.put("address", avatarUrl);
                        String result = HttpUtil.put(token, Urls.User_Update + userId, params);
                        resultCallback.onSuccess(ContactEntity.parse(new Json(result)));
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                }
            }
        });

    }


    /**
     * 获取某个用户信息
     * @param token
     * @param userId
     * @param resultCallback
     */
    public void userGet(final String token, final String userId, final Back.Result<UserEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)) {
                        resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.get(token, Urls.User_Get + userId, params);
                        resultCallback.onSuccess(UserEntity.parse(new Json(result)));
                    }
                }catch (Exception e){
                    resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                }
            }
        });

    }

    /**
     * 从通讯录中删除某个用户
     * @param userId 自己用户唯一标识码
     * @param id 被删除联系人唯一标识码
     * @param callback
     */
    public void contactDelete(final String token,final String userId,final String id , final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(id)) {
                        callback.onError(ErrorCode.PARAMETER_ERROR);
                    } else {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.delete(token, Urls.User_Contact_Delete + userId + Urls.User_Contact_Delete_Last + id, params);
                        callback.onSuccess();
                    }
                }catch (Exception e){
                    callback.onError(ErrorCode.TOKEN_ERROR);
                }
            }
        });

    }

    /***********************************************Session***************************************************/

    /**
     * 获取会话信息
     * @param sessionId 会话Id
     * @param resultCallback
     */
    public void getSessionInfo(final String token,final String sessionId , final Back.Result<SessionEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else{
                    SessionEntity group = new SessionEntity();
                    group.setId("123456");
                    group.setName("˭");
                    resultCallback.onSuccess(group);
                }

            }
        });

    }

    /**
     * 获取自己的群组列表
     * @param userId 自己的唯一标识码
     * @param resultCallback
     */
    public void getUserSessionsList(final String token,final String userId,final Back.Result<List<UserSessionEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else{
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.get(token,Urls.User_Sessions_Get+userId+Urls.User_Sessions_Get_Last, params);
                        List<UserSessionEntity> userSessionEntities = new ArrayList<UserSessionEntity>();
                        List<Json> jSons = new Json(result).get("entries").toList();
                        for(Json json:jSons){
                            userSessionEntities.add(UserSessionEntity.parse(json));
                        }
                        resultCallback.onSuccess(userSessionEntities);
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }
            }
        });

    }

    /**
     * 创建会话
     * @param name 会话名称
     * @param secureType 会话安全类型
     * @param type 会话类型
     * @param avatarUrl 会话头像
     * @param resultCallback
     */

    public void sessionCreate(final String token,final String name,final String secureType, final String type, final String avatarUrl,
                              final Back.Result<SessionEntity> resultCallback){

        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(name)|| StringUtil.isEmpty(secureType)
                        || StringUtil.isEmpty(type)|| StringUtil.isEmpty(avatarUrl)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else{
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("name",name);
                        params.put("secureType",secureType);
                        params.put("type",type);
                        params.put("avatarUrl",avatarUrl);
                        String result = HttpUtil.post(token, Urls.User_Session_Create, params);
                        resultCallback.onSuccess(SessionEntity.parse(new Json(result)));
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }

            }
        });
    }

    /**
     * 将会话添加到自己的会话列表
     * @param userId 自己的唯一标识码
     * @param name 会话名称
     * @param sessionId 会话Id
     * @param avatarUrl 会话头像
     * @param resultCallback
     */
    public void sessionStore(final String token,final String userId, final String sessionId, final String name,final String avatarUrl,
                           final Back.Result<UserSessionEntity> resultCallback){

        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)|| StringUtil.isEmpty(sessionId)|| StringUtil.isEmpty(name)|| StringUtil.isEmpty(avatarUrl)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else{
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("sessionId",sessionId);
                        params.put("name",name);
                        params.put("avatarUrl",avatarUrl);
                        String result = HttpUtil.post(token, Urls.User_Sessions_Store + userId + Urls.User_Sessions_Store_Last, params);
                        resultCallback.onSuccess(UserSessionEntity.parse(new Json(result)));
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }

            }
        });
    }

    /**
     * 将会话从自己的会话列表中删除
     * @param userId 自己的唯一标识码
     * @param id 用户群组唯一标识码
     * @param callback
     */
    public void userSessionDelete(final String token,final String userId,final String id , final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(id)){
                    callback.onError(ErrorCode.PARAMETER_ERROR);
                }else{
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.delete(token, Urls.User_Sessions_Delete + userId + Urls.User_Sessions_Delete_Last + id, params);
                        callback.onSuccess();
                    }catch (Exception e){
                        callback.onError(ErrorCode.TOKEN_ERROR);
                    }
                }
            }
        });
    }

    /**
     * 会话添加成员
     * @param sessionId 会话Id
     * @param userId 成员Id
     * @param name 成员名称
     * @param role 成员角色
     * @param avatarUrl 成员头像
     * @param status 成员状态
     * @param resultCallback
     */
    public void sessionMemberAdd(final String token,final String sessionId,final String userId,final String name,final String role,final String avatarUrl,final String status,final Back.Result<SessionMemberEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else{
                    try{
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userId",userId);
                        params.put("name",name);
                        params.put("role",role);
                        params.put("avatarUrl",avatarUrl);
                        params.put("status",status);
                        String result = HttpUtil.post(token, Urls.User_SessionMember_Add + sessionId + Urls.User_SessionMember_Add_Last, params);
                        resultCallback.onSuccess(SessionMemberEntity.parse(new Json(result)));
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }
            }
        });

    }

    /**
     * 删除本会话某个成员
     * @param sessionId 会话Id
     * @param id 会话成员唯一标识码
     * @param callback
     */
    public void sessionMemberDelete(final String token,final String sessionId , final String id, final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId) || StringUtil.isEmpty(id)){
                    callback.onError(ErrorCode.PARAMETER_ERROR);
                }else{
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.delete(token, Urls.User_SessionMember_Delete + sessionId + Urls.User_SessionMember_Delete_Last + id, params);
                        callback.onSuccess();
                    }catch (Exception e){
                        callback.onError(ErrorCode.TOKEN_ERROR);
                    }
                }

            }
        });

    }

    /**
     * 获取本会话成员列表.
     * @param sessionId 会话Id
     * @param resultCallback
     */
    public void sessionMembersGet(final String token,String updateAt, final String sessionId , final Back.Result<List<SessionMemberEntity>> resultCallback){

        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else{
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.get(token,Urls.User_SessionMembers_Get+sessionId+Urls.User_SessionMembers_Get_Last, params);
                        List<SessionMemberEntity> sessionMembers = new ArrayList<SessionMemberEntity>();
                        List<Json> jSons = new Json(result).get("entries").toList();
                        for(Json json:jSons){
                            sessionMembers.add(SessionMemberEntity.parse(json));
                        }
                        resultCallback.onSuccess(sessionMembers);
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }
            }
        });
    }
    /************************************************用户会话****************************************************/


    /**
     * 获取某个用户会话信息
     * @param userId
     * @param userConversationId
     * @param resultCallback
     */
    public void getUserConversation(final String token,final String userId,final String userConversationId,final Back.Result<UserConversationEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userConversationId)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.get(token, Urls.User_UserConversation_Get + userId + Urls.User_UserConversation_Last + userConversationId, params);
                        resultCallback.onSuccess(UserConversationEntity.parse(new Json(result)));
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }
            }
        });

    }


    /**
     * 获取自己的用户会话列表
     * @param updateAt 更新时间戳
     * @param userId 自己的唯一标识码
     * @param resultCallback
     */
    public void getUserConversations(final String token, final String updateAt,final String userId,final Back.Result<List<UserConversationEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.get(token,updateAt,Urls.User_UserConversations_Get+userId+Urls.User_UserConversations_Last, params);
                        List<UserConversationEntity> userConversations = new ArrayList<UserConversationEntity>();
                        List<Json> jsons = new Json(result).get("entries").toList();
                        for(Json json:jsons){
                            userConversations.add(UserConversationEntity.parse(json));
                        }
                        resultCallback.onSuccess(userConversations);
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }
            }
        });

    }

    /**
     * 删除用户会话列表某个会话
     * @param userId
     * @param userConversationId
     * @param callback
     */
    public void deleteUserConversation(final String token,final String userId,final String userConversationId,final Back.Callback callback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(userConversationId)){
                    callback.onError(ErrorCode.PARAMETER_ERROR);
                }else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.delete(token, Urls.User_UserConversations_delete + userId + Urls.User_UserConversations_Last, params);
                        callback.onSuccess();
                    }catch (Exception e){
                        callback.onError(ErrorCode.UNKNOWN);
                    }
                }
            }
        });

    }

    /************************************************会话****************************************************/

    /**
     * 获取一对一会话
     * @param userId 自己的唯一标识码
     * @param otherId 对方的唯一标识码
     * @param resultCallback
     */
    public void getP2PSession(final String token,final String userId, final String otherId, final Back.Result<SessionEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(otherId)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.get(token, Urls.User_SessionP2p_Get + userId +"/" + otherId , params);
                        resultCallback.onSuccess(SessionEntity.parse(new Json(result)));
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }
            }
        });
    }


    public void getSession(final String token,final String sessionId, final Back.Result<SessionEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        String result = HttpUtil.get(token, Urls.User_Session_Get + sessionId, params);
                        resultCallback.onSuccess(SessionEntity.parse(new Json(result)));
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                    }
                }
            }
        });
    }

    /************************************************消息****************************************************/

    /**
     * 发送消息
     * @param sessionId
     * @param sender
     * @param content
     * @param type
     * @param resultCallback
     */
    public void sendMessage(final String token,final String sessionId,final String sender, final String content, final String type, final Back.Result<MessageEntity> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId) || StringUtil.isEmpty(sender) || StringUtil.isEmpty(content) || StringUtil.isEmpty(type)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else {
                    String result ="";
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("sender", sender);
                        params.put("content", content);
                        params.put("type", type);
                        result = HttpUtil.post(token, Urls.User_Messages_Send + sessionId + Urls.User_Messages_Send_Last, params);
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                        return;
                    }
                    resultCallback.onSuccess(MessageEntity.parse(new Json(result)));

                }
            }
        });

    }

    /**
     * 同步会话消息
     * @param sessionId 会话Id
     * @param syncTime 时间戳
     *  @param limit 消息条数极限
     * @param resultCallback
     */
    public void getMessage(final String token,final String sessionId,final String syncTime, final int limit, final Back.Result<List<MessageEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else {
                    List<MessageEntity> models = new ArrayList<MessageEntity>();
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("limit", ""+limit);
                        String result = HttpUtil.get(token, syncTime, Urls.User_Messages_Get + sessionId + Urls.User_Messages_Get_Last, params);
                        List<Json> jSons = new Json(result).get("entries").toList();
                        for(Json json:jSons){
                            models.add(MessageEntity.parse(json));
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                        return ;
                    }
                    resultCallback.onSuccess(models);
                }
            }
        });

    }
    /**
     * 同步历史会话消息
     * @param sessionId 会话Id
     * @param syncTime 时间戳
     *  @param limit 消息条数极限
     * @param resultCallback
     */
    public void getMessageHistory(final String token,final String sessionId,final String syncTime, final int limit, final Back.Result<List<MessageEntity>> resultCallback){
        ThreadPool.getThreadPool().executNet(new Runnable() {
            @Override
            public void run() {
                if(StringUtil.isEmpty(token) || StringUtil.isEmpty(sessionId)){
                    resultCallback.onError(ErrorCode.PARAMETER_ERROR,"");
                }else {
                    List<MessageEntity> models = new ArrayList<MessageEntity>();
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("limit", ""+limit);
                        String result = HttpUtil.get(token, syncTime, Urls.User_Messages_Get + sessionId + Urls.User_Messages_Get_Hostory, params);
                        List<Json> jSons = new Json(result).toList();
                        for(Json json:jSons){
                            models.add(MessageEntity.parse(json));
                        }
                    }catch (Exception e){
                        resultCallback.onError(ErrorCode.UNKNOWN,e.toString());
                        return ;
                    }
                    resultCallback.onSuccess(models);
                }
            }
        });

    }
}
