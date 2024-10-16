package ConsoleApp;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


public class UploadAsyncTask extends AsyncTask {
    private String uploadURL;
    private String caption;
    private String date;
    private String filePath; // File path to be uploaded

    public UploadAsyncTask(String uploadUrl, String caption, String date, String filePath) {
        this.uploadURL = uploadUrl;
        this.caption = caption;
        this.date = date;
        this.filePath = filePath; // Set the file path in the constructor
        System.out.println("Upload URL: " + uploadUrl);
    }


    @Override
    protected String doInBackground() {
        HttpURLConnection connection = null;
        DataOutputStream dos = null;
        BufferedReader reader = null;

        try {
            // Create the connection
            URL url = new URL(uploadURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            String boundary = "---BOUNDARY---";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Prepare the file to upload
            File fileToUpload = new File(filePath); // Use the provided file path

            // Writing the multipart/form-data to the output stream
            dos = new DataOutputStream(connection.getOutputStream());
            // Write the caption
            dos.writeBytes(boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"caption\"\r\n\r\n");
            dos.writeBytes(caption + "\r\n");

            // Write the date
            dos.writeBytes(boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"date\"\r\n\r\n");
            dos.writeBytes(date + "\r\n");

            // Write the file
            dos.writeBytes(boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"fileName\"; filename=\"" + fileToUpload.getName() + "\"\r\n");
            dos.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

            // Correctly instantiate the FileInputStream using the fileToUpload
            try (FileInputStream fileInputStream = new FileInputStream(fileToUpload)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
            }

            dos.writeBytes("\r\n--"+ boundary + "--\r\n");
            dos.flush();
            dos.close();

        // Get the response from the server
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(responseStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } else {
            throw new IOException("Server returned non-OK status: " + responseCode);
        }
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

@Override
protected void onPostExecute(String result) {
    System.out.println("Upload Result: " + result);
}
}
