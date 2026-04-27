import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

public class BibliotecaGUI extends JFrame {

    private Biblioteca biblioteca;

    // ===== AREAS =====
    private JTable tablaUsuarios;
    private JTable tablaLibros;
    private JTable tablaPrestamos;

    private DefaultTableModel modeloUsuarios;
    private DefaultTableModel modeloLibros;
    private DefaultTableModel modeloPrestamos;

    // ===== BUSQUEDA =====
    private JTextField txtBuscarDNI;
    private JTextField txtBuscarLibro;
    private boolean esValido(String... valores) {

        for (String v : valores) {
            if (v == null || v.trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }
    public BibliotecaGUI() {

        biblioteca = new Biblioteca();
        
        setTitle("Sistema Biblioteca");
        setSize(1100, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(1, 4)); // 4 columnas

        add(panelUsuarios());
        add(panelLibros());
        add(panelPrestamos());
        add(panelAcciones());

        refrescarTodo();
    }


    
private boolean esDniValido(String dni) {
    return dni != null && dni.matches("\\d{8}");
}

private boolean esIdLibroValido(String id) {
    return id != null && id.matches("\\d{6}");
}
private JPanel panelUsuarios() {

    JPanel panel = new JPanel(new BorderLayout());

    JLabel titulo = new JLabel("USUARIOS", SwingConstants.CENTER);
    panel.add(titulo, BorderLayout.NORTH);

    // MODELO
    modeloUsuarios = new DefaultTableModel(
        new String[]{"Nombre", "Libros Prestados"}, 0
    );

    tablaUsuarios = new JTable(modeloUsuarios);

    panel.add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);

    // BUSQUEDA
    JPanel abajo = new JPanel();
    txtBuscarDNI = new JTextField(8);
    JButton btnBuscar = new JButton("Buscar");

    abajo.add(new JLabel("DNI:"));
    abajo.add(txtBuscarDNI);
    abajo.add(btnBuscar);

    panel.add(abajo, BorderLayout.SOUTH);

    btnBuscar.addActionListener(e -> buscarUsuarioTabla());

    return panel;
}
private JPanel panelLibros() {

    JPanel panel = new JPanel(new BorderLayout());

    JLabel titulo = new JLabel("LIBROS", SwingConstants.CENTER);
    panel.add(titulo, BorderLayout.NORTH);

    modeloLibros = new DefaultTableModel(
        new String[]{"Título", "Estado"}, 0
    );

    tablaLibros = new JTable(modeloLibros);

    panel.add(new JScrollPane(tablaLibros), BorderLayout.CENTER);

    JPanel abajo = new JPanel();
    txtBuscarLibro = new JTextField(8);
    JButton btnBuscar = new JButton("Buscar");

    abajo.add(new JLabel("ID:"));
    abajo.add(txtBuscarLibro);
    abajo.add(btnBuscar);

    panel.add(abajo, BorderLayout.SOUTH);

    btnBuscar.addActionListener(e -> buscarLibroTabla());

    return panel;
}

private JPanel panelPrestamos() {

    JPanel panel = new JPanel(new BorderLayout());

    JLabel titulo = new JLabel("PRÉSTAMOS", SwingConstants.CENTER);
    panel.add(titulo, BorderLayout.NORTH);

    modeloPrestamos = new DefaultTableModel(
        new String[]{"Libro ID", "DNI", "Días"}, 0
    );

    tablaPrestamos = new JTable(modeloPrestamos);

    panel.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);

    return panel;
}

private JPanel panelAcciones() {

    JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));

    JButton btnRegUsuario = new JButton("Registrar Usuario");
    JButton btnRegLibro = new JButton("Registrar Libro");
    JButton btnPrestar = new JButton("Prestar Libro");
    JButton btnDevolver = new JButton("Devolver Libro");

    panel.add(btnRegUsuario);
    panel.add(btnRegLibro);
    panel.add(btnPrestar);
    panel.add(btnDevolver);

    // EVENTOS
    btnRegUsuario.addActionListener(e -> {

        String nombre = JOptionPane.showInputDialog("Nombre:");
        String dni = JOptionPane.showInputDialog("DNI:");

        if (!esValido(nombre, dni)) {
            JOptionPane.showMessageDialog(null, "Campos vacíos");
            return;
        }

        if (!esDniValido(dni)) {
            JOptionPane.showMessageDialog(null, "El DNI debe tener 8 dígitos");
            return;
        }

        biblioteca.registrarUsuario(nombre.trim(), dni.trim());
        refrescarTodo();
    });

    btnRegLibro.addActionListener(e -> {

        String id = JOptionPane.showInputDialog("ID (6 dígitos):");
        String titulo = JOptionPane.showInputDialog("Título:");
        String autor = JOptionPane.showInputDialog("Autor:");

        if (!esValido(id, titulo, autor)) {
            JOptionPane.showMessageDialog(null, "Campos vacíos");
            return;
        }

        if (!esIdLibroValido(id)) {
            JOptionPane.showMessageDialog(null, "El ID debe tener 6 dígitos");
            return;
        }

        biblioteca.registrarLibro(id.trim(), titulo.trim(), autor.trim());
        refrescarTodo();
    });

    btnPrestar.addActionListener(e -> {

        String id = JOptionPane.showInputDialog("ID Libro:");
        String dni = JOptionPane.showInputDialog("DNI:");

        if (!esValido(id, dni)) {
            JOptionPane.showMessageDialog(null, "Campos vacíos");
            return;
        }

        if (!esIdLibroValido(id)) {
            JOptionPane.showMessageDialog(null, "ID inválido (6 dígitos)");
            return;
        }

        if (!esDniValido(dni)) {
            JOptionPane.showMessageDialog(null, "DNI inválido (8 dígitos)");
            return;
        }

        JOptionPane.showMessageDialog(null,
            biblioteca.prestarLibro(id, dni)
        );

        refrescarTodo();
    });

    btnDevolver.addActionListener(e -> {

        String id = JOptionPane.showInputDialog("ID Libro:");

        if (!esValido(id)) {
            JOptionPane.showMessageDialog(null, "Campo vacío");
            return;
        }

        if (!esIdLibroValido(id)) {
            JOptionPane.showMessageDialog(null, "ID inválido (6 dígitos)");
            return;
        }

        JOptionPane.showMessageDialog(null,
            biblioteca.devolverLibro(id)
        );

        refrescarTodo();
    });

    return panel;
}
private void refrescarTodo() {

    cargarUsuarios();
    cargarLibros();
    cargarPrestamos();
}
private void cargarUsuarios() {

    modeloUsuarios.setRowCount(0); // limpiar

    for (Usuario u : biblioteca.getUsuariosOrdenados()) {

        int cantidad = biblioteca.contarPrestamosActivos(u.getDNI());

        modeloUsuarios.addRow(new Object[]{
            u.getNombre(),
            cantidad
        });
    }
}
private void cargarLibros() {

    modeloLibros.setRowCount(0);

    for (Libro l : biblioteca.getLibrosOrdenados()) {

        modeloLibros.addRow(new Object[]{
            l.getTitulo(),
            l.isDisponible() ? "Disponible" : "Prestado"
        });
    }
}
private void cargarPrestamos() {

    modeloPrestamos.setRowCount(0);

    for (Prestamo p : biblioteca.obtenerPrestamosActivos()) {

        long dias = biblioteca.calcularDiasPrestamo(p);

        modeloPrestamos.addRow(new Object[]{
            p.getLibro().getId(),
            p.getUsuario().getDNI(),
            dias
        });
    }

    // COLOR
    tablaPrestamos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

            int dias = (int) table.getValueAt(row, 2);

            if (dias > 7) {
                c.setForeground(Color.RED);
            } else {
                c.setForeground(Color.GREEN);
            }

            return c;
        }
    });
}
private void buscarUsuarioTabla() {

    String dni = txtBuscarDNI.getText();

    Usuario u = biblioteca.buscarUsuarioPublico(dni);

    modeloUsuarios.setRowCount(0);

    if (u != null) {
        int cantidad = biblioteca.contarPrestamosActivos(dni);

        modeloUsuarios.addRow(new Object[]{
            u.getNombre(),
            cantidad
        });
    }
}
private void buscarLibroTabla() {

    String id = txtBuscarLibro.getText();

    Libro l = biblioteca.buscarLibroPublico(id);

    modeloLibros.setRowCount(0);

    if (l != null) {
        modeloLibros.addRow(new Object[]{
            l.getTitulo(),
            l.isDisponible() ? "Disponible" : "Prestado"
        });
    }
}

}