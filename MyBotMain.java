import org.jibble.pircbot.*;

public class MyBotMain {
    public static void main(String[] args) throws Exception {

        // Now start our bot up.
        MyBot bot = new MyBot();

        // Enable debugging output.
        bot.setVerbose(true);

        // Connect to the IRC server.
        bot.connect("irc.us.libera.chat");
        bot.joinChannel("#CS2336");

    }

}
