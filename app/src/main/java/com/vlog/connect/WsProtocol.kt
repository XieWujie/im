package com.vlog.connect

object WsProtocol {
    /** Byte 0 flag for whether this is the final fragment in a message. */
    internal const val B0_FLAG_FIN = 128
    /** Byte 0 reserved flag 1. Must be 0 unless negotiated otherwise. */
    internal const val B0_FLAG_RSV1 = 64
    /** Byte 0 reserved flag 2. Must be 0 unless negotiated otherwise. */
    internal const val B0_FLAG_RSV2 = 32
    /** Byte 0 reserved flag 3. Must be 0 unless negotiated otherwise. */
    internal const val B0_FLAG_RSV3 = 16
    /** Byte 0 mask for the frame opcode. */
    internal const val B0_MASK_OPCODE = 15
    /** Flag in the opcode which indicates a control frame. */
    internal const val OPCODE_FLAG_CONTROL = 8

    /**
     * Byte 1 flag for whether the payload data is masked.
     *
     * If this flag is set, the next four
     * bytes represent the mask key. These bytes appear after any additional bytes specified by [B1_MASK_LENGTH].
     */
    internal const val B1_FLAG_MASK = 128
    /**
     * Byte 1 mask for the payload length.
     *
     * If this value is [PAYLOAD_SHORT], the next two
     * bytes represent the length. If this value is [PAYLOAD_LONG], the next eight bytes
     * represent the length.
     */
    internal const val B1_MASK_LENGTH = 127

    internal const val OPCODE_CONTINUATION = 0x0
    internal const val OPCODE_TEXT = 0x1
    internal const val OPCODE_BINARY = 0x2

    internal const val OPCODE_CONTROL_CLOSE = 0x8
    internal const val OPCODE_CONTROL_PING = 0x9
    internal const val OPCODE_CONTROL_PONG = 0xa

    /**
     * Maximum length of frame payload. Larger payloads, if supported by the frame type, can use the
     * special values [PAYLOAD_SHORT] or [PAYLOAD_LONG].
     */
    internal const val PAYLOAD_BYTE_MAX = 125L
    /** Maximum length of close message in bytes. */
    internal const val CLOSE_MESSAGE_MAX = PAYLOAD_BYTE_MAX - 2
    /**
     * Value for [B1_MASK_LENGTH] which indicates the next two bytes are the unsigned length.
     */
    internal const val PAYLOAD_SHORT = 126
    /** Maximum length of a frame payload to be denoted as [PAYLOAD_SHORT]. */
    internal const val PAYLOAD_SHORT_MAX = 0xffffL
    /**
     * Value for [B1_MASK_LENGTH] which indicates the next eight bytes are the unsigned
     * length.
     */
    internal const val PAYLOAD_LONG = 127

    /** Used when an unchecked exception was thrown in a listener. */
    internal const val CLOSE_CLIENT_GOING_AWAY = 1001
    /** Used when an empty close frame was received (i.e., without a status code). */
    internal const val CLOSE_NO_STATUS_CODE = 1005
}