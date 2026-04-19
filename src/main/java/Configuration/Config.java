package Configuration;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class Config {
    private final Path dir;
    private final Path dbFileName;

    private Config(Builder builder) {
        this.dir = builder.dir;
        this.dbFileName = builder.dbFileName;
    }

    private static class Holder {
        private static Config INSTANCE;
    }

    public static synchronized Config getInstance() {
        if (Holder.INSTANCE == null) {
            throw new IllegalStateException("Singleton not initialized.");
        }
        return Holder.INSTANCE;
    }

    public static synchronized void initialize(Builder builder) {
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Already initialized");
        }
        Holder.INSTANCE = builder.build();
    }

    public static class Builder {

        private Path dir;
        private Path dbFileName;

        public Builder() {

        }

        public Builder setDir(String dString) {
            Path path;
            try {
                path = Path.of(dString);
            } catch (InvalidPathException e) {
                throw new IllegalArgumentException("Invalid dir argument: " + dString);
            }

            // if (!Files.exists(path) || !Files.isDirectory(path)) {
            // throw new IllegalArgumentException("Path is not accessible: " + dString);
            // }

            this.dir = path;
            return this;
        }

        public Builder setDbFileName(String dString) {
            Path path;
            try {
                path = Path.of(dString);
            } catch (InvalidPathException e) {
                throw new IllegalArgumentException("Invalid dbFileName argument: " + dString);
            }

            // if (!Files.exists(path) || !Files.isDirectory(path)) {
            // throw new IllegalArgumentException("Path is not accessible: " + dString);
            // }

            this.dbFileName = path;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }

    public Path getDir() {
        if (dir == null) {
            throw new IllegalStateException("dir has not been configured");
        }
        return dir;
    }

    public Path getDbFileName() {
        if (dbFileName == null) {
            throw new IllegalStateException("dbfilename has not been configured");
        }
        return dbFileName;
    }
}
