package UploadServer;

import java.io.*;
import java.time.Clock;
public class UploadServlet extends HttpServlet {
   public static final String CRLF = "\r\n";

   protected void doGet(HttpServletRequest request, HttpServletResponse response) {
      try {
         OutputStream out = response.getOutputStream();

         String htmlContent = "<!DOCTYPE html>" +
                 "<html>" +
                 "<body>" +
                 "<h2>HTML Forms</h2>" +
                 "<form action=\"http://localhost:8999\" method=\"post\" enctype=\"multipart/form-data\">" +
                 "Caption: <input type=\"text\" name=\"caption\" /><br /><br />" +
                 "Date: <input type=\"date\" name=\"date\" /><br />" +
                 "<input type=\"file\" name=\"fileName\" /><br /><br />" +
                 "<input type=\"submit\" value=\"Submit\" />" +
                 "</form>" +
                 "</body>" +
                 "</html>";

         // Calculate the content length
         int contentLength = htmlContent.getBytes("UTF-8").length;

         // Set response headers
         String headers = "HTTP/1.1 200 OK" + CRLF +
                 "Content-Type: text/html; charset=UTF-8" + CRLF +
                 "Content-Length: " + contentLength + CRLF +
                 "Connection: close" + CRLF + CRLF;

         String htmlMessage = headers + htmlContent + CRLF + CRLF;
         out.write(htmlMessage.getBytes("UTF-8"));

         System.out.println("User connects and gets the form.");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) {
      try {
         InputStream in = request.getInputStream();   
         ByteArrayOutputStream baos = new ByteArrayOutputStream();  
         byte[] content = new byte[1];
         int bytesRead = -1;

         while( ( bytesRead = in.read( content ) ) != -1 ) {  
            baos.write( content, 0, bytesRead );  
         }

         Clock clock = Clock.systemDefaultZone();
         long milliSeconds=clock.millis();

         OutputStream outputStream = new FileOutputStream(new File(String.valueOf(milliSeconds) + ".png"));
         // baos.writeTo(outputStream);
         outputStream.close();

         PrintWriter out = new PrintWriter(response.getOutputStream(), true);
         File dir = new File(".");
         String[] chld = dir.list();

      	 for(int i = 0; i < chld.length; i++) {
            String fileName = chld[i];
            out.println(fileName+"\n");
            System.out.println(fileName);
         }
      } catch(Exception ex) {
         System.err.println(ex);
      }
   }
}