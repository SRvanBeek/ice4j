package org.ice4j.pseudotcp;

import java.io.IOException;

public interface TcpState {
    void connect() throws IOException;
    void notifyMTU(int mtu);
    void notifyClock(long now);
    void setOption(Option opt, long value);
    int recv(byte[] buffer, int offset, int len) throws IOException;
    int send(byte[] buffer, int offset, int len) throws IOException;
    long clock_check(long now, long snd_buffered);
    boolean process(Segment seg);
    boolean transmit(SSegment seg, long now);
    void setConversationID(long convID);
    void closedown(IOException ioException);
    void close(boolean force);

    long ordinal();
}
