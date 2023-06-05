package org.ice4j.pseudotcp.ConcreteTcpStates;

import org.ice4j.pseudotcp.*;

import java.io.IOException;
import java.util.logging.Level;

import static org.ice4j.pseudotcp.PseudoTCPBase.*;

public class EstablishedTcpState implements TcpState {
    private PseudoTCPBase pseudoTCPBase;

    public EstablishedTcpState(PseudoTCPBase pseudoTCPBase) {
        this.pseudoTCPBase = pseudoTCPBase;
    }

    @Override
    public void connect() throws IOException {
        throw new IOException("Invalid socket state: " + this);
    }

    @Override
    public void notifyMTU(int mtu) {
        pseudoTCPBase.adjustMTU();
    }

    @Override
    public void notifyClock(long now) {
        if (timeDiff(pseudoTCPBase.m_lastrecv + IDLE_TIMEOUT, now) <= 0) {
            this.closedown(new IOException("Connection aborted"));
        }
        if (timeDiff(pseudoTCPBase.m_lasttraffic + (pseudoTCPBase.m_bOutgoing ? IDLE_PING * 3 / 2 : IDLE_PING), now) <= 0) {
            pseudoTCPBase.packet(pseudoTCPBase.m_snd_nxt, (short) 0, 0, 0);
        }
    }

    @Override
    public void setOption(Option opt, long value) {
        pseudoTCPBase.resizeSendBuffer((int) value);
    }

    @Override
    public int recv(byte[] buffer, int offset, int len) throws IOException {
        int read = pseudoTCPBase.m_rbuf.read(buffer, offset, len);

        // If there's no data in |m_rbuf|.
        if (read == 0) {
            pseudoTCPBase.m_bReadEnable = true;
            return 0;
        }
        assert read != -1;

        int available_space = pseudoTCPBase.m_rbuf.getWriteRemaining();
        if (available_space - pseudoTCPBase.m_rcv_wnd >= Math.min(pseudoTCPBase.m_rbuf_len / 8, pseudoTCPBase.m_mss)) {
            boolean bWasClosed = (pseudoTCPBase.m_rcv_wnd == 0); // !?! Not sure about this was closed business
            pseudoTCPBase.m_rcv_wnd = available_space;

            if (bWasClosed) {
                pseudoTCPBase.attemptSend(SendFlags.sfImmediateAck);
            }
        }
        return read;
    }


    @Override
    public int send(byte[] buffer, int offset, int len) throws IOException {
        long available_space;
        available_space = pseudoTCPBase.m_sbuf.getWriteRemaining();

        if (available_space == 0) {
            pseudoTCPBase.m_bWriteEnable = true;
            return 0;
        }

        int written = pseudoTCPBase.queue(buffer, offset, len, false);
        pseudoTCPBase.attemptSend(SendFlags.sfNone);
        return written;
    }


    @Override
    public long clock_check(long now, long snd_buffered) {
        long nTimeout;
        nTimeout = DEFAULT_TIMEOUT;

        if (pseudoTCPBase.m_t_ack > 0) {
            nTimeout = Math.min(nTimeout, timeDiff(pseudoTCPBase.m_t_ack + pseudoTCPBase.m_ack_delay, now));
        }
        if (pseudoTCPBase.m_rto_base > 0) {
            nTimeout = Math.min(nTimeout, timeDiff(pseudoTCPBase.m_rto_base + pseudoTCPBase.m_rx_rto, now));
        }
        if (pseudoTCPBase.getM_snd_wnd() == 0) {
            nTimeout = Math.min(nTimeout, timeDiff(pseudoTCPBase.m_lastsend + pseudoTCPBase.m_rx_rto, now));
        }
        if (PSEUDO_KEEPALIVE) {
            nTimeout = Math.min(
                    nTimeout,
                    timeDiff(pseudoTCPBase.m_lasttraffic + (pseudoTCPBase.m_bOutgoing ? IDLE_PING * 3 / 2 : IDLE_PING), now));
        }
        //nTimeout is used on wait methods, so cannot be equal to 0
        return nTimeout <= 0 ? 1 : nTimeout;
    }

    @Override
    public boolean process(Segment seg) {
        return false;
    }

    @Override
    public boolean transmit(SSegment seg, long now) {
        if (seg.xmit >= 15)
        {
            logger.log(Level.FINE, "too many retransmits");
            return false;
        }
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
        if (force) {
            pseudoTCPBase.changeState(new ClosedTcpState(pseudoTCPBase));
        }
    }


    @Override
    public long ordinal() {
        return 3;
    }
}
