
#Kurz gesagt

A buddy running a small company once asked me to build a microservice that would enable his customers manage files via his website.
Hence the dropwizard-based Archion-microservice was born.

The users' files are stored on a cloud service in the long term (AWS here), and on a local machine in the short term for faster retrieval.

We have three categories of files:
1. Normal, which are deleted after a year of not being accessed.
2. Temporary, which are forcibly deleted after a month
3. Archives, which can only be manually deleted.
(4. Test, which are deleted after two minutes)

There is a postgres database containing the metadata of the files, including UUIDs, owner's id, last access dates, and more.

A scheduled function running once a day, removes the files from both the local machine and the cloud service depending on the category.
Another scheduled function removes from the local machine all files that have not been accessed in the last two days.

The requests are not being sent directly by the user, but by another application. Hence that all necessary debug messages
are written in the requests.


##IMPORTANT NOTES:
1. The version here includes no authentication/authorization.
2. The requests are not being sent directly by the user, but by another application. Hence that all necessary debug messages
   are written in the requests.


=======================================

#Running in IntelliJ:
All you need is a configuration pointing the ArchionApplication class as an entry, and "server config.yml" in the CLI arguments.

One ought to fill the config.yml with one's preferred values.

Make sure the SDK is Java 9 or later, as there are functionalities not supported by Java 8.
It has been tested and runs fine with Oracle JDK 11.0.9

=======================================

##DETAILS

#ArchionRequest & ArchionResponse
    The ArchionRequest is sent by another  application when the latter intends to store, retrieve, or remove a file.

    Archion treats the ArchionResponse as an entity dependent on an ArchionRequest, and  thereby one of its' fields.

    Unlike other applications implementing a Resource/Service/Model/DAO-like pattern, the Response-object
    to be returned (ArchionResponse in our case) is not created after all functionalities complete, but already
    exists in advance and is constantly updated (at least the updatedFileMetadata field) while the code runs.

    The ArchionResponse contains the updated fileMetadata, if everything went well, otherwise it contains a nulled
    fileMetadata field and details about the problem, as well as the last instance of saved fileMetadata in the DB
    as a debug message. There was the consideration of adding a debugFileMetadata field in ArchionResponse
    yet as of 3/2022 it doesn't seem to be of any use to the caller application and a String for debug
    purposes seems enough. Still it could be a good idea for anyone who may want to adjust and use the project
    for one's own needs.


#Authentication
    The original Archion includes a form of Authentication for the caller application. The code here
    assumes that the caller application has a total control over the files.


#Files
    There are four categories (or scopes) of files: normal, temporary, archive, and test.
    The same procedures are applied to all of them, and only the time of deletion varies accordingly.

    The life-cycle of a file consists of five phases:
        1. DB_METADATA_STORED, when the metadata is saved in a database, but no actual data is
            stored yet.
        2. LOCAL_MACHINE_STORED, when the actual data is saved in the local machine but not yet
            in the cloud service.
        3. CLOUD_SERVICE_STORED, when the data is stored in both the local machine and the cloud
        4. LOCAL_MACHINE_REMOVED, when the file is removed from the local machine after not being
            accessed in three days (non-test files only, of course), and only exists on the cloud.
        5. CLOUD_SERVICE_REMOVED, the file is removed from both the cloud service and the local machine
            and is soon to be removed from the db also.

        (NOTE: In the current implementation, there are not database entries with CLOUD_SERVICE_REMOVED)


#Scheduled tasks
    Since Dropwizard provides no native support for quartz, we rely on the
    external library dropwizard-jobs-core.

    The following scheduled tasks are available:
    1. DeleteOldFiles, removing normal files last accessed two years ago.
    2. DeleteOldTempFiles, removing temporary files a month old.
    3. DeleteOldTestFiles, removing test files one minute old.
    4. DeleteDuplicates, removing duplicate files of the same user based on their
        SHA-1 digest.
    5. CleanLocalMachine, removing from the local machine files not accessed
        the last three days.

    All run once about 15 seconds after the Application starts.
    1,2, 4, and 5 run every 24 hours afterwards.
    3 runs once a minute.


#Database
    The project relies on a postgres-db currently having a single table.

    Given the simplicity of the db, a plain jdbc connection with hard-coded queries is used
    instead of an advanced technology like Hibernate or JPA. No, I am not fond of hard-coded
    queries either, but it seems enough for a project with just one table in the db.
    At least for now.

    There is the consideration of switching to jOOQ, but it is far from certain.








