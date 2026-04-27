import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {

    private List<Libro> libros = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Prestamo> prestamos = new ArrayList<>();
    
    // =========================
    // REGISTRAR LIBRO
    // =========================
    public void registrarLibro(String id, String titulo, String autor) {
        if (!id.matches("\\d{6}")) {
            System.out.println("ID inválido");
            return;
        }
        for (Libro l : libros) {
            if (l.getId().equals(id)) {
                System.out.println("Libro ya existe");
                return;
            }
        }
        libros.add(new Libro(id, titulo, autor, true));
        System.out.println("Libro registrado");
    }
    public List<Libro> getLibrosOrdenados() {

        List<Libro> lista = new ArrayList<>(libros);

        lista.sort((a, b) -> a.getTitulo().compareToIgnoreCase(b.getTitulo()));

        return lista;
    }
    // =========================
    // REGISTRAR USUARIO
    // =========================
    public void registrarUsuario(String nombre, String dni) {
        if (!dni.matches("\\d{8}")) {
            System.out.println("DNI inválido");
            return;
        }
            for (Usuario u : usuarios) {
                if (u.getDNI().equals(dni)) {
                    System.out.println("Usuario ya existe");
                    return;
                }
            }

            usuarios.add(new Usuario(nombre, dni));
            System.out.println("Usuario registrado");
        }

        private Usuario buscarUsuario(String dni) {
        for (Usuario u : usuarios) {
            if (u.getDNI().equals(dni)) {
                return u;
            }
        }
        return null;
    }
    public int contarPrestamosActivos(String dni) {

        int contador = 0;

        for (Prestamo p : prestamos) {
            if (p.getUsuario().getDNI().equals(dni) && p.getFechaDevolucion() == null) {
                contador++;
            }
        }

        return contador;
    }
    public Usuario buscarUsuarioPublico(String dni) {
        return buscarUsuario(dni);
    }
    public List<Usuario> getUsuariosOrdenados() {

        List<Usuario> lista = new ArrayList<>(usuarios);

        lista.sort((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()));

        return lista;
    }
    public String obtenerUsuariosTexto() {

    StringBuilder sb = new StringBuilder();

        usuarios.stream()
            .sorted((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()))
            .forEach(u -> {

                long cantidad = prestamos.stream()
                    .filter(p -> p.getUsuario().getDNI().equals(u.getDNI())
                            && p.getFechaDevolucion() == null)
                    .count();

                sb.append(u.getNombre())
                    .append(" - ")
                    .append(cantidad)
                    .append("\n");
            });

        return sb.toString();
    }
    public String buscarUsuarioTexto(String dni) {

        Usuario u = buscarUsuario(dni);

        if (u == null) return "Usuario no encontrado";

        long cantidad = prestamos.stream()
            .filter(p -> p.getUsuario().getDNI().equals(dni)
                    && p.getFechaDevolucion() == null)
            .count();

        return u.getNombre() + " - " + cantidad;
    }
    // =========================
    // BUSCAR LIBRO
    // =========================
    private Libro buscarLibro(String idLibro) {
        for (Libro l : libros) {
            if (l.getId().equals(idLibro)) {
                return l;
            }
        }
        return null;
    }
    public Libro buscarLibroPublico(String id) {
        return buscarLibro(id);
    }
    public String obtenerLibrosTexto() {

        StringBuilder sb = new StringBuilder();

        libros.stream()
            .sorted((a, b) -> a.getTitulo().compareToIgnoreCase(b.getTitulo()))
            .forEach(l -> {
                sb.append(l.getTitulo())
                .append(" - ")
                .append(l.isDisponible() ? "Disponible" : "Prestado")
                .append("\n");
            });

        return sb.toString();
    }
    
    public String buscarLibroTexto(String id) {

        Libro l = buscarLibro(id);

        if (l == null) return "Libro no encontrado";

        return l.getTitulo() + " - " + (l.isDisponible() ? "Disponible" : "Prestado");
    }

    // =========================
    // PRESTAR LIBRO
    // =========================
    public String prestarLibro(String idLibro, String dniUsuario) {

        Libro libro = buscarLibro(idLibro);
        Usuario usuario = buscarUsuario(dniUsuario);

        if (usuario == null) return "Usuario no registrado";
        if (!validarPrestamoUsuario(dniUsuario)) return "No cumple condiciones";
        if (libro == null) return "Libro no encontrado";
        if (!libro.isDisponible()) return "Libro no disponible";

        Prestamo prestamo = new Prestamo(
            "P" + (prestamos.size() + 1),
            usuario,
            libro,
            LocalDate.now()
        );

        prestamos.add(prestamo);
        libro.setDisponible(false);

        return "Préstamo realizado";
    }

    public List<Prestamo> obtenerPrestamosActivos() {

        List<Prestamo> activos = new ArrayList<>();

        for (Prestamo p : prestamos) {
            if (p.getFechaDevolucion() == null) {
                activos.add(p);
            }
        }

        return activos;
    }
    public long calcularDiasPrestamo(Prestamo p) {
        return java.time.temporal.ChronoUnit.DAYS.between(
            p.getFechaPrestamo(),
            LocalDate.now()
        );
    }

    // =========================
    // DEVOLVER LIBRO
    // =========================
    public String devolverLibro(String idLibro) {

        for (Prestamo p : prestamos) {

            if (p.getLibro().getId().equals(idLibro) && p.getFechaDevolucion() == null) {

                p.setFechaDevolucion(LocalDate.now());
                p.getLibro().setDisponible(true);

                return "Libro devuelto";
            }
        }

        return "El libro no estaba prestado";
    }

    // =========================
    // CONSULTAR DISPONIBILIDAD
    // =========================
    public boolean consultarDisponibilidad(String idLibro) {

        Libro libro = buscarLibro(idLibro);

        if (libro == null) {
            System.out.println("Libro no existe");
            return false;
        }

        return libro.isDisponible();
    }

    // =========================
    // LISTAR PRÉSTAMOS
    // =========================
    public void listarPrestamos() {

        for (Prestamo p : prestamos) {

            String estado = (p.getFechaDevolucion() == null) ? "ACTIVO" : "DEVUELTO";

            System.out.println(
                "Libro: " + p.getLibro().getTitulo() +
                " | Usuario: " + p.getUsuario().getNombre() +
                " | Fecha préstamo: " + p.getFechaPrestamo() +
                " | Estado: " + estado
            );
        }
    }
    
    private boolean validarPrestamoUsuario(String dni) {

        int cantidad = 0;
        LocalDate hoy = LocalDate.now();

        for (Prestamo p : prestamos) {

            if (p.getUsuario().getDNI().equals(dni) && p.getFechaDevolucion() == null) {

                LocalDate fechaLimite = p.getFechaPrestamo().plusDays(7);

                // 🔴 VALIDAR SI ESTÁ VENCIDO
                if (hoy.isAfter(fechaLimite)) {
                    System.out.println("El usuario tiene préstamos vencidos. No puede prestar más libros.");
                    return false;
                }

                cantidad++;
            }
        }

        if (cantidad >= 3) {
            System.out.println("El usuario alcanzó el límite de préstamos");
            return false;
        }

        return true;
    }
    public List<Prestamo> listarPrestamosInterno() {
    return prestamos;
}
}