import org.wings.session.Session;

class Client {

    private final Session session;
    private final Chat chat;

    public Client(Session session, Chat chat) {
        this.session = session;
        this.chat = chat;
    }

    public void change(final String text) {
        session.getDispatcher().invokeLater(new Runnable() {
            public void run() {
                chat.appendText(text);
            }
        });
    }
}
