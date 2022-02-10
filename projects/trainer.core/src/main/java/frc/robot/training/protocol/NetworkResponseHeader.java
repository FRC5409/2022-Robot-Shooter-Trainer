package frc.robot.training.protocol;

import java.io.IOException;

public class NetworkResponseHeader {
    public static final int FLAG_NULL             = 0x0;
    public static final int FLAG_SENDABLE_PAYLOAD = 0x1;

    public static final int HEADER_STATUS_MASK    = 0x0FF;
    public static final int HEADER_FLAGS_MASK     = 0xF00;

    public final NetworkStatus status;
    public final int flags;

    public static NetworkResponseHeader decode(int data) throws IOException {
        int statusCode = data & HEADER_STATUS_MASK;
        NetworkStatus status = NetworkStatus.fromId(statusCode);

        if (status == null)
            throw new IOException("Unexpected status code 0x" + Integer.toHexString(statusCode));

        return new NetworkResponseHeader(
            status,
            (data & HEADER_FLAGS_MASK) >> 8
        );
    }

    public static int encode(NetworkResponseHeader header) {
        return (
            (header.status.id() & HEADER_STATUS_MASK) |
            ((header.flags << 8) & HEADER_FLAGS_MASK)
        );
    }

    public NetworkResponseHeader() {
        this(NetworkStatus.STATUS_NONE, FLAG_NULL);
    }

    public NetworkResponseHeader(NetworkStatus status) {
        this(status, FLAG_NULL);
    }

    public NetworkResponseHeader(NetworkStatus status, int flags) {
        this.status = status;
        this.flags = flags;
    }

    public boolean hasFlag(int flag) {
        return (flags & flag) != 0;
    }
}
