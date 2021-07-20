package WebService.DeleteInterfaces;

import WebService.DELETE;

public interface DeleteMessage extends DELETE {
    default void deleteMessage(String messageId) {
        String extension = "/message/" + messageId;
        delete(extension);
    }
}
