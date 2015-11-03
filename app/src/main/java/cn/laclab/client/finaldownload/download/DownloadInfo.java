package cn.laclab.client.finaldownload.download;


import java.io.File;

import cn.laclab.client.database.annotation.Column;
import cn.laclab.client.database.annotation.Id;
import cn.laclab.client.database.annotation.Table;
import cn.laclab.client.finaldownload.core.http.HttpHandler;

@Table(name = "t_downloadinfo")
public class DownloadInfo {

    public DownloadInfo() {
    }

    @Id
    @Column(name = "id")
    private long id;

    private HttpHandler<File> handler;

    @Column(name = "state")
    private HttpHandler.State state;

    @Column(name = "downloadUrl")
    private String downloadUrl;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "fileSavePath")
    private String fileSavePath;

    @Column(name = "progress")
    private long progress;

    @Column(name = "fileLength")
    private long fileLength;

    @Column(name = "autoResume")
    private boolean autoResume;

    @Column(name = "autoRename")
    private boolean autoRename;

    @Column(name = "icon")
    private String icon;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HttpHandler<File> getHandler() {
        return handler;
    }

    public void setHandler(HttpHandler<File> handler) {
        this.handler = handler;
    }

    public HttpHandler.State getState() {
        return state;
    }

    public void setState(HttpHandler.State state) {
        this.state = state;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public boolean isAutoResume() {
        return autoResume;
    }

    public void setAutoResume(boolean autoResume) {
        this.autoResume = autoResume;
    }

    public boolean isAutoRename() {
        return autoRename;
    }

    public void setAutoRename(boolean autoRename) {
        this.autoRename = autoRename;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DownloadInfo)) return false;

        DownloadInfo that = (DownloadInfo) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "id=" + id +
                ", handler=" + handler +
                ", state=" + state +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
