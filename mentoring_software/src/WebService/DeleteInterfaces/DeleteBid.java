package WebService.DeleteInterfaces;

import WebService.DELETE;

public interface DeleteBid extends DELETE {
    default void deleteBid(String bidId) {
        String extension = "/bid/" + bidId;
        delete(extension);
    }
}
