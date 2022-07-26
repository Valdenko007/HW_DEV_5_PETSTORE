package ua.goit.api.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import ua.goit.api.command.model.*;
import ua.goit.api.service.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PetCommand implements Command {
    private final CloseableHttpClient defaultClient = HttpClients.createDefault();
    private final Gson gson = new Gson();
    private final ResponseHandler responseHandler = new ResponseHandler();
    private final JSONConverter converter = new JSONConverter();
    private final ConsoleManager consoleManager;
    private final TypeToken<Pet> petTypeToken = new TypeToken<>(){};
    private final TypeToken<ArrayList<Pet>> listOfPetTypeToken = new TypeToken<>(){};
    private final TypeToken<ApiResponse> apiResponseTypeToken = new TypeToken<>(){};
    private final HttpClient client = HttpClient.newHttpClient();

    private static final String UPDATE_PET = "pet/";
    private static final String UPLOAD_IMAGE = "/uploadImage";
    private static final String HOST = "https://petstore.swagger.io/v2/";
    private static final String PET = "pet";
    private static final String FIND_BY_STATUS = "findByStatus?status=";
    private static final String PET_MENU =
           " Enter the number of the command you want to execute\n" +
                   "            1 - find by status\n" +
                   "            2 - find by id\n" +
                   "            3 - upload an image\n" +
                   "            4 - add new pet\n" +
                   "            5 - update a pet\n" +
                   "            6 - delete\n" +
                   "            exit - return ti the main menu";

    public PetCommand(ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
    }

    private String findByStatus(String status) throws IOException, InterruptedException {
        String temp;

        switch (status){
            case "SOLD", "AVAILABLE", "PENDING" -> temp = status.toLowerCase();
            default -> temp = "available";
        }

        String url = String.format("%s%s/%s%s", HOST, PET, FIND_BY_STATUS, temp);
        HttpRequest request = HttpHelper.createRequest(url, "GET");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Pet> pets = converter.convertJSONToList(response.body(), listOfPetTypeToken);
        return Util.joinListElements(pets);
    }

    private String findById(long id) throws IOException, InterruptedException {
        String url = String.format("%s%s/%d", HOST, PET, id);
        HttpRequest request = HttpHelper.createRequest(url, "GET");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Pet pet = converter.convertJSONToObject(response.body(), petTypeToken);
        return pet.toString();
    }

    private String uploadImage(long id, String metaData, File image) throws IOException, InterruptedException {
        FileBody fileBody = new FileBody(image, ContentType.DEFAULT_BINARY);
        StringBody stringBody = new StringBody(metaData, ContentType.MULTIPART_FORM_DATA);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addPart("additionalMetadata", stringBody)
                .addPart("file", fileBody);
        HttpEntity build = builder.build();
        String url = String.format("%s%s%d%s", HOST, UPDATE_PET, id, UPLOAD_IMAGE);
        HttpPost post = new HttpPost(url);
        post.setEntity(build);
        String execute = defaultClient.execute(post, responseHandler);
        ApiResponse apiResponse = gson.fromJson(execute, ApiResponse.class);
        return Util.getResponseMessage(apiResponse);
    }

    public String delete(long id) throws IOException, InterruptedException {
        String url = String.format("%s%s/%d", HOST, PET, id);
        HttpRequest request = HttpHelper.createRequest(url, "DELETE");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse apiResponse = converter.convertJSONToObject(response.body(), apiResponseTypeToken);
        return Util.getResponseMessage(apiResponse);
    }

    public String create(Pet entity) throws IOException, InterruptedException {
        String url = String.format("%s%s", HOST, PET);
        HttpRequest request = HttpHelper.createRequest(url, "POST", entity);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Pet pet = converter.convertJSONToObject(response.body(), petTypeToken);
        return Util.getResponseMessage(pet);
    }

    public String update(Pet entity) throws IOException, InterruptedException {
        String url = String.format("%s%s", HOST, PET);
        HttpRequest request = HttpHelper.createRequest(url, "PUT", entity);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Pet pet = converter.convertJSONToObject(response.body(), petTypeToken);
        return Util.getResponseMessage(pet);
    }

    private Pet createPet() throws NumberFormatException{
        Pet pet = new Pet();
        Category category = new Category();
        List<String> urls = new LinkedList<>();
        List<Tag> tags = new LinkedList<>();
        consoleManager.write("Enter pet ID");
        pet.setId(Long.parseLong(consoleManager.read()));
        consoleManager.write("Enter Category ID");
        category.setId(Long.parseLong(consoleManager.read()));
        consoleManager.write("Enter category name");
        category.setName(consoleManager.read());
        consoleManager.write("Enter your pet's name");
        pet.setName(consoleManager.read());
        consoleManager.write("How many photo links do you want to add?");
        int amount = Integer.parseInt(consoleManager.read());
        for (int i = 0; i < amount; i++) {
            consoleManager.write("Enter a link to a photo");
            urls.add(consoleManager.read());
        }
        pet.setPhotoUrls(urls);
        consoleManager.write("How many tags do you want to add");
        amount = Integer.parseInt(consoleManager.read());
        for (int i = 0; i < amount; i++) {
            Tag tag = new Tag();
            consoleManager.write("Enter Tag ID");
            tag.setId(Integer.parseInt(consoleManager.read()));
            consoleManager.write("Enter tag name");
            tag.setName(consoleManager.read());
            tags.add(tag);
        }
        pet.setTags(tags);
        consoleManager.write("Select pet status\n" +
                "                            1 - AVAILABLE\n" +
                "                            2 - PENDING\n" +
                "                            3 - SOLD");
        switch (consoleManager.read()){
            case "2" -> pet.setPetStatus(PetStatus.PENDING);
            case "3" -> pet.setPetStatus(PetStatus.SOLD);
            default -> pet.setPetStatus(PetStatus.AVAILABLE);
        }
        return pet;
    }

    @Override
    public void process() {
        boolean running = true;
        while (running){
            try {
                    consoleManager.write(PET_MENU);
                    String obtained = consoleManager.read();
                switch (obtained) {
                    case "1" -> {
                        consoleManager.write("Enter the status number of the pets you want to receive\n" +
                                "                                1 - AVAILABLE\n" +
                                "                                2 - PENDING,\n" +
                                "                                3 - SOLD");
                        String temp = consoleManager.read();
                        consoleManager.write(findByStatus(temp));
                    }
                    case "2" -> {
                        consoleManager.write("Enter the id of the pet you want to get");
                        long id = Long.parseLong(consoleManager.read());
                        consoleManager.write(findById(id));
                    }
                    case "3" -> {
                        consoleManager.write("Enter pet ID");
                        long idPhoto = Long.parseLong(consoleManager.read());
                        consoleManager.write("Enter photo description");
                        String metaData = consoleManager.read();
                        File image = null;
                        consoleManager.write("Enter the path to the photo");
                        String imagePath = consoleManager.read();
                        new FileReader(imagePath);
                        String extension = FilenameUtils.getExtension(imagePath);
                        if (extension.equalsIgnoreCase("jpeg") ||
                                extension.equalsIgnoreCase("png") ||
                                extension.equalsIgnoreCase("jpg")) {
                            image = new File(imagePath);
                        } else {
                            consoleManager.write("Retry entering data");
                        }
                        consoleManager.write(uploadImage(idPhoto, metaData, image));
                    }
                    case "4" -> consoleManager.write(create(createPet()));
                    case "5" -> consoleManager.write(update(createPet()));
                    case "6" -> {
                        consoleManager.write("Enter the ID of the pet you want to delete");
                        long delete = Long.parseLong(consoleManager.read());
                        consoleManager.write(delete(delete));
                    }
                    case "exit" -> running = false;
                    default -> consoleManager.write(Util.error());
                }
            } catch (IOException | InterruptedException | NumberFormatException e){
                consoleManager.write(Util.error());
            }
        }
    }

    @Override
    public String commandName() {
        return "pet";
    }
}