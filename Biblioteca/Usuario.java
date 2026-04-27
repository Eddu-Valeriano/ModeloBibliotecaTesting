public class Usuario{
    private String nombre;
    private String DNI;
    
    public Usuario(String nombre, String dNI) {
        this.nombre = nombre;
        DNI = dNI;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDNI() {
        return DNI;
    }
    public void setDNI(String dNI) {
        DNI = dNI;
    }
}