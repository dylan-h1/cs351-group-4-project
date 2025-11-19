package constants;

public enum Command {
    CREATE_ACCOUNT("CREATE_ACCOUNT"),
    LOGIN("LOGIN"),
    BALANCE("BALANCE"),
    DEPOSIT("DEPOSIT"),
    WITHDRAW("WITHDRAW"),
    TRANSFER("TRANSFER"),
    LOGOUT("LOGOUT"),
    VIEW_TRANSACTIONS("VIEW_TRANSACTIONS");

    private final String text;

    Command(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Command fromString(String s) {
        for (Command c : values()) {
            if (c.text.equalsIgnoreCase(s)) {
                return c;
            }
        }
        return null;
    }

}
