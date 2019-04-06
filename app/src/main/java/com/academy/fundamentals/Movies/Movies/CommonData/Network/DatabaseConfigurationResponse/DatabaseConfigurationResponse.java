
package com.academy.fundamentals.Movies.Movies.CommonData.Network.DatabaseConfigurationResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatabaseConfigurationResponse {

    @SerializedName("images")
    @Expose
    private ImageResult imageResult;
    @SerializedName("change_keys")
    @Expose
    private List<String> changeKeys = null;

    public ImageResult getImageResult() {
        return imageResult;
    }

    public void setImageResult(ImageResult imageResult) {
        this.imageResult = imageResult;
    }

    public List<String> getChangeKeys() {
        return changeKeys;
    }

    public void setChangeKeys(List<String> changeKeys) {
        this.changeKeys = changeKeys;
    }

}
