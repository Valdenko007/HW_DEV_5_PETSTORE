package ua.goit.api.command;

import com.google.gson.reflect.TypeToken;
import ua.goit.api.command.model.ApiResponse;
import ua.goit.api.command.model.User;
import ua.goit.api.service.ConsoleManager;
import ua.goit.api.service.HttpHelper;
import ua.goit.api.service.JSONConverter;
import ua.goit.api.service.Util;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class UserCommand implements Command {
    private final JSONConverter converter = new JSONConverter();
    private final ConsoleManager consoleManager;
    private final HttpClient client = HttpClient.newHttpClient();
    private final TypeToken<User> userTypeToken = new TypeToken<>(){};
    private final TypeToken<ArrayList<User>> usersTypeToken = new TypeToken<>(){};
    private final TypeToken<ApiResponse> apiResponseTypeToken = new TypeToken<>(){};

    private static final String HOST = "https://petstore.swagger.io/v2/";
    private static final String USER = "user/";
    private static final String LOG = "login?username=%s&password=%s";
    private static final String USER_MENU = " Enter the number of the command you want\n" +
            "            1 - create a list\n" +
            "            2 - get by name\n" +
            "            3 - update by name\n" +
            "            4 - delete by name\n" +
            "            5 - save user data\n" +
            "            6 - log out of the current session of the logged in user\n" +
            "            7 - create user\n" +
            "            \n" +
            "            exit - finish";

    public UserCommand(ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
    }

    private String getByName(String name) throws IOException, InterruptedException {
        String url = String.format("%s%s%s", HOST, USER, name);
        HttpRequest request = HttpHelper.createRequest(url, "GET");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return converter.convertJSONToObject(response.body(), userTypeToken).toString();
    }


    private String create(User entity) throws IOException, InterruptedException {
        String url = String.format("%s%s", HOST, USER);
        HttpRequest httpRequest = HttpHelper.createRequest(url, "POST", entity);
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ApiResponse response1 = converter.convertJSONToObject(response.body(), apiResponseTypeToken);
        return Util.getResponseMessage(response1);
    }

    private String delete(String name) throws IOException, InterruptedException {
        String url = String.format("%s%s%s", HOST, USER, name);
        HttpRequest httpRequest = HttpHelper.createRequest(url, "DELETE");
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ApiResponse response1 = converter.convertJSONToObject(response.body(), apiResponseTypeToken);
        return Util.getResponseMessage(response1);
    }

    private String update(String name, User entity) throws IOException, InterruptedException {
        String url = String.format("%s%s%s", HOST, USER, name);
        HttpRequest request = HttpHelper.createRequest(url, "PUT", entity);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse response1 = converter.convertJSONToObject(response.body(), apiResponseTypeToken);
        return Util.getResponseMessage(response1);
    }
    private User createUser() throws NumberFormatException{
        consoleManager.write("Enter user ID");
        long obtainedId =  Long.parseLong(consoleManager.read());
        consoleManager.write("Enter account name");
        String obtainedUserName = consoleManager.read();
        consoleManager.write("Enter username");
        String obtainedFirstName = consoleManager.read();
        consoleManager.write("Enter last name");
        String obtainedLastName = consoleManager.read();
        consoleManager.write("Enter user email");
        String obtainedEmail = consoleManager.read();
        consoleManager.write("Enter user password");
        String obtainedPassword = consoleManager.read();
        consoleManager.write("Enter the user's phone number");
        String obtainedPhone = consoleManager.read();
        consoleManager.write("Enter user status");
        int obtainedStatus = Integer.parseInt(consoleManager.read());
        return new User(obtainedId, obtainedUserName, obtainedFirstName, obtainedLastName, obtainedEmail,
                obtainedPassword, obtainedPhone, obtainedStatus);
    }

    private String createUsersWithList(ArrayList<User> users) throws IOException, InterruptedException {
        String url = String.format("%s%s%s", HOST, USER, "createWithList");
        HttpRequest request = HttpHelper.createRequest(url, "POST", users);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse response1 = converter.convertJSONToObject(response.body(), apiResponseTypeToken);
        return Util.getResponseMessage(response1);
    }

    private String logUot() throws IOException, InterruptedException {
        String url = String.format("%s%s%s", HOST, USER, "logout");
        HttpRequest request = HttpHelper.createRequest(url, "GET");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse response1 = converter.convertJSONToObject(response.body(), apiResponseTypeToken);
        return Util.getResponseMessage(response1);
    }

    private String logUser(String name, String pass) throws IOException, InterruptedException {
        String login = String.format(LOG, name, pass);
        String url = String.format("%s%s%s", HOST, USER, login);
        HttpRequest request = HttpHelper.createRequest(url, "GET");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse response1 = converter.convertJSONToObject(response.body(), apiResponseTypeToken);
        return Util.getResponseMessage(response1);
    }

    @Override
    public String commandName() {
        return "user";
    }

    @Override
    public void process() {
        boolean running = true;
        while (running) {
            try {
                consoleManager.write(USER_MENU);
                switch (consoleManager.read()) {
                    case "1" -> {
                        consoleManager.write("Enter how many users you want to add");
                        int amount = Integer.parseInt(consoleManager.read());
                        ArrayList<User> users = new ArrayList<>();
                        for (int i = 0; i < amount; i++) {
                            users.add(createUser());
                            consoleManager.write(" User successfully added to the list");
                        }
                        consoleManager.write(createUsersWithList(users));
                    }
                    case "2" -> {
                        consoleManager.write("Enter username");
                        consoleManager.write(getByName(consoleManager.read()));
                    }
                    case "3" -> {
                        consoleManager.write("Enter the username of which you want to update");
                        String oldName = consoleManager.read();
                        User user = createUser();
                        consoleManager.write(update(oldName, user));
                    }
                    case "4" -> {
                        consoleManager.write("Enter username");
                        consoleManager.write(delete(consoleManager.read()));
                    }
                    case "5" -> {
                        consoleManager.write("Enter Name");
                        String name = consoleManager.read();
                        consoleManager.write("Enter password");
                        String pass = consoleManager.read();
                        consoleManager.write(logUser(name, pass));
                    }
                    case "6" -> consoleManager.write(logUot());
                    case "7" -> consoleManager.write(create(createUser()));
                    case "exit" -> running = false;
                }
            } catch (IOException | InterruptedException | NumberFormatException e){
                e.printStackTrace();
            }
        }
    }
}
