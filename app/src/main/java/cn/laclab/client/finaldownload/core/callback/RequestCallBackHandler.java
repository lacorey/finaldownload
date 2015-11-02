package cn.laclab.client.finaldownload.core.callback;

public interface RequestCallBackHandler {
    /**
     * @param total
     * @param current
     * @param forceUpdateUI
     * @return continue
     */
    boolean updateProgress(long total, long current, boolean forceUpdateUI);
}
