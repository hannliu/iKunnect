package server.use_case.friend_request;

import common.packet.PacketClientFriendRequest;
import common.packet.PacketServerFriendRequestResponse;
import server.data_access.network.ConnectionInfo;
import server.entity.PacketIn;
import server.entity.ServerUser;
import server.entity.ServerUsers;
import server.use_case.ServerThreadPool;
import utils.TextUtils;

public class ServerFriendRequestInteractor implements ServerFriendRequestInputBoundary {
    private final ServerFriendRequestOutputBoundary friendRequestPresenter;
    private final ServerFriendRequestDataAccessInterface serverFriendRequestDataAccessInterface;

    public ServerFriendRequestInteractor(ServerFriendRequestDataAccessInterface serverFriendRequestDataAccessInterface, ServerFriendRequestOutputBoundary friendRequestPresenter) {
        this.friendRequestPresenter = friendRequestPresenter;
        this.serverFriendRequestDataAccessInterface = serverFriendRequestDataAccessInterface;
        ServerThreadPool.submit(() -> {
            try {
                while (!Thread.interrupted()) {
                    handlePacket(serverFriendRequestDataAccessInterface.getPacketClientFriendRequests());
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                friendRequestPresenter.addMessage("ServerFriendRequestInteractor ended");
            }
        }, "ServerFriendRequestInteractor");
    }

    private void handlePacket(PacketIn<PacketClientFriendRequest> packetIn) {
        ConnectionInfo info = packetIn.getConnectionInfo();
        try {
            if (packetIn.getConnectionInfo().getStatus() != ConnectionInfo.Status.LOGGED) {
                friendRequestPresenter.addMessage("FriendRequest Failed: connection with id " + info.getConnectionId() + " haven't logged in yet.");
                serverFriendRequestDataAccessInterface.sendTo(new PacketServerFriendRequestResponse(null, PacketServerFriendRequestResponse.Status.NOT_LOGGED_IN), info);
            } else {
                ServerUser user = info.getUser();
                ServerUser friend = serverFriendRequestDataAccessInterface.getUserByUsername(packetIn.getPacket().getUsername());
                if (friend == null || user.getUsername().equals(friend.getUsername())) {
                    friendRequestPresenter.addMessage("FriendRequest Failed: friend request to " + packetIn.getPacket().getUsername() + " by " + user.getUsername() + " is invalid.");
                    serverFriendRequestDataAccessInterface.sendTo(new PacketServerFriendRequestResponse(null, PacketServerFriendRequestResponse.Status.INFO_INVALID), info);
                } else if (user.isFriend(friend.getUserId()) != friend.isFriend(user.getUserId())) {
                    throw new IllegalStateException("How " + user.getUsername() + " and " + friend.getUsername() + " are both friend and not friend?");
                } else if (user.isFriend(friend.getUserId())) {
                    friendRequestPresenter.addMessage("FriendRequest Failed: " + user.getUsername() + " and " + friend.getUsername() + " already established friendship.");
                    serverFriendRequestDataAccessInterface.sendTo(new PacketServerFriendRequestResponse(null, PacketServerFriendRequestResponse.Status.ALREADY_FRIEND), info);
                } else if (user.wasFriend(friend.getUserId()) != friend.wasFriend(user.getUserId())) {
                    throw new IllegalStateException("How " + user.getUsername() + " and " + friend.getUsername() + " are both ex-friend and not ex-friend?");
                } else if (user.wasFriend(friend.getUserId())) {
                    if (user.isFriendRequestedBy(friend.getUserId())) {
                        user.removeFriendRequest(friend.getUserId());
                        user.makeupFriend(friend.getUserId());
                        friend.makeupFriend(user.getUserId());
                        friendRequestPresenter.addMessage("FriendRequest Success: " + user.getUsername() + " and " + friend.getUsername() + " made up their friendship.");
                        serverFriendRequestDataAccessInterface.sendTo(new PacketServerFriendRequestResponse(null, PacketServerFriendRequestResponse.Status.ACCEPTED), info);
                        //TODO: notify friend
                    } else {
                        friend.addFriendRequest(user.getUserId());
                        friendRequestPresenter.addMessage("FriendRequest Sent: from " + user.getUsername() + " to " + friend.getUsername() + ".");
                        serverFriendRequestDataAccessInterface.sendTo(new PacketServerFriendRequestResponse(null, PacketServerFriendRequestResponse.Status.SENT), info);
                        //TODO: notify friend
                    }
                } else if (user.isFriendRequestedBy(friend.getUserId())) {
                    user.removeFriendRequest(friend.getUserId());
                    user.addFriend(friend.getUserId(), -1);//TODO: Chat not implemented yet.
                    friend.addFriend(user.getUserId(), -1);
                    friendRequestPresenter.addMessage("FriendRequest Success: " + user.getUsername() + " and " + friend.getUsername() + " become friends.");
                    serverFriendRequestDataAccessInterface.sendTo(new PacketServerFriendRequestResponse(null, PacketServerFriendRequestResponse.Status.ACCEPTED), info);
                    //TODO: notify friend
                } else {
                    friend.addFriendRequest(user.getUserId());
                    friendRequestPresenter.addMessage("FriendRequest Sent: from " + user.getUsername() + " to " + friend.getUsername() + ".");
                    serverFriendRequestDataAccessInterface.sendTo(new PacketServerFriendRequestResponse(null, PacketServerFriendRequestResponse.Status.SENT), info);
                    //TODO: notify friend
                }
            }
        } catch (Exception e) {
            friendRequestPresenter.addMessage(TextUtils.error("FriendRequest Failed: " + e.getMessage()));
            serverFriendRequestDataAccessInterface.sendTo(new PacketServerFriendRequestResponse(null, PacketServerFriendRequestResponse.Status.SERVER_ERROR), info);
        } finally {
            ServerUsers.save();
        }
    }
}
