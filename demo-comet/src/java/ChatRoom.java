import java.util.LinkedList;

class ChatRoom {

    private final LinkedList<Client> clientList = new LinkedList<Client>();

    public synchronized void addMessage(String text) {
        for (Client client : clientList) {
            client.change(text);
        }
    }

    public synchronized void register(Client client) {
        clientList.add(client);
    }

    public synchronized void unregister(Client client) {
        clientList.remove(client);
    }
}
