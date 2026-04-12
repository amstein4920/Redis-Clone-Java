package DataStorage;

import java.time.Instant;

public class ValueEntry {
    private String value;
    private Instant expiry;

    private ValueEntry(Builder builder) {
        this.value = builder.value;
        this.expiry = builder.expiry;
    }

    public static Builder builder(String value) {
        return new Builder(value);
    }

    public static class Builder {

        // Required values
        private final String value;

        // Optional values
        private Instant expiry; // Initial value should be null

        private Builder(String value) {
            this.value = value;
        }

        public Builder expiry(long expiryInput) {
            this.expiry = Instant.now().plusMillis(expiryInput);
            return this;
        }

        public ValueEntry build() {
            return new ValueEntry(this);
        }
    }

    public String getValue() {
        return value;
    }

    public Instant getExpiry() {
        return expiry;
    }
}