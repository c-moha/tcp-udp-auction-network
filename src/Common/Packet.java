package Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Packet {
    private final String type;
    private final String RQ;
    private final List<String> other;

    private static int R_count = 1;

    // Constructor
    public Packet(String type, String requestId, String... other) {
        this.type = type;
        this.RQ = requestId;
        this.other = new ArrayList<>(Arrays.asList(other)); // auto converts "other" into a list
    }

    // Get
    public String getType() {
        return type;
    }

    public String getRequestId() {
        return RQ;
    }

    public List<String> getOtherAttributes() {
        return new ArrayList<>(other);
    }

    // Parse
    public static Packet parse(String raw) throws IllegalArgumentException {
        String[] fields = raw.split("\\|");

        if (fields.length < 2)
            throw new IllegalArgumentException("Too few tokens");
        String type = fields[0];
        String rq = fields[1];

        String[] otherAttr = Arrays.copyOfRange(fields, 2, fields.length);

        return new Packet(type, rq, otherAttr);
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder();

        sb.append(type).append("|").append(RQ);

        if (other != null) {
            for (String attr : other) {
                sb.append("|").append(attr);
            }
        }

        return sb.toString();
    }

    public static synchronized String getCount() {
        return String.format("RQ%03d", R_count++);
    }

}
