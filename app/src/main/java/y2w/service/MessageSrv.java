package y2w.service;

import android.content.Context;

import com.yun2win.imlib.IMClient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import y2w.base.AppData;
import y2w.base.ClientFactory;
import y2w.common.AsyncMultiPartPost;
import y2w.entities.MessageEntity;

/**
 * Created by maa2 on 2016/1/28.
 */
public class MessageSrv {

    private static MessageSrv messageSrv = null;
    public static MessageSrv getInstance(){
        if(messageSrv == null){
            messageSrv = new MessageSrv();
        }
        return messageSrv;
    }
    public void store(String token, MessageEntity entity, Back.Result<MessageEntity> result){
        ClientFactory.getInstance().sendMessage(token, entity.getSessionId(), entity.getSender(), entity.getContent(), entity.getType(), result);
    }

    public void getLastMessage(String token, String sessionId, String syncTime,final int limit, Back.Result<List<MessageEntity>> result){
        ClientFactory.getInstance().getMessageHistory(token, sessionId, syncTime, limit, result);
    }

    public void sync(String token, String sessionId, String syncTime, final int limit, Back.Result<List<MessageEntity>> result){
        ClientFactory.getInstance().getMessage(token, sessionId, syncTime, limit, result);
    }

}
