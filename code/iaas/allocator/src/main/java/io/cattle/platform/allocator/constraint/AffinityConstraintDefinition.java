package io.cattle.platform.allocator.constraint;

public class AffinityConstraintDefinition {
    public enum AffinityOps {
        SOFT_NE("!=~", "_soft_ne", "should not"),
        SOFT_EQ("==~", "_soft", "should"),
        NE("!=", "_ne", "must not"),
        EQ("==", "", "must");

        String envSymbol;
        String labelSymbol;
        String displayString;

        private AffinityOps(String envSymbol, String labelSymbol, String displayString) {
            this.envSymbol = envSymbol;
            this.labelSymbol = labelSymbol;
            this.displayString = displayString;
        }

        public String getEnvSymbol() {
            return envSymbol;
        }

        public String getLabelSymbol() {
            return labelSymbol;
        }

        public String getDisplayMessage() {
            return displayString;
        }
    }

    AffinityOps op;
    String key;
    String value;

    public AffinityConstraintDefinition(AffinityOps op, String key, String value) {
        this.op = op;
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
