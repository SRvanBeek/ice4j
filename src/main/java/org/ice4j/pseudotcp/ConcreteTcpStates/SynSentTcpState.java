package org.ice4j.pseudotcp.ConcreteTcpStates;

import org.ice4j.pseudotcp.*;

import java.io.IOException;

public class SynSentTcpState implements TcpState {
    private PseudoTCPBase pseudoTCPBase;

    public SynSentTcpState(PseudoTCPBase pseudoTCPBase) {
        this.pseudoTCPBase = pseudoTCPBase;
    }

    @Override
    public void connect() throws IOException {
        throw new IOException("Invalid socket state: "+ this);
    }

    @Override
    public void notifyMTU(int mtu) {

    }

    @Override
    public void notifyClock(long now) {

    }

    @Override
    public void setOption(Option opt, long value) {
        pseudoTCPBase.resizeSendBuffer((int) value);
    }

    @Override
    public int recv(byte[] buffer, int offset, int len) throws IOException {
        throw new IOException("Socket not connected");
    }

    public int send(byte[] buffer, int offset, int len) throws IOException {
        throw new IOException("Socket not connected");
    }

    @Override
    public long clock_check(long now, long snd_buffered) {
        if ((pseudoTCPBase.m_shutdown == EnShutdown.SD_GRACEFUL) || ((snd_buffered == 0) && (pseudoTCPBase.m_t_ack == 0)))
        {
            return -1;
        }
        return 0;
    }

    @Override
    public boolean process(Segment seg) {
        return false;
    }

    @Override
    public boolean transmit(SSegment seg, long now) {
        return true;
    }

    @Override
    public void setConversationID(long convID) {
        throw new IllegalStateException();
    }

    @Override
    public void closedown(IOException ioException) {
        pseudoTCPBase.changeState(new ClosedTcpState(pseudoTCPBase));
    }

    @Override
    public void close(boolean force) {
        if (force)
        {
            pseudoTCPBase.changeState(new ClosedTcpState(pseudoTCPBase));
        }
    }

    @Override
    public long ordinal() {
        return 1;
    }
}
