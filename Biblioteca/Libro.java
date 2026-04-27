public class Libro {
    private String id;
    private String titulo;
    private String autor;
    private boolean disponible;

    public Libro(String id, String titulo, String autor, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = disponible;
    }

    public String getId(){return id;}
    public String getTitulo(){return titulo;}
    public String getAutor(){return autor;}
    public boolean isDisponible(){return disponible;}

    public void setDisponible(boolean disponible){this.disponible = disponible;}
}