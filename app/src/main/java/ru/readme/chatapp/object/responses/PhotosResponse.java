package ru.readme.chatapp.object.responses;

/**
 * Created by dima on 31.12.16.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dima
 */
public class PhotosResponse extends BaseResponse implements Serializable{

    private List<PhotoResponse> photos = new ArrayList<PhotoResponse>();
    private List<FolderResponse> folders = new ArrayList<FolderResponse>();

    public PhotosResponse() {
    }

    public PhotosResponse(String status) {
        super(status);
    }

    public List<PhotoResponse> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoResponse> photos) {
        this.photos = photos;
    }

    public static class Builder {

        public Builder() {
            this.item = new PhotosResponse();
        }
        private PhotosResponse item;

        public Builder setPhotos(final List<PhotoResponse> photos) {
            this.item.photos = photos;
            return this;
        }

        public Builder setFolders(final List<FolderResponse> folders) {
            this.item.folders = folders;
            return this;
        }

        public PhotosResponse build() {
            return this.item;
        }
    }

    public List<FolderResponse> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderResponse> folders) {
        this.folders = folders;
    }





}