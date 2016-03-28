package y2w.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by maa2 on 2016/1/18.
 */
public class ConversationEntity implements Parcelable {


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    private String id;
    private String targetId;
    private String type;
    private boolean isDelete;
    private String createdAt;
    private String updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    /**
     * ��ȡ�Ự����
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * ���ûỰ����
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * ��ȡ�Ự�Ƿ�ɾ��
     * @return
     */
    public boolean isDelete() {
        return isDelete;
    }

    /**
     * ���ûỰ�Ƿ�ɾ��
     * @param isDelete
     */
    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * ��ȡ�Ự����ʱ��
     * @return
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * ���ûỰ����ʱ��
     * @param createdAt
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * ��ȡ�Ự����ʱ��
     * @return
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * ���ûỰ����ʱ��
     * @param updatedAt
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
