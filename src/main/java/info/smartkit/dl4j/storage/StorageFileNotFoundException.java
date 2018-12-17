package info.smartkit.dl4j.storage;

class StorageFileNotFoundException extends StorageException {

    StorageFileNotFoundException(String message) {
        super(message);
    }

    StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
