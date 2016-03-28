package y2w.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.y2w.uikit.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by maa2 on 2016/3/21.
 */

@DatabaseTable(tableName = "SyncQueueEntity")
public class SyncQueueEntity implements Serializable{
    @DatabaseField
    private int type;
    @DatabaseField
    private String sessionId;
    @DatabaseField
    private String status;
    @DatabaseField
    private String time;
    @DatabaseField
    private String myId;

    public static SyncQueueEntity parse(int type,String sessionId,String status){
        SyncQueueEntity syncQueue = new SyncQueueEntity();
        syncQueue.setType(type);
        syncQueue.setSessionId(sessionId);
        syncQueue.setStatus(status);
        syncQueue.setTime(StringUtil.getNowTime());
        return syncQueue;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }
}
