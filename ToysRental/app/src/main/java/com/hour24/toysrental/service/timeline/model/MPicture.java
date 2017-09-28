package com.hour24.toysrental.service.timeline.model;

import java.io.File;
import java.io.Serializable;

/**
 * Created by 장세진 on 2016-08-12.
 */
public class MPicture implements Serializable {

    private String attachSeq;
    private File file;
    private String url;

    public MPicture() {

    }

    public String getAttachSeq() {
        return attachSeq;
    }

    public void setAttachSeq(String attachSeq) {
        this.attachSeq = attachSeq;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
