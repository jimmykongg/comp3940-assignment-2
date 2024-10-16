package ConsoleApp;


import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Date;


public class UploadClient {
    public String uploadFile(String caption, String date, File file) {
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String result = "";


        try {
            URL url = new URL("http://localhost:8083/upload");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();


            // Set the connection properties for POST request
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);


            // Create output stream
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());


            // Add 'caption' field
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"caption\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(caption + lineEnd);


            // Add 'date' field
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"date\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(date + lineEnd);


            // Add the file field
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + Files.probeContentType(file.toPath()) + lineEnd); // Set correct MIME type
            outputStream.writeBytes(lineEnd);


            // Write the file content
            FileInputStream fileInputStream = new FileInputStream(file);
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];


            // Read file and write it to the output stream
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }


            // Close file input stream
            fileInputStream.close();


            // End of multipart form data
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            // Flush and close output stream
            outputStream.flush();
            outputStream.close();


            // Get the response from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();


            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();


            // Get result from response
            result = response.toString();


            // Check for successful response (HTTP 200 OK)
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("File uploaded successfully!");
            } else {
                System.out.println("Failed to upload file. Server returned HTTP code: " + responseCode);
            }


        } catch (Exception e) {
            e.printStackTrace();
            result = "Error: " + e.getMessage();
        }


        return result;
    }
}
