package net.rocketeer.sevens.player;

import java.io.*;
import java.util.UUID;

public interface PlayerDatabase {
  SevensPlayer findPlayer(UUID uuid) throws Exception;
  Record fetchRecord(SevensPlayer player1, SevensPlayer player2) throws Exception;
  void updateRecord(Record record, int newKills, int newDeaths) throws Exception;
  void updateScore(SevensPlayer player, int addScore) throws Exception;

  static byte[] uuidToBytes(UUID uuid) throws IOException {
    ByteArrayOutputStream ba = new ByteArrayOutputStream(16);
    DataOutputStream os = new DataOutputStream(ba);
    os.writeLong(uuid.getMostSignificantBits());
    os.writeLong(uuid.getLeastSignificantBits());
    return ba.toByteArray();
  }

  static ByteArrayInputStream uuidToStream(UUID uuid) throws IOException {
    return new ByteArrayInputStream(uuidToBytes(uuid));
  }

  static UUID streamToUuid(InputStream stream) throws IOException {
    byte[] uuidBytes = new byte[16];
    stream.read(uuidBytes);

    ByteArrayInputStream ba = new ByteArrayInputStream(uuidBytes);
    DataInputStream is = new DataInputStream(ba);

    return new UUID(is.readLong(), is.readLong());
  }
}
