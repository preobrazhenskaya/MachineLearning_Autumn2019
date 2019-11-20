import com.google.gson.*;
import java.io.*;

public class Reader {
    public static Gson gson = new Gson();

    public static void writeToFile(Object object, String fileName){
        String json = gson.toJson(object);

        try(FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}