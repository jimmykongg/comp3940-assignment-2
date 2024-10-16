package ConsoleApp;


import java.io.*;
public class Activity {
   public static void main(String[] args) throws IOException {
      new Activity().onCreate();
   }
   public Activity() {
   }
   public void onCreate() {
      String uploadUrl = "http://localhost:8999/upload";
      String filePath = "src/main/java/ConsoleApp/AndroidLogo.png";
      // Gather the caption and date for the upload
      String caption = "T E S T" +
              "";
      String date = "2024-10-15"; // todays date

      UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(uploadUrl, caption, date, filePath);
      uploadAsyncTask.execute();
      System.out.println("Waiting for Callback");
   }
}
