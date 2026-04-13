package org.araymond.joal.core.torrent.torrent;

import com.turn.ttorrent.bcodec.InvalidBEncodingException;
import com.turn.ttorrent.common.Torrent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.lang.reflect.Field;

/**
 * Created by raymo on 23/01/2017.
 */
@SuppressWarnings("ClassWithOnlyPrivateConstructors")
@EqualsAndHashCode(callSuper = false)
@Getter
@Slf4j
public class MockedTorrent extends Torrent {
    public static final Charset BYTE_ENCODING = Charset.forName(Torrent.BYTE_ENCODING);

    private final InfoHash torrentInfoHash;
    private final String fileName;
    private final String comment;

    /**
     * Create a new torrent from meta-info binary data.
     * <p>
     * Parses the meta-info data (which should be B-encoded as described in the
     * BitTorrent specification) and create a Torrent object from it.
     *
     * @param torrent The meta-info byte data.
     * @throws IOException When the info dictionary can't be read or
     *                     encoded and hashed back to create the torrent's SHA-1 hash.
     */
    private MockedTorrent(final byte[] torrent, final String fileName) throws IOException, NoSuchAlgorithmException {
        super(torrent, false);

        try {
            // Torrent validity tests
            final int pieceLength = this.decoded_info.get("piece length").getInt();
            final ByteBuffer piecesHashes = ByteBuffer.wrap(this.decoded_info.get("pieces").getBytes());

            if (piecesHashes.capacity() / Torrent.PIECE_HASH_SIZE * (long) pieceLength < this.getSize()) {
                throw new IllegalArgumentException("Torrent size does not match the number of pieces and the piece size!");
            }
        } catch (final InvalidBEncodingException ex) {
            throw new IllegalArgumentException("Error reading torrent meta-info fields!", ex);
        }

        this.torrentInfoHash = new InfoHash(this.getInfoHash());
        this.fileName = fileName;
        
        String tempComment = null;
        if (this.decoded != null && this.decoded.containsKey("comment")) {
            try {
                tempComment = this.decoded.get("comment").getString();
            } catch (InvalidBEncodingException ignored) {
            }
        }
        this.comment = tempComment;
        
        this.clearUnneededBaseClassMemory();
    }

    private void clearUnneededBaseClassMemory() {
        final String[] fieldsToClear = {"encoded", "encoded_info", "decoded", "decoded_info"};
        for (final String fieldName : fieldsToClear) {
            try {
                final Field field = Torrent.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(this, null);
            } catch (final NoSuchFieldException | IllegalAccessException e) {
                if (log.isTraceEnabled()) {
                    log.trace("Could not clear field {} from Torrent class to save memory", fieldName, e);
                }
            }
        }
    }

    public static MockedTorrent fromFile(final File torrentFile) throws IOException, NoSuchAlgorithmException {
        final byte[] data = FileUtils.readFileToByteArray(torrentFile);
        return new MockedTorrent(data, torrentFile.getName());
    }

    public static MockedTorrent fromBytes(final byte[] bytes, final String fileName) throws IOException, NoSuchAlgorithmException {
        return new MockedTorrent(bytes, fileName);
    }

    @Override
    public String getComment() {
        return this.comment;
    }
}
