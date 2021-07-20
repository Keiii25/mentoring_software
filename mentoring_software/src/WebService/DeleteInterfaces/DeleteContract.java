package WebService.DeleteInterfaces;

import WebService.DELETE;

public interface DeleteContract extends DELETE {
    default void deleteContract(String contractId) {
        String extension = "/contract/" + contractId;
        delete(extension);
    }
}
