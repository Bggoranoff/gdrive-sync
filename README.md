# GDrive Sync
A maven library for integrating [Google Drive]() synchronisation to your Java app.

## Supported functionality:
- Google drive authorisation
- Uploading new files/directories to a registered Google Drive account
- Updating modified files
- Deleting unnecessary cloud files/directories
- Downloading new files from Google Drive (with required permissions)
- Synchronising renamed files/directories

## Installation
Add the following lines to your pom.xml file:
<br />
```xml
<project>
    <repositories>
        <repository>
            <id>nexus</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>com.github.bggoranoff</groupId>
            <artifactId>gdrive-sync</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
</project>
```
## How to use
The main function which should be used with the service is `syncFolderWithCloud()`. Here is a code snippet showing how the library should be used correctly:
```java
public class Main {
    // This program saves lastTimeSynced to a file in order to support continuous synchronisation
    private static java.io.File lastTimeSynced = new java.io.File("/path/to/lastTimeSynced.txt");

    public static void main(String... args) throws GeneralSecurityException, IOException, ParseException {
        GDriveSyncService service = new GDriveSyncService("test", "/path/to/credentials.json", "/path/to/tokens", "/path/to/root");
        // Updating the saved lastTimeSynced. Default value 0.
        Scanner s = new Scanner(new FileInputStream(lastTimeSynced));
        service.setLastTimeSynced(s.nextLong());
        service.syncFolderWithCloud();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(lastTimeSynced));
        System.out.println(service.getLastTimeSynced());
        bufferedWriter.write(service.getLastTimeSynced() + "\n");
        bufferedWriter.close();
    }
}
```