package pt.tecnico.mydrive.service.dto;

public class VariableDto implements Comparable<VariableDto> {

    private String name;
    private String value;

    public VariableDto(String name, String value) {
        this.name = name;
        this.value = value;
    }

     public final String getName() {
        return this.name;
    }
     
     public final String getValue() {
         return this.value;
     }

    @Override
    public int compareTo(VariableDto other) {
        return getName().compareTo(other.getName() +
                 getValue().compareTo(other.getValue()));
    } 
}