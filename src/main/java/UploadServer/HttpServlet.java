package UploadServer;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class HttpServlet {
   protected void doGet(HttpServletRequest request, HttpServletResponse response) { return; };

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { return; };
}