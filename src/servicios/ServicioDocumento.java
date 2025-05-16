package servicios;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import entidades.Documento;

public class ServicioDocumento {

    private static List<Documento> documentos = new ArrayList<>();

    public static List<Documento> getDocumentos() {
        return documentos;
    }

    public static void cargar(String nombreArchivo) {
        documentos.clear();
        BufferedReader br = Archivo.abrirArchivo(nombreArchivo);
        if (br != null) {
            try {
                String linea = br.readLine(); // linea de encabezados
                linea = br.readLine();
                while (linea != null) {
                    String[] textos = linea.split(";");
                    Documento documento = new Documento(textos[0], textos[1], textos[2], textos[3]);
                    documentos.add(documento);
                    linea = br.readLine();
                }
            } catch (Exception ex) {

            }
        }
    }

    public static void mostrar(JTable tbl) {
        String[] encabezados = new String[] { "#", "Apellido 1", "Apellido2", "Nombre", "Documento" };
        String[][] datos = new String[documentos.size()][encabezados.length];
        int fila = 0;
        for (var documento : documentos) {
            datos[fila][0] = String.valueOf(fila + 1);
            datos[fila][1] = documento.getApellido1();
            datos[fila][2] = documento.getApellido2();
            datos[fila][3] = documento.getNombre();
            datos[fila][4] = documento.getDocumento();
            fila++;
        }

        DefaultTableModel dtm = new DefaultTableModel(datos, encabezados);
        tbl.setModel(dtm);
        ;
    }

    private static boolean esMayor(Documento d1, Documento d2, int criterio) {
        if (criterio == 0) {
            return d1.getNombreCompleto().compareTo(d2.getNombreCompleto()) > 0 ||
                    (d1.getNombreCompleto().equals(d2.getNombreCompleto()) &&
                            d1.getDocumento().compareTo(d2.getDocumento()) > 0);
        } else {
            return d1.getDocumento().compareTo(d2.getDocumento()) > 0 ||
                    (d1.getDocumento().equals(d2.getDocumento()) &&
                            d1.getNombreCompleto().compareTo(d2.getNombreCompleto()) > 0);
        }
    }

    private static void intercambiar(int origen, int destino) {
        var temporal = documentos.get(origen);
        documentos.set(origen, documentos.get(destino));
        documentos.set(destino, temporal);
    }

    public static void ordenarBurbuja(int criterio) {
        for (int i = 0; i < documentos.size() - 1; i++) {
            for (int j = i + 1; j < documentos.size(); j++) {
                if (esMayor(documentos.get(i), documentos.get(j), criterio)) {
                    intercambiar(i, j);
                }
            }
        }
    }

    private static int getPivote(int inicio, int fin, int criterio) {
        int pivote = inicio;
        Documento documentoPivote = documentos.get(pivote);
        for (int i = inicio + 1; i <= fin; i++) {
            if (esMayor(documentoPivote, documentos.get(i), criterio)) {
                pivote++;
                if (i != pivote)
                    intercambiar(i, pivote);
            }
        }
        if (inicio != pivote) {
            intercambiar(inicio, pivote);
        }
        return pivote;
    }

    private static void ordernarRapido(int inicio, int fin, int criterio) {
        if (fin > inicio) {
            int pivote = getPivote(inicio, fin, criterio);
            ordernarRapido(inicio, pivote - 1, criterio);
            ordernarRapido(pivote + 1, fin, criterio);
        }
    }

    public static void ordenarRapido(int criterio) {
        ordernarRapido(0, documentos.size() - 1, criterio);
    }

    public static void ordenarInsercion(int criterio) {
        for (int i = 1; i < documentos.size(); i++) {
            var documentoActual = documentos.get(i);
            int j = i - 1;
            while (j >= 0 && esMayor(documentos.get(j), documentoActual, criterio)) {
                documentos.set(j + 1, documentos.get(j));
                j--;
            }
            documentos.set(j + 1, documentoActual);
        }
    }

    private static List<Documento> combinar(List<Documento> lista1, List<Documento> lista2, int criterio) {
        List<Documento> resultado = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < lista1.size() && j < lista2.size()) {
            if (esMayor(lista1.get(i), lista2.get(j), criterio)) {
                resultado.add(lista2.get(j));
                j++;
            } else {
                resultado.add(lista1.get(i));
                i++;
            }
        }
        while (i < lista1.size()) {
            resultado.add(lista1.get(i));
            i++;
        }
        while (j < lista2.size()) {
            resultado.add(lista2.get(j));
            j++;
        }
        return resultado;
    }

    private static List<Documento> ordenarMezcla(List<Documento> lista, int criterio) {
        if (lista.size() <= 1) {
            return lista;
        }
        int mitad = lista.size() / 2;
        List<Documento> lista1 = ordenarMezcla(lista.subList(0, mitad), criterio);
        List<Documento> lista2 = ordenarMezcla(lista.subList(mitad, lista.size()), criterio);

        return combinar(lista1, lista2, criterio);
    }

    public static void ordenarMezcla(int criterio) {
        documentos = ordenarMezcla(documentos, criterio);
    }

}
