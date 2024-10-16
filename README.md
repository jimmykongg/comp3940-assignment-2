# File Upload Server Set up instructions
This project is a simple native web server and client written in Java. Maven is used to manage dependencies and build the project

## Prerequisites

Before you can run this project, ensure that you have the following installed on your machine:

1. Java Development Kit (JDK) 
   - Version 8 or above

## Build and run the project

### Step 1: Clone the Repository
First, clone the repository to your local machine:
```
git clone <repository-url>
cd my-project
```

### Step 2: Start Upload Server
1. Compile all java files from UploadServer directory

2. Run UploadServer on the default port (8999). Or alternatively, you may change the port you prefer in UploadServer.java


### Step 3: Start Console App Client
Once the file upload server is running, you may start the console app client to upload the file

1. Compile all java files from UploadServer directory
   
2. Run Activity


### (Optional) Step 4: Test the Upload Server using your browser
This is the step for users who prefer using UI interface to uplaod their files
Once the file upload server is running, use any web browser you prefer to visit the following site
```
http://localhost:8999
```
This will generate a HTML form for you to upload your file and then list all the files from FileSystem directory
