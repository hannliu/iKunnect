package server.use_case.login;

import common.packet.Packet;
import common.packet.PacketClientLogin;
import server.data_access.network.ConnectionInfo;
import server.entity.PacketIn;
import server.entity.ServerUser;
import utils.Tuple;

/**
 * The ServerLoginDataAccessInterface provides an interface for data access operations related to the login process.
 * Implementations of this interface handle interactions with the server user database and communication with clients.
 */
public interface ServerLoginDataAccessInterface {
    /**
     * Retrieves a login packet from the input queue, blocking if no packet is available.
     *
     * @return The login packet received from a client.
     * @throws InterruptedException If the thread is interrupted while waiting for a packet.
     */
    PacketIn<PacketClientLogin> getPacketClientLogin() throws InterruptedException;

    /**
     * Sends a packet to the specified connection using the provided ConnectionInfo.
     *
     * @param packet The packet to be sent.
     * @param info   The ConnectionInfo of the target connection.
     */
    void sendTo(Packet packet, ConnectionInfo info);

    /**
     * Checks the password for a given username in the server user database.
     *
     * @param username The username to check for.
     * @param password The password to validate against the stored password.
     * @return A Tuple representing the result of the password check.
     * - If the username and password match, returns Tuple with integer 0 and the corresponding ServerUser.
     * - If the username exists but the password does not match, returns Tuple with integer -1 and null.
     * - If the username does not exist, returns Tuple with integer -2 and null.
     */
    Tuple<Integer, ServerUser> checkPassword(String username, String password);
}
