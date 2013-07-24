package com.easou.news.crawl.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

//import com.easou.hector.utils.ByteUtil;
import com.easou.news.crawl.factory.TransformFactory;
import com.easou.news.crawl.service.ITransformService;

@Service
public class TransformServiceImpl implements ITransformService {
    private static Logger logger = LoggerFactory.getLogger(TransformServiceImpl.class);

    private TransformFactory transformFactory = TransformFactory.getInstance();
    private String url;

    @Override
    public String transform(byte[] htmlData) {
        String transformedContent = "";
        Socket socket = transformFactory.getSocket();
        try {
            byte[] bLen = new byte[4];

            // send the length of data, in reversed byte order
            bLen[3] = (byte) ((int) htmlData.length >> 24);
            bLen[2] = (byte) ((int) htmlData.length >> 16);
            bLen[1] = (byte) ((int) htmlData.length >> 8);
            bLen[0] = (byte) ((int) htmlData.length >> 0);
            socket.getOutputStream().write(bLen, 0, 4);

            // send the data to server
            socket.getOutputStream().write(htmlData, 0, htmlData.length);
            socket.getOutputStream().flush();

            byte[] length = new byte[4];
            // receive the data length
            socket.getInputStream().read(length);
//            int len = ByteUtil.getInt(length, 0);
            int len = 0;

            byte[] transformedData = new byte[len];
            // receive the data
            readAll(socket.getInputStream(), transformedData, 0, len);
            // socket.getInputStream().read(transformedData, 0, len);
            transformedContent = new String(transformedData, "utf-8");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close the socket
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return transformedContent;
    }

    /**
     * Guarantees that all of len bytes are actually read off the transport.
     * @param buf Array to read into
     * @param off Index to start reading at
     * @param len Maximum number of bytes to read
     * @return The number of bytes actually read, which must be equal to len
     * @throws TTransportException if there was an error reading data
     * @throws IOException
     */
    public int readAll(InputStream inputStream, byte[] buf, int off, int len) throws IOException {
        int got = 0;
        int ret = 0;
        while (got < len) {
            ret = inputStream.read(buf, off + got, len - got);
            if (ret <= 0) {
                logger.error("Cannot read. Remote side has closed. Tried to read " + len + " bytes, but only got " + got + " bytes.");
            }
            got += ret;
        }
        return got;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
