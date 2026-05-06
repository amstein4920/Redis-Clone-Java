package DataStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class RdbLoader {

    public void load(PushbackInputStream stream, DataStore store) throws IOException {
        // Consume the header
        stream.readNBytes(9);

        int b;
        while (true) {
            b = stream.read();

            // End of section
            if (b == 0xFF) {
                return;
            }

            // Consume any metadata subsections, but don't do anything with them
            if (b == 0xFA) {
                stringEncoding(stream);
                stringEncoding(stream);
                continue;
            }

            // Target Byte for starting DB section
            if (b == 0xFE) {
                // Read in index, but can just be skipped for now
                if (stream.read() == -1) {
                    throw new IOException("Interrupted Database Section");
                }

                b = stream.read();
                // Read in size data, but just skip the actual values
                // Pre-allocating HashMap space seems more complicated than valuable
                if (b == 0xFB) {
                    // Doesn't really matter what happens with these so leaving the exceptions
                    // unhandled
                    sizeEncoding(stream);
                    sizeEncoding(stream);
                    continue;
                }
                continue;
            }

            Long expiry = -1L;
            if (b == 0xFC) {
                // Expiry in milliseconds
                expiry = ByteBuffer.wrap(stream.readNBytes(8)).order(ByteOrder.LITTLE_ENDIAN).getLong();
            } else if (b == 0xFD) {
                // Expiry in seconds
                expiry = ByteBuffer.wrap(stream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt() * 1000L;
            }

            if (expiry != -1) {
                // Type, but current only 0x00 string types are considered, so we just read and
                // discard
                stream.read();
            }

            String key = stringEncoding(stream);
            String value = stringEncoding(stream);

            // Build ValueEntry
            ValueEntry.Builder entryBuilder = ValueEntry.builder(value);
            if (expiry != -1) {
                entryBuilder.expiry(Instant.ofEpochMilli(expiry));
            }

            store.setEntity(key, entryBuilder.build());
        }
    }

    private int sizeEncoding(InputStream stream) throws IOException {
        int result = 0;

        byte[] sizeBytes = stream.readNBytes(1);
        int firstTwoBits = (sizeBytes[0] & 0xFF) >> 6;

        switch (firstTwoBits) {
            case 0:
                result = sizeBytes[0] & ((1 << 6) - 1);
                break;
            case 1:
                result = (sizeBytes[0] & 0x3F) << 8 |
                        Byte.toUnsignedInt(stream.readNBytes(1)[0]);
                break;
            case 2:
                byte[] bytes = stream.readNBytes(4);
                result = ByteBuffer.wrap(bytes).getInt();
                break;
            default:
                throw new IOException("Unexpected size encoding prefix: " + firstTwoBits);
        }
        return result;
    }

    private String stringEncoding(PushbackInputStream stream) throws IOException {
        byte[] sizeBytes = stream.readNBytes(1);
        int firstTwoBits = (sizeBytes[0] & 0xFF) >> 6;
        if (firstTwoBits == 3) {
            int size = sizeBytes[0] & ((1 << 6) - 1);
            switch (size) {
                case 0:
                    return String.valueOf(ByteBuffer.wrap(stream.readNBytes(1)).order(ByteOrder.LITTLE_ENDIAN).get());
                case 1:
                    return String
                            .valueOf(ByteBuffer.wrap(stream.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getShort());
                case 2:
                    return String
                            .valueOf(ByteBuffer.wrap(stream.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).getInt());
            }
            return "";
        }
        stream.unread(sizeBytes);
        int length = sizeEncoding(stream);
        byte[] stringBytes = stream.readNBytes(length);
        return new String(stringBytes, StandardCharsets.UTF_8);
    }
}
