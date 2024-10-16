package ConsoleApp;


import java.io.*;
public class Activity {
   public static void main(String[] args) throws IOException {
      new Activity().onCreate();
   }
   public Activity() {
   }
   public void onCreate() {
      // CHANGE LOCALHOST FOR TYPE TO TOMCAT IF USING TOMCAT SERVER
      // FOR TOMCAT, please change 8083 to Tomcat port
//      String uploadUrl = "http://localhost:8083/upload/upload";
      // FOR NATIVE, please change to native port
      String uploadUrl = "http://localhost:8999/upload";


      String filePath = "src/main/java/ConsoleApp/AndroidLogo.png";
      // Gather the caption and date for the upload
      String caption = "TESTING final" +
              "";
      String date = "2024-10-15"; // Today's date

      UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(uploadUrl, caption, date, filePath);
      uploadAsyncTask.execute();
      System.out.println("Waiting for Callback");

   }
}
