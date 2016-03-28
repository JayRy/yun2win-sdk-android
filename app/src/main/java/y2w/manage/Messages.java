package y2w.manage;

import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.SendReturnCode;
import com.yun2win.message.IMSession;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import y2w.common.Constants;
import y2w.db.MessageDb;
import y2w.db.SessionDb;
import y2w.entities.MessageEntity;
import y2w.entities.SessionEntity;
import y2w.model.MessageModel;
import y2w.model.Session;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.service.MessageSrv;
import com.y2w.uikit.utils.StringUtil;

/**
 * 消息管理类
 * Created by maa2 on 2016/1/16.
 */
public class Messages implements Serializable {
    private String TAG = Messages.class.getSimpleName();
    private Session session;
    private String updateAt;
    private Remote remote;

    public Messages(Session session){
       this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public String getUpdateAt() {
        SessionEntity entity = SessionDb.queryBySessionId(session.getEntity().getMyId(), session.getEntity().getId());
        if(entity != null){
            updateAt = entity.getCreateMTS();
        }else {
            updateAt = Constants.TIME_ORIGIN;
        }
        return updateAt;
    }


    public String getMessageUpdateAt() {
        MessageEntity entity = MessageDb.queryLastMessage(session.getEntity().getMyId(), session.getEntity().getId());
        if(entity != null){
            updateAt = entity.getUpdatedAt();
        }else {
            updateAt = Constants.TIME_ORIGIN;
        }

        return updateAt;
    }
    public String getTimeStamp(){
        return StringUtil.getTimeStamp(getUpdateAt());
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public void getCount(){

    }

    /**
     * 根据消息Id，获取某个消息
     * @param mid
     * @return
     */
    public MessageModel getMessage(String mid){
        MessageEntity entity = MessageDb.queryById(session.getEntity().getMyId(),mid);
        return new MessageModel(this,entity);
    }

    /**
     * 获取某个会话 model消息 后面 maxRow 条消息
     * @param model 消息
     * @param maxRow 条数极限
     * @param result
     */
    public void getMessages(MessageModel model,int maxRow,Back.Result<List<MessageModel>> result){
        List<MessageModel> models = new ArrayList<MessageModel>();
        List<MessageEntity> entities = null;
        if(model != null){
            entities = MessageDb.query(session.getEntity().getMyId(), session.getEntity().getId(),model.getEntity().getUpdatedAt(),maxRow);
        }else{
            entities = MessageDb.query(session.getEntity().getMyId(), session.getEntity().getId(),Constants.TIME_QUERY_BEFORE,maxRow);
        }
        for(MessageEntity entity:entities){
            models.add(new MessageModel(this,entity));
        }
        result.onSuccess(models);
    }
    /**
     * 发消息时，创建一条消息
     * @param content
     * @param type
     * @return
     */
    public MessageModel createMessage(String content,String type){
        MessageEntity entity = new MessageEntity();
        entity.setSessionId(session.getEntity().getId());
        entity.setSender(session.getEntity().getMyId());
        entity.setContent(content);
        entity.setType(type);
        entity.setStatus(MessageEntity.MessageState.storing.toString());
        return new MessageModel(this,entity);
    }

    /**
     * 将多条消息保存到数据库
     * @param messageList
     */

    public void add(List<MessageModel> messageList){
        for(MessageModel message:messageList){
            addMessage(message);
        }
    }

    /**
     * 将一条消息保存到数据库
     * @param message
     */
    public void addMessage(MessageModel message){
        message.getEntity().setMyId(session.getEntity().getMyId());
        MessageDb.addMessageEntity(message.getEntity());
    }

    public Remote getRemote(){
        if(remote == null){
            remote = new Remote(this);
        }
        return remote;
    }

    /*****************************remote*****************************/

    public class Remote implements Serializable{

        private Messages messages;
        public Remote(Messages messages) {
            this.messages = messages;

        }

        /**
         * 获取最新的消息消息
         * @param result
         */

        public void getLastMessages(final int count,final Back.Result<List<MessageModel>> result){
           MessageSrv.getInstance().getLastMessage(session.getSessions().getUser().getToken(), session.getEntity().getId(), Constants.TIME_QUERY_BEFORE, count, new Back.Result<List<MessageEntity>>() {
               @Override
               public void onSuccess(List<MessageEntity> entities) {
                   List<MessageModel> messageList = new ArrayList<MessageModel>();
                   for (MessageEntity entity : entities) {
                       messageList.add(new MessageModel(messages, entity));
                   }
                   result.onSuccess(messageList);
               }

               @Override
               public void onError(ErrorCode errorCode,String error) {
                   result.onError(errorCode,error);
               }
           });
        }

        /**
         * 同步某个时间戳后面消息
         * @param syncTime
         * @param limit
         * @param result
         * @param isstore
         */
        public void sync(final boolean isstore,String syncTime, int limit, final Back.Result<List<MessageModel>> result){
            MessageSrv.getInstance().sync(session.getSessions().getUser().getToken(), session.getEntity().getId(), syncTime, limit, new Back.Result<List<MessageEntity>>() {
                @Override
                public void onSuccess(List<MessageEntity> entities) {
                    List<MessageModel> messageList = new ArrayList<MessageModel>();
                    for (MessageEntity entity : entities) {
                        messageList.add(new MessageModel(messages, entity));
                    }
                    if(isstore) {
                        add(messageList);
                    }
                    result.onSuccess(messageList);
                }

                @Override
                public void onError(ErrorCode errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

        /**
         * 保存消息到业务服务器
         * @param message
         * @param result
         */
        public void store(final MessageModel message, final Back.Result<MessageModel> result){
            MessageSrv.getInstance().store(session.getSessions().getUser().getToken(), message.getEntity(), new Back.Result<MessageEntity>() {
                @Override
                public void onSuccess(MessageEntity entity) {
                    MessageModel model = new MessageModel(messages,entity);
                    model.getEntity().setSessionId(message.getEntity().getSessionId());
                    model.getEntity().setType(message.getEntity().getType());
                    model.getEntity().setStatus(MessageEntity.MessageState.stored.toString());
                    sendMessage(message.getEntity().getContent(), new IMClient.SendCallback() {

                        @Override
                        public void onReturnCode(int code, IMSession imSession, String sendMsg) {
                            switch (code) {
                                case SendReturnCode.SRC_SUCCESS:
                                    break;
                                case SendReturnCode.SRC_CMD_INVALID:
                                    LogUtil.getInstance().log(TAG, "returnCode:" + code, null);
                                    break;
                                case SendReturnCode.SRC_SESSION_INVALID:
                                    LogUtil.getInstance().log(TAG, "returnCode:" + code, null);
                                    break;
                                case SendReturnCode.SRC_SESSION_ID_INVALID:
                                    LogUtil.getInstance().log(TAG, "returnCode:" + code, null);
                                    break;
                                case SendReturnCode.SRC_SESSION_MTS_INVALID:
                            }
                        }
                    });
                    result.onSuccess(model);
                }

                @Override
                public void onError(ErrorCode errorCode,String error) {
                    result.onError(errorCode,error);
                }
            });
        }

        /**
         * 发送推送命令消息
         * @param message
         * @param callback
         */
        public void sendMessage(String message,IMClient.SendCallback callback){
            session.getSessions().getUser().getImBridges().getImBridge().sendMessage(session,message,callback);
           /* IMSession imSession = new IMSession();
            imSession.setId(session.getEntity().getType() + "_" + session.getEntity().getId());
            imSession.setMts(getTimeStamp());
            message = "{\"syncs\":[{\"type\":0},{\"type\":1,\"sessionId\":\"" + session.getEntity().getType()+"_" + session.getEntity().getId() + "\"}]}";
            LogUtil.getInstance().log(TAG, "updateAt:" + imSession.getMts(), null);
            LogUtil.getInstance().log(TAG, "message:"+message, null);
            Users.getInstance().getCurrentUser().getImBridges().getImBridge().getImClient().sendMessage(imSession, message, callback);*/
        }


    }


}
