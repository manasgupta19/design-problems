package Tier2.TaggingService.model;

public class TagResponse {
    boolean success;
    String error;
    public TagResponse(boolean s, String e) { this.success = s; this.error = e; }

    public boolean isSuccess() { return success; }
    public String getError() { return error; }
}
