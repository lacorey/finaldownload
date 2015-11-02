package cn.laclab.client.finaldownload.core.http;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


public class RequestParams {

    private String charset = "UTF-8";

    private List<HeaderItem> headers;

    public RequestParams() {
    }

    public RequestParams(String charset) {
        if (!TextUtils.isEmpty(charset)) {
            this.charset = charset;
        }
    }

    /**
     * Adds a header to this message. The header will be appended to the end of the list.
     *
     * @param header
     */
    public void addHeader(String header) {
        if (this.headers == null) {
            this.headers = new ArrayList<HeaderItem>();
        }
        this.headers.add(new HeaderItem(header));
    }



    public List<HeaderItem> getHeaders() {
        return headers;
    }

    public class HeaderItem {
        public final boolean overwrite;
        public final String header;

        public HeaderItem(String header) {
            this.overwrite = false;
            this.header = header;
        }

        public HeaderItem(String header, boolean overwrite) {
            this.overwrite = overwrite;
            this.header = header;
        }

    }
}