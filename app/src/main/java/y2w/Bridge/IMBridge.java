package y2w.Bridge;

import com.yun2win.imlib.IMClient;
import com.yun2win.message.IMSession;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;

import y2w.manage.CurrentUser;
import y2w.model.Session;

/**
 * 服务器连接类
 * Created by maa2 on 2016/3/10.
 */
public class IMBridge implements Serializable{
    private String TAG = IMBridge.class.getSimpleName();
    private IMClient imClient;
    private CurrentUser user;
    public IMBridge(CurrentUser user){
        this.user = user;
        imClient = new IMClient(user.getImToken().getAccessToken(),user.getEntity().getId());
    }

    public IMClient getImClient() {
        return imClient;
    }

    /**
     * 连接服务器
     * @param changed
     * @param receiveListener
     */
    public void connect(final IMClient.onConnectionStatusChanged changed, final IMClient.OnMessageReceiveListener receiveListener){
        imClient.connect(changed, receiveListener);
    }


    /**
     * 断开服务器连接
     */
    public void disConnect(){
        imClient.disConnect();
    }

    public void sendMessage(Session session,String message,IMClient.SendCallback callback){
        IMSession imSession = new IMSession();
        String sessionId = session.getEntity().getType() + "_" + session.getEntity().getId();
        imSession.setId(sessionId);
        imSession.setMts(session.getMessages().getTimeStamp());
        imClient.sendMessage(imSession, CmdBuilder.buildMessage(sessionId), callback);
    }

    public void updateSession(IMSession imSession,String members,IMClient.SendCallback callback){

        imClient.updateSession(imSession, members, callback);
    }
}
