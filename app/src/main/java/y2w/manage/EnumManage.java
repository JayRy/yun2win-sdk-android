package y2w.manage;

/**
 * Created by maa46 on 2016/3/17.
 */
public class EnumManage {
    public static enum SessionType{
        p2p,group,single
    };
    //安全类型级别
    public final static String SecureType_public = "public";
    public final static String SecureType_private = "private";
    public static enum GroupRole{
        master,admin,user
    };
    public static enum UserStatus{
        active,inactive
    };

    public static enum SyncType{
        userConversation,contact,group
    };

    public static enum ReceiveSyncStatusType{
        none,syncing,repeat
    };
}
