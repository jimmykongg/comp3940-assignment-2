package UploadServer;

import java.net.*;
import java.io.*;
import java.time.Clock;

public class UploadServerThread extends Thread {
   private Socket socket = null;

   public UploadServerThread(Socket socket) {
      super("UploadServerThread");
      System.out.println("ServerSocket: accept");
      this.socket = socket;
   }

   public void run() {
      try {
         System.out.println("UploadServerThread: run");
         InputStream in = socket.getInputStream();
         OutputStream out = socket.getOutputStream();

         StringBuilder firstLine = new StringBuilder();
         char c;
         while((c = (char) in.read()) != '\n'){
            firstLine.append(c);
         }

         String requestLine;
         String httpMethod = "";

         // Read the first line of HTTP request
         requestLine = firstLine.toString();

         // Parse HTTP method from the request line
         if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            if (parts.length > 0) {
               httpMethod = parts[0];
            } else {
               System.out.println("Invalid request line");
            }
         } else {
            System.out.println("No request line received");
         }

         // use Java Reflection to dynamically compile the FileUploadServlet
         Class<?> uploadServletClass;
         try {
            uploadServletClass = Class.forName("UploadServer.UploadServlet");
            HttpServlet httpServlet = (HttpServlet) uploadServletClass.newInstance();
            System.out.println("UploadServerThread: (HttpServlet) uploadServletClass.newInstance()");

            HttpServletRequest req = new HttpServletRequest(in);
            HttpServletResponse res = new HttpServletResponse(out);

            if (httpMethod.equals("GET")) {
               httpServlet.doGet(req, res);
               System.out.println("HttpServlet: doGet");
            }
            else {
               httpServlet.doPost(req, res);
               System.out.println("HttpServlet: doPost");
            }
         } catch (ClassNotFoundException e) {
            System.out.println(e);
         }

         socket.close();
      } catch (Exception e) { e.printStackTrace(); }
   }
}
