package pt.tecnico.mydrive.service.dto;

public class FileDto implements Comparable<FileDto> {

    private String type = "";
    private String permissions = "";
    private String size = "";
    private String owner = "";
    private String id = "";
    private String date = "";
    private String name="";

    public FileDto(String t, String p, String s, String o, String i, String d, String n) {
        this.type = t;
        this.permissions = p;
        this.size = s;
        this.owner = o;
        this.id = i;
        this.date = d;
        this.name = n;
    }

    public final String getType() {
        return this.type;
    }

    public final String getPermissions() {
        return this.permissions;
    }

    public final String getSize() {
        return this.size;
    }

    public final String getOwner() {
        return this.owner;
    }

    public final String getId() {
        return this.id;
    }

    public final String getDate() {
        return this.date;
    }

     public final String getName() {
        return this.name;
    }

    @Override
    public int compareTo(FileDto other) {
        return getType().compareTo(other.getType()) +
                 getPermissions().compareTo(other.getPermissions()) +
                 getSize().compareTo(other.getSize()) +
                 getOwner().compareTo(other.getOwner()) +
                 getId().compareTo(other.getId()) +
                 getDate().compareTo(other.getDate() +
                 getName().compareTo(other.getName()));
    } 
}