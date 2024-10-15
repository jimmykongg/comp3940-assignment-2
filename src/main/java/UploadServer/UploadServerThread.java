package UploadServer;

import java.net.*;
import java.io.*;

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
         while ((c = (char) in.read()) != '\n') {
            firstLine.append(c);
         }

         if (firstLine.length() == 0) {
            throw new UploadServerException("Empty request received, unable to process.");
         }

         String requestLine = firstLine.toString();
         String httpMethod = "";

         // Parse HTTP method from the request line
         if (!requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            if (parts.length > 0) {
               httpMethod = parts[0];
            } else {
               throw new UploadServerException("Invalid request line format: " + requestLine);
            }
         }

         // Initialize servlet objects for request processing
         HttpServlet httpServlet = new UploadServlet();
         HttpServletRequest req = new HttpServletRequest(in);
         HttpServletResponse res = new HttpServletResponse(out);

         // Handle HTTP methods
         if (httpMethod.equals("GET")) {
            httpServlet.doGet(req, res);
         } else if (httpMethod.equals("POST")) {
            httpServlet.doPost(req, res);
         } else {
            throw new UploadServerException("Unsupported HTTP method: " + httpMethod);
         }

         // Clean up
         socket.close();
      } catch (IOException e) {
         System.err.println("I/O error occurred while handling socket: " + e.getMessage());
      } catch (UploadServerException e) {
         System.err.println("Server error: " + e.getMessage());
      } catch (Exception e) {
         System.err.println("Unexpected error: " + e.getMessage());
      }
   }
}
