package y2w.manage;

import android.os.Handler;
import android.os.Message;

import com.yun2win.imlib.ConnectionReturnCode;
import com.yun2win.imlib.ConnectionStatus;
import com.yun2win.imlib.IMClient;
import com.yun2win.message.IMSession;
import com.yun2win.utils.LogUtil;

import java.io.Serializable;

import y2w.base.AppContext;
import y2w.Bridge.IMBridge;
import y2w.Bridge.ReceiveUtil;
import y2w.model.MToken;
import y2w.service.Back;
import y2w.service.ErrorCode;

/**
 * 服务器连接管理类
 * Created by maa2 on 2016/3/16.
 */
public class IMBridges implements Serializable{

    private String TAG = IMBridges.class.getSimpleName();
    private CurrentUser user;
    private ReceiveUtil receiveUtil;
    private IMBridge imBridge;
    public IMBridges(CurrentUser user){
        this.user = user;
        this.receiveUtil = new ReceiveUtil(user);
    }

    public IMBridge getImBridge() {
        return imBridge;
    }

    /**
     * 服务器连接
     */
    public void connect(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    MToken mToken = (MToken) msg.obj;
                    user.setImToken(mToken);
                    imBridge = new IMBridge(user);
                    imBridge.connect(new IMClient.onConnectionStatusChanged() {
                        @Override
                        public void onChanged(int status, int error) {
                            if (status == ConnectionStatus.CS_DISCONNECTED) {
                                switch (error) {
                                    case ConnectionReturnCode.CRC_KICKED:
                                        AppContext.getAppContext().logout();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }, new IMClient.OnMessageReceiveListener() {
                        @Override
                        public void onMessage(String message, IMSession imSession, String sendMsg, String data) {
                            receiveUtil.receiveMessage(message, imSession, sendMsg, data);
                        }
                    });
                }else{
                    LogUtil.getInstance().log(TAG,"get imToken failure",null);
                }
            }
        };
        user.getImToken(new Back.Result<MToken>() {
            @Override
            public void onSuccess(MToken mToken) {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = mToken;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(ErrorCode errorCode,String error) {
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 断开服务器连接
     */
    public void disConnect(){
        if(imBridge != null)
        imBridge.disConnect();
    }

    /**
     * 带回调的服务器连接
     * @param changed
     * @param receiveListener
     */
   /* public void connect(final IMClient.onConnectionStatusChanged changed, final IMClient.OnMessageReceiveListener receiveListener){
        user.getImToken(new Back.Result<MToken>() {
            @Override
            public void onSuccess(MToken mToken) {
                imBridge.connect(mToken.getAccessToken(), user.getEntity().getId(), changed, receiveListener);
            }

            @Override
            public void onError(ErrorCode errorCode,String error) {
                changed.onChanged(ConnectionStatus.CS_DISCONNECTED, ConnectionReturnCode.CRC_TOKEN_INVALID);
            }
        });
    }*/


}
