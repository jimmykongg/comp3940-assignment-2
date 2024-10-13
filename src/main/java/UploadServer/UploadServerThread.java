package UploadServer;

import java.net.*;
import java.io.*;
import java.time.Clock;
public class UploadServerThread extends Thread {
   private Socket socket = null;

   public UploadServerThread(Socket socket) {
      super("DirServerThread");
      this.socket = socket;
   }

   public void run() {
      try {

         InputStream in = socket.getInputStream();
         OutputStream out = socket.getOutputStream();

         StringBuilder firstLine = new StringBuilder();
         char c;
         while((c = (char) in.read()) != '\n'){
            firstLine.append(c);
         }

         System.out.println(firstLine);

         String requestLine;
         String httpMethod = "";

         // Read the first line of HTTP request
         requestLine = firstLine.toString();

         // Parse HTTP method from the request line
         if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            if (parts.length > 0) {
               httpMethod = parts[0];
               System.out.println(httpMethod);
            } else {
               System.out.println("Invalid request line");
            }
         } else {
            System.out.println("No request line received");
         }

         HttpServlet httpServlet = new UploadServlet();
         HttpServletRequest req = new HttpServletRequest(in);
         HttpServletResponse res = new HttpServletResponse(out);

         if (httpMethod.equals("GET"))  httpServlet.doGet(req, res);
         else{
            httpServlet.doPost(req, res);
         }


         socket.close();
      } catch (Exception e) { e.printStackTrace(); }
   }
}
