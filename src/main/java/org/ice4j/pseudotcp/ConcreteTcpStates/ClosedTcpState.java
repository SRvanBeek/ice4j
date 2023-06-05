package org.ice4j.pseudotcp.ConcreteTcpStates;

import org.ice4j.pseudotcp.*;

import java.io.IOException;

import static org.ice4j.pseudotcp.PseudoTCPBase.CLOSED_TIMEOUT;

public class ClosedTcpState implements TcpState {
    private PseudoTCPBase pseudoTCPBase;

    public ClosedTcpState(PseudoTCPBase pseudoTCPBase) {
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
        return CLOSED_TIMEOUT;
    }

    @Override
    public boolean process(Segment seg) {
        return false;
    }

    @Override
    public boolean transmit(SSegment seg, long now) {
        return false;
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
        return 4;
    }
}
