package org.araymond.joal.core.torrent.torrent;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;

public class MockedTorrentMemoryTest {
    @Test
    public void testClearBaseMemory() throws Exception {
        System.out.println("Starting test...");
        // create a dummy torrent file to test with
        File tempFile = File.createTempFile("test", ".torrent");
        // write fake basic bencode dict for torrent
        Files.write(tempFile.toPath(), "d4:infod12:piece lengthi1e6:pieces20:123456789012345678904:name4:testee".getBytes());
        MockedTorrent torrent = MockedTorrent.fromFile(tempFile);
        System.out.println("Torrent name before: " + torrent.getName());
        
        // try to nullify fields
        String[] fieldsToNullify = {"encoded", "encoded_info", "decoded", "decoded_info"};
        for (String fieldName : fieldsToNullify) {
            try {
                Field f = com.turn.ttorrent.common.Torrent.class.getDeclaredField(fieldName);
                f.setAccessible(true);
                f.set(torrent, null);
                System.out.println("Nullified: " + fieldName);
            } catch (Exception e) {
                System.out.println("Failed to nullify: " + fieldName + " " + e.getMessage());
            }
        }
        
        System.out.println("Size: " + torrent.getSize());
        System.out.println("InfoHash: " + torrent.getTorrentInfoHash().getHumanReadable());
        try {
            System.out.println("Torrent name after: " + torrent.getName());
        } catch(Exception e) {
            System.out.println("Name failed: " + e.getMessage());
        }
        tempFile.delete();
    }
}
