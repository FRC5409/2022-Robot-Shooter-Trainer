package frc.robot.training.protocol;

import java.util.Map;

import frc.robot.training.protocol.NetworkStatus;
import org.jetbrains.annotations.Nullable;

public enum NetworkStatus {
    STATUS_NONE(0x00),
    STATUS_OK(0xA0),
    STATUS_ERROR(0x3C),
    STATUS_UNAVAILABLE(0x44),
    STATUS_INTERRUPTED(0x55);

    @Nullable
    public static NetworkStatus fromId(int id) {
        return STATUS_ID_MAP.get(id);
    }

    private static final Map<Integer, NetworkStatus> STATUS_ID_MAP = Map.of(
        STATUS_OK.id(), STATUS_OK,
        STATUS_ERROR.id(), STATUS_ERROR,
        STATUS_UNAVAILABLE.id(), STATUS_UNAVAILABLE
    );

    private final int _id;

    NetworkStatus(int id) {
        _id = id;
    }

    public final int id() {
        return _id;
    }
}