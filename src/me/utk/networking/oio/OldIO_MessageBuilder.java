package me.utk.networking.oio;

import java.util.LinkedList;

class OldIO_MessageBuilder implements me.utk.networking.MessageBuilder {
    private final LinkedList<String[]> MESSAGES = new LinkedList<>();
    private final LinkedList<String> LINES = new LinkedList<>();

    private static final String[] CAST_ARRAY = new String[0];

    public OldIO_MessageUtil addLine(String line) {
        OldIO_MessageUtil type = OldIO_MessageUtil.fromString(line);
        if (type == OldIO_MessageUtil.NONE)
            LINES.add(line);
        else if (!LINES.isEmpty()) {
            if (type == OldIO_MessageUtil.END_MESSAGE)
                LINES.add("" + OldIO_MessageUtil.END_MESSAGE);
            MESSAGES.add(LINES.toArray(CAST_ARRAY));
            LINES.clear();
        }
        return type;
    }

    @Override
    public boolean hasMoreMessages() {
        return !MESSAGES.isEmpty();
    }
    @Override
    public String[] nextMessage() {
        return MESSAGES.removeFirst();
    }
}
