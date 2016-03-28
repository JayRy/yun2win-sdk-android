package y2w.model;

import y2w.manage.Messages;
import y2w.entities.MessageEntity;

/**
 * Created by maa2 on 2016/2/18.
 */
public class MessageModel {
    private MessageEntity entity;
    private Messages messages;

    public MessageModel(Messages messages, MessageEntity entity){
        this.messages = messages;
        this.entity = entity;
    }

    public MessageEntity getEntity() {
        return entity;
    }

    public Messages getMessages() {
        return messages;
    }
}
