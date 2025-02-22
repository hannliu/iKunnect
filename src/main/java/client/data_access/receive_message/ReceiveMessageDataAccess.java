package client.data_access.receive_message;

import client.data_access.ServerDataAccessObject;
import client.use_case.receive_message.ReceiveMessageDataAccessInterface;
import client.use_case.receive_message.ReceiveMessageOutputData;
import common.packet.PacketServerTextMessage;

import static utils.MessageEncryptionUtils.AES_decrypt;

public class ReceiveMessageDataAccess implements ReceiveMessageDataAccessInterface {
    private final ServerDataAccessObject serverDataAccessObject;

    public ReceiveMessageDataAccess(ServerDataAccessObject serverDataAccessObject) {
        this.serverDataAccessObject = serverDataAccessObject;
    }

    /**
     * Retrieves a message from the server and decrypts it.
     *
     * @return The output data containing the decrypted message and associated details.
     * @throws RuntimeException if there is an error during the decryption process.
     */
    @Override
    public ReceiveMessageOutputData receiveMessage() {
        PacketServerTextMessage response = serverDataAccessObject.getReceiveMessage();
        try {
            return new ReceiveMessageOutputData(response.getSender(), AES_decrypt(response.getEncryptedMessage()), response.getTimestamp());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
