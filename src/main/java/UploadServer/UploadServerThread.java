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

         /*
            Convert socket input stream into a BufferedInputStream
            so that we could use readLine() method to read the first line of incoming HTTP request
          */
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         String requestLine;
         String responseLine;

         // Read the first line of HTTP request
         requestLine = reader.readLine();

         HttpServletRequest req = new HttpServletRequest(in);
         OutputStream baos = new ByteArrayOutputStream();
         HttpServletResponse res = new HttpServletResponse(baos); 
         HttpServlet httpServlet = new UploadServlet();
         // httpServlet.doPost(req, res);
         out.write(((ByteArrayOutputStream) baos).toByteArray());
         socket.close();
      } catch (Exception e) { e.printStackTrace(); }
   }
}
