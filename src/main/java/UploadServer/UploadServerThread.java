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

         HttpServlet httpServlet = new UploadServlet();

         /*
            Convert socket input stream into a BufferedInputStream
            so that we could use readLine() method to read the first line of incoming HTTP request
          */
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         String requestLine;
         String responseLine;
         String httpMethod = "";

         // Read the first line of HTTP request
         requestLine = reader.readLine();

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

         if (httpMethod.equals("GET")) {
            HttpServletRequest req = new HttpServletRequest(in);
            HttpServletResponse res = new HttpServletResponse(out);

            httpServlet.doGet(req, res);
         }

         // OutputStream baos = new ByteArrayOutputStream();
         // HttpServletResponse res = new HttpServletResponse(baos);
         // httpServlet.doPost(req, res);
         // out.write(((ByteArrayOutputStream) baos).toByteArray());
         socket.close();
      } catch (Exception e) { e.printStackTrace(); }
   }
}
