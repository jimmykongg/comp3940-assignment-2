package UploadServer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

      System.out.println("start processing form");

      Map<String, String> fields = new HashMap<>();
      InputStream inputStream = req.getInputStream();

      byte[] content = new byte[1];
      StringBuilder lineBuilder = new StringBuilder();
      String line;
      boolean hitDataPart = false;
      int bytesRead = -1;

      while((bytesRead = inputStream.read(content)) != -1){
         if(hitDataPart){
            char garbage = (char) inputStream.read();
            while(garbage != '\n'){
               garbage = (char) inputStream.read();
            }
            break;
         }
         else{
            char c = (char)content[0];
            if(c != '\n')
               lineBuilder.append(c);
            else{
               line = lineBuilder.toString();
               lineBuilder = new StringBuilder();
               if(line.startsWith("Content-Disposition:")){
                  String[] name = line.split("\"");
                  String field = name[1];
                  if(field.equals("fileName")){
                     fields.put(field, name[3]);
                     hitDataPart = true;
                  } else{
                     inputStream.read();
                     inputStream.read();
                     char temp = (char) inputStream.read();
                     StringBuilder valueBuilder = new StringBuilder();
                     while(temp != '\n'){
                        valueBuilder.append(temp);
                        temp = (char)inputStream.read();
                     }
                     fields.put(field, valueBuilder.toString());
                  }
               }else if(line.startsWith("Content-Type: multipart/form-data")){
                  String boundary = line.split("boundary=")[1];
                  fields.put("boundary", boundary);
               }
            }
         }
      }

      inputStream.read();
      inputStream.read();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      byte[] lineBuffer = new byte[4096];
      int index = 0;
      StringBuilder builder = new StringBuilder();

      while ((bytesRead = inputStream.read(content)) != -1) {
         char newLine = (char) content[0];

         if (newLine == '\n' && index != 0) {
            // Convert the line buffer to a string
            String currentLine = builder.toString().trim();
            String boundary = fields.get("boundary");

            // Clear the builder for the next line
            builder = new StringBuilder();


            // Check if this line contains the boundary
            if (compareBoundary(currentLine, boundary)) {
               System.out.println("Reached boundary, stop writing data");
               break;
            } else {
               baos.write(lineBuffer, 0, index);
               index = 0; // Reset index for the next line
            }
         } else if(newLine != '\n'){
            // Append to builder for boundary checking
            builder.append(newLine);
            // Continue accumulating data into the lineBuffer
            lineBuffer[index++] = content[0];
         }
      }


      OutputStream outputStream = new FileOutputStream(new File(fields.get("fileName")));
      baos.writeTo(outputStream);
      outputStream.close();
   }

   private boolean compareBoundary(String a, String b) {
      // Normalize and trim both strings to ensure no extra spaces or newline characters
      String normalizedA = a.trim().replaceAll("\\s+", "").replaceAll("-", "");
      String normalizedB = b.trim().replaceAll("\\s+", "").replaceAll("-", "");

      System.out.println("Comparing two strings: '" + normalizedA + "' and '" + normalizedB + "'");

      if (normalizedA.contains(normalizedB)) {
         System.out.println("should be true");
         return true;
      } else {
         System.out.println("should be false");
         return false;
      }
   }


}