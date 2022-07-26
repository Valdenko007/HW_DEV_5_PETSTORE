package ua.goit.api.service;

import ua.goit.api.command.model.ApiResponse;
import java.util.List;
import java.util.StringJoiner;

public class Util {
    public static String menuMessage() {
        return ("Enter command category number\n" +
                "                pet - pet category\n" +
                "                user - user category\n" +
                "                store - store category\n" +
                "                exit - finish");
    }

    public static <T> String joinListElements(List<T> t){
        StringJoiner joiner = new StringJoiner("\n\n");
        for (T temp:t) {
            joiner.add(temp.toString());
        }
        return joiner.toString();
    }

    public static String getResponseMessage(ApiResponse response){
        if (response.getCode() == 200){
            return successful();
        } else {
            return error();
        }
    }

    public static <T> String getResponseMessage(T t){
        if (t!=null){
            return successful();
        } else {
            return error();
        }
    }

    public static String successful(){
        return "Your request has been processed successfully";
    }

    public static String error(){
        return "An error has occurred, please re-enter your data.";
    }
}
