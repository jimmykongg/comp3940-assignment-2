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


   static final String CONTENT_TYPE = "Content-Type: multipart/form-data; boundary=";
   static final String CONTENT_DISPOSITION_FILENAME="Content-Disposition: form-data; name=\"fileName\"; filename=\"";
   static final String CONTENT_DISPOSITION_FIELDS = "Content-Disposition: form-data; name=\"";

   /**
    * Read bytes from inputStream until meet \n char
    * @param is
    * @return
    * @throws IOException
    */
   private byte[] readByteLine(InputStream is) throws IOException {
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      int c;
      while ( (c = is.read()) >= 0 )
      {
         buf.write(c);
         if (c == '\n') break;
      }
      return buf.toByteArray();
   }

   /**
    * judge byte array start with another byte array or not
    * @param src
    * @param find
    * @return true
    */
   private boolean startsWith(byte[] src, byte[] find) {
      if( src.length < find.length ) return false;
      for(int i=0;i<find.length;i++) {
         if( find[i] != src[i] ) return false;
      }
      return true;
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

      System.out.println("start processing form");

      InputStream inputStream = req.getInputStream();
      byte[] line;
      String boundary = null;
      StringBuilder endBoundary = new StringBuilder();
      String caption = null;
      String date = null;
      String filename = null;
      ByteArrayOutputStream content = new ByteArrayOutputStream();
      while( (line = readByteLine(inputStream)).length > 0 ) {
         // get boundary
         if( startsWith(line, CONTENT_TYPE.getBytes()) ) {
            boundary = new String(line, CONTENT_TYPE.length(), line.length - CONTENT_TYPE.length() - 2);
            // last line should add -- at the begin and end of boundary
            endBoundary.append("--").append(boundary).append("--");
         } else {

            if( (boundary != null) && startsWith(line, CONTENT_DISPOSITION_FILENAME.getBytes()) ) {
               // get upload filename, here fix the input name with "filename"
               filename = new String(line, CONTENT_DISPOSITION_FILENAME.length(), line.length - CONTENT_DISPOSITION_FILENAME.length() - 3);
               // after here we need skip 2 lines
               readByteLine(inputStream);
               readByteLine(inputStream);
            } else if( (boundary != null) && startsWith(line, CONTENT_DISPOSITION_FIELDS.getBytes()) ) {
               // get the type name, caption or date.
               String fieldType = new String(line, CONTENT_DISPOSITION_FIELDS.length(), line.length - CONTENT_DISPOSITION_FIELDS.length() - 3);
               // skip the next empty line
               readByteLine(inputStream);
               // get the value for the field
               byte[] fieldValue = readByteLine(inputStream);
               // assign the value of field based on the fieldType
               switch(fieldType){
                  case "caption" :
                     caption = new String(fieldValue, 0, fieldValue.length - 2);break;
                  case "date" :
                     date = new String(fieldValue, 0, fieldValue.length - 2);break;
               }
            }  else if ((boundary != null) && (filename != null)) {
               // here is upload file content part
               if( !startsWith(line, endBoundary.toString().getBytes()) ) {
                  // here is file content
                  content.write(line);
               } else {
                  // we got the end boundary
                  break;
               }
            }
         }
      }

      System.out.println("Form process complete.");
      System.out.println("Caption: " + caption);
      System.out.println("Date: " + date);
      System.out.println("Filename: " + filename);

      String newFileName = caption + "_" + date + "_" + filename;
      File dir = new File("FileSystem");
      if(!dir.exists()) dir.mkdir();
      // output file content to local file
      File f = new File(dir, newFileName);
      // if file exists, delete first
      if( f.exists() ) f.delete();
      FileOutputStream fos = new FileOutputStream(f);
      byte[] contentBytes = content.toByteArray();
      // we need skip last 2 chars, \r\n, that is start the end boundary line.
      fos.write(contentBytes, 0, contentBytes.length - 2);
      fos.close();
   }
}