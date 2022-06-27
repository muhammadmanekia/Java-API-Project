import org.jibble.pircbot.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class MyBot extends PircBot {
    static Scanner input = new Scanner(System.in);
    private static HttpURLConnection connection;

    public MyBot() {
        this.setName("Bot");
    }

    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        String content = message;
        String command[] = content.split(" ");

        // message is time
        if (message.equalsIgnoreCase("time")) {
            String time = new java.util.Date().toString();
            sendMessage(channel, sender + ": The time is now " + time);
        }

        // message is quote
        if (message.equalsIgnoreCase("quote")) {
            String quote = quotesURL();
            sendMessage(channel, sender + ": " + quote);

        }

        // message is weather
        if (command[0].equalsIgnoreCase("weather")) {
            if (command.length == 1) {
                sendMessage(channel, sender + ": Please enter the word 'weather' with  city or zip");
            }

            boolean isNumeric = true;
            int zipcode = 0;
            String temperature = "";

            String address = command[1];

            try {
                zipcode = Integer.parseInt(address);
            } catch (NumberFormatException e) {
                isNumeric = false;
            }

            if (isNumeric) {
                temperature = weatherURL("null", zipcode);
            } else {
                temperature = weatherURL(address, zipcode);
            }

            sendMessage(channel, sender + ":" + temperature);

        }
    }

    // returns weather from a specific city or zip
    public static String weatherURL(String address, int zipcode) {
        String setUrl = "";
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        String temp = "";

        try {
            if (address == "null") {
                setUrl = ("http://api.openweathermap.org/data/2.5/weather?zip=" + zipcode
                        + "&APPID=26aa1d90a24c98fad4beaac70ddbf274");
            } else if (zipcode == 0) {
                setUrl = ("https://api.openweathermap.org/data/2.5/weather?q=" + address
                        + "&APPID=26aa1d90a24c98fad4beaac70ddbf274");
            }
            URL url = new URL(setUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }

            JsonObject jsonObject = new Gson().fromJson(responseContent.toString(), JsonObject.class);

            JsonObject mainObject = jsonObject.getAsJsonObject("main");

            if (mainObject == null) {
                JsonElement message = jsonObject.get("message");
                temp = message.toString();
            } else {
                JsonElement temperature = mainObject.get("temp");

                double tempDouble = temperature.getAsDouble();

                double farenheit = (9 * (tempDouble - 273.15) / 5) + 32;
                temp = "Temperature: " + String.format("%.2f", farenheit) + " F";
            }

        } catch (

        MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return temp;
    }

    // returns a random quote on every call
    public static String quotesURL() {
        String setUrl = "";
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        String quote = "";

        try {
            setUrl = "https://zenquotes.io/api/random";
            URL url = new URL(setUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }

            JsonArray jsonObject = new Gson().fromJson(responseContent.toString(), JsonArray.class);

            JsonArray mainArray = jsonObject.getAsJsonArray();

            JsonObject object = mainArray.get(0).getAsJsonObject();

            quote = object.get("q") + " – " + object.get("a");

            System.out.println(object.get("q"));
            System.out.println(object.get("a"));

        } catch (

        MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return quote;
    }
}
